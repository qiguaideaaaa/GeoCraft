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
import net.minecraftforge.fluids.IFluidBlock;
import top.qiguaiaaaa.geocraft.api.util.FluidUtil;
import top.qiguaiaaaa.geocraft.api.util.math.FlowChoice;
import top.qiguaiaaaa.geocraft.configs.FluidPhysicsConfig;
import top.qiguaiaaaa.geocraft.util.fluid.BlockLiquidUtil;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static net.minecraft.block.BlockLiquid.LEVEL;

/**
 * 参考{@link BlockDynamicLiquid}和{@link BlockLiquid}实现
 * @author QiguaiAAAA
 */
public final class RealityBlockLiquidUtil {
    private static final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

    /**
     * 检查方块四周可流动的选择
     * @param worldIn 所在世界
     * @param pos 方块位置
     * @param liquidQuanta 液体量
     * @param averageModeFlowDirections 平均流动模式下的选择列表
     * @param slopeModeFlowDirections Q>1 坡度流动模式下的选择集合
     */
    public static void checkNeighborsToFindFlowChoices(World worldIn, BlockPos pos, BlockLiquid liquid, int liquidQuanta, List<FlowChoice> averageModeFlowDirections,@Nullable Set<EnumFacing> slopeModeFlowDirections){
        for(EnumFacing facing:EnumFacing.Plane.HORIZONTAL){
            IBlockState facingState = worldIn.getBlockState(mutablePos.setPos(pos.getX()+facing.getXOffset(),pos.getY(),pos.getZ()+facing.getZOffset()));
            if(slopeModeFlowDirections==null?
                    !BlockLiquidUtil.canFlowInto(facingState,liquid): !canFlowInto2(facingState,liquid))
                continue;
            if(!canFlowIntoWhenSnowLayer(facingState,liquidQuanta,liquid)) continue;
            int facingMeta = BlockLiquidUtil.getDepth(facingState,liquid);
            if(facingMeta <0 || facingMeta>7) facingMeta = 8;
            int facingQuanta = 8-facingMeta;
            if(facingQuanta<liquidQuanta-1){
                averageModeFlowDirections.add(new FlowChoice(facingQuanta,facing));
            }
            if(slopeModeFlowDirections != null && facingQuanta<liquidQuanta) slopeModeFlowDirections.add(facing);
        }
    }

    /**
     * Q>1 坡度流动模式下检查是否能够流入指定方块
     * @param state 检测位置方块状态
     * @return 如果可以，则返回true
     */
    public static boolean canFlowInto2(IBlockState state,BlockLiquid liquid){
        if(BlockLiquidUtil.canFlowInto(state,liquid)) return true;
        return isSameLiquid(state,liquid.getDefaultState().getMaterial());
    }

    /**
     * 是否是相同的原版液体
     * @param state 需要检测的方块状态
     * @param material 液体
     */
    public static boolean isSameLiquid(IBlockState state,Material material){
        Block block = state.getBlock();
        if(block instanceof IFluidBlock) return false;
        return state.getMaterial() == material;
    }

    public static int getSlopeFindDistance(World worldIn, BlockLiquid block) {
        return block.getDefaultState().getMaterial() == Material.LAVA && !worldIn.provider.doesWaterVaporize() ? 2 : 4;
    }

    public static boolean canFlowIntoWhenSnowLayer(IBlockState state, int thisQuanta, BlockLiquid liquid){
        if(liquid.getDefaultState().getMaterial() != Material.WATER) return true;
        if(state.getBlock() != Blocks.SNOW_LAYER) return true;
        int layer = state.getValue(BlockSnow.LAYERS);
        return layer < thisQuanta-2;
    }

    /**
     * 液体是否可以往下流动
     * @param state 下方方块状态
     * @return 如果可以往下流动，则返回true
     */
    public static boolean canMoveDownTo(Material thisMaterial,IBlockState state){
        if(state.getBlock() instanceof IFluidBlock) return false;
        Material material = state.getMaterial();
        if(material.isLiquid()){
            if(material == Material.WATER && thisMaterial == Material.LAVA) return true;
            if(material != thisMaterial) return false;
            return state.getValue(LEVEL) != 0;
        }
        if(state.getBlock() == Blocks.SNOW_LAYER){
            return state.getValue(BlockSnow.LAYERS) < 8;
        }
        return !BlockLiquidUtil.isBlocked(state);
    }

