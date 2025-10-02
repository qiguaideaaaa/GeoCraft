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
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.WorldServer;
import top.qiguaiaaaa.geocraft.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.geocraft.api.atmosphere.AtmosphereWorldInfo;
import top.qiguaiaaaa.geocraft.api.atmosphere.gen.IAtmosphereDataProvider;
import top.qiguaiaaaa.geocraft.api.atmosphere.raypack.HeatPack;
import top.qiguaiaaaa.geocraft.api.atmosphere.storage.AtmosphereData;
import top.qiguaiaaaa.geocraft.api.atmosphere.system.IAtmosphereSystem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 大气和外部进行交互的接口
 * 推荐通过大气系统{@link IAtmosphereSystem}获取自己的{@link IAtmosphereAccessor}对象以操作大气，例如获取温度，释放热量等
 */
public interface IAtmosphereAccessor {
    @Nonnull
    default WorldServer getWorld(){
        return getSystem().getAtmosphereWorldInfo().getWorld();
    }
    @Nonnull
    default IAtmosphereDataProvider getDataProvider(){
        return getSystem().getDataProvider();
    }
    @Nonnull
    default AtmosphereWorldInfo getAtmosphereWorldInfo(){return getSystem().getAtmosphereWorldInfo();}
    @Nonnull
    IAtmosphereSystem getSystem();

    /**
     * 获取该Accessor目前位置的大气
     * @return 大气,若大气未加载则为null
     */
    @Nullable
    Atmosphere getAtmosphereHere();

    /**
     * 获取该Accessor目前位置的大气数据
     * @return 大气数据,若大气未加载则为null
     */
    @Nullable
    AtmosphereData getAtmosphereDataHere();

    /**
     * 获取该大气接口指向的大气是否处于加载状态
     * @return 大气是否在加载
     */
    boolean isAtmosphereLoaded();

    /**
     * 刷新该Accessor的状态
     * @return 状态是否有更新
     */
    boolean refresh();

    /**
     * 设置当前位置的天光亮度,设置为负数以忽略天光亮度
     * @param light {@link EnumSkyBlock#SKY}天光亮度的值
     */
    void setSkyLight(int light);

    /**
     * 设置当前方块是否不是空气
     */
    void setNotAir(boolean notAir);
    double getTemperature();
    double getTemperature(boolean notAir);
    double getPressure();
    double getWaterPressure();
    @Nonnull
    Vec3d getWind();
    void putHeatToAtmosphere(double amount);
    void putHeatToUnderlying(double amount);
    void putHeatToCurrentLayer(double amount);

    /**
     * 从大气中吸取热量
     * @param amount 吸取量
     * @return 实际吸取量
     */
    double drawHeatFromAtmosphere(double amount);
    double drawHeatFromUnderlying(double amount);
    double drawHeatFromCurrentLayer(double amount);

    /**
     * 从该层往指定方向发送{@link HeatPack}，注意不应当让该层吸收对应的包
     * @param pack 热量包
     * @param direction 发射方向
     */
    void sendHeat(@Nonnull HeatPack pack,@Nullable EnumFacing direction);
    void sendHeat(@Nonnull HeatPack pack,@Nullable Vec3d directionVec);
    void sendHeat(@Nonnull HeatPack pack,@Nullable Vec3i directionVec);
}
