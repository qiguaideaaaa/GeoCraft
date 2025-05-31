package top.qiguaiaaaa.fluidgeography.api.configs.value.base;

import top.qiguaiaaaa.fluidgeography.api.configs.value.Configurable;

public class ConfigBoolean implements Configurable,Comparable<Boolean> {
    public final boolean value;
    public ConfigBoolean(boolean value){
        this.value = value;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
        if(obj instanceof ConfigBoolean)
            return ((ConfigBoolean) obj).value == value;
        return obj.equals(value);
    }

    @Override
    public String toString() {
        return Boolean.toString(value);
    }

    @Override
    public ConfigBoolean getInstanceByString(String content) {
        return new ConfigBoolean(Boolean.parseBoolean(content));
    }

    @Override
    public int compareTo(Boolean o) {
        return Boolean.valueOf(value).compareTo(o);
    }
}
