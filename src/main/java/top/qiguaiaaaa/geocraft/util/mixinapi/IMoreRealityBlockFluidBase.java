package top.qiguaiaaaa.geocraft.util.mixinapi;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;
import top.qiguaiaaaa.geocraft.util.FluidOperationUtil;
import top.qiguaiaaaa.geocraft.api.util.FluidUtil;
import top.qiguaiaaaa.geocraft.configs.SimulationConfig;
import top.qiguaiaaaa.geocraft.mixin.common.BlockFluidBaseAccessor;

import java.util.EnumSet;
import java.util.Set;

import static net.minecraft.block.BlockLiquid.LEVEL;

public interface IMoreRealityBlockFluidBase<BlockType extends BlockFluidBase> {
    BlockType getThis();
    default void flowDown(World worldIn,BlockPos currentPos,BlockPos downPos,IBlockState thisState,IBlockState downState,int liquidQuanta,int quantaPerBlock){
        int belowQuanta = FluidUtil.getFluidQuanta(worldIn,downPos,downState);
        int totalQuanta = liquidQuanta+belowQuanta;
        if(totalQuanta<=quantaPerBlock){
            worldIn.setBlockToAir(currentPos);
            worldIn.setBlockState(downPos, downState.withProperty(LEVEL, quantaPerBlock - totalQuanta), Constants.BlockFlags.DEFAULT);
        }else{
            int remain = totalQuanta-quantaPerBlock;
            worldIn.setBlockState(currentPos,thisState.withProperty(LEVEL, quantaPerBlock - remain),Constants.BlockFlags.DEFAULT);
            worldIn.scheduleUpdate(currentPos,getThis(),((BlockFluidBaseAccessor)this).getTickRate());
            worldIn.setBlockState(downPos,downState.withProperty(LEVEL,0), Constants.BlockFlags.DEFAULT);
        }
    }
    /**
     * Q=1 坡度流动模式的可流动方向寻找算法
     */
    default Set<EnumFacing> getPossibleFlowDirections(World worldIn, BlockPos pos,int densityDir,int quantaPerBlock) {
        int difficulty = 1000;
        Set<EnumFacing> possibleDirections = EnumSet.noneOf(EnumFacing.class);

        for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
            BlockPos facingPos = pos.offset(enumfacing);
            IBlockState state = worldIn.getBlockState(facingPos);
            if (!getThis().canDisplace(worldIn,facingPos) || FluidUtil.isFluid(state)) {
                continue;
            }
            int slope;
            if (!canMoveDownTo(worldIn,facingPos.up(densityDir))) {
                slope = this.getSlopeDistance(worldIn, facingPos, 1, enumfacing.getOpposite(),densityDir,quantaPerBlock);
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
        return possibleDirections;
    }
    /**
     * Q=1 坡度流动模式的可流动方向寻找内层递归算法
     */
    default int getSlopeDistance(World worldIn, BlockPos pos, int distance, EnumFacing from,int densityDir,int quantaPerBlock) {
        int difficulty = 1000;

        for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
            if (enumfacing == from) continue;
            BlockPos facingPos = pos.offset(enumfacing);
            IBlockState state = worldIn.getBlockState(facingPos);

            if (!getThis().canDisplace(worldIn,facingPos) || FluidUtil.isFluid(state)) {
                continue;
            }
            if (canMoveDownTo(worldIn,facingPos.up(densityDir))) {
                return distance;
            }

            if (distance < quantaPerBlock / 2) {
                int newDistance = this.getSlopeDistance(worldIn, facingPos, distance + 1, enumfacing.getOpposite(),densityDir,quantaPerBlock);
                if (newDistance < difficulty) difficulty = newDistance;
            }
        }

        return difficulty;
    }
    /**
     * Q>1 坡度流动模式的可流动方向寻找算法
     * @param worldIn 所在世界
     * @param pos 位置
     * @param accessibleDirections 可流动的方向
     * @param thisQuanta 搜寻者的液体量
     * @return 一个流动方向的集合，意味着最佳的流动方向
     */
    default Set<EnumFacing> getPossibleFlowDirections(World worldIn, BlockPos pos, Set<EnumFacing> accessibleDirections, int densityDir, int quantaPerBlock, int thisQuanta) {
        double difficulty = 10000d;
        Set<EnumFacing> possibleDirections = EnumSet.noneOf(EnumFacing.class);

        for (EnumFacing enumfacing : accessibleDirections) {
            BlockPos facingPos = pos.offset(enumfacing);

            double slope = this.getSlopeDistance(worldIn, facingPos, 1,densityDir,quantaPerBlock,thisQuanta, enumfacing.getOpposite());

            if (slope < difficulty)
                possibleDirections.clear();
            if (slope <= difficulty) {
                possibleDirections.add(enumfacing);
                difficulty = slope;
            }
        }
        if(difficulty == 10000d) possibleDirections.clear();
        return possibleDirections;
    }
    /***
     * Q>1 坡度流动模式的可流动方向寻找内层递归算法
     * @param worldIn 所在世界
     * @param pos 位置
     * @param distance 当前距离原点的距离
     * @param thisQuanta 搜寻者的液体量
     * @param from 来源方向
     * @return 难易度，即坡度的余切值
     */
    default double getSlopeDistance(World worldIn, BlockPos pos, int distance, int densityDir, int quantaPerBlock, int thisQuanta , EnumFacing from) {
        double difficulty = 10000d;

        for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
            if (enumfacing == from) continue;
            BlockPos facingPos = pos.offset(enumfacing);
            IBlockState state = worldIn.getBlockState(facingPos);
            int quantaDiffer = getQuantaDiffer(state,quantaPerBlock,thisQuanta);
            boolean isFluid = FluidUtil.isFluid(state);
            boolean isAir = state.getMaterial() == Material.AIR;
            if (!this.canDisplaceEvenIsFluid(worldIn,facingPos) || (isFluid && quantaDiffer <1)) {
                continue;
            }
            if(isAir){
                if (canMoveDownTo(worldIn,facingPos.up(densityDir))) {
                    return FluidUtil.getFlowDifficulty(distance*quantaPerBlock,quantaPerBlock+thisQuanta);
                }else{
                    return FluidUtil.getFlowDifficulty(distance*quantaPerBlock,thisQuanta);
                }
            }else if(quantaDiffer >1){ //同样的流体
                return FluidUtil.getFlowDifficulty(distance*quantaPerBlock,thisQuanta-quantaDiffer);
            }else if(!isFluid){ //例如火把
                return FluidUtil.getFlowDifficulty(distance*quantaPerBlock,thisQuanta);
            }

            if (distance < (quantaPerBlock* SimulationConfig.slopeFindDistanceMultiplierForModFluidWhenQuantaAbove1.getValue())/2) {
                double slope = this.getSlopeDistance(worldIn, facingPos, distance + 1,densityDir,quantaPerBlock,thisQuanta, enumfacing.getOpposite());
                if (slope < difficulty) difficulty = slope;
            }
        }
        return difficulty;
    }
    /**
     * 获得对应方块状态的流体量与自身流体量的差值
     * @param state 对应方块状态
     * @param quantaPerBlock 自身每个方块的流体量
     * @param thisQuanta 自身流体量
     * @return 如果不是一个流体，则返回INT整形最大值。如果是一个流体，则返回自身流体量减去对方流体量的结果。
     */
    default int getQuantaDiffer(IBlockState state,int quantaPerBlock,int thisQuanta){
        if(!isSameLiquid(state)) return Integer.MIN_VALUE;
        int quanta = quantaPerBlock-state.getValue(LEVEL);
        if(quanta<0) quanta = 0;
        return thisQuanta - quanta;
    }
    default void flowIntoBlockDirectly(World world, BlockPos pos,IBlockState rawState, int meta) {
        if (meta < 0) return;
        FluidOperationUtil.triggerDestroyBlockEffectByFluid(world,pos,world.getBlockState(pos),getThis().getFluid());
        world.setBlockState(pos, rawState.withProperty(LEVEL, meta));
    }
    default boolean canMoveDownTo(World worldIn, BlockPos pos){
        IBlockState state = worldIn.getBlockState(pos);
        if(FluidUtil.isFluid(state)){
            if(FluidUtil.getFluid(state) != getThis().getFluid()){
                return getThis().canDisplace(worldIn,pos);
            }
            return !FluidUtil.isFullFluid(worldIn,pos,state);
        }
        return getThis().canDisplace(worldIn,pos);
    }
    default boolean canDisplaceEvenIsFluid(World world, BlockPos pos){
        IBlockState state = world.getBlockState(pos);
        if(isSameLiquid(state)) return true;
        return getThis().canDisplace(world,pos);
    }
    default boolean isSameLiquid(IBlockState state){
        Fluid thisFluid = getThis().getFluid();
        Fluid fluid = FluidUtil.getFluid(state);
        return thisFluid == fluid;
    }
}
