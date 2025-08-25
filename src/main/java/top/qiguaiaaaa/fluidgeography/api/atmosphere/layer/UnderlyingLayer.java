package top.qiguaiaaaa.fluidgeography.api.atmosphere.layer;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.Chunk;
import top.qiguaiaaaa.fluidgeography.api.FGAtmosphereProperties;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.state.GasState;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.state.TemperatureState;
import top.qiguaiaaaa.fluidgeography.api.util.math.Altitude;

import javax.annotation.Nullable;

public abstract class UnderlyingLayer extends BaseAtmosphereLayer{
    protected long heatCapacity;
    protected final TemperatureState temperature = FGAtmosphereProperties.TEMPERATURE.getStateInstance();
    protected Altitude altitude = new Altitude(63);
    public UnderlyingLayer(Atmosphere atmosphere) {
        super(atmosphere);
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
        this.update(chunk);
    }

    @Override
    public boolean isInitialise() {
        return heatCapacity > 0;
    }

    @Override
    public double drawHeat(double quanta) {
        temperature.add热量(-quanta, heatCapacity);
        return quanta;
    }

    @Override
    public boolean addSteam(BlockPos pos, int amount) {
        if(upperLayer == null) return true;
        return upperLayer.addSteam(pos,amount);
    }

    @Override
    public boolean addWater(BlockPos pos, int amount) {
        if(upperLayer == null) return true;
        return upperLayer.addWater(pos,amount);
    }

    @Override
    public double getPressure(BlockPos pos) {
        if(upperLayer == null) return 0;
        return upperLayer.getPressure(pos);
    }

    @Override
    public double getWaterPressure(BlockPos pos) {
        if(upperLayer == null) return 0;
        return upperLayer.getWaterPressure(pos);
    }

    @Override
    public double getWaterPressure() {
        if(upperLayer == null) return 0;
        return upperLayer.getWaterPressure();
    }

    @Override
    public float getTemperature(BlockPos pos, boolean notAir) {
        return temperature.get();
    }

    @Override
    public Vec3d getWind(BlockPos pos) {
        return Vec3d.ZERO;
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
    public GasState getWater() {
        return upperLayer.getWater();
    }

    @Nullable
    @Override
    public GasState getSteam() {
        return upperLayer.getSteam();
    }
}
