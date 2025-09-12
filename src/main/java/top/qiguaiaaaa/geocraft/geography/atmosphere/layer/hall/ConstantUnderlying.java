package top.qiguaiaaaa.geocraft.geography.atmosphere.layer.hall;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.chunk.Chunk;
import org.apache.commons.lang3.tuple.Triple;
import top.qiguaiaaaa.geocraft.api.GeoCraftProperties;
import top.qiguaiaaaa.geocraft.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.geocraft.api.atmosphere.layer.UnderlyingLayer;
import top.qiguaiaaaa.geocraft.api.atmosphere.raypack.HeatPack;
import top.qiguaiaaaa.geocraft.api.state.TemperatureState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

public class ConstantUnderlying extends UnderlyingLayer {
    protected final TemperatureState temperature = GeoCraftProperties.FINAL_TEMPERATURE.getStateInstance();
    public ConstantUnderlying(Atmosphere atmosphere) {
        super(atmosphere);
        this.heatCapacity = (long) 1e10;
    }

    @Override
    public void sendHeat(@Nonnull HeatPack pack, @Nullable EnumFacing direction) {}

    @Override
    public void sendHeat(@Nonnull HeatPack pack, @Nullable Vec3i direction) {}

    @Override
    public double drawHeat(double quanta, @Nullable BlockPos pos) {
        return quanta;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        return new NBTTagCompound();
    }

    @Override
    public void tick(@Nullable Chunk chunk, @Nonnull Map<EnumFacing, Triple<Atmosphere, Chunk, EnumFacing>> neighbors, int x, int z) {}

    @Override
    public void sendHeat(@Nonnull HeatPack pack, @Nullable Vec3d direction) {}

    @Override
    public TemperatureState getTemperature() {
        return temperature;
    }

    @Override
    public float getTemperature(BlockPos pos) {
        return temperature.get();
    }

    @Override
    public String getTagName() {
        return "fg";
    }

    @Override
    public boolean isSerializable() {
        return false;
    }

    @Override
    public UnderlyingLayer load(@Nonnull Chunk chunk) {
        return this;
    }

    @Override
    public boolean isInitialise() {
        return true;
    }

    @Override
    public void putHeat(double quanta, @Nullable BlockPos pos) {}
}
