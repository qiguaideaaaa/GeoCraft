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
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import top.qiguaiaaaa.geocraft.api.GeoFluids;
import top.qiguaiaaaa.geocraft.api.atmosphere.AtmosphereSystemManager;
import top.qiguaiaaaa.geocraft.api.atmosphere.accessor.IAtmosphereAccessor;
import top.qiguaiaaaa.geocraft.api.block.BlockProperties;
import top.qiguaiaaaa.geocraft.api.block.IPermeableBlock;
import top.qiguaiaaaa.geocraft.api.util.AtmosphereUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

import static net.minecraft.block.BlockLiquid.LEVEL;

/**
 * @author QiguaiAAAA
 */
public interface IPermeableBlockLiquid extends IPermeableBlock {
    int HEIGHT_PER_QUANTA = 90090;
    @Nonnull
    Fluid getFluid();

    @Nonnull
    @Override
    default Set<Fluid> getFluid(@Nonnull IBlockState state){
        if(getFluid() == FluidRegistry.WATER) return GeoFluids.FluidSets.SNOW_LAYER_SET;
        else return GeoFluids.FluidSets.LAVA_SET;
    }

    @Override
    default int getQuanta(@Nonnull IBlockState state,@Nullable Fluid fluid){
        if(fluid == getFluid() || fluid == null) return Math.max(8-state.getValue(LEVEL),1);
        return 0;
    }

    @Override
    default int getMaxQuanta(@Nonnull IBlockState state,@Nullable Fluid fluid) {
        if(getFluid() == FluidRegistry.WATER && fluid == GeoFluids.SNOW){
            return 8-getQuanta(state,FluidRegistry.WATER);
        }
        if(fluid == getFluid() || fluid == null) return 8;
        return 0;
    }

    @Override
    default int getEmptyHeight(@Nonnull IBlockState state,@Nullable Fluid fluid){
        if(getFluid() == FluidRegistry.WATER && fluid == GeoFluids.SNOW){
            return getHeight(state,FluidRegistry.WATER);
        }
        return 0;
    }

    @Override
    default int getHeightPerQuanta(@Nonnull IBlockState state){
        return EIGHTH_OF_HEIGHT;
    }

    @Override
    default void addQuanta(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state,@Nonnull Fluid fluid ,int quanta){
        if(fluid == GeoFluids.SNOW && getFluid() == FluidRegistry.WATER){
            int quantaWater = getQuanta(state,FluidRegistry.WATER);
            quanta = MathHelper.clamp(quanta,0,8-getQuanta(state,FluidRegistry.WATER));
            if(quanta == 0) return;
            IAtmosphereAccessor accessor = AtmosphereSystemManager.getAtmosphereAccessor(world,pos,true);
            int light = world.getLightFor(EnumSkyBlock.SKY,pos);
            if(accessor != null) accessor.setSkyLight(light);
            if(quantaWater == quanta){
                world.setBlockState(pos,Blocks.SNOW_LAYER.getDefaultState()
                        .withProperty(BlockProperties.MIXTURE,true)
                        .withProperty(BlockSnow.LAYERS,quanta+quantaWater));
            }else if(quantaWater<quanta){
                world.setBlockState(pos,Blocks.SNOW_LAYER.getDefaultState()
                        .withProperty(BlockSnow.LAYERS,quanta+quantaWater));
                if(accessor != null)
                    accessor.putHeatToUnderlying(AtmosphereUtil.Constants.WATER_MELT_LATENT_HEAT_PER_QUANTA*quantaWater);
            }else {
                setQuanta(world,pos,state,FluidRegistry.WATER,quantaWater+quanta);
                if(accessor != null)
                    accessor.drawHeatFromUnderlying(AtmosphereUtil.Constants.WATER_MELT_LATENT_HEAT_PER_QUANTA*quanta);
            }
        }
        if(fluid != getFluid()) return;
        int newQuanta = 8-state.getValue(LEVEL)+quanta;
        setQuanta(world,pos,state,fluid,newQuanta);
    }

    @Override
    default void setQuanta(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state,@Nonnull Fluid fluid ,int newQuanta){
        if(fluid == GeoFluids.SNOW && getFluid() == FluidRegistry.WATER){
            this.addQuanta(world, pos, state, fluid, newQuanta);
            return;
        }
        if(fluid != getFluid()) return;
        newQuanta = Math.min(newQuanta,8);
        if(newQuanta <= 0) {
            world.setBlockToAir(pos);
            return;
        }
        world.setBlockState(pos,state.withProperty(LEVEL,8-newQuanta), Constants.BlockFlags.SEND_TO_CLIENTS);
    }

    @Nullable
    @Override
    default IBlockState getQuantaState(@Nonnull IBlockState state, @Nonnull Fluid fluid, int quanta){
        if(fluid == GeoFluids.SNOW && getFluid() == FluidRegistry.WATER){
            int quantaWater = getQuanta(state,FluidRegistry.WATER);
            if(quanta<0 || quanta + quantaWater>8) return null;
            if(quanta==0) return state;
            if(quanta<quantaWater) return getQuantaState(state,FluidRegistry.WATER,quanta+quantaWater);
            if(quanta == quantaWater) return Blocks.SNOW_LAYER.getDefaultState().withProperty(BlockProperties.MIXTURE,true)
                    .withProperty(BlockSnow.LAYERS,quanta+quantaWater);
            return Blocks.SNOW_LAYER.getDefaultState().withProperty(BlockSnow.LAYERS,quanta+quantaWater);
        }
        if(fluid != getFluid()) return null;
        if(quanta <= 0) return Blocks.AIR.getDefaultState();
        return state.withProperty(LEVEL,Math.max(8-quanta,0));
    }

    @Override
    default boolean canFill(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Fluid fluid, @Nonnull EnumFacing side, @Nullable IBlockState source){
        if(state.getValue(LEVEL) == 0) return false;
        return fluid == getFluid() || (fluid == GeoFluids.SNOW && getFluid() == FluidRegistry.WATER);
    }

    @Override
    default boolean isFull(@Nonnull IBlockState state,@Nullable Fluid fluid) {
        if(fluid == GeoFluids.SNOW && getFluid() == FluidRegistry.WATER) return state.getValue(LEVEL) == 0;
        if(fluid != null && fluid != getFluid()) return true;
        return state.getValue(LEVEL) == 0;
    }
}
