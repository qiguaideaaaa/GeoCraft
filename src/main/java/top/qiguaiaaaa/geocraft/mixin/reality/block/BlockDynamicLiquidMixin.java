package top.qiguaiaaaa.geocraft.mixin.reality.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.qiguaiaaaa.geocraft.api.block.IPermeableBlock;
import top.qiguaiaaaa.geocraft.api.setting.GeoFluidSetting;
import top.qiguaiaaaa.geocraft.api.util.FluidUtil;
import top.qiguaiaaaa.geocraft.api.util.math.FlowChoice;
import top.qiguaiaaaa.geocraft.geography.fluid_physics.FluidUpdateManager;
import top.qiguaiaaaa.geocraft.geography.fluid_physics.reality.RealityBlockLiquidUtil;
import top.qiguaiaaaa.geocraft.geography.fluid_physics.reality.update.RealityBlockDynamicLiquidUpdateTask;
import top.qiguaiaaaa.geocraft.util.mixinapi.FluidSettable;
import top.qiguaiaaaa.geocraft.util.mixinapi.IVanillaFlowChecker;

import javax.annotation.Nonnull;
import java.util.*;

@Mixin(value = BlockDynamicLiquid.class)
public class BlockDynamicLiquidMixin extends BlockLiquid implements FluidSettable, IVanillaFlowChecker, IPermeableBlock {
    private Fluid thisFluid;

    protected BlockDynamicLiquidMixin(Material materialIn) {
        super(materialIn);
    }

    @Inject(method = "updateTick",at = @At("HEAD"),cancellable = true)
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand, CallbackInfo ci) {
        if(!GeoFluidSetting.isFluidToBePhysical(thisFluid)) return;
        ci.cancel();
        if (!worldIn.isAreaLoaded(pos,1)){
            return;
        }
        FluidUpdateManager.addTask(worldIn,new RealityBlockDynamicLiquidUpdateTask(thisFluid,pos,(BlockDynamicLiquid) (Block)this));
    }

    @Override
    public boolean canFlow(World worldIn,BlockPos pos,IBlockState state,Random rand){
        if (!worldIn.isAreaLoaded(pos, RealityBlockLiquidUtil.getSlopeFindDistance(worldIn,this))){
            return false;
        }

        int liquidMeta = state.getValue(LEVEL);
        if(liquidMeta >= 8){
            return false;
        }
        int liquidQuanta = 8-liquidMeta;

        IBlockState stateBelow = worldIn.getBlockState(pos.down());
        boolean canMoveDown = RealityBlockLiquidUtil.canMoveDownTo(material,stateBelow);
        if(canMoveDown) return true;

        //坡度流动模式
        if(liquidMeta == 7){
            if(FluidUtil.getFluid(stateBelow) == thisFluid) return false;
            Set<EnumFacing> directions = RealityBlockLiquidUtil.getPossibleFlowDirections(worldIn, pos,this);
            return !directions.isEmpty();
        }
        //可流动方向检查
        final ArrayList<FlowChoice> averageModeFlowDirections = new ArrayList<>();//平均流动模式可用方向
        RealityBlockLiquidUtil.checkNeighborsToFindFlowChoices(worldIn,pos,this,liquidQuanta,averageModeFlowDirections);

        return !averageModeFlowDirections.isEmpty();
    }

    @Inject(method = "getPossibleFlowDirections",at = @At("HEAD"),cancellable = true)
    private void getPossibleFlowDirections(World worldIn, BlockPos pos, CallbackInfoReturnable<Set<EnumFacing>> cir) {
        if(!GeoFluidSetting.isFluidToBePhysical(thisFluid)) return;
        cir.cancel();
        cir.setReturnValue(RealityBlockLiquidUtil.getPossibleFlowDirections(worldIn,pos,this));
    }

    @Inject(method = "getSlopeDistance",at = @At("HEAD"),cancellable = true)
    private void getSlopeDistance(World worldIn, BlockPos pos, int distance, EnumFacing from, CallbackInfoReturnable<Integer> cir) {
        if(!GeoFluidSetting.isFluidToBePhysical(thisFluid)) return;
        cir.cancel();
        cir.setReturnValue(RealityBlockLiquidUtil.getSlopeDistance(worldIn,pos,distance,from,this));
    }

    //********
    // FluidSettable
    //********

    @Override
    public void setCorrespondingFluid(Fluid fluid) {
        if(thisFluid != null)return;
        this.thisFluid = fluid;
    }

    //*********
    // 透水方块
    //*********

    @Nonnull
    @Override
    public Fluid getFluid(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        return thisFluid;
    }

    @Override
    public int getQuanta(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        return 8-state.getValue(BlockLiquid.LEVEL);
    }

    @Override
    public int getHeight(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        return getQuanta(world,pos,state)*2;
    }

    @Override
    public int getHeightPerQuanta() {
        return 2;
    }

    @Override
    public void addQuanta(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, int quanta) {
        int newQuanta = 8-(state.getValue(BlockLiquid.LEVEL)-quanta);
        setQuanta(world,pos,state,newQuanta);
    }

    @Override
    public void setQuanta(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, int newQuanta) {
        if(newQuanta <= 0) world.setBlockToAir(pos);
        world.setBlockState(pos,state.withProperty(BlockLiquid.LEVEL,8-newQuanta), net.minecraftforge.common.util.Constants.BlockFlags.SEND_TO_CLIENTS);
    }

    @Nonnull
    @Override
    public IBlockState getQuantaState(@Nonnull IBlockState state, int newQuanta) {
        if(newQuanta <= 0) return Blocks.AIR.getDefaultState();
        return state.withProperty(BlockLiquid.LEVEL,8-newQuanta);
    }
}
