package top.qiguaiaaaa.fluidgeography.api.atmosphere.property;

import top.qiguaiaaaa.fluidgeography.api.atmosphere.state.GroundTemperatureState;

public class GroundTemperature extends AtmosphereProperty{
    public static final GroundTemperature GROUND_TEMPERATURE = new GroundTemperature();

    @Override
    public GroundTemperatureState getStateInstance() {
        return new GroundTemperatureState(300);
    }
}
