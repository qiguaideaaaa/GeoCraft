package top.qiguaiaaaa.geocraft.atmosphere.state;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import top.qiguaiaaaa.geocraft.api.atmosphere.layer.UnderlyingLayer;
import top.qiguaiaaaa.geocraft.api.atmosphere.property.TemperatureProperty;
import top.qiguaiaaaa.geocraft.api.atmosphere.state.TemperatureState;
import top.qiguaiaaaa.geocraft.atmosphere.property.DefaultTemperature;
import top.qiguaiaaaa.geocraft.util.ChunkUtil;

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

    @Override
    public DefaultTemperature getProperty() {
        return DefaultTemperature.TEMPERATURE;
    }

    @Override
    public String getNBTTagKey() {
        return "temp";
    }

    public static float calculateBaseTemperature(Chunk chunk , UnderlyingLayer underlying){
        Biome mainBiome = ChunkUtil.getMainBiome(chunk);
        float biomeTemp = mainBiome.getTemperature(new BlockPos((chunk.x<<4)+8, underlying.getAltitude().get(),(chunk.z<<4)+8));
        if(biomeTemp <= 0.15){
            return TemperatureProperty.ICE_POINT +(biomeTemp*TEMPERATURE_MULTI)-TEMPERATURE_TRANSFER_OFFSET-10;
        }else{
            return TemperatureProperty.ICE_POINT +(biomeTemp*TEMPERATURE_MULTI)-TEMPERATURE_TRANSFER_OFFSET;
        }
    }
}
