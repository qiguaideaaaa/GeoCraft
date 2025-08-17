package top.qiguaiaaaa.fluidgeography.api.atmosphere.state;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.WorldInfo;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.Underlying;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.property.AtmosphereTemperature;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.property.AtmosphereProperty;
import top.qiguaiaaaa.fluidgeography.api.util.ChunkUtil;

import static top.qiguaiaaaa.fluidgeography.api.util.AtmosphereUtil.*;

public class TemperatureState extends AbstractTemperatureState {
    protected static final int TEMPERATURE_MULTI = 30;
    protected static final int TEMPERATURE_TRANSFER_OFFSET = 3;
    public TemperatureState(float temp){
        super(temp);
    }
    @Override
    public AtmosphereProperty getProperty() {
        return AtmosphereTemperature.TEMPERATURE;
    }

    @Override
    public String getNBTTagKey() {
        return "temp";
    }
    @Override
    public void onUpdate(Atmosphere atmosphere, Chunk chunk){
        if(atmosphere.getAtmosphereWorldInfo().isTemperatureConstant()) return;
        double temperatureChange = getTemperatureChange(atmosphere.getAtmosphereWorldInfo().getWorld(),atmosphere,atmosphere.get下垫面());
        this.add(temperatureChange);
    }
    @Deprecated
    public static double getTemperatureChange(WorldServer world, Atmosphere atmosphere, Underlying underlying){
        WorldInfo worldInfo = world.getWorldInfo();
        double cloudInsulationEffect = get大气透过率(atmosphere,worldInfo);
        double receiveQ = getSunEnergyPerChunk(worldInfo)*(1-underlying.平均返照率)*cloudInsulationEffect
                - getHeatEnergyRadiationLoss(atmosphere,cloudInsulationEffect);
        return receiveQ/(atmosphere.get低层大气热容()+underlying.热容);
    }

    public static float calculateBaseTemperature(Chunk chunk , Underlying underlying){
        Biome mainBiome = ChunkUtil.getMainBiome(chunk);
        float biomeTemp = mainBiome.getTemperature(new BlockPos((chunk.x<<4)+8, underlying.get地面平均海拔().get(),(chunk.z<<4)+8));
        if(biomeTemp <= 0.15){
            return AtmosphereTemperature.ICE_POINT +(biomeTemp*TEMPERATURE_MULTI)-TEMPERATURE_TRANSFER_OFFSET-10;
        }else{
            return AtmosphereTemperature.ICE_POINT +(biomeTemp*TEMPERATURE_MULTI)-TEMPERATURE_TRANSFER_OFFSET;
        }
    }
}
