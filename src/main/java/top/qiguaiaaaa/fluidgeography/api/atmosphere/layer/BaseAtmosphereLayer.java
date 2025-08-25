package top.qiguaiaaaa.fluidgeography.api.atmosphere.layer;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.property.AtmosphereProperty;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.property.GasProperty;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.property.TemperatureProperty;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.raypack.HeatPack;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.state.GasState;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.state.IAtmosphereState;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.state.TemperatureState;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseAtmosphereLayer implements AtmosphereLayer{
    protected final Map<AtmosphereProperty, IAtmosphereState> states = new HashMap<>();
    protected final Atmosphere atmosphere;
    protected AtmosphereLayer lowerLayer,upperLayer;

    public BaseAtmosphereLayer(Atmosphere atmosphere) {
        this.atmosphere = atmosphere;
    }

    @Override
    public boolean isInitialise() {
        for(IAtmosphereState state:states.values()){
            if(!state.isInitialised()) return false;
        }
        return true;
    }

    @Override
    public void putHeat(double quanta, BlockPos pos) {
        getTemperature().add热量(quanta,getHeatCapacity());
    }

    @Override
    public void sendHeat(HeatPack pack, @Nullable EnumFacing direction) {
        if(direction == null || pack.getType() == null){
            putHeat(pack.getHeat(),null);
            return;
        }
        sendHeat(pack,direction.getDirectionVec());
    }

    @Override
    public void sendHeat(HeatPack pack, @Nullable Vec3i direction) {
        if(direction == null || pack.getType() == null){
            putHeat(pack.getHeat(),null);
            return;
        }
        sendHeat(pack,new Vec3d(direction));
    }

    @Nullable
    @Override
    public AtmosphereLayer getLowerLayer() {
        return lowerLayer;
    }

    @Nullable
    @Override
    public AtmosphereLayer getUpperLayer() {
        return upperLayer;
    }

    @Override
    public void setLowerLayer(AtmosphereLayer layer) {
        if(layer == this) return;
        this.lowerLayer = layer;
    }

    @Override
    public void setUpperLayer(AtmosphereLayer layer) {
        if(layer == this) return;
        this.upperLayer = layer;
    }

    @Override
    public Atmosphere getAtmosphere() {
        return atmosphere;
    }

    @Nullable
    @Override
    public GasState getGas(GasProperty property) {
        final IAtmosphereState state = states.get(property);
        if(state instanceof GasState) return (GasState) state;
        return null;
    }

    @Nullable
    @Override
    public TemperatureState getTemperature(TemperatureProperty property) {
        final IAtmosphereState state = states.get(property);
        if(state instanceof TemperatureState) return (TemperatureState) state;
        return null;
    }

    @Nullable
    @Override
    public IAtmosphereState getState(AtmosphereProperty property) {
        return states.get(property);
    }

    @Nullable
    @Override
    public IAtmosphereState addState(AtmosphereProperty property) {
        IAtmosphereState oldState = getState(property);
        IAtmosphereState newState = property.getStateInstance();
        states.put(property,newState);
        newState.initialise(this);
        return oldState;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        for(IAtmosphereState state:states.values()){
            if(!state.toBeSavedIntoNBT()) continue;
            compound.setTag(state.getNBTTagKey(),state.serializeNBT());
        }
        return compound;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        for(IAtmosphereState state:states.values()){
            if(!state.toBeLoadedFromNBT()) continue;
            state.deserializeNBT(nbt.getTag(state.getNBTTagKey()));
        }
    }
}
