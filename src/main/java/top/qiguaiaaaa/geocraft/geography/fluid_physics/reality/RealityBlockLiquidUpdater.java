/*
 * Copyright 2025 QiguaiAAAA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * 版权所有 2025 QiguaiAAAA
 * 根据Apache许可证第2.0版（“本许可证”）许可；
 * 除非符合本许可证的规定，否则你不得使用此文件。
 * 你可以在此获取本许可证的副本：
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * 除非所适用法律要求或经书面同意，在本许可证下分发的软件是“按原样”分发的，
 * 没有任何形式的担保或条件，不论明示或默示。
 * 请查阅本许可证了解有关本许可证下许可和限制的具体要求。
 * 中文译文来自开放原子开源基金会，非官方译文，如有疑议请以英文原文为准
 */

package top.qiguaiaaaa.geocraft.geography.fluid_physics.reality;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.IFluidBlock;
import top.qiguaiaaaa.geocraft.api.block.IPermeableBlock;
import top.qiguaiaaaa.geocraft.api.util.FluidUtil;
import top.qiguaiaaaa.geocraft.api.util.annotation.ThreadOnly;
import top.qiguaiaaaa.geocraft.api.util.annotation.ThreadType;
import top.qiguaiaaaa.geocraft.api.util.math.FlowChoice;
import top.qiguaiaaaa.geocraft.configs.FluidPhysicsConfig;
import top.qiguaiaaaa.geocraft.geography.fluid_physics.vanilla.BlockLiquidUpdater;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static net.minecraft.block.BlockLiquid.LEVEL;

/**
 * 参考{@link BlockDynamicLiquid}和{@link BlockLiquid}实现
 * @author QiguaiAAAA
 */
public class RealityBlockLiquidUpdater extends BlockLiquidUpdater {
    @ThreadOnly(ThreadType.MINECRAFT_SERVER)
    private static final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

    public RealityBlockLiquidUpdater(@Nonnull BlockDynamicLiquid liquid, @Nonnull Material material,@Nonnull Fluid fluid) {
        super(liquid, material,fluid);
    }

    /**
     * 检查方块四周可流动的选择
     * @param worldIn 所在世界
     * @param pos 方块位置
     * @param liquidQuanta 液体量
     * @param averageModeFlowDirections 平均流动模式下的选择列表
     * @param slopeModeFlowDirections Q>1 坡度流动模式下的选择集合
     */
    @ThreadOnly(ThreadType.MINECRAFT_SERVER)
    public void checkNeighborsToFindFlowChoices(World worldIn, BlockPos pos,IBlockState state , int liquidQuanta, List<FlowChoice> averageModeFlowDirections,@Nullable Set<EnumFacing> slopeModeFlowDirections){
        for(EnumFacing facing:EnumFacing.Plane.HORIZONTAL){
            IBlockState facingState = worldIn.getBlockState(mutablePos.setPos(pos.getX()+facing.getXOffset(),pos.getY(),pos.getZ()+facing.getZOffset()));
            if(slopeModeFlowDirections==null?
                    !canFlowIntoRegardedPermeable(worldIn,mutablePos,facingState,state,facing.getOpposite()):
                    !canFlowInto2(worldIn,mutablePos,facingState,state,facing.getOpposite()))
                continue;

            Block block = facingState.getBlock();
            IPermeableBlock permeableBlock = (block instanceof IPermeableBlock)?(IPermeableBlock) block:null;

            int facingHeight,facingQuanta;
            if(permeableBlock != null){
                facingHeight = permeableBlock.getHeight(facingState,fluid);
                facingQuanta = permeableBlock.getQuanta(facingState,fluid);
            }else {
                int facingMeta = getDepth(facingState);
                if(facingMeta <0 || facingMeta>7) facingMeta = 8;
                facingQuanta = 8-facingMeta;
                facingHeight = facingQuanta*IPermeableBlockLiquid.HEIGHT_PER_QUANTA;
            }

            if(facingHeight<(liquidQuanta-1)*IPermeableBlockLiquid.HEIGHT_PER_QUANTA){
                averageModeFlowDirections.add(permeableBlock == null?
                        new FlowChoice(facingQuanta,facing,IPermeableBlockLiquid.HEIGHT_PER_QUANTA):
                        new FlowChoice(facing,permeableBlock,facingState,fluid));
            }

            if(!canFlowInto2RegardlessPermeable(facingState)) continue;
            if(slopeModeFlowDirections != null && facingHeight<liquidQuanta*IPermeableBlockLiquid.HEIGHT_PER_QUANTA) slopeModeFlowDirections.add(facing);
        }
    }

