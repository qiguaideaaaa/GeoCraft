package top.qiguaiaaaa.geocraft.api.atmosphere.layer;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import top.qiguaiaaaa.geocraft.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.geocraft.api.property.GeographyProperty;
import top.qiguaiaaaa.geocraft.api.property.TemperatureProperty;
import top.qiguaiaaaa.geocraft.api.atmosphere.raypack.HeatPack;
import top.qiguaiaaaa.geocraft.api.state.GeographyState;
import top.qiguaiaaaa.geocraft.api.state.TemperatureState;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseLayer implements Layer{
    protected final Map<GeographyProperty, GeographyState> states = new HashMap<>();
    protected final Atmosphere atmosphere;
    protected Layer lowerLayer,upperLayer;

    public BaseLayer(Atmosphere atmosphere) {
        this.atmosphere = atmosphere;
    }

    @Override
    public boolean isInitialise() {
        for(GeographyState state:states.values()){
            if(!state.isInitialised()) return false;
        }
        return true;
    }

    @Override
    public void putHeat(double quanta, @Nullable BlockPos pos) {
        TemperatureState temperature = getTemperature();
        double capacity = getHeatCapacity();
        if(temperature.get()+quanta/capacity< TemperatureProperty.MIN){
            temperature.set(TemperatureProperty.MIN);
            return;
        }
        getTemperature().add热量(quanta,getHeatCapacity());
    }

    @Override
    public void sendHeat(HeatPack pack, @Nullable EnumFacing direction) {
        if(direction == null || pack.getType() == null){
            putHeat(pack.getAmount(),null);
            return;
        }
        sendHeat(pack,direction.getDirectionVec());
    }

    @Override
    public void sendHeat(HeatPack pack, @Nullable Vec3i direction) {
        if(direction == null || pack.getType() == null){
            putHeat(pack.getAmount(),null);
            return;
        }
        sendHeat(pack,new Vec3d(direction));
    }

    @Override
    public double drawHeat(double quanta,@Nullable BlockPos pos) {
        TemperatureState temperature = getTemperature();
        double capacity = getHeatCapacity();
        if(temperature.get()-quanta/capacity< TemperatureProperty.MIN){
            quanta = Math.max(temperature.get()- TemperatureProperty.MIN-0.1,0)/capacity;
            temperature.set(TemperatureProperty.MIN+0.1f);
            return quanta;
        }
        getTemperature().add热量(-quanta,getHeatCapacity());
        return quanta;
    }

    @Nullable
    @Override
    public Layer getLowerLayer() {
        return lowerLayer;
    }

    @Nullable
    @Override
    public Layer getUpperLayer() {
        return upperLayer;
    }

    @Override
    public void setLowerLayer(Layer layer) {
        if(layer == this) return;
        this.lowerLayer = layer;
    }

    @Override
    public void setUpperLayer(Layer layer) {
        if(layer == this) return;
        this.upperLayer = layer;
    }

    @Override
    public Atmosphere getAtmosphere() {
        return atmosphere;
    }

    @Nullable
    @Override
    public TemperatureState getTemperature(TemperatureProperty property) {
        final GeographyState state = states.get(property);
        if(state instanceof TemperatureState) return (TemperatureState) state;
        return null;
    }

    @Nullable
    @Override
    public GeographyState getState(GeographyProperty property) {
        return states.get(property);
    }

    @Nullable
    @Override
    public GeographyState addState(GeographyProperty property) {
        GeographyState oldState = getState(property);
        GeographyState newState = property.getStateInstance();
        states.put(property,newState);
        newState.initialise(this);
        return oldState;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        for(GeographyState state:states.values()){
            if(!state.toBeSavedIntoNBT()) continue;
            compound.setTag(state.getNBTTagKey(),state.serializeNBT());
        }
        return compound;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        for(GeographyState state:states.values()){
            if(!state.toBeLoadedFromNBT()) continue;
            state.deserializeNBT(nbt.getTag(state.getNBTTagKey()));
        }
    }
}
