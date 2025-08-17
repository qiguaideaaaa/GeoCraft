package top.qiguaiaaaa.fluidgeography.api.atmosphere;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.listener.IAtmosphereListener;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.property.AtmosphereProperty;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.state.AtmosphereStates;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.state.IAtmosphereState;

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
    boolean add水量(int addAmount);

    void add低层大气温度(double temp);

    void add地表温度(double temp);

    void add低层大气热量(double Q);
    void add地表热量(double Q);

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

    void set水量(int waterAmount);

    void set低层大气温度(float temperature);
    void set地表温度(float temperature);

    void setAtmosphereWorldInfo(AtmosphereWorldInfo worldInfo);

    void 重置温度(Chunk chunk);

    Vec3d getWindSpeed(EnumFacing direction);

    AtmosphereWorldInfo getAtmosphereWorldInfo();

    long get低层大气热容();

    /**
     * 返回降雨强度,应当介于0~100之间
     * @return 表示降雨强度的值
     */
    double getRainStrong();

    Underlying get下垫面();

    int get水量();

    float get低层大气温度();

    float get温度(BlockPos pos,boolean isAir);
    float get地表温度();

    Set<IAtmosphereListener> getListeners();

    AtmosphereStates getStates();


    boolean isInitialised();
    long tickTime();
}