    public boolean canFlowIntoRegardedPermeable(@Nonnull World world,@Nonnull BlockPos pos,@Nonnull IBlockState state,@Nonnull IBlockState fromState,@Nonnull EnumFacing from){
        if(canFlowInto(state)) return true;
        if(state.getBlock() instanceof IPermeableBlock){
            IPermeableBlock block = (IPermeableBlock) state.getBlock();
            return block.canFill(world, pos, state, fluid,from,fromState);
        }
        return false;
    }

    /**
     * Q>1 坡度流动模式下检查是否能够流入指定方块
     * @param state 检测位置方块状态
     * @return 如果可以，则返回true
     */
    public boolean canFlowInto2(@Nonnull World world,@Nonnull BlockPos pos,@Nonnull IBlockState state,@Nonnull IBlockState fromState,@Nonnull EnumFacing from){
        if(canFlowIntoRegardedPermeable(world,pos,state,fromState,from)) return true;
        return isSameLiquid(state);
    }

    /**
     * Q>1 坡度流动模式下检查是否能够流入指定方块
     * @param state 检测位置方块状态
     * @return 如果可以，则返回true
     */
    public boolean canFlowInto2RegardlessPermeable(@Nonnull IBlockState state){
        if(canFlowInto(state)) return true;
        return isSameLiquid(state);
    }

    /**
     * 是否是相同的原版液体
     * @param state 需要检测的方块状态
     */
    public boolean isSameLiquid(@Nonnull IBlockState state){
        Block block = state.getBlock();
        if(block instanceof IFluidBlock) return false;
        return state.getMaterial() == this.material;
    }

    public int getSlopeFindDistance(@Nonnull World worldIn) {
        return material == Material.LAVA && !worldIn.provider.doesWaterVaporize() ? 2 : 4;
    }

    public boolean canFlowIntoWhenSnowLayer(@Nonnull IBlockState state,int quanta){
        if(state.getBlock() != Blocks.SNOW_LAYER) return true;
        return state.getValue(BlockSnow.LAYERS)<quanta-1;
    }

    /**
     * 液体是否可以往下流动，会检查透水方块
     * @param world 世界
     * @param downPos 下方位置
     * @param curQuanta 当前量
     * @param fromState 当前状态
     * @param downState 下方方块状态
     * @return 如果可以往下流动，则返回true
     */
    public boolean canMoveDownTo(@Nonnull World world,@Nonnull BlockPos downPos,@Nonnull IBlockState downState,int curQuanta,IBlockState fromState){
        if(canMoveDownTo(downState)) return true;
        if(downState.getBlock() instanceof IPermeableBlock){
            if(!((IPermeableBlock)downState.getBlock()).canFill(world,downPos,downState, fluid,EnumFacing.UP,fromState)) return false;
            return ((IPermeableBlock)downState.getBlock()).addQuanta(world,downPos,downState,fluid,curQuanta,false)>0;
        }
        return false;
    }

    /**
     * 液体是否可以往下流动
     * @param downState 下方方块状态
     * @return 如果可以往下流动，则返回true
     */
    public boolean canMoveDownTo(IBlockState downState){
        if(downState.getBlock() instanceof IFluidBlock) return false;
        Material material = downState.getMaterial();
        if(material.isLiquid()){
            if(material == Material.WATER && this.material == Material.LAVA) return true;
            if(material != this.material) return false;
            return downState.getValue(LEVEL) != 0;
        }
        if(downState.getBlock() == Blocks.SNOW_LAYER){
            return downState.getValue(BlockSnow.LAYERS) < 8;
        }
        return !BlockLiquidUpdater.isBlocked(downState);
    }

