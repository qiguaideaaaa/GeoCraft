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

package top.qiguaiaaaa.geocraft.block;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import top.qiguaiaaaa.geocraft.api.block.IPermeableBlock;
import top.qiguaiaaaa.geocraft.api.configs.value.geo.SimulationMode;
import top.qiguaiaaaa.geocraft.api.util.FluidUtil;
import top.qiguaiaaaa.geocraft.api.util.math.FlowChoice;
import top.qiguaiaaaa.geocraft.configs.SimulationConfig;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

import static top.qiguaiaaaa.geocraft.api.block.BlockProperties.HUMIDITY;

public interface IBlockDirt extends IPermeableBlock {
    /**
     * 土壤将自身水掉下去的能力
     * @return 湿度变化
     */
    default int dropWaterDown(World worldIn, BlockPos pos, IBlockState state){
        if(SimulationConfig.SIMULATION_MODE.getValue() == SimulationMode.MORE_REALITY){
            BlockPos down = pos.down();
            IBlockState downState = worldIn.getBlockState(down);
            if(downState.getMaterial() == Material.AIR){
                worldIn.setBlockState(down, Blocks.FLOWING_WATER.getDefaultState().withProperty(BlockLiquid.LEVEL,7), Constants.BlockFlags.DEFAULT);
                return -1;
            }else if(FluidUtil.getFluid(downState) == FluidRegistry.WATER){
                int meta = downState.getValue(BlockLiquid.LEVEL);
                if(meta >0 && meta<=7){
                    worldIn.setBlockState(down,downState.withProperty(BlockLiquid.LEVEL,meta-1),Constants.BlockFlags.DEFAULT);
                    return -1;
                }
            }
        }
        return 0;
    }

    /**
     * 土壤水向四周流动的能力
     * @param humidity 当前湿度
     */
    default void flowWaterHorizontally(World worldIn,BlockPos pos,IBlockState state,int humidity){
        if (!worldIn.isAreaLoaded(pos, 3)) return;
        int newHumidity = humidity;
        //可流动方向检查
        final ArrayList<FlowChoice> averageModeFlowDirections = new ArrayList<>();//平均流动模式可用方向
        for(EnumFacing facing:EnumFacing.Plane.HORIZONTAL){
            BlockPos facingPos = pos.offset(facing);
            IBlockState facingState = worldIn.getBlockState(facingPos);
            if(!canFlowInto(worldIn,facingPos,facingState)) continue;
            if(facingState.getMaterial() == Material.AIR){
                averageModeFlowDirections.add(new FlowChoice(0,facing,2,null));
                continue;
            }
            IPermeableBlock flowIntoable = (IPermeableBlock)facingState.getBlock();
            int facingHeight = flowIntoable.getHeight(worldIn,facingPos,facingState);
            if(facingHeight<humidity*4-1){
                averageModeFlowDirections.add(new FlowChoice(flowIntoable.getQuanta(worldIn,facingPos,facingState),facing,flowIntoable.getHeightPerQuanta(),flowIntoable));
            }
        }

        if(!averageModeFlowDirections.isEmpty()){ //平均流动模式
            averageModeFlowDirections.sort(Comparator.comparingInt(FlowChoice::getHeight));
            while(averageModeFlowDirections.get(0).getHeight()<newHumidity*4-1){ //向四周分配流量
                averageModeFlowDirections.get(0).addQuanta(1);
                newHumidity--;
                if(newHumidity <=getMaxStableHumidity(state)) break;
                averageModeFlowDirections.sort(Comparator.comparingInt(FlowChoice::getHeight));
            }
            if(humidity == newHumidity) return;
            worldIn.setBlockState(pos,state.withProperty(HUMIDITY,newHumidity),0);
            for(FlowChoice choice:averageModeFlowDirections){ //向四周流动
                if(choice.getQuanta() == 0) continue;
                if(choice.direction == null) continue;
                BlockPos facingPos = pos.offset(choice.direction);
                if(choice.block == null){
                    worldIn.setBlockState(facingPos,Blocks.FLOWING_WATER.getDefaultState().withProperty(BlockLiquid.LEVEL,8-choice.getQuanta()));
                    continue;
                }
                IBlockState facingState = worldIn.getBlockState(facingPos);
                choice.block.setQuanta(worldIn,facingPos,facingState,choice.getQuanta());
            }
        }
    }

