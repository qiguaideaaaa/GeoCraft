package top.qiguaiaaaa.fluidgeography.atmosphere.state;

import net.minecraftforge.fluids.FluidRegistry;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.property.FluidProperty;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.state.FluidState;
import top.qiguaiaaaa.fluidgeography.atmosphere.property.AtmosphereSteam;

public class SteamState extends FluidState {
    public SteamState(int amount) {
        super(FluidRegistry.WATER, amount);
    }

    @Override
    public FluidProperty getProperty() {
        return AtmosphereSteam.STEAM;
    }

    @Override
    public String getNBTTagKey() {
        return "steam";
    }
}
