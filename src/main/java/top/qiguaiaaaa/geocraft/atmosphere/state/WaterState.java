package top.qiguaiaaaa.geocraft.atmosphere.state;

import net.minecraftforge.fluids.FluidRegistry;
import top.qiguaiaaaa.geocraft.api.atmosphere.property.FluidProperty;
import top.qiguaiaaaa.geocraft.api.atmosphere.state.FluidState;
import top.qiguaiaaaa.geocraft.atmosphere.property.AtmosphereWater;

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
