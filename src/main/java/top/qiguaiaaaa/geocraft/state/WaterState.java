package top.qiguaiaaaa.geocraft.state;

import net.minecraftforge.fluids.FluidRegistry;
import top.qiguaiaaaa.geocraft.api.property.FluidProperty;
import top.qiguaiaaaa.geocraft.api.state.FluidState;
import top.qiguaiaaaa.geocraft.property.AtmosphereWater;

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
