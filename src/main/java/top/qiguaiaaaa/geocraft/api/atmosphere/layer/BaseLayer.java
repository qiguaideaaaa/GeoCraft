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

package top.qiguaiaaaa.geocraft.api.atmosphere.layer;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import top.qiguaiaaaa.geocraft.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.geocraft.api.property.GeographyProperty;
import top.qiguaiaaaa.geocraft.api.property.TemperatureProperty;
import top.qiguaiaaaa.geocraft.api.atmosphere.raypack.HeatPack;
import top.qiguaiaaaa.geocraft.api.state.GeographyState;
import top.qiguaiaaaa.geocraft.api.state.TemperatureState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * 一个基本的抽象层,实现了基本的层级结构、热量管理和{@link GeographyState}管理
 */
public abstract class BaseLayer implements Layer{
    protected final Map<GeographyProperty, GeographyState> states = new HashMap<>();
    protected final Atmosphere atmosphere;
    protected Layer lowerLayer,upperLayer;

    public BaseLayer(@Nonnull Atmosphere atmosphere) {
        this.atmosphere = atmosphere;
    }

    @Override
    public boolean isInitialise() {
        for(GeographyState state:states.values()){
            if(!state.isInitialised()) return false;
        }
        return true;
    }

    @Override
    public void putHeat(double quanta, @Nullable BlockPos pos) {
        TemperatureState temperature = getTemperature();
        double capacity = getHeatCapacity();
        if(temperature.get()+quanta/capacity< TemperatureProperty.MIN){
            temperature.set(TemperatureProperty.MIN);
            return;
        }
        getTemperature().addHeat(quanta,getHeatCapacity());
    }

    @Override
    public void sendHeat(@Nonnull HeatPack pack, @Nullable EnumFacing direction) {
        if(direction == null || pack.getType() == null){
            putHeat(pack.getAmount(),null);
            return;
        }
        sendHeat(pack,direction.getDirectionVec());
    }

    @Override
    public void sendHeat(@Nonnull HeatPack pack, @Nullable Vec3i direction) {
        if(direction == null || pack.getType() == null){
            putHeat(pack.getAmount(),null);
            return;
        }
        sendHeat(pack,new Vec3d(direction));
    }

    @Override
    public double drawHeat(double quanta,@Nullable BlockPos pos) {
        TemperatureState temperature = getTemperature();
        double capacity = getHeatCapacity();
        if(temperature.get()-quanta/capacity< TemperatureProperty.MIN){
            quanta = Math.max(temperature.get()- TemperatureProperty.MIN-0.1,0)/capacity;
            temperature.set(TemperatureProperty.MIN+0.1f);
            return quanta;
        }
        getTemperature().addHeat(-quanta,getHeatCapacity());
        return quanta;
    }

    @Nullable
    @Override
    public Layer getLowerLayer() {
        return lowerLayer;
    }

    @Nullable
    @Override
    public Layer getUpperLayer() {
        return upperLayer;
    }

    @Override
    public void setLowerLayer(Layer layer) {
        if(layer == this) return;
        this.lowerLayer = layer;
    }

    @Override
    public void setUpperLayer(Layer layer) {
        if(layer == this) return;
        this.upperLayer = layer;
    }

    @Override
    public Atmosphere getAtmosphere() {
        return atmosphere;
    }

    @Nullable
    @Override
    public TemperatureState getTemperature(TemperatureProperty property) {
        final GeographyState state = states.get(property);
        if(state instanceof TemperatureState) return (TemperatureState) state;
        return null;
    }

    @Nullable
    @Override
    public GeographyState getState(@Nonnull GeographyProperty property) {
        return states.get(property);
    }

    @Nullable
    @Override
    public GeographyState addState(@Nonnull GeographyProperty property) {
        GeographyState oldState = getState(property);
        GeographyState newState = property.getStateInstance();
        states.put(property,newState);
        newState.initialise(this);
        return oldState;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        for(GeographyState state:states.values()){
            if(!state.toBeSavedIntoNBT()) continue;
            compound.setTag(state.getNBTTagKey(),state.serializeNBT());
        }
        return compound;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        for(GeographyState state:states.values()){
            if(!state.toBeLoadedFromNBT()) continue;
            state.deserializeNBT(nbt.getTag(state.getNBTTagKey()));
        }
    }
}
