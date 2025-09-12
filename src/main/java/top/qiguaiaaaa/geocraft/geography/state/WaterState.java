package top.qiguaiaaaa.geocraft.geography.state;

import net.minecraftforge.fluids.FluidRegistry;
import top.qiguaiaaaa.geocraft.api.property.FluidProperty;
import top.qiguaiaaaa.geocraft.api.state.FluidState;
import top.qiguaiaaaa.geocraft.geography.property.AtmosphereWater;

import javax.annotation.Nonnull;

public class WaterState extends FluidState {
    public WaterState(int amount) {
        super(FluidRegistry.WATER,amount);
    }

    @Nonnull
    @Override
    public FluidProperty getProperty() {
        return AtmosphereWater.WATER;
    }

    @Nonnull
    @Override
    public String getNBTTagKey() {
        return "water";
    }
}
