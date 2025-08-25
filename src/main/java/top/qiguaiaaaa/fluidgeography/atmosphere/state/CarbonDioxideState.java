package top.qiguaiaaaa.fluidgeography.atmosphere.state;

import top.qiguaiaaaa.fluidgeography.api.FGAtmosphereProperties;
import top.qiguaiaaaa.fluidgeography.api.FGFluids;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.property.AtmosphereProperty;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.property.GasProperty;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.state.GasState;

public class CarbonDioxideState extends GasState {
    public CarbonDioxideState(int amount) {
        super(FGFluids.CARBON_DIOXIDE, amount);
    }

    @Override
    public boolean isInitialised() {
        return true;
    }

    @Override
    public GasProperty getProperty() {
        return FGAtmosphereProperties.CARBON_DIOXIDE;
    }

    @Override
    public String getNBTTagKey() {
        return null;
    }
}
