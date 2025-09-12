package top.qiguaiaaaa.geocraft.geography.state;

import net.minecraftforge.fluids.FluidRegistry;
import top.qiguaiaaaa.geocraft.api.property.FluidProperty;
import top.qiguaiaaaa.geocraft.api.state.FluidState;
import top.qiguaiaaaa.geocraft.geography.property.AtmosphereSteam;

import javax.annotation.Nonnull;

public class SteamState extends FluidState {
    public SteamState(int amount) {
        super(FluidRegistry.WATER, amount);
    }

    @Nonnull
    @Override
    public FluidProperty getProperty() {
        return AtmosphereSteam.STEAM;
    }

    @Nonnull
    @Override
    public String getNBTTagKey() {
        return "steam";
    }
}
