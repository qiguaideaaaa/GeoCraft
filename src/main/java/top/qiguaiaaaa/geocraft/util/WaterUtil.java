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

package top.qiguaiaaaa.geocraft.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import top.qiguaiaaaa.geocraft.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.geocraft.api.atmosphere.AtmosphereSystemManager;
import top.qiguaiaaaa.geocraft.api.atmosphere.accessor.IAtmosphereAccessor;
import top.qiguaiaaaa.geocraft.api.property.TemperatureProperty;
import top.qiguaiaaaa.geocraft.api.util.FluidUtil;

public final class WaterUtil {
    public static boolean isWater(World world, BlockPos pos) {
        return world.getBlockState(pos).getMaterial() == Material.WATER;
    }
    /**
     * 获取水蒸发的概率
     * @param atmosphere 大气
     * @return 一个介于0~1的值，表示概率
     */
    public static double getWaterEvaporatePossibility(IAtmosphereAccessor accessor,Atmosphere atmosphere, BlockPos pos) {
        final int 单层水质量 = FluidUtil.ONE_IN_EIGHT_OF_BUCKET_VOLUME;
        double temp = accessor.getTemperature();
        if(temp>= TemperatureProperty.BOILED_POINT) return 1;
        double 期望质量 = getWaterEvaporateAmount(atmosphere,pos);
        return  1.0 - Math.exp(-期望质量 / 单层水质量);
    }

    /**
     * 获取水蒸发量
     * @param atmosphere 大气
     * @param pos 蒸发位置
     * @return 水蒸发量,单位kg
     */
    public static double getWaterEvaporateAmount(Atmosphere atmosphere,BlockPos pos){
        final double 交换系数 = 1.0e-6;
        final int 时间步长 = 216;
        double 水汽压 = atmosphere.getWaterPressure(pos);
        double 饱和水汽压 = 计算饱和水汽压(atmosphere.getAtmosphereTemperature(pos));
        double 水汽压差 = Math.max(饱和水汽压 - 水汽压, 0);
        double 通量_kg每平米每秒 = 交换系数 * 水汽压差;
        return 通量_kg每平米每秒 * 时间步长;
    }

    /**
     * 获取降雨概率
     * @param atmosphere 大气
     * @param pos 降雨位置
     * @return 一个介于0~1的值，表示概率
     */
    public static double getRainPossibility(Atmosphere atmosphere, BlockPos pos) {
        float temp = atmosphere.getAtmosphereTemperature(pos);
        if(temp <= TemperatureProperty.UNAVAILABLE) return 0;
        if(temp>= TemperatureProperty.BOILED_POINT) return 0;
        if(temp<= TemperatureProperty.ICE_POINT-100) return 1;
        double strong = atmosphere.getCloudExponent();
        return strong/(strong+4096);
    }

    /**
     * 获取冻结概率
     * @param accessor 大气访问器
     * @return 一个介于0~1的值，表示概率
     */
    public static double getFreezePossibility(IAtmosphereAccessor accessor) {
        double temp = accessor.getTemperature();
        if(temp>= TemperatureProperty.ICE_POINT) return 0;
        if(temp< TemperatureProperty.ICE_POINT-100) return 1;
        double diff = TemperatureProperty.ICE_POINT-temp;
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
        if(atmosphere.drainWater(FluidUtil.ONE_IN_EIGHT_OF_BUCKET_VOLUME,pos,true)< FluidUtil.ONE_IN_EIGHT_OF_BUCKET_VOLUME) return false;
        if (atmosphere.getAtmosphereTemperature(pos) >= TemperatureProperty.ICE_POINT) {
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
        IAtmosphereAccessor accessor = AtmosphereSystemManager.getAtmosphereAccessor(world,pos,true);
        if(accessor == null) return world.canBlockFreezeBody(pos,neighborWaterCheck);

        if (accessor.getTemperature() < TemperatureProperty.ICE_POINT) {
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

    /**
     * 使用Tetens公式计算指定温度下的饱和水汽压
     * @param temperature 温度，单位K
     * @return 饱和水汽压，单位为帕 Pa
     */
    public static double 计算饱和水汽压(double temperature){
        return 610.78 * Math.exp((17.27 * TemperatureProperty.toCelsiusFromKelvin(temperature)) / (TemperatureProperty.toCelsiusFromKelvin(temperature) + 237.3));
    }
}
