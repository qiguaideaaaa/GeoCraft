package top.qiguaiaaaa.geocraft.block;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidRegistry;
import top.qiguaiaaaa.geocraft.api.configs.value.geo.SimulationMode;
import top.qiguaiaaaa.geocraft.api.util.FluidUtil;
import top.qiguaiaaaa.geocraft.api.util.math.FlowChoice;
import top.qiguaiaaaa.geocraft.configs.SimulationConfig;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

import static top.qiguaiaaaa.geocraft.api.block.BlockProperties.HUMIDITY;

public interface IBlockDirt {
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
        //下面来自物理水，删减版
        //可流动方向检查
        final ArrayList<FlowChoice> averageModeFlowDirections = new ArrayList<>();//平均流动模式可用方向
        for(EnumFacing facing:EnumFacing.Plane.HORIZONTAL){
            IBlockState facingState = worldIn.getBlockState(pos.offset(facing));
            if(!canFlowInto(worldIn,pos.offset(facing),facingState)) continue;
            int facingHumidity = facingState.getValue(HUMIDITY);
            if(facingHumidity<humidity-1){
                averageModeFlowDirections.add(new FlowChoice(facingHumidity,facing));
            }
        }

        if(!averageModeFlowDirections.isEmpty()){ //平均流动模式
            averageModeFlowDirections.sort(Comparator.comparingInt(FlowChoice::getQuanta));
            while(averageModeFlowDirections.get(0).getQuanta()<newHumidity-1){ //向四周分配流量
                averageModeFlowDirections.get(0).addQuanta(1);
                newHumidity--;
                if(newHumidity <=1) break;
                averageModeFlowDirections.sort(Comparator.comparingInt(FlowChoice::getQuanta));
            }
            if(humidity == newHumidity) return;
            worldIn.setBlockState(pos,state.withProperty(HUMIDITY,newHumidity),0);
            for(FlowChoice choice:averageModeFlowDirections){ //向四周流动
                if(choice.getQuanta() == 0) continue;
                if(choice.direction == null) continue;
                BlockPos facingPos = pos.offset(choice.direction);
                IBlockState facingState = worldIn.getBlockState(facingPos);
                worldIn.setBlockState(facingPos, facingState.withProperty(HUMIDITY, choice.getQuanta()), 0);
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
        if(FluidUtil.getFluid(upState) == FluidRegistry.WATER){
            if(SimulationConfig.SIMULATION_MODE.getValue() == SimulationMode.MORE_REALITY){
                int meta = upState.getValue(BlockLiquid.LEVEL);
                if(meta ==7){
                    worldIn.setBlockToAir(upPos);
                }else{
                    worldIn.setBlockState(upPos,upState.withProperty(BlockLiquid.LEVEL,meta+1), Constants.BlockFlags.DEFAULT);
                }
            }
            return 1;
        }else if(upState.getBlock() == Blocks.DIRT || upState.getBlock() == Blocks.GRASS){
            int upLevel = upState.getValue(HUMIDITY);
            if(upLevel > 0){
                worldIn.setBlockState(upPos,upState.withProperty(HUMIDITY,upLevel-1),0);
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
            if(humidity >0){
                newHumidity += dropWaterDown(worldIn, pos, state);
            }
        }else if(humidity>1) { //水平平衡
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
        return state.getBlock() == Blocks.DIRT;
    }
}
