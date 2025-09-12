package top.qiguaiaaaa.geocraft.api.atmosphere.layer;

import net.minecraft.world.chunk.Chunk;
import top.qiguaiaaaa.geocraft.api.GeoCraftProperties;
import top.qiguaiaaaa.geocraft.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.geocraft.api.state.FluidState;
import top.qiguaiaaaa.geocraft.api.state.GeographyState;
import top.qiguaiaaaa.geocraft.api.state.TemperatureState;
import top.qiguaiaaaa.geocraft.api.util.math.Altitude;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 下垫面层级
 * 所有下垫面层级应当从该类继承
 */
public abstract class UnderlyingLayer extends BaseLayer{
    protected long heatCapacity; //热容
    protected final Altitude altitude = new Altitude(63); //表层海拔高度

    /**
     * 创建一个下垫面层级
     * @param atmosphere 该层级所在大气
     */
    public UnderlyingLayer(@Nonnull Atmosphere atmosphere) {
        super(atmosphere);
    }

    /**
     * 基于区块加载自身属性
     * @param chunk 下垫面所在区块
     * @return 自身
     */
    public abstract UnderlyingLayer load(@Nonnull Chunk chunk);

    /**
     * 设置地面海拔，类型为游戏海拔
     * @param altitude 类型为游戏海拔
     */
    public void setAltitude(double altitude) {
        if(altitude<0) return;
        this.altitude.set(altitude);
    }

    /**
     * 设置地面海拔
     * @param altitude 目标海拔高度
     */
    public void setAltitude(@Nonnull Altitude altitude) {
        if(altitude.get()<0) return;
        this.altitude.set(altitude);
    }

    /**
     * 获取地面平均海拔
     * @return 地面平均海拔，类型为游戏海拔
     */
    @Nonnull
    public Altitude getAltitude() {
        return altitude;
    }

    /**
     * {@inheritDoc}
     * @param chunk 层级所在区块
     */
    @Override
    public void onLoad(@Nonnull Chunk chunk) {
        onLoadWithoutChunk();
        this.load(chunk);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onLoadWithoutChunk() {
        for(GeographyState state:states.values())
            if(!state.isInitialised())
                state.initialise(this);
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public boolean isInitialise() {
        return heatCapacity > 0 && getTemperature().isInitialised();
    }

    /**
     * {@inheritDoc}
     * 一般情况下下垫面是最下面的一层，所以默认从-4096开始
     * @return {@inheritDoc}
     */
    @Override
    public double getBeginY() {
        return -4096;
    }

    /**
     * {@inheritDoc}
     * 对下垫面层一般没有太大意义，因为一般都是最低一层
     * @return {@inheritDoc}
     */
    @Override
    public double getDepth() {
        return getTopY()-getBeginY();
    }

    /**
     * 获得层级顶端高度，即海拔高度
     * @return 该下垫面的海拔高度
     */
    @Override
    public double getTopY() {
        return altitude.get();
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public double getHeatCapacity() {
        return heatCapacity;
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Nullable
    @Override
    public FluidState getWater() {
        return upperLayer.getWater();
    }
}
