package top.qiguaiaaaa.fluidgeography.api.configs.value.number;

import top.qiguaiaaaa.fluidgeography.api.configs.value.Configurable;

public abstract class ConfigNumber<Type extends Number> extends Number implements Configurable,Comparable<Type> {
    public final Type value;
    public ConfigNumber(Type number){
        value = number;
    }

    @Override
    public int intValue() {
        return value.intValue();
    }

    @Override
    public long longValue() {
        return value.longValue();
    }

    @Override
    public float floatValue() {
        return value.floatValue();
    }

    @Override
    public double doubleValue() {
        return value.doubleValue();
    }
}
