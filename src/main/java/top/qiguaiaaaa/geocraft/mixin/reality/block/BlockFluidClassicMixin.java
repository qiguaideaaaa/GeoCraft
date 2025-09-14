package top.qiguaiaaaa.geocraft.mixin.reality.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.qiguaiaaaa.geocraft.api.setting.GeoFluidSetting;
import top.qiguaiaaaa.geocraft.api.util.FluidUtil;
import top.qiguaiaaaa.geocraft.api.util.math.FlowChoice;
import top.qiguaiaaaa.geocraft.geography.fluid_physics.FluidUpdateManager;
import top.qiguaiaaaa.geocraft.geography.fluid_physics.reality.MoreRealityBlockFluidClassicUpdateTask;
import top.qiguaiaaaa.geocraft.util.FluidOperationUtil;
import top.qiguaiaaaa.geocraft.util.mixinapi.IMoreRealityBlockFluidBase;

import java.util.*;

@Mixin(value = BlockFluidClassic.class)
public abstract class BlockFluidClassicMixin extends BlockFluidBase implements IMoreRealityBlockFluidBase<BlockFluidClassic> {

    public BlockFluidClassicMixin(Fluid fluid, Material material, MapColor mapColor) {
        super(fluid, material, mapColor);
    }

    public BlockFluidClassicMixin(Fluid fluid, Material material) {
        super(fluid, material);
    }

    @Inject(method = "updateTick",at = @At("HEAD"),cancellable = true)
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand, CallbackInfo ci) {
        if(!GeoFluidSetting.isFluidToBePhysical(this.getFluid())) return;
        ci.cancel();
        FluidUpdateManager.addTask(world,new MoreRealityBlockFluidClassicUpdateTask(this.getFluid(),pos,getThis(),quantaPerBlock,tickRate,densityDir));
    }

    @Inject(method = "drain",at = @At("HEAD"),cancellable = true,remap = false)
    private void drain(World world, BlockPos pos, boolean doDrain, CallbackInfoReturnable<FluidStack> cir) {
        if(!GeoFluidSetting.isFluidToBePhysical(this.getFluid())) return;
        final FluidStack fluidStack = new FluidStack(this.getFluid(), MathHelper.floor((this.getQuantaPercentage(world, pos) * Fluid.BUCKET_VOLUME)));

        if (doDrain) {
            world.setBlockToAir(pos);
        }
        cir.setReturnValue(fluidStack);
        cir.cancel();
    }
    //下面这段代码参考自Forge的BlockFluidFinite类
    @Inject(method = "place",at =@At("HEAD"),cancellable = true,remap = false)
    private void place(World world, BlockPos pos, FluidStack fluidStack, boolean doPlace, CallbackInfoReturnable<Integer> cir) {
        if(!GeoFluidSetting.isFluidToBePhysical(this.getFluid())) return;
        cir.cancel();
        IBlockState existing = world.getBlockState(pos);
        float amountPerQuanta = Fluid.BUCKET_VOLUME / this.quantaPerBlockFloat;

        int amountInFact = Fluid.BUCKET_VOLUME;
        int quantaExpected = this.quantaPerBlock;
        if (fluidStack.amount < amountInFact) {
            amountInFact = MathHelper.floor(amountPerQuanta * MathHelper.floor(fluidStack.amount / amountPerQuanta));
            quantaExpected = MathHelper.floor(amountInFact / amountPerQuanta);
        }
        if (existing.getBlock() == this) {
            int existingQuanta = FluidUtil.getFluidQuanta(world,pos,existing);
            int missingQuanta = this.quantaPerBlock - existingQuanta;
            amountInFact = Math.min(amountInFact, MathHelper.floor(missingQuanta * amountPerQuanta));
            quantaExpected = Math.min(quantaExpected + existingQuanta,this.quantaPerBlock);
        }

        if (quantaExpected < 1 || quantaExpected > 16)
            cir.setReturnValue(0);

        if (doPlace) {
            net.minecraftforge.fluids.FluidUtil.destroyBlockOnFluidPlacement(world, pos);
            world.setBlockState(pos,this.getDefaultState().withProperty(LEVEL, this.quantaPerBlock-quantaExpected), Constants.BlockFlags.SEND_TO_CLIENTS);
        }

        cir.setReturnValue(amountInFact);
    }

    @Inject(method = "canDrain",at = @At("HEAD"),cancellable = true,remap = false)
    private void canDrain(World world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if(!GeoFluidSetting.isFluidToBePhysical(this.getFluid())) return;
        cir.setReturnValue(true);
        cir.cancel();
    }

    @Override
    public BlockFluidClassic getThis() {
        return (BlockFluidClassic) (Block)this;
    }
}
