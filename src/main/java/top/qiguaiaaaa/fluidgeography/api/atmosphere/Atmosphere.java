package top.qiguaiaaaa.fluidgeography.api.atmosphere;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.layer.AtmosphereLayer;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.layer.UnderlyingLayer;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.listener.IAtmosphereListener;

import java.util.Random;
import java.util.Set;

public interface Atmosphere {
    NoiseGeneratorPerlin TEMPERATURE_NOISE = new NoiseGeneratorPerlin(new Random(1234L), 1);

    /**
     * 大气初始化
     * @param chunk 大气所在区块
     * @param info 大气世界信息
     */
    void initialise(Chunk chunk, AtmosphereWorldInfo info);

    //******************
    // Getter And Setter
    //******************
    boolean addSteam(int addAmount, BlockPos pos);

    boolean addWater(int amount, BlockPos pos);

    /**
     * 向大气提供或从大气吸收热量
     * @param Q 提供或吸收的热量。正为提供，负为吸收。
     * @param pos 提供者或吸收着的位置
     */
    void putHeat(double Q, BlockPos pos);

    /**
     * 增加大气监听器
     * @param listener 一个监听器
     */
    void addListener(IAtmosphereListener listener);

    /**
     * 移除指定的监听器
     * @param listener 指定监听器
     */
    void removeListener(IAtmosphereListener listener);

    void setAtmosphereWorldInfo(AtmosphereWorldInfo worldInfo);

    /**
     * 获取大气指定位置的风速
     * @param pos 方块位置,为游戏位置
     * @return 风速向量
     */
    Vec3d getWind(BlockPos pos);

    AtmosphereWorldInfo getAtmosphereWorldInfo();

    /**
     * 返回降雨强度,应当介于0~100之间
     * @return 表示降雨强度的值
     */
    @Deprecated
    double getRainStrong();
    @Deprecated
    int get水量();
    /**
     * 获得某位置的大气水汽压
     * @return 大气水汽压，单位帕 Pa
     */
    double getWaterPressure(BlockPos pos);
    /**
     * 获取指定位置的气压
     * @return 气压，单位Pa
     */
    double getPressure(BlockPos pos);
    default float getTemperature(BlockPos pos){
        return getTemperature(pos,false);
    }
    float getTemperature(BlockPos pos, boolean notAir);

    Set<IAtmosphereListener> getListeners();

    boolean isInitialised();
    long tickTime();

    AtmosphereLayer getLayer(BlockPos pos);
    AtmosphereLayer getTopLayer();
    AtmosphereLayer getBottomLayer();
    UnderlyingLayer getUnderlying();
}
