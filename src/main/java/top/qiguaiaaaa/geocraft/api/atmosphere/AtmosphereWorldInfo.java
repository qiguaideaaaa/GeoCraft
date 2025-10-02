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

package top.qiguaiaaaa.geocraft.api.atmosphere;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import top.qiguaiaaaa.geocraft.api.atmosphere.accessor.IAtmosphereAccessor;
import top.qiguaiaaaa.geocraft.api.atmosphere.system.IAtmosphereSystem;
import top.qiguaiaaaa.geocraft.api.property.TemperatureProperty;

import javax.annotation.Nonnull;

public class AtmosphereWorldInfo {
    protected final WorldServer world;
    protected IAtmosphereSystem system;

    protected boolean waterFreeze = true;
    protected boolean waterEvaporate = true;

    /**
     * 下雨概率的平滑值,用于{@link top.qiguaiaaaa.geocraft.util.WaterUtil#getRainPossibility(IAtmosphereAccessor)}
     */
    protected int rainSmoothingConstant = 4096;
    /**
     * 水汽交换率,用于{@link top.qiguaiaaaa.geocraft.util.WaterUtil#getWaterEvaporateAmount(IAtmosphereAccessor)}
     */
    protected double vaporExchangeRate = 1e-6;

    public void waterFreeze(boolean waterFreeze) {
        this.waterFreeze = waterFreeze;
    }

    public void waterEvaporate(boolean waterEvaporate){
        this.waterEvaporate = waterEvaporate;
    }

    public void setRainSmoothingConstant(int rainSmoothingConstant) {
        if(rainSmoothingConstant<1) throw new IllegalArgumentException();
        this.rainSmoothingConstant = rainSmoothingConstant;
    }

    public void setVaporExchangeRate(double vaporExchangeRate) {
        if(vaporExchangeRate<0) throw new IllegalArgumentException();
        this.vaporExchangeRate = vaporExchangeRate;
    }

    public boolean canWaterFreeze() {
        return waterFreeze;
    }

    public boolean canWaterEvaporate() {
        return waterEvaporate;
    }

    public int getRainSmoothingConstant() {
        return rainSmoothingConstant;
    }

    public double getVaporExchangeRate() {
        return vaporExchangeRate;
    }

    /**
     * 指定位置的水是否能够蒸发
     * @param pos 位置
     * @return 若能,则返回true,否则返回false
     */
    public boolean canWaterEvaporate(@Nonnull BlockPos pos){
        return canWaterEvaporate();
    }

    /**
     * 指定位置是否能够凝结水成冰
     * @param pos 位置
     * @param neighborWaterCheck 是否检查周边水方块
     * @return 如果能，则返回true
     */
    public boolean canWaterFreeze(@Nonnull BlockPos pos, boolean neighborWaterCheck) {
        if(!canWaterFreeze()) return false;
        IAtmosphereAccessor accessor = system.getAccessor(pos,true);
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

    public AtmosphereWorldInfo(@Nonnull WorldServer world) {
        this.world = world;
    }

    @Nonnull
    public WorldServer getWorld() {
        return world;
    }

    @Nonnull
    public IAtmosphereSystem getSystem() {
        return system;
    }

    public void setSystem(@Nonnull IAtmosphereSystem system) {
        this.system = system;
    }

    private static boolean isWater(@Nonnull World world,@Nonnull BlockPos pos) {
        return world.getBlockState(pos).getMaterial() == Material.WATER;
    }
}
