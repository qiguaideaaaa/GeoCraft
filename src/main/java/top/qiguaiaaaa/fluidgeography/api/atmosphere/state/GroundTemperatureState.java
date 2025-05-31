package top.qiguaiaaaa.fluidgeography.api.atmosphere.state;

import net.minecraft.world.chunk.Chunk;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.property.AtmosphereProperty;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.property.GroundTemperature;

public class GroundTemperatureState extends AbstractTemperatureState{
    public GroundTemperatureState(float temp) {
        super(temp);
    }

    @Override
    public void onUpdate(Atmosphere atmosphere, Chunk chunk) {

    }

    @Override
    public AtmosphereProperty getProperty() {
        return GroundTemperature.GROUND_TEMPERATURE;
    }

    @Override
    public String getNBTTagKey() {
        return "gtemp";
    }
}
