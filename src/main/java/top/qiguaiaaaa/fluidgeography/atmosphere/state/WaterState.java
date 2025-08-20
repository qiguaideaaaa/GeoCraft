package top.qiguaiaaaa.fluidgeography.atmosphere.state;

import net.minecraftforge.fluids.FluidRegistry;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.property.GasProperty;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.state.GasState;
import top.qiguaiaaaa.fluidgeography.atmosphere.property.AtmosphereWater;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.property.AtmosphereProperty;

public class WaterState extends GasState {
    public WaterState(int amount) {
        super(FluidRegistry.WATER,amount);
    }

    @Override
    public GasProperty getProperty() {
        return AtmosphereWater.WATER;
    }

    @Override
    public String getNBTTagKey() {
        return "water";
    }
}
