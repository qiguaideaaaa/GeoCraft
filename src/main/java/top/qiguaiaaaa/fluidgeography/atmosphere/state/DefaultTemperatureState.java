package top.qiguaiaaaa.fluidgeography.atmosphere.state;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.layer.UnderlyingLayer;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.property.TemperatureProperty;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.state.TemperatureState;
import top.qiguaiaaaa.fluidgeography.atmosphere.property.DefaultTemperature;
import top.qiguaiaaaa.fluidgeography.api.util.ChunkUtil;

public class DefaultTemperatureState extends TemperatureState {
    protected static final int TEMPERATURE_MULTI = 30;
    protected static final int TEMPERATURE_TRANSFER_OFFSET = 3;
    public DefaultTemperatureState(float temp){
        super(temp);
    }

    @Override
    public boolean isInitialised() {
        return temperature>0;
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
