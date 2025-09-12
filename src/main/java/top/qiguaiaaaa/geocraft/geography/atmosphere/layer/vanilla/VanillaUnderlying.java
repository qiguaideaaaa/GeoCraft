package top.qiguaiaaaa.geocraft.geography.atmosphere.layer.vanilla;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import org.apache.commons.lang3.tuple.Triple;
import top.qiguaiaaaa.geocraft.api.GeoCraftProperties;
import top.qiguaiaaaa.geocraft.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.geocraft.api.atmosphere.layer.UnderlyingLayer;
import top.qiguaiaaaa.geocraft.api.atmosphere.raypack.HeatPack;
import top.qiguaiaaaa.geocraft.api.state.TemperatureState;
import top.qiguaiaaaa.geocraft.api.util.math.Altitude;
import top.qiguaiaaaa.geocraft.geography.atmosphere.VanillaAtmosphere;
import top.qiguaiaaaa.geocraft.geography.property.AltitudeProperty;
import top.qiguaiaaaa.geocraft.geography.state.AltitudeState;
import top.qiguaiaaaa.geocraft.geography.state.DefaultTemperatureState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public class VanillaUnderlying extends UnderlyingLayer {
    protected final TemperatureState temperature = GeoCraftProperties.TEMPERATURE.getStateInstance();
    protected final AltitudeState altitudeState = new AltitudeState(altitude);
    public VanillaUnderlying(VanillaAtmosphere atmosphere) {
        super(atmosphere);
        heatCapacity = (long)2e8;
        altitude.set(AltitudeProperty.UNAVAILABLE);
        states.put(GeoCraftProperties.TEMPERATURE,temperature);
        states.put(altitudeState.getProperty(),altitudeState);
    }

    @Override
    public void putHeat(double quanta, @Nullable BlockPos pos) {}

    @Override
    public void sendHeat(@Nonnull HeatPack pack, @Nullable Vec3d direction) {
        if(direction == null) return;
        if(direction.y >0){
            if(upperLayer == null) return;
            upperLayer.sendHeat(pack,direction);
        }
    }

    @Override
    public double drawHeat(double quanta, @Nullable BlockPos pos) {
        return quanta;
    }

    @Override
    public void tick(@Nullable Chunk chunk, @Nonnull Map<EnumFacing, Triple<Atmosphere, Chunk, EnumFacing>> neighbors, int x, int z) {}

    @Override
    public TemperatureState getTemperature() {
        return temperature;
    }

    @Override
    public float getTemperature(BlockPos pos) {
        if(atmosphere.getAtmosphereWorldInfo().getWorld().isBlockLoaded(pos)){
            Biome curBiome = atmosphere.getAtmosphereWorldInfo().getWorld().getBiome(pos);
            return DefaultTemperatureState.toRealTemperature(curBiome.getTemperature(pos));
        }
        return DefaultTemperatureState.toRealTemperature(((VanillaAtmosphere)atmosphere).getBiome().getTemperature(pos));
    }

    @Override
    public String getTagName() {
        return "vg";
    }

    @Override
    public boolean isSerializable() {
        return true;
    }

    @Override
    public UnderlyingLayer load(@Nonnull Chunk chunk) {
        temperature.set(DefaultTemperatureState.calculateBaseTemperature(chunk,this));
        return this;
    }

    @Override
    public void onLoad(@Nonnull Chunk chunk) {
        setAltitude(Altitude.getMiddleHeight(chunk));
        super.onLoad(chunk);
    }
}
