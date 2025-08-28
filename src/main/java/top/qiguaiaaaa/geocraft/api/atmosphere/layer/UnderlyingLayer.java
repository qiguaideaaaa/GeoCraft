package top.qiguaiaaaa.geocraft.api.atmosphere.layer;

import net.minecraft.world.chunk.Chunk;
import top.qiguaiaaaa.geocraft.api.GEOProperties;
import top.qiguaiaaaa.geocraft.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.geocraft.api.atmosphere.state.FluidState;
import top.qiguaiaaaa.geocraft.api.atmosphere.state.GeographyState;
import top.qiguaiaaaa.geocraft.api.atmosphere.state.TemperatureState;
import top.qiguaiaaaa.geocraft.api.util.math.Altitude;

import javax.annotation.Nullable;

public abstract class UnderlyingLayer extends BaseLayer{
    protected long heatCapacity;
    protected final TemperatureState temperature = GEOProperties.TEMPERATURE.getStateInstance();
    protected Altitude altitude = new Altitude(63);
    public UnderlyingLayer(Atmosphere atmosphere) {
        super(atmosphere);
        states.put(GEOProperties.TEMPERATURE, temperature);
    }

    /**
     * 基于区块更新自身属性
     * @param chunk 下垫面所在区块
     * @return 自身
     */
    public abstract UnderlyingLayer update(Chunk chunk);

    /**
     * 设置地面平均海拔，类型为游戏海拔
     * @param altitude 类型为游戏海拔
     */
    public void setAltitude(double altitude) {
        if(altitude<0) return;
        this.altitude.set(altitude);
    }
    public void setAltitude(Altitude altitude) {
        if(altitude.get()<0) return;
        this.altitude.set(altitude);
    }

    /**
     * 获取地面平均海拔
     * @return 地面平均海拔，类型为游戏海拔
     */
    public Altitude getAltitude() {
        return altitude;
    }

    @Override
    public void initialise(Chunk chunk) {
        for(GeographyState state:states.values())
            if(!state.isInitialised())
                state.initialise(this);
        this.update(chunk);
    }

    @Override
    public boolean isInitialise() {
        return heatCapacity > 0 && temperature.isInitialised();
    }

    @Override
    public double getBeginY() {
        return -4096;
    }

    @Override
    public TemperatureState getTemperature() {
        return temperature;
    }

    @Override
    public double getHeatCapacity() {
        return heatCapacity;
    }

    @Nullable
    @Override
    public FluidState getWater() {
        return upperLayer.getWater();
    }
}
