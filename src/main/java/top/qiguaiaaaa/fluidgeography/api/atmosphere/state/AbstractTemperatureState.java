package top.qiguaiaaaa.fluidgeography.api.atmosphere.state;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagFloat;

public abstract class AbstractTemperatureState implements IAtmosphereState{
    protected float temperature;
    public AbstractTemperatureState(float temp){
        this.temperature = temp;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        if(temperature <0) temperature = 0;
        this.temperature = temperature;
    }
    public void setTemperature(AbstractTemperatureState temperature){
        this.temperature = temperature.getTemperature();
    }

    public void addTemperature(double temp){
        this.temperature += temp;
        if(temperature<0) temperature = 0;
    }

    public void addHeatQuantity(double Q,long heatCapacity){
        double tempChange = Q/heatCapacity;
        this.addTemperature(tempChange);
    }

    @Override
    public NBTTagFloat serializeNBT() {
        return new NBTTagFloat(temperature);
    }

    @Override
    public void deserializeNBT(NBTBase nbt) {
        if(nbt instanceof NBTPrimitive){
            this.temperature = ((NBTPrimitive) nbt).getFloat();
        }
    }

}
