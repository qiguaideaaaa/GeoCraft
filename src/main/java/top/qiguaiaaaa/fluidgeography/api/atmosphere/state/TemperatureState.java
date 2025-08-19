package top.qiguaiaaaa.fluidgeography.api.atmosphere.state;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagFloat;

public abstract class TemperatureState implements IAtmosphereState{
    protected float temperature;
    public TemperatureState(float temp){
        this.temperature = temp;
    }

    public float get() {
        return temperature;
    }

    public void set(float temperature) {
        if(temperature <0) temperature = 0;
        this.temperature = temperature;
    }
    public void set(TemperatureState temp){
        this.temperature = temp.temperature;
    }

    public void add(double temp){
        this.temperature += temp;
        if(temperature<0) temperature = 0;
    }

    public void add热量(double Q, long 热容){
        double tempChange = Q/热容;
        this.add(tempChange);
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