    /**
     * 土壤吸收上层水的能力
     * @return 湿度变化
     */
    default int drainUpWater(World worldIn, BlockPos pos, IBlockState state){
        BlockPos upPos = pos.up();
        IBlockState upState = worldIn.getBlockState(upPos);
        if(upState.getBlock() instanceof IPermeableBlock){
            IPermeableBlock block = (IPermeableBlock) upState.getBlock();
            if(block instanceof IBlockDirt){
                IBlockDirt dirt = (IBlockDirt) block;
                if(state.getValue(HUMIDITY)<=dirt.getMaxStableHumidity(upState)) return 0;
            }
            int upQuanta = block.getQuanta(worldIn,upPos,upState);
            if(upQuanta > 0){
                block.addQuanta(worldIn,upPos,upState,-1);
                return 1;
            }
        }
        return 0;
    }

    default void onRandomTick(World worldIn, BlockPos pos, IBlockState state, Random random){
        if(worldIn.isRemote) return;
        int humidity = state.getValue(HUMIDITY);
        int newHumidity = humidity;
        int rnd = random.nextInt(3);
        if(rnd == 0){ //吸收上面的水
            if(humidity < 4) {
                newHumidity += drainUpWater(worldIn,pos,state);
            }
        }else if(rnd == 1){ //向下掉水
            if(humidity >getMaxStableHumidity(state)){
                newHumidity += dropWaterDown(worldIn, pos, state);
            }
        }else if(humidity>getMaxStableHumidity(state)) { //水平平衡
            flowWaterHorizontally(worldIn,pos,state,humidity);
            return;
        }
        if(humidity == newHumidity) return;
        worldIn.setBlockState(pos,state.withProperty(HUMIDITY,newHumidity),0);
    }

    /**
     * 土壤在破坏时掉水的能力
     */
    default void dropWaterWhenBroken(World world, BlockPos pos, IBlockState state){
        int humidity = state.getValue(HUMIDITY);
        if(humidity == 0) return;
        if(SimulationConfig.SIMULATION_MODE.getValue() != SimulationMode.MORE_REALITY) return;
        world.setBlockState(pos, Blocks.FLOWING_WATER.getDefaultState().withProperty(BlockLiquid.LEVEL,8-humidity),Constants.BlockFlags.DEFAULT);
    }

    /**
     * 检查土壤水是否能够流进指定方块
     * @param world 世界
     * @param pos 目标位置
     * @param state 目标方块状态
     * @return 能，则true，否，则反之
     */
    default boolean canFlowInto(World world,BlockPos pos,IBlockState state){
        return (state.getBlock() instanceof IPermeableBlock && ((IPermeableBlock)state.getBlock()).getFluid(world,pos,state) == FluidRegistry.WATER) || state.getMaterial() == Material.AIR;
    }

    int getMaxStableHumidity(IBlockState state);

    @Nonnull
    @Override
    default Fluid getFluid(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state){
        return FluidRegistry.WATER;
    }

    @Override
    default int getQuanta(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state){
        return state.getValue(HUMIDITY);
    }

    @Override
    default int getHeight(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state){
        return state.getValue(HUMIDITY)*4;
    }

    @Override
    default int getHeightPerQuanta(){
        return 4;
    }

    @Override
    default void addQuanta(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, int quanta){
        if(isFull(world,pos,state) && quanta>0) throw new IllegalArgumentException();
        world.setBlockState(pos,state.withProperty(HUMIDITY,state.getValue(HUMIDITY)+quanta),0);
    }

    @Override
    default void setQuanta(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, int newQuanta){
        world.setBlockState(pos,state.withProperty(HUMIDITY,newQuanta),0);
    }

    @Nonnull
    @Override
    default IBlockState getQuantaState(@Nonnull IBlockState state, int newQuanta){
        return state.withProperty(HUMIDITY,newQuanta);
    }

    @Override
    default boolean isFull(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        return state.getValue(HUMIDITY) == 4;
    }
}
