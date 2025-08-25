package top.qiguaiaaaa.fluidgeography.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.AtmosphereSystemManager;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.property.TemperatureProperty;
import top.qiguaiaaaa.fluidgeography.api.util.AtmosphereUtil;
import top.qiguaiaaaa.fluidgeography.api.util.FluidUtil;

public final class WaterUtil {
    public static boolean isWater(World world, BlockPos pos) {
        return world.getBlockState(pos).getMaterial() == Material.WATER;
    }
    /**
     * 获取水蒸发的概率
     * @param atmosphere 大气
     * @return 一个介于0~1的值，表示概率
     */
    public static double getWaterEvaporatePossibility(Atmosphere atmosphere, BlockPos pos) {
        final double 交换系数 = 1.0e-7;
        final int 时间步长 = 216;
        final int 单层水质量 = FluidUtil.ONE_IN_EIGHT_OF_BUCKET_VOLUME;
        double temp = atmosphere.getTemperature(pos,true);
        if(temp>= TemperatureProperty.BOILED_POINT) return 1;
        double 水汽压 = atmosphere.getWaterPressure(pos);
        double 饱和水汽压 = AtmosphereUtil.计算饱和水汽压(atmosphere.getTemperature(pos));
        double 水汽压差 = Math.max(饱和水汽压 - 水汽压, 0);
        double 通量_kg每平米每秒 = 交换系数 * 水汽压差;
        double 期望质量 = 通量_kg每平米每秒 * 时间步长;
        return  1.0 - Math.exp(-期望质量 / 单层水质量);
//        double possibility = 1.0d;
//        possibility *= Math.pow(1.07,temp - TemperatureProperty.ICE_POINT)/Math.pow(1.07,100);
//        possibility *= Math.sqrt(1-Math.pow(atmosphere.getWaterPressure(pos)/1024.0,2)/1000.0);
//        possibility *= 0.5;
//        return possibility;
    }

    /**
     * 获取降雨概率
     * @param atmosphere 大气
     * @param pos 降雨位置
     * @return 一个介于0~1的值，表示概率
     */
    public static double getRainPossibility(Atmosphere atmosphere, BlockPos pos) {
        float temp = atmosphere.getTemperature(pos);
        if(temp>= TemperatureProperty.BOILED_POINT) return 0;
        if(temp<= TemperatureProperty.ICE_POINT-100) return 1;
        double strong = atmosphere.getRainStrong();
        return strong/(strong+16384);
    }

    /**
     * 获取冻结概率
     * @param atmosphere 大气
     * @param pos 冻结位置
     * @return 一个介于0~1的值，表示概率
     */
    public static double getFreezePossibility(Atmosphere atmosphere, BlockPos pos) {
        float temp = atmosphere.getTemperature(pos,true);
        if(temp>= TemperatureProperty.ICE_POINT) return 0;
        if(temp< TemperatureProperty.ICE_POINT-100) return 1;
        float diff = TemperatureProperty.ICE_POINT-temp;
        return (diff/100)*0.94f+0.06f;
    }

    /**
     * 是否能够在指定位置放置雪，注意不是降雪，该方法只会检查亮度条件
     * @param world 世界
     * @param pos 位置
     * @return 如果能，则返回true
     */
    public static boolean canPlaceSnow(World world, BlockPos pos) {
        if (pos.getY() >= 0 && pos.getY() < 256 && world.getLightFor(EnumSkyBlock.BLOCK, pos) < 10) {
            return Blocks.SNOW_LAYER.canPlaceBlockAt(world, pos);
        }
        return false;
    }

    /**
     * 是否能够在指定位置降雪
     * @param world 世界
     * @param pos 位置
     * @param checkLight 是否检查亮度
     * @return 如果能，则返回true
     */
    public static boolean canSnowAt(World world,BlockPos pos, boolean checkLight) {
        Atmosphere atmosphere = AtmosphereSystemManager.getAtmosphere(world, pos);
        if(atmosphere == null) return world.canSnowAtBody(pos,checkLight);
        if(atmosphere.get水量()< FluidUtil.ONE_IN_EIGHT_OF_BUCKET_VOLUME) return false;
        if (atmosphere.getTemperature(pos) >= TemperatureProperty.ICE_POINT) {
            return false;
        } else if (!checkLight) {
            return true;
        } else {
            if (pos.getY() >= 0 && pos.getY() < 256 && world.getLightFor(EnumSkyBlock.BLOCK, pos) < 10) {
                IBlockState state = world.getBlockState(pos);

                return state.getBlock().isAir(state, world, pos) && Blocks.SNOW_LAYER.canPlaceBlockAt(world, pos);
            }

            return false;
        }
    }

    /**
     * 指定位置是否能够凝结水成冰
     * @param world 世界
     * @param pos 位置
     * @param neighborWaterCheck 是否检查周边水方块
     * @return 如果能，则返回true
     */
    public static boolean canWaterFreeze(World world,BlockPos pos, boolean neighborWaterCheck) {
        Atmosphere atmosphere = AtmosphereSystemManager.getAtmosphere(world, pos);
        if(atmosphere == null) return world.canBlockFreezeBody(pos,neighborWaterCheck);

        if (atmosphere.getTemperature(pos,true) < TemperatureProperty.ICE_POINT) {
            if (pos.getY() < 0 || pos.getY() >= 256)
                return false;
            IBlockState state = world.getBlockState(pos);
            Block block = state.getBlock();

            if ((block == Blocks.WATER || block == Blocks.FLOWING_WATER) && state.getValue(BlockLiquid.LEVEL) == 0) {
                if (!neighborWaterCheck) {
                    return true;
                }

                boolean isWaterSurrounded = isWater(world, pos.west()) && isWater(world, pos.east()) && isWater(world, pos.north()) && isWater(world, pos.south());

                return !isWaterSurrounded;
            }

        }
        return false;
    }
}
