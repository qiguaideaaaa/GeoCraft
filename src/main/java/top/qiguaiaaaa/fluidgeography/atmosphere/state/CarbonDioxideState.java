package top.qiguaiaaaa.fluidgeography.atmosphere.state;

import top.qiguaiaaaa.fluidgeography.api.FGAtmosphereProperties;
import top.qiguaiaaaa.fluidgeography.api.FGFluids;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.property.FluidProperty;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.state.FluidState;

public class CarbonDioxideState extends FluidState {
    public CarbonDioxideState(int amount) {
        super(FGFluids.CARBON_DIOXIDE, amount);
    }

    @Override
    public FluidProperty getProperty() {
        return FGAtmosphereProperties.CARBON_DIOXIDE;
    }

    @Override
    public String getNBTTagKey() {
        return null;
    }
}
