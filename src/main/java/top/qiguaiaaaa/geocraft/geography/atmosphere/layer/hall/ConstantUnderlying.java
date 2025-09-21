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

package top.qiguaiaaaa.geocraft.geography.atmosphere.layer.hall;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.chunk.Chunk;
import org.apache.commons.lang3.tuple.Triple;
import top.qiguaiaaaa.geocraft.api.GeoCraftProperties;
import top.qiguaiaaaa.geocraft.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.geocraft.api.atmosphere.layer.UnderlyingLayer;
import top.qiguaiaaaa.geocraft.api.atmosphere.raypack.HeatPack;
import top.qiguaiaaaa.geocraft.api.state.TemperatureState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public class ConstantUnderlying extends UnderlyingLayer {
    protected final TemperatureState temperature = GeoCraftProperties.FINAL_TEMPERATURE.getStateInstance();
    public ConstantUnderlying(Atmosphere atmosphere) {
        super(atmosphere);
        this.heatCapacity = (long) 1e10;
    }

    @Override
    public void sendHeat(@Nonnull HeatPack pack, @Nullable EnumFacing direction) {}

    @Override
    public void sendHeat(@Nonnull HeatPack pack, @Nullable Vec3i direction) {}

    @Override
    public double drawHeat(double quanta, @Nullable BlockPos pos) {
        return quanta;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        return new NBTTagCompound();
    }

    @Override
    public void tick(@Nullable Chunk chunk, @Nonnull Map<EnumFacing, Triple<Atmosphere, Chunk, EnumFacing>> neighbors, int x, int z) {}

    @Override
    public void sendHeat(@Nonnull HeatPack pack, @Nullable Vec3d direction) {}

    @Override
    public TemperatureState getTemperature() {
        return temperature;
    }

    @Override
    public float getTemperature(BlockPos pos) {
        return temperature.get();
    }

    @Override
    public String getTagName() {
        return "fg";
    }

    @Override
    public boolean isSerializable() {
        return false;
    }

    @Override
    public UnderlyingLayer load(@Nonnull Chunk chunk) {
        return this;
    }

    @Override
    public boolean isInitialise() {
        return true;
    }

    @Override
    public void putHeat(double quanta, @Nullable BlockPos pos) {}
}
