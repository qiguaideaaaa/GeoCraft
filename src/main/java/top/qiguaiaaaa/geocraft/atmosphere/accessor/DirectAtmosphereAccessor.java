package top.qiguaiaaaa.geocraft.atmosphere.accessor;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import top.qiguaiaaaa.geocraft.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.geocraft.api.atmosphere.accessor.IAtmosphereAccessor;
import top.qiguaiaaaa.geocraft.api.atmosphere.system.IAtmosphereSystem;
import top.qiguaiaaaa.geocraft.api.property.TemperatureProperty;

public class DirectAtmosphereAccessor implements IAtmosphereAccessor {
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
        return this.system.isAtmosphereLoaded(new ChunkPos(pos));
    }

    @Override
    public double getTemperature(BlockPos pos) {
        Atmosphere atmosphere = system.getAtmosphere(pos);
        return atmosphere==null? TemperatureProperty.UNAVAILABLE:atmosphere.getTemperature(pos,false);
    }

    @Override
    public double getPressure(BlockPos pos) {
        Atmosphere atmosphere = system.getAtmosphere(pos);
        return atmosphere==null? TemperatureProperty.UNAVAILABLE:atmosphere.getPressure(pos);
    }

    @Override
    public double getWaterPressure(BlockPos pos) {
        Atmosphere atmosphere = system.getAtmosphere(pos);
        return atmosphere==null? TemperatureProperty.UNAVAILABLE:atmosphere.getWaterPressure(pos);
    }
}