    /**
     * Q=1 坡度流动模式的可流动方向寻找算法
     */
    @Nonnull
    public Set<EnumFacing> getPossibleFlowDirections(@Nonnull World worldIn,@Nonnull BlockPos pos){
        int difficulty = 1000;
        Set<EnumFacing> possibleDirections = EnumSet.noneOf(EnumFacing.class);

        for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
            BlockPos facingPos = pos.offset(enumfacing);

            IBlockState state = worldIn.getBlockState(facingPos);

            if (BlockLiquidUpdater.isBlocked(state) || state.getBlock() == Blocks.SNOW_LAYER || FluidUtil.isFluid(state)) {
                continue;
            }

            int slope;
            IBlockState stateBelow = worldIn.getBlockState(facingPos.down());
            if (!canMoveDownTo(stateBelow)) {
                slope = getSlopeDistance(worldIn, facingPos, 1, enumfacing.getOpposite());
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

    public int getSlopeDistance(@Nonnull World worldIn,@Nonnull BlockPos pos, int distance,@Nonnull EnumFacing from) {
        int difficulty = 1000;

        for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
            if (enumfacing == from) continue;
            BlockPos facingPos = pos.offset(enumfacing);
            IBlockState state = worldIn.getBlockState(facingPos);

            if (BlockLiquidUpdater.isBlocked(state) || state.getBlock() == Blocks.SNOW_LAYER|| FluidUtil.isFluid(state)) {
                continue;
            }
            IBlockState stateBelow = worldIn.getBlockState(facingPos.down());
            if (canMoveDownTo(stateBelow)) {
                return distance;
            }

            if (distance < getSlopeFindDistance(worldIn)) {
                int newDistance = getSlopeDistance(worldIn, facingPos, distance + 1, enumfacing.getOpposite());
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
    public Set<EnumFacing> getPossibleFlowDirections(World worldIn, BlockPos pos,Set<EnumFacing> accessibleDirections,int thisQuanta) {
        double difficulty = 10000d;
        Set<EnumFacing> possibleDirections = EnumSet.noneOf(EnumFacing.class);

        for (EnumFacing enumfacing : accessibleDirections) {
            BlockPos facingPos = pos.offset(enumfacing);

            double slope = getSlopeDistance(worldIn, facingPos, 1,thisQuanta, enumfacing.getOpposite());

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
    public double getSlopeDistance(World worldIn, BlockPos pos, int distance,int thisQuanta ,EnumFacing from) {
        double difficulty = 10000d;

        for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
            if (enumfacing == from) continue;
            BlockPos facingPos = pos.offset(enumfacing);
            IBlockState state = worldIn.getBlockState(facingPos);
            int quantaDiffer = getQuantaDiffer(state,thisQuanta);
            boolean isFluid = FluidUtil.isFluid(state);
            boolean isAir = state.getMaterial() == Material.AIR;
            if (BlockLiquidUpdater.isBlocked(state) || !canFlowIntoWhenSnowLayer(state,thisQuanta) || (isFluid && quantaDiffer <1)) {
                continue;
            }
            if(isAir){
                IBlockState stateBelow = worldIn.getBlockState(facingPos.down());
                if (canMoveDownTo(stateBelow)) {
                    return FluidUtil.getFlowDifficulty(distance*8,8+thisQuanta);
                }else{
                    return FluidUtil.getFlowDifficulty(distance*8,thisQuanta);
                }
            }else if(quantaDiffer >1){ //同样的流体
                return FluidUtil.getFlowDifficulty(distance*8,thisQuanta-quantaDiffer);
            }else if(!isFluid){ //例如火把
                return FluidUtil.getFlowDifficulty(distance*8,thisQuanta);
            }

            if (distance < getSlopeFindDistance2(worldIn)) {
                double slope = getSlopeDistance(worldIn, facingPos, distance + 1,thisQuanta, enumfacing.getOpposite());
                if (slope < difficulty) difficulty = slope;
            }
        }

        return difficulty;
    }

    /**
     * Q>1 坡度流动模式的搜寻距离
     * @param worldIn 所在世界
     */
    public int getSlopeFindDistance2(World worldIn) {
        if(!FluidPhysicsConfig.slopeModeForVanillaWhenOnLiquidsAndQuantaAbove1.getValue()) return 0;
        int ans = FluidPhysicsConfig.slopeFindDistanceForWaterWhenQuantaAbove1.getValue();
        if(this.material == Material.LAVA && !worldIn.provider.doesWaterVaporize()){
            ans = FluidPhysicsConfig.slopeFindDistanceForLavaWhenQuantaAbove1.getValue();
        }
        return ans;
    }

    /**
     * 获得对应方块状态的流体量与自身流体量的差值
     * @param state 对应方块状态
     * @param thisQuanta 自身流体量
     * @return 如果不是一个流体，则返回INT整形最大值。如果是一个流体，则返回自身流体量减去对方流体量的结果。
     */
    public int getQuantaDiffer(IBlockState state,int thisQuanta){
        if(!isSameLiquid(state)) return Integer.MIN_VALUE;
        int quanta = 8- getDepth(state);
        if(quanta<0) quanta = 0;
        return thisQuanta - quanta;
    }
}
