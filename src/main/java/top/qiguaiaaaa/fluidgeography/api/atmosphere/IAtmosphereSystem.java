package top.qiguaiaaaa.fluidgeography.api.atmosphere;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;

import javax.annotation.Nullable;

public interface IAtmosphereSystem {
    void updateTick();
    void setStop(boolean status);
    boolean isStopped();
    @Nullable
    Atmosphere getAtmosphere(BlockPos pos);
    @Nullable
    Atmosphere getAtmosphere(Chunk chunk);
    AtmosphereWorldInfo getAtmosphereWorldInfo();
}