    /**
     * Q=1 坡度流动模式的可流动方向寻找算法
     */
    public static Set<EnumFacing> getPossibleFlowDirections(World worldIn, BlockPos pos, BlockLiquid liquid){
        int difficulty = 1000;
        Set<EnumFacing> possibleDirections = EnumSet.noneOf(EnumFacing.class);

        for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
            BlockPos facingPos = pos.offset(enumfacing);
            IBlockState state = worldIn.getBlockState(facingPos);

            if (BlockLiquidUtil.isBlocked(state) || !canFlowIntoWhenSnowLayer(state,1,liquid) || FluidUtil.isFluid(state)) {
                continue;
            }
            int slope;
            IBlockState stateBelow = worldIn.getBlockState(facingPos.down());
            if (!canMoveDownTo(liquid.getDefaultState().getMaterial(),stateBelow)) {
                slope = getSlopeDistance(worldIn, facingPos, 1, enumfacing.getOpposite(),liquid);
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

    public static int getSlopeDistance(World worldIn, BlockPos pos, int distance, EnumFacing from, BlockLiquid liquid) {
        int difficulty = 1000;

        for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
            if (enumfacing == from) continue;
            BlockPos facingPos = pos.offset(enumfacing);
            IBlockState state = worldIn.getBlockState(facingPos);

            if (BlockLiquidUtil.isBlocked(state) || !canFlowIntoWhenSnowLayer(state,1,liquid) || FluidUtil.isFluid(state)) {
                continue;
            }
            IBlockState stateBelow = worldIn.getBlockState(facingPos.down());
            if (canMoveDownTo(liquid.getDefaultState().getMaterial(),stateBelow)) {
                return distance;
            }

            if (distance < getSlopeFindDistance(worldIn,liquid)) {
                int newDistance = getSlopeDistance(worldIn, facingPos, distance + 1, enumfacing.getOpposite(),liquid);
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
     * @param liquid 搜寻者的流体方块
     * @return 一个流动方向的集合，意味着最佳的流动方向
     */
    public static Set<EnumFacing> getPossibleFlowDirections(World worldIn, BlockPos pos,Set<EnumFacing> accessibleDirections,int thisQuanta, BlockLiquid liquid) {
        double difficulty = 10000d;
        Set<EnumFacing> possibleDirections = EnumSet.noneOf(EnumFacing.class);

        for (EnumFacing enumfacing : accessibleDirections) {
            BlockPos facingPos = pos.offset(enumfacing);

            double slope = getSlopeDistance(worldIn, facingPos, 1,thisQuanta, enumfacing.getOpposite(),liquid);

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
     * @param liquid 搜寻者的流体方块
     * @return 难易度，即坡度的余切值
     */
    public static double getSlopeDistance(World worldIn, BlockPos pos, int distance,int thisQuanta ,EnumFacing from,BlockLiquid liquid) {
        double difficulty = 10000d;

        for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
            if (enumfacing == from) continue;
            BlockPos facingPos = pos.offset(enumfacing);
            IBlockState state = worldIn.getBlockState(facingPos);
            int quantaDiffer = getQuantaDiffer(state,thisQuanta,liquid);
            boolean isFluid = FluidUtil.isFluid(state);
            boolean isAir = state.getMaterial() == Material.AIR;
            if (BlockLiquidUtil.isBlocked(state) || !canFlowIntoWhenSnowLayer(state,thisQuanta,liquid) || (isFluid && quantaDiffer <1)) {
                continue;
            }
            if(isAir){
                IBlockState stateBelow = worldIn.getBlockState(facingPos.down());
                if (canMoveDownTo(liquid.getDefaultState().getMaterial(),stateBelow)) {
                    return FluidUtil.getFlowDifficulty(distance*8,8+thisQuanta);
                }else{
                    return FluidUtil.getFlowDifficulty(distance*8,thisQuanta);
                }
            }else if(quantaDiffer >1){ //同样的流体
                return FluidUtil.getFlowDifficulty(distance*8,thisQuanta-quantaDiffer);
            }else if(!isFluid){ //例如火把
                return FluidUtil.getFlowDifficulty(distance*8,thisQuanta);
            }

            if (distance < getSlopeFindDistance2(worldIn,liquid)) {
                double slope = getSlopeDistance(worldIn, facingPos, distance + 1,thisQuanta, enumfacing.getOpposite(),liquid);
                if (slope < difficulty) difficulty = slope;
            }
        }

        return difficulty;
    }

    /**
     * Q>1 坡度流动模式的搜寻距离
     * @param worldIn 所在世界
     * @param liquid 流体方块
     */
    public static int getSlopeFindDistance2(World worldIn,BlockLiquid liquid) {
        if(!FluidPhysicsConfig.slopeModeForVanillaWhenOnLiquidsAndQuantaAbove1.getValue()) return 0;
        int ans = FluidPhysicsConfig.slopeFindDistanceForWaterWhenQuantaAbove1.getValue();
        if(liquid.getDefaultState().getMaterial() == Material.LAVA && !worldIn.provider.doesWaterVaporize()){
            ans = FluidPhysicsConfig.slopeFindDistanceForLavaWhenQuantaAbove1.getValue();
        }
        return ans;
    }

    /**
     * 获得对应方块状态的流体量与自身流体量的差值
     * @param state 对应方块状态
     * @param thisQuanta 自身流体量
     * @param liquid 自身流体类型
     * @return 如果不是一个流体，则返回INT整形最大值。如果是一个流体，则返回自身流体量减去对方流体量的结果。
     */
    public static int getQuantaDiffer(IBlockState state,int thisQuanta,BlockLiquid liquid){
        if(!isSameLiquid(state,liquid.getDefaultState().getMaterial())) return Integer.MIN_VALUE;
        int quanta = 8-BlockLiquidUtil.getDepth(state,liquid);
        if(quanta<0) quanta = 0;
        return thisQuanta - quanta;
    }
}
