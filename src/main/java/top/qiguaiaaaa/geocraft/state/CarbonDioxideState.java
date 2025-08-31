package top.qiguaiaaaa.geocraft.state;

import top.qiguaiaaaa.geocraft.api.GeoCraftProperties;
import top.qiguaiaaaa.geocraft.api.GeoCraftFluids;
import top.qiguaiaaaa.geocraft.api.property.FluidProperty;
import top.qiguaiaaaa.geocraft.api.state.FluidState;

public class CarbonDioxideState extends FluidState {
    public CarbonDioxideState(int amount) {
        super(GeoCraftFluids.CARBON_DIOXIDE, amount);
    }

    @Override
    public FluidProperty getProperty() {
        return GeoCraftProperties.CARBON_DIOXIDE;
    }

    @Override
    public String getNBTTagKey() {
        return null;
    }
}
