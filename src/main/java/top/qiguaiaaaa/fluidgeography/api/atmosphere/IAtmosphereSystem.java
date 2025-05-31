package top.qiguaiaaaa.fluidgeography.api.atmosphere;

import net.minecraft.util.math.BlockPos;

import javax.annotation.Nullable;

public interface IAtmosphereSystem {
    void updateTick();
    @Nullable
    Atmosphere getAtmosphere(BlockPos pos);
    AtmosphereWorldInfo getAtmosphereWorldInfo();
}
