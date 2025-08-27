package top.qiguaiaaaa.fluidgeography.atmosphere.state;

import net.minecraftforge.fluids.FluidRegistry;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.property.FluidProperty;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.state.FluidState;
import top.qiguaiaaaa.fluidgeography.atmosphere.property.AtmosphereWater;

public class WaterState extends FluidState {
    public WaterState(int amount) {
        super(FluidRegistry.WATER,amount);
    }

    @Override
    public FluidProperty getProperty() {
        return AtmosphereWater.WATER;
    }

    @Override
    public String getNBTTagKey() {
        return "water";
    }
}
