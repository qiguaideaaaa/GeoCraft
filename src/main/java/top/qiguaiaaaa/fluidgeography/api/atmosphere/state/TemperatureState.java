package top.qiguaiaaaa.fluidgeography.api.atmosphere.state;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagFloat;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.property.AtmosphereProperty;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.property.TemperatureProperty;

public abstract class TemperatureState implements IAtmosphereState{
    protected float temperature;
    public TemperatureState(float temp){
        this.temperature = temp;
    }

    /**
     * 获取温度值
     * @return 温度，单位为开尔文 K
     */
    public float get() {
        return temperature;
    }

    /**
     * 获取摄氏度单位下的温度
     * @return 温度，单位摄氏度
     */
    public final float getCelsius(){
        return temperature-TemperatureProperty.ICE_POINT;
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

    public void add热量(double Q, double 热容){
        double tempChange = Q/热容;
        this.add(tempChange);
    }

    @Override
    public abstract TemperatureProperty getProperty() ;

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

    @Override
    public String toString() {
        return temperature+"";
    }
}
