package top.qiguaiaaaa.geocraft.api.atmosphere.accessor;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.WorldServer;
import top.qiguaiaaaa.geocraft.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.geocraft.api.atmosphere.gen.IAtmosphereDataProvider;
import top.qiguaiaaaa.geocraft.api.atmosphere.raypack.HeatPack;
import top.qiguaiaaaa.geocraft.api.atmosphere.storage.AtmosphereData;
import top.qiguaiaaaa.geocraft.api.atmosphere.system.IAtmosphereSystem;

import javax.annotation.Nullable;

/**
 * 大气和外部进行交互的接口
 * 推荐通过大气系统{@link IAtmosphereSystem}获取自己的{@link IAtmosphereAccessor}对象以操作大气，例如获取温度，释放热量等
 */
public interface IAtmosphereAccessor {
    default WorldServer getWorld(){
        return getSystem().getAtmosphereWorldInfo().getWorld();
    }
    default IAtmosphereDataProvider getDataProvider(){
        return getSystem().getDataProvider();
    }
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
    void sendHeat(HeatPack pack, EnumFacing direction);
    void sendHeat(HeatPack pack, Vec3d directionVec);
    void sendHeat(HeatPack pack, Vec3i directionVec);
}
