package top.qiguaiaaaa.fluidgeography.api.atmosphere.state;

import net.minecraftforge.fluids.FluidRegistry;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.property.AtmosphereWater;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.property.AtmosphereProperty;

public class WaterState extends GasState {
    public WaterState(int amount) {
        super(FluidRegistry.WATER,amount);
    }

    @Override
    public AtmosphereProperty getProperty() {
        return AtmosphereWater.WATER;
    }

    @Override
    public String getNBTTagKey() {
        return "water";
    }
}
