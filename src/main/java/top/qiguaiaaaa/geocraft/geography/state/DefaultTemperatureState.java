package top.qiguaiaaaa.geocraft.geography.state;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import top.qiguaiaaaa.geocraft.api.atmosphere.layer.UnderlyingLayer;
import top.qiguaiaaaa.geocraft.api.property.TemperatureProperty;
import top.qiguaiaaaa.geocraft.api.state.TemperatureState;
import top.qiguaiaaaa.geocraft.geography.property.DefaultTemperature;
import top.qiguaiaaaa.geocraft.util.ChunkUtil;

import javax.annotation.Nonnull;

/**
 * Temperature的默认实现
 * 注意Temperature的初始化依赖大气本身层级，因此这里没有初始化方法
 */
public class DefaultTemperatureState extends TemperatureState {
    protected static final int TEMPERATURE_MULTI = 30;
    protected static final int TEMPERATURE_TRANSFER_OFFSET = 3;
    public DefaultTemperatureState(float temp){
        super(temp);
    }

    @Override
    public boolean isInitialised() {
        return temperature > 0 && !Float.isInfinite(temperature);
    }

    @Nonnull
    @Override
    public DefaultTemperature getProperty() {
        return DefaultTemperature.TEMPERATURE;
    }

    @Nonnull
    @Override
    public String getNBTTagKey() {
        return "temp";
    }

    public static float calculateBaseTemperature(Chunk chunk , UnderlyingLayer underlying){
        Biome mainBiome = ChunkUtil.getMainBiome(chunk);
        float biomeTemp = mainBiome.getTemperature(new BlockPos((chunk.x<<4)+8, underlying.getAltitude().get(),(chunk.z<<4)+8));
        return toRealTemperature(biomeTemp);
    }

    public static float toRealTemperature(float biomeTemperature){
        if(biomeTemperature <= 0.15){
            return TemperatureProperty.ICE_POINT +(biomeTemperature*TEMPERATURE_MULTI)-TEMPERATURE_TRANSFER_OFFSET-10;
        }else{
            return TemperatureProperty.ICE_POINT +(biomeTemperature*TEMPERATURE_MULTI)-TEMPERATURE_TRANSFER_OFFSET;
        }
    }
}
