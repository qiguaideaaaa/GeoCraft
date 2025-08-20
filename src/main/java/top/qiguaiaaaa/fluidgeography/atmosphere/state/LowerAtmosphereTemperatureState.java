package top.qiguaiaaaa.fluidgeography.atmosphere.state;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.WorldInfo;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.Underlying;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.property.TemperatureProperty;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.state.TemperatureState;
import top.qiguaiaaaa.fluidgeography.atmosphere.property.LowerAtmosphereTemperature;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.property.AtmosphereProperty;
import top.qiguaiaaaa.fluidgeography.api.util.ChunkUtil;

import static top.qiguaiaaaa.fluidgeography.api.util.AtmosphereUtil.*;

public class LowerAtmosphereTemperatureState extends TemperatureState {
    protected static final int TEMPERATURE_MULTI = 30;
    protected static final int TEMPERATURE_TRANSFER_OFFSET = 3;
    public LowerAtmosphereTemperatureState(float temp){
        super(temp);
    }
    @Override
    public LowerAtmosphereTemperature getProperty() {
        return LowerAtmosphereTemperature.TEMPERATURE;
    }

    @Override
    public String getNBTTagKey() {
        return "temp";
    }

    public static float calculateBaseTemperature(Chunk chunk , Underlying underlying){
        Biome mainBiome = ChunkUtil.getMainBiome(chunk);
        float biomeTemp = mainBiome.getTemperature(new BlockPos((chunk.x<<4)+8, underlying.get地面平均海拔().get(),(chunk.z<<4)+8));
        if(biomeTemp <= 0.15){
            return TemperatureProperty.ICE_POINT +(biomeTemp*TEMPERATURE_MULTI)-TEMPERATURE_TRANSFER_OFFSET-10;
        }else{
            return TemperatureProperty.ICE_POINT +(biomeTemp*TEMPERATURE_MULTI)-TEMPERATURE_TRANSFER_OFFSET;
        }
    }
}
