package top.qiguaiaaaa.geocraft.api.state;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagFloat;
import top.qiguaiaaaa.geocraft.api.property.TemperatureProperty;
import top.qiguaiaaaa.geocraft.api.util.APIUtil;

import static top.qiguaiaaaa.geocraft.api.util.APIUtil.LOGGER;

public abstract class TemperatureState implements GeographyState {
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
        if(temperature < TemperatureProperty.MIN){
            LOGGER.warn("{} wants to set temp to very small.", APIUtil.callerInfo(1));
            temperature = 3;
        }
        this.temperature = temperature;
    }
    public void set(TemperatureState temp){
        set(temp.temperature);
    }

    public void add(double temp){
        this.temperature += temp;
    }

    public void add热量(double Q, double 热容){
        if(Double.isInfinite(Q) || Double.isNaN(Q)){
            LOGGER.warn("{} wants to add a temperature state from {} K by {} FE",APIUtil.callerInfo(1),temperature,Q);
            return;
        }
        double tempChange = Q/热容;
        double res = temperature+tempChange;
        if(res< TemperatureProperty.MIN){
            LOGGER.warn("{} wants to add a temperature state to {} K from {} K by {} FE, min temperature is {}"
                    ,APIUtil.callerInfo(1),res,temperature,Q,TemperatureProperty.MIN);
            return;
        }else if(Double.isInfinite(res)){
            LOGGER.warn("{} wants to add a temperature state to {} from {} K by {} FE"
                    ,APIUtil.callerInfo(1),res,temperature,Q);
            return;
        }
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
        return Float.toString(temperature);
    }
}
