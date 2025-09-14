package top.qiguaiaaaa.geocraft.mixin.reality.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.IFluidBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.qiguaiaaaa.geocraft.api.block.IPermeableBlock;
import top.qiguaiaaaa.geocraft.api.setting.GeoFluidSetting;
import top.qiguaiaaaa.geocraft.api.util.FluidUtil;
import top.qiguaiaaaa.geocraft.api.util.math.FlowChoice;
import top.qiguaiaaaa.geocraft.geography.fluid_physics.FluidUpdateManager;
import top.qiguaiaaaa.geocraft.geography.fluid_physics.reality.MoreRealityBlockDynamicLiquidUpdateTask;
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
        FluidUpdateManager.addTask(worldIn,new MoreRealityBlockDynamicLiquidUpdateTask(thisFluid,pos,(BlockDynamicLiquid) (Block)this));
    }

    @Override
    public boolean canFlow(World worldIn,BlockPos pos,IBlockState state,Random rand){
        if (!worldIn.isAreaLoaded(pos, this.getSlopeFindDistance(worldIn))){
            return false;
        }

        int liquidMeta = state.getValue(LEVEL);
        if(liquidMeta >= 8){
            return false;
        }
        int liquidQuanta = 8-liquidMeta;

        IBlockState stateBelow = worldIn.getBlockState(pos.down());
        boolean canMoveDown = this.canMoveDownTo(worldIn, pos.down(), stateBelow);
        if(canMoveDown) return true;

        //坡度流动模式
        if(liquidMeta == 7){
            if(FluidUtil.getFluid(stateBelow) == thisFluid) return false;
            Set<EnumFacing> directions = this.getPossibleFlowDirections(worldIn, pos);
            return !directions.isEmpty();
        }
        //可流动方向检查
        final ArrayList<FlowChoice> averageModeFlowDirections = new ArrayList<>();//平均流动模式可用方向
        this.checkNeighborsToFindFlowChoices(worldIn,pos,liquidQuanta,averageModeFlowDirections);

        return !averageModeFlowDirections.isEmpty();
    }

    /**
     * 检查方块四周可流动的选择
     * @param worldIn 所在世界
     * @param pos 方块位置
     * @param liquidQuanta 液体量
     * @param averageModeFlowDirections 平均流动模式下的选择列表
     */
    private void checkNeighborsToFindFlowChoices(World worldIn,BlockPos pos,int liquidQuanta,List<FlowChoice> averageModeFlowDirections){
        for(EnumFacing facing:EnumFacing.Plane.HORIZONTAL){
            IBlockState facingState = worldIn.getBlockState(pos.offset(facing));
            if(!canFlowInto(worldIn,pos.offset(facing),facingState)) continue;
            if(!canFlowIntoWhenItIsSnowLayer(facingState,liquidQuanta)) continue;
            int facingMeta = this.getDepth(facingState);
            if(facingMeta <0 || facingMeta>7) facingMeta = 8;
            int facingQuanta = 8-facingMeta;
            if(facingQuanta<liquidQuanta-1){
                averageModeFlowDirections.add(new FlowChoice(facingQuanta,facing));
            }
        }
    }

    /**
     * 液体是否可以往下流动
     * @param worldIn 所在世界
     * @param pos 下方位置
     * @param state 下方方块状态
     * @return 如果可以往下流动，则返回true
     */
    private boolean canMoveDownTo(World worldIn, BlockPos pos, IBlockState state){
        if(state.getBlock() instanceof IFluidBlock) return false;
        Material material = state.getMaterial();
        if(material.isLiquid()){
            if(material == Material.WATER && this.material == Material.LAVA) return true;
            if(material != this.material) return false;
            return state.getValue(LEVEL) != 0;
        }
        if(state.getBlock() == Blocks.SNOW_LAYER){
            return state.getValue(BlockSnow.LAYERS) < 8;
        }
        return !this.isBlocked(worldIn,pos,state);
    }

    private boolean canFlowIntoWhenItIsSnowLayer(IBlockState state,int thisQuanta){
        if(thisFluid != FluidRegistry.WATER) return true;
        if(state.getBlock() != Blocks.SNOW_LAYER) return true;
        int layer = state.getValue(BlockSnow.LAYERS);
        return layer < thisQuanta-2;
    }
    @Inject(method = "getPossibleFlowDirections",at = @At("HEAD"),cancellable = true)
    private void getPossibleFlowDirections(World worldIn, BlockPos pos, CallbackInfoReturnable<Set<EnumFacing>> cir) {
        if(!GeoFluidSetting.isFluidToBePhysical(thisFluid)) return;
        cir.cancel();
        int difficulty = 1000;
        Set<EnumFacing> possibleDirections = EnumSet.noneOf(EnumFacing.class);

        for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
            BlockPos facingPos = pos.offset(enumfacing);
            IBlockState state = worldIn.getBlockState(facingPos);

            if (this.isBlocked(worldIn, facingPos, state) || !canFlowIntoWhenItIsSnowLayer(state,1) || FluidUtil.isFluid(state)) {
                continue;
            }
            int slope;
            IBlockState stateBelow = worldIn.getBlockState(facingPos.down());
            if (!canMoveDownTo(worldIn,facingPos.down(),stateBelow)) {
                slope = this.getSlopeDistance(worldIn, facingPos, 1, enumfacing.getOpposite());
            } else{
                slope = 0;
            }

            if (slope < difficulty)
                possibleDirections.clear();
            if (slope <= difficulty) {
                possibleDirections.add(enumfacing);
                difficulty = slope;
            }
        }
        if(difficulty == 1000) possibleDirections.clear();
        cir.setReturnValue(possibleDirections);
    }
    @Inject(method = "getSlopeDistance",at = @At("HEAD"),cancellable = true)
    private void getSlopeDistance(World worldIn, BlockPos pos, int distance, EnumFacing from, CallbackInfoReturnable<Integer> cir) {
        if(!GeoFluidSetting.isFluidToBePhysical(thisFluid)) return;
        cir.cancel();
        int difficulty = 1000;

        for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
            if (enumfacing == from) continue;
            BlockPos facingPos = pos.offset(enumfacing);
            IBlockState state = worldIn.getBlockState(facingPos);

            if (this.isBlocked(worldIn, facingPos, state) || !canFlowIntoWhenItIsSnowLayer(state,1) || FluidUtil.isFluid(state)) {
                continue;
            }
            IBlockState stateBelow = worldIn.getBlockState(facingPos.down());
            if (canMoveDownTo(worldIn,facingPos.down(),stateBelow)) {
                cir.setReturnValue(distance);
                return;
            }

            if (distance < this.getSlopeFindDistance(worldIn)) {
                int newDistance = this.getSlopeDistance(worldIn, facingPos, distance + 1, enumfacing.getOpposite());
                if (newDistance < difficulty) difficulty = newDistance;
            }
        }

        cir.setReturnValue(difficulty);
    }

    @Override
    public void setCorrespondingFluid(Fluid fluid) {
        if(thisFluid != null)return;
        this.thisFluid = fluid;
    }

    @Shadow
    private boolean canFlowInto(World worldIn, BlockPos pos, IBlockState state) {return false;}
    /**
     * Q=1 坡度流动模式的可流动方向寻找算法
     */
    @Shadow
    private Set<EnumFacing> getPossibleFlowDirections(World worldIn, BlockPos pos) {return EnumSet.noneOf(EnumFacing.class);}
    /**
     * Q=1 坡度流动模式的可流动方向寻找内层递归算法
     */
    @Shadow
    private int getSlopeDistance(World worldIn,BlockPos pos,int distance,EnumFacing from){return 0;}
    @Shadow
    private int getSlopeFindDistance(World worldIn) {
        return 0;
    }
    @Shadow
    private boolean isBlocked(World worldIn, BlockPos pos, IBlockState state){return false;}

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
