package top.qiguaiaaaa.geocraft.api.atmosphere.accessor;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import top.qiguaiaaaa.geocraft.api.atmosphere.system.IAtmosphereSystem;

public class AverageAtmosphereAccessor implements IAtmosphereAccessor{
    protected IAtmosphereSystem system;

    public void setSystem(IAtmosphereSystem system) {
        this.system = system;
    }

    @Override
    public IAtmosphereSystem getSystem() {
        return system;
    }

    @Override
    public boolean isAtmosphereLoaded(BlockPos pos, int radis) {
        return system.isAtmosphereLoaded(new ChunkPos(pos));
    }

    @Override
    public double getTemperature(BlockPos pos) {
        return 0;
    }

    @Override
    public double getPressure(BlockPos pos) {
        return 0;
    }

    @Override
    public double getWaterPressure(BlockPos pos) {
        return 0;
    }
}
