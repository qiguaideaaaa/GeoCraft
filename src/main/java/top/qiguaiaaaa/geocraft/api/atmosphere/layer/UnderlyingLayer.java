package top.qiguaiaaaa.geocraft.api.atmosphere.layer;

import net.minecraft.world.chunk.Chunk;
import top.qiguaiaaaa.geocraft.api.GeoCraftProperties;
import top.qiguaiaaaa.geocraft.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.geocraft.api.state.FluidState;
import top.qiguaiaaaa.geocraft.api.state.GeographyState;
import top.qiguaiaaaa.geocraft.api.state.TemperatureState;
import top.qiguaiaaaa.geocraft.api.util.math.Altitude;

import javax.annotation.Nullable;

public abstract class UnderlyingLayer extends BaseLayer{
    protected long heatCapacity;
    protected final Altitude altitude = new Altitude(63);
    public UnderlyingLayer(Atmosphere atmosphere) {
        super(atmosphere);
    }

    /**
     * 基于区块加载自身属性
     * @param chunk 下垫面所在区块
     * @return 自身
     */
    public abstract UnderlyingLayer load(Chunk chunk);

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
    public void onLoad(Chunk chunk) {
        onLoadWithoutChunk();
        this.load(chunk);
    }

    @Override
    public void onLoadWithoutChunk() {
        for(GeographyState state:states.values())
            if(!state.isInitialised())
                state.initialise(this);
    }

    @Override
    public boolean isInitialise() {
        return heatCapacity > 0 && getTemperature().isInitialised();
    }

    @Override
    public double getBeginY() {
        return -4096;
    }

    @Override
    public double getDepth() {
        return getTopY()-getBeginY();
    }

    @Override
    public double getTopY() {
        return altitude.get();
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
