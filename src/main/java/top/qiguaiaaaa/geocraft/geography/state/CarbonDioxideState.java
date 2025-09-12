package top.qiguaiaaaa.geocraft.geography.state;

import top.qiguaiaaaa.geocraft.api.GeoCraftProperties;
import top.qiguaiaaaa.geocraft.api.GeoCraftFluids;
import top.qiguaiaaaa.geocraft.api.property.FluidProperty;
import top.qiguaiaaaa.geocraft.api.state.FluidState;

import javax.annotation.Nonnull;

public class CarbonDioxideState extends FluidState {
    public CarbonDioxideState(int amount) {
        super(GeoCraftFluids.CARBON_DIOXIDE, amount);
    }

    @Nonnull
    @Override
    public FluidProperty getProperty() {
        return GeoCraftProperties.CARBON_DIOXIDE;
    }

    @Nonnull
    @Override
    public String getNBTTagKey() {
        return null;
    }
}
