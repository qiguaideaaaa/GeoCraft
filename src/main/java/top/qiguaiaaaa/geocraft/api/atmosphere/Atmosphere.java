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

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import net.minecraftforge.common.util.INBTSerializable;
import top.qiguaiaaaa.geocraft.api.atmosphere.accessor.IAtmosphereAccessor;
import top.qiguaiaaaa.geocraft.api.atmosphere.layer.AtmosphereLayer;
import top.qiguaiaaaa.geocraft.api.atmosphere.layer.Layer;
import top.qiguaiaaaa.geocraft.api.atmosphere.layer.UnderlyingLayer;
import top.qiguaiaaaa.geocraft.api.atmosphere.tracker.IAtmosphereTracker;
import top.qiguaiaaaa.geocraft.api.atmosphere.weather.Weather;
import top.qiguaiaaaa.geocraft.api.property.TemperatureProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

/**
 * 一个大气<br/>
 * 其实更应该叫做地理单元，因为实际上一个Atmosphere实例还包括下垫面（也就是地下），但是却不一定真的有大气层
 */
public interface Atmosphere extends INBTSerializable<NBTTagCompound> {
    NoiseGeneratorPerlin TEMPERATURE_NOISE = new NoiseGeneratorPerlin(new Random(1234L), 1);

    /**
     * 当区块已经加载的时候初始化大气
     * @param chunk 大气所在区块
     * @param info 世界大气信息
     */
    void onLoad(@Nonnull Chunk chunk,@Nonnull AtmosphereWorldInfo info);

    /**
     * 在区块没有加载的情况下加载大气
     */
    void onLoadWithoutChunk(@Nonnull AtmosphereWorldInfo info);

    /**
     * 大气是否已经加载完成。使用任何大气都应当事先检查该大气是否已经加载完成。
     * @return 若已经加载完成，则返回true
     */
    boolean isLoaded();

    /**
     * 当大气需要被卸载时调用
     * 大气应当在此时完成诸如数据保存等重要操作
     */
    void onUnload();

    /**
     * 该大气已经tick的大气刻
     * @return 大气刻
     */
    long tickTime();

    /**
     * 向大气提供气态水
     * @param addAmount 水量,应为正数
     * @param pos 位置,不应为NULL
     * @return 添加液态水是否成功
     */
    boolean addSteam(int addAmount,@Nonnull BlockPos pos);

    /**
     * 向大气提供液态水,若要吸收液态水请使用 {@link Atmosphere#drainWater(int, BlockPos, boolean)}
     * @param amount 水量,应为正数
     * @param pos 位置,不应为NULL
     * @return 添加液态水是否成功
     */
    boolean addWater(int amount,@Nonnull BlockPos pos);

    /**
     * 在指定位置吸收液态水
     * @param amount 期望吸收的量
     * @param pos 位置
     * @param test 是否为测试
     * @return 实际吸收的量
     */
    int drainWater(int amount,@Nonnull BlockPos pos, boolean test);

    /**
     * 获取大气温度，绝对不能返回地面温度
     * @param pos 位置
     * @return 大气温度
     */
    default float getAtmosphereTemperature(@Nonnull BlockPos pos){
        return getTemperature(pos,false);
    }

    /**
     * 获取温度,一般情况下请使用{@link IAtmosphereAccessor}获取温度，不要使用这个方法，也不要通过Layer层获取温度
     * @param pos 位置
     * @param notAir 是否不为大气温度
     * @return 返回对应位置的温度。若指定位置没有可用温度,则返回 {@link TemperatureProperty#UNAVAILABLE}
     */
    float getTemperature(@Nonnull BlockPos pos, boolean notAir);

    /**
     * 向大气提供或从大气吸收热量,不会操作也不应该操作到下垫面
     * 若要操作下垫面,应先使用 {@link Atmosphere#getUnderlying()} 获取到下垫面再操作
     * 一般情况下请使用{@link IAtmosphereAccessor}
     * @param Q 提供或吸收的热量。正为提供，负为吸收。
     * @param pos 提供者或吸收着的位置
     */
    void putHeat(double Q, BlockPos pos);

    /**
     * 获取大气指定位置的风速
     * 一般情况下请使用{@link IAtmosphereAccessor}
     * @param pos 方块位置,为游戏位置
     * @return 风速向量
     */
    @Nonnull
    Vec3d getWind(@Nonnull BlockPos pos);

    /**
     * 获取当前的天气
     * @param pos 位置
     * @return 天气
     */
    @Nonnull
    Weather getWeather(@Nonnull BlockPos pos);

    /**
     * 获得某位置的大气水汽压
     * 一般情况下请使用{@link IAtmosphereAccessor}
     * @return 大气水汽压，单位帕 Pa。若无可用气压，则返回 0
     */
    double getWaterPressure(@Nonnull BlockPos pos);

    /**
     * 获取大气指定位置的气压
     * 一般情况下请使用{@link IAtmosphereAccessor}
     * @param pos 位置
     * @return 气压,单位Pa。若无可用气压，则返回 0
     */
    double getPressure(@Nonnull BlockPos pos);

    /**
     * 获取大气世界信息<br/>
     * 请不要在{@link #isLoaded()}为false的情况下使用该方法
     * @return 大气世界信息
     */
    @Nonnull
    AtmosphereWorldInfo getAtmosphereWorldInfo();

    /**
     * 增加大气监听器
     * @param tracker 一个监听器
     */
    void addTracker(@Nonnull IAtmosphereTracker tracker);

    /**
     * 移除指定的监听器
     * @param tracker 指定监听器
     */
    void removeTracker(@Nonnull IAtmosphereTracker tracker);

    /**
     * 获取当前位置的层级
     * @param pos 层级
     * @return 一个层级
     */
    @Nullable
    Layer getLayer(@Nonnull BlockPos pos);

    /**
     * 获取顶端层级
     * @return 顶端层级
     */
    @Nonnull
    Layer getTopLayer();

    /**
     * 获取底端层级
     * @return 底端层级
     */
    @Nonnull
    Layer getBottomLayer();

    /**
     * 获取底端大气层级，若没有大气层则返回null
     * @return 底端大气层级
     */
    @Nullable
    AtmosphereLayer getBottomAtmosphereLayer();

    /**
     * 获取下垫面层级
     * @return 下垫面层级
     */
    @Nonnull
    UnderlyingLayer getUnderlying();

    /**
     * 返回云量指数,应当介于0~100之间，越大云越多
     * @return 表示云量的值
     */
    double getCloudExponent();
}
