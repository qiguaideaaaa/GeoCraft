package top.qiguaiaaaa.geocraft.atmosphere.state;

import top.qiguaiaaaa.geocraft.api.GEOProperties;
import top.qiguaiaaaa.geocraft.api.GEOFluids;
import top.qiguaiaaaa.geocraft.api.atmosphere.property.FluidProperty;
import top.qiguaiaaaa.geocraft.api.atmosphere.state.FluidState;

public class CarbonDioxideState extends FluidState {
    public CarbonDioxideState(int amount) {
        super(GEOFluids.CARBON_DIOXIDE, amount);
    }

    @Override
    public FluidProperty getProperty() {
        return GEOProperties.CARBON_DIOXIDE;
    }

    @Override
    public String getNBTTagKey() {
        return null;
    }
}
