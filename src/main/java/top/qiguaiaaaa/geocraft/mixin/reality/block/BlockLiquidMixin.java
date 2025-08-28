package top.qiguaiaaaa.geocraft.mixin.reality.block;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.qiguaiaaaa.geocraft.mixin.common.BlockAccessor;
import top.qiguaiaaaa.geocraft.util.FluidOperationUtil;
import top.qiguaiaaaa.geocraft.api.util.FluidUtil;

import static net.minecraft.block.BlockLiquid.LEVEL;

@Mixin(value = BlockLiquid.class)
public abstract class BlockLiquidMixin {
    @Inject(method = "getFlow",at = @At("HEAD"),cancellable = true)
    protected void getFlow(IBlockAccess worldIn, BlockPos pos, IBlockState state, CallbackInfoReturnable<Vec3d> cir){
        if(state.getBlock() instanceof BlockStaticLiquid){
            cir.setReturnValue(new Vec3d(0,0,0));
            cir.cancel();
        }
    }
    /**
     * @author QiguaiAAAA
     * @reason make Water disappear when mixing
     */
    @Inject(method = "checkForMixing",at = @At("HEAD"),cancellable = true)
    public void checkForMixing(World worldIn, BlockPos pos, IBlockState state, CallbackInfoReturnable<Boolean> cir) {
        cir.cancel();
        if (((BlockAccessor)this).getMaterial() == Material.LAVA) {
            boolean flag = false;
            EnumFacing waterFacing = null;
            for (EnumFacing facing : EnumFacing.values()) {
                if(facing == EnumFacing.DOWN) continue;
                if (worldIn.getBlockState(pos.offset(facing)).getMaterial() == Material.WATER) {
                    flag = true;
                    waterFacing = facing;
                    break;
                }
            }

            if (flag) {
                int meta = state.getValue(LEVEL);
                if (meta == 0) {
                    worldIn.setBlockState(pos, ForgeEventFactory.fireFluidPlaceBlockEvent(worldIn, pos, pos, Blocks.OBSIDIAN.getDefaultState()));
                    this.triggerMixEffects(worldIn, pos);
                }else{
                    worldIn.setBlockState(pos, ForgeEventFactory.fireFluidPlaceBlockEvent(worldIn, pos, pos, Blocks.COBBLESTONE.getDefaultState()));
                    this.triggerMixEffects(worldIn, pos);
                }
                cir.setReturnValue(true);
                BlockPos waterPos = pos.offset(waterFacing);
                IBlockState waterState = worldIn.getBlockState(waterPos);
                int waterQuanta = FluidUtil.getFluidQuanta(worldIn,waterPos,waterState);
                int newWaterQuanta = waterQuanta-1;
                FluidOperationUtil.setQuanta(worldIn,waterPos,waterState,newWaterQuanta);
                return;
            }
        }
        cir.setReturnValue(false);
    }

    @Shadow
    protected void triggerMixEffects(World worldIn, BlockPos pos) {
    }
}
