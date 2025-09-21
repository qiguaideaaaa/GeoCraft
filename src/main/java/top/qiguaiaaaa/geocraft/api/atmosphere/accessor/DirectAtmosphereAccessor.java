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

package top.qiguaiaaaa.geocraft.api.atmosphere.accessor;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import top.qiguaiaaaa.geocraft.api.atmosphere.layer.AtmosphereLayer;
import top.qiguaiaaaa.geocraft.api.atmosphere.layer.Layer;
import top.qiguaiaaaa.geocraft.api.atmosphere.layer.UnderlyingLayer;
import top.qiguaiaaaa.geocraft.api.atmosphere.raypack.HeatPack;
import top.qiguaiaaaa.geocraft.api.atmosphere.storage.AtmosphereData;
import top.qiguaiaaaa.geocraft.api.atmosphere.system.IAtmosphereSystem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 一个最简单的{@link IAtmosphereAccessor}实现，所有数据均没有经过平滑处理
 */
public class DirectAtmosphereAccessor extends AbstractAtmosphereAccessor {

    public DirectAtmosphereAccessor(@Nonnull IAtmosphereSystem system, @Nonnull AtmosphereData data, @Nonnull BlockPos pos, boolean notAir) {
        super(system, data, pos, notAir);
    }

    @Override
    public double getTemperature() {
        checkAtmosphereDataLoaded();
        return data.getAtmosphere().getTemperature(pos,notAir);
    }

    @Override
    public double getPressure() {
        checkAtmosphereDataLoaded();
        return data.getAtmosphere().getPressure(pos);
    }

    @Override
    public double getWaterPressure() {
        checkAtmosphereDataLoaded();
        return data.getAtmosphere().getWaterPressure(pos);
    }

    @Nonnull
    @Override
    public Vec3d getWind() {
        checkAtmosphereDataLoaded();
        return data.getAtmosphere().getWind(pos);
    }

    @Override
    public void putHeatToAtmosphere(double amount) {
        checkAtmosphereDataLoaded();
        data.getAtmosphere().putHeat(amount,pos);
    }

    @Override
    public void putHeatToUnderlying(double amount) {
        checkAtmosphereDataLoaded();
        data.getAtmosphere().getUnderlying().putHeat(amount,pos);
    }

    @Override
    public void putHeatToCurrentLayer(double amount) {
        checkAtmosphereDataLoaded();
        Layer layer = data.getAtmosphere().getLayer(pos);
        if(layer == null) return;
        layer.putHeat(amount,pos);
    }

    @Override
    public double drawHeatFromAtmosphere(double amount) {
        checkAtmosphereDataLoaded();
        Layer layer = data.getAtmosphere().getLayer(pos);
        if(layer == null) return 0;
        if(layer instanceof AtmosphereLayer){
            return layer.drawHeat(amount,pos);
        }
        data.getAtmosphere().putHeat(-amount,pos);
        return amount;
    }

    @Override
    public double drawHeatFromUnderlying(double amount) {
        checkAtmosphereDataLoaded();
        UnderlyingLayer underlying = data.getAtmosphere().getUnderlying();
        return underlying.drawHeat(amount,pos);
    }

    @Override
    public double drawHeatFromCurrentLayer(double amount) {
        checkAtmosphereDataLoaded();
        Layer layer = data.getAtmosphere().getLayer(pos);
        if(layer == null) return 0;
        return layer.drawHeat(amount,pos);
    }

    @Override
    public void sendHeat(@Nonnull HeatPack pack,@Nullable EnumFacing direction) {
        checkAtmosphereDataLoaded();
        Layer layer = data.getAtmosphere().getLayer(pos);
        if(layer == null) return;
        layer.sendHeat(pack,direction);
    }

    @Override
    public void sendHeat(@Nonnull HeatPack pack,@Nullable Vec3d directionVec) {
        checkAtmosphereDataLoaded();
        Layer layer = data.getAtmosphere().getLayer(pos);
        if(layer == null) return;
        layer.sendHeat(pack,directionVec);
    }

    @Override
    public void sendHeat(@Nonnull HeatPack pack,@Nullable Vec3i directionVec) {
        checkAtmosphereDataLoaded();
        Layer layer = data.getAtmosphere().getLayer(pos);
        if(layer == null) return;
        layer.sendHeat(pack,directionVec);
    }
}
