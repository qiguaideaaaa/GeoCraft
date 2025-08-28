package top.qiguaiaaaa.geocraft.api.atmosphere.state;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagFloat;
import top.qiguaiaaaa.geocraft.api.GEOInfo;
import top.qiguaiaaaa.geocraft.api.atmosphere.property.TemperatureProperty;

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
            StackTraceElement[] 调用栈 = Thread.currentThread().getStackTrace();
            StackTraceElement 调用点 = 调用栈[2];
            GEOInfo.getLogger().warn("Someone want to set temp to very small at {}.{}:{}",调用点.getClassName(),调用点.getMethodName(),调用点.getLineNumber());
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
            StackTraceElement[] 调用栈 = Thread.currentThread().getStackTrace();
            StackTraceElement 调用点 = 调用栈[2];
            GEOInfo.getLogger().error("Someone want to add temp {} by Q= {} at {}.{}:{}",temperature,Q,调用点.getClassName(),调用点.getMethodName(),调用点.getLineNumber());
            throw new RuntimeException();
        }
        double tempChange = Q/热容;
        if(temperature+tempChange< TemperatureProperty.MIN){
            StackTraceElement[] 调用栈 = Thread.currentThread().getStackTrace();
            StackTraceElement 调用点 = 调用栈[2];
            GEOInfo.getLogger().error("Someone want to add temp to 0 from {} by {} at {}.{}:{}",temperature,tempChange,调用点.getClassName(),调用点.getMethodName(),调用点.getLineNumber());
            throw new RuntimeException();
        }
        if(Double.isInfinite(temperature+tempChange)){
            StackTraceElement[] 调用栈 = Thread.currentThread().getStackTrace();
            StackTraceElement 调用点 = 调用栈[2];
            GEOInfo.getLogger().error("Someone want to add temp to INFINITY from {} by {} at {}.{}:{}",temperature,tempChange,调用点.getClassName(),调用点.getMethodName(),调用点.getLineNumber());
            throw new RuntimeException();
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
        return temperature+"";
    }
}
