package top.qiguaiaaaa.fluidgeography.atmosphere.state;

import top.qiguaiaaaa.fluidgeography.api.FGFluids;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.property.AtmosphereProperty;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.state.GasState;

public class CarbonDioxideState extends GasState {
    public CarbonDioxideState(int amount) {
        super(FGFluids.CARBON_DIOXIDE, amount);
    }

    @Override
    public AtmosphereProperty getProperty() {
        return null;
    }

    @Override
    public String getNBTTagKey() {
        return null;
    }
}
