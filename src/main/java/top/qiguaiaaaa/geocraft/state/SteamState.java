package top.qiguaiaaaa.geocraft.state;

import net.minecraftforge.fluids.FluidRegistry;
import top.qiguaiaaaa.geocraft.api.property.FluidProperty;
import top.qiguaiaaaa.geocraft.api.state.FluidState;
import top.qiguaiaaaa.geocraft.property.AtmosphereSteam;

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
