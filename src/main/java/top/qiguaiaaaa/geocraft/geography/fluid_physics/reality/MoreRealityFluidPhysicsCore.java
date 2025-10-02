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

import net.minecraft.block.BlockSnow;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import top.qiguaiaaaa.geocraft.api.atmosphere.AtmosphereSystemManager;
import top.qiguaiaaaa.geocraft.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.geocraft.api.atmosphere.accessor.IAtmosphereAccessor;
import top.qiguaiaaaa.geocraft.api.property.TemperatureProperty;
import top.qiguaiaaaa.geocraft.api.util.AtmosphereUtil;
import top.qiguaiaaaa.geocraft.api.util.FluidUtil;
import top.qiguaiaaaa.geocraft.util.BaseUtil;
import top.qiguaiaaaa.geocraft.util.WaterUtil;

import javax.annotation.Nullable;
import java.util.Random;

import static net.minecraft.block.BlockLiquid.LEVEL;

public final class MoreRealityFluidPhysicsCore {
    @Nullable
    public static IBlockState evaporateWater(World world, BlockPos pos, IBlockState state, Random rand){
        int light = world.getLightFor(EnumSkyBlock.SKY,pos);
        if(light <= 0) return state;
        IAtmosphereAccessor accessor = AtmosphereSystemManager.getAtmosphereAccessor(world,pos,true);
        if(accessor == null) return state;
        Atmosphere atmosphere = accessor.getAtmosphereHere();
        if(atmosphere == null) return state;
        if(!accessor.getAtmosphereWorldInfo().canWaterEvaporate(pos)) return state;
        accessor.setSkyLight(light);

        double possibility = getWaterEvaporatePossibility(world,pos,state,accessor);
        int meta = state.getValue(LEVEL);
        if(!BaseUtil.getRandomResult(rand,possibility)){
            return state;
        }
        if(meta >=8) return null;
        if(!atmosphere.addSteam(FluidUtil.ONE_IN_EIGHT_OF_BUCKET_VOLUME,pos)) return state;
        accessor.drawHeatFromUnderlying(AtmosphereUtil.Constants.WATER_EVAPORATE_LATENT_HEAT_PER_QUANTA);
        if(meta == 7) return null;
        state = state.withProperty(LEVEL,meta+1);
        return state;
    }
    public static IBlockState freezeWater(World world, BlockPos pos, IBlockState state, Random rand){
        int light = world.getLightFor(EnumSkyBlock.SKY,pos);
        if(light == 0) return state;
        int meta = state.getValue(LEVEL);
        if(meta >=8) return state;
        IAtmosphereAccessor accessor = AtmosphereSystemManager.getAtmosphereAccessor(world,pos,true);
        if(accessor == null) return state;
        Atmosphere atmosphere = accessor.getAtmosphereHere();
        if(atmosphere == null) return state;
        accessor.setSkyLight(light);
        if(!accessor.getSystem().getAtmosphereWorldInfo().canWaterFreeze()) return state;

        double possibility  = WaterUtil.getFreezePossibility(accessor);
        if(possibility <= 0) return state;
        if(meta == 7) possibility = Math.min(possibility*8,1);
        else if(meta == 6) possibility = Math.min(possibility*4,1);
        else if(meta == 5) possibility = Math.min(possibility*2,1);
        if(!BaseUtil.getRandomResult(rand,possibility*0.85+0.15)){
            return state;
        }
        if(meta == 0){
            if(!accessor.getSystem().getAtmosphereWorldInfo().canWaterFreeze(pos,true)) return state;
            return Blocks.ICE.getDefaultState();
        }
        if(!WaterUtil.canPlaceSnow(world,pos)) return state;
        int quanta = 8-meta;
        accessor.putHeatToUnderlying(AtmosphereUtil.Constants.WATER_MELT_LATENT_HEAT_PER_QUANTA*quanta);
        return Blocks.SNOW_LAYER.getDefaultState().withProperty(BlockSnow.LAYERS,quanta);
    }

    public static double getWaterEvaporatePossibility(World world, BlockPos pos, IBlockState state,IAtmosphereAccessor accessor){
        double possibility = WaterUtil.getWaterEvaporatePossibility(accessor);
        if(!world.isAreaLoaded(pos,1)) return possibility;

        int meta = state.getValue(LEVEL);
        if(meta <5) return possibility;

        byte neighborsWater = 0;
        for(EnumFacing facing:EnumFacing.HORIZONTALS){
            BlockPos facingPos = pos.offset(facing);
            IBlockState facingState = world.getBlockState(facingPos);
            if(FluidUtil.getFluid(facingState) == FluidRegistry.WATER){
                neighborsWater++;
            }
        }
        if(neighborsWater == 4) return possibility;

        if(pos.getY() <= 0) return possibility;
        IBlockState downState= world.getBlockState(pos.down());
        if(FluidUtil.getFluid(downState) == FluidRegistry.WATER) return possibility;

        if(meta == 7) possibility = Math.min(possibility*8,1);
        else if(meta == 6) possibility = Math.min(possibility*4,1);
        else if(meta == 5) possibility = Math.min(possibility*2,1);

        return possibility;
    }

    /**
     * 是否能够在指定位置降水
     * @param world 世界
     * @param pos 位置
     * @return 如果能，则返回true
     */
    public static boolean canRainAt(World world,BlockPos pos){
        Atmosphere atmosphere = AtmosphereSystemManager.getAtmosphere(world, pos);
        if(atmosphere == null) return false;
        if(atmosphere.drainWater(FluidUtil.ONE_IN_EIGHT_OF_BUCKET_VOLUME,pos,true)<FluidUtil.ONE_IN_EIGHT_OF_BUCKET_VOLUME) return false;
        float temp = atmosphere.getAtmosphereTemperature(pos);
        if(temp <= TemperatureProperty.UNAVAILABLE) return false;
        if (!(temp < TemperatureProperty.ICE_POINT) && !(temp > TemperatureProperty.BOILED_POINT)) {
            if (pos.getY() >= 0 && pos.getY() < 256) {
                IBlockState state = world.getBlockState(pos);

                return state.getBlock().isAir(state, world, pos) && Blocks.FLOWING_WATER.canPlaceBlockAt(world, pos);
            }

        }
        return false;
    }
}
