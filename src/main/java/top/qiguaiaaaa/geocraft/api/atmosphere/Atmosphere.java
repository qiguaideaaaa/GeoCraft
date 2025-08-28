package top.qiguaiaaaa.geocraft.api.atmosphere;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import top.qiguaiaaaa.geocraft.api.atmosphere.layer.AtmosphereLayer;
import top.qiguaiaaaa.geocraft.api.atmosphere.layer.Layer;
import top.qiguaiaaaa.geocraft.api.atmosphere.layer.UnderlyingLayer;
import top.qiguaiaaaa.geocraft.api.atmosphere.tracker.IAtmosphereTracker;
import top.qiguaiaaaa.geocraft.api.atmosphere.property.TemperatureProperty;

import javax.annotation.Nullable;
import java.util.Random;

public interface Atmosphere {
    NoiseGeneratorPerlin TEMPERATURE_NOISE = new NoiseGeneratorPerlin(new Random(1234L), 1);

    void initialise(Chunk chunk, AtmosphereWorldInfo info);

    boolean isInitialised();

    long tickTime();

    boolean addSteam(int addAmount, BlockPos pos);

    boolean addWater(int amount, BlockPos pos);

    /**
     * 在指定位置吸收液态水
     * @param amount 期望吸收的量
     * @param pos 位置
     * @param test 是否为测试
     * @return 实际吸收的量
     */
    int drainWater(int amount, BlockPos pos, boolean test);

    /**
     * 获取大气温度，绝对不能返回地面温度
     * @param pos 位置
     * @return 大气温度
     */
    default float getAtmosphereTemperature(BlockPos pos){
        return getTemperature(pos,false);
    }

    /**
     * 获取温度
     * @param pos 位置
     * @param notAir 是否不为大气温度
     * @return 返回对应位置的温度。若指定位置没有可用温度,则返回 {@link TemperatureProperty#UNAVAILABLE}
     */
    float getTemperature(BlockPos pos, boolean notAir);

    /**
     * 向大气提供或从大气吸收热量,不会操作也不应该操作到下垫面
     * @param Q 提供或吸收的热量。正为提供，负为吸收。
     * @param pos 提供者或吸收着的位置
     */
    void putHeat(double Q, BlockPos pos);

    /**
     * 获取大气指定位置的风速
     * @param pos 方块位置,为游戏位置
     * @return 风速向量
     */
    Vec3d getWind(BlockPos pos);

    /**
     * 获得某位置的大气水汽压
     * @return 大气水汽压，单位帕 Pa。若无可用气压，则返回 0
     */
    double getWaterPressure(BlockPos pos);

    /**
     * 获取大气指定位置的气压
     * @param pos 位置
     * @return 气压,单位Pa。若无可用气压，则返回 0
     */
    double getPressure(BlockPos pos);

    void setAtmosphereWorldInfo(AtmosphereWorldInfo worldInfo);

    AtmosphereWorldInfo getAtmosphereWorldInfo();

    /**
     * 增加大气监听器
     * @param tracker 一个监听器
     */
    void addTracker(IAtmosphereTracker tracker);

    /**
     * 移除指定的监听器
     * @param tracker 指定监听器
     */
    void removeTracker(IAtmosphereTracker tracker);

    Layer getLayer(BlockPos pos);

    /**
     * 获取顶端层级
     * @return 顶端层级
     */
    Layer getTopLayer();

    /**
     * 获取底端层级
     * @return 底端层级
     */
    Layer getBottomLayer();

    /**
     * 获取底端大气层级
     * @return 底端大气层级
     */
    @Nullable
    AtmosphereLayer getBottomAtmosphereLayer();

    UnderlyingLayer getUnderlying();

    /**
     * 返回云量指数,应当介于0~100之间，越大云越多
     * @return 表示云量的值
     */
    double getCloudExponent();
}
