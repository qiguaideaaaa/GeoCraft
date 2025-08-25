package top.qiguaiaaaa.fluidgeography.atmosphere.state;

import net.minecraftforge.fluids.FluidRegistry;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.property.GasProperty;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.state.GasState;
import top.qiguaiaaaa.fluidgeography.atmosphere.property.AtmosphereSteam;

public class SteamState extends GasState {
    public SteamState(int amount) {
        super(FluidRegistry.WATER, amount);
    }

    @Override
    public boolean isInitialised() {
        return true;
    }

    @Override
    public GasProperty getProperty() {
        return AtmosphereSteam.STEAM;
    }

    @Override
    public String getNBTTagKey() {
        return "steam";
    }
}
