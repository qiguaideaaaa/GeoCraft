package top.qiguaiaaaa.fluidgeography.atmosphere.state;

import top.qiguaiaaaa.fluidgeography.api.atmosphere.property.AtmosphereProperty;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.state.TemperatureState;
import top.qiguaiaaaa.fluidgeography.atmosphere.property.GroundTemperature;

public class GroundTemperatureState extends TemperatureState {
    public GroundTemperatureState(float temp) {
        super(temp);
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
