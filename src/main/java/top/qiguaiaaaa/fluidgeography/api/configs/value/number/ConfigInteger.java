package top.qiguaiaaaa.fluidgeography.api.configs.value.number;

import top.qiguaiaaaa.fluidgeography.api.configs.value.Configurable;

public class ConfigInteger extends ConfigNumber<Integer> {
    public ConfigInteger(int value){
        super(value);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
        if(obj instanceof ConfigDouble){
            return ((ConfigDouble) obj).doubleValue() == value;
        }else if(obj instanceof ConfigInteger){
            return ((ConfigInteger) obj).intValue() == value;
        }else if(obj instanceof ConfigLong){
            return ((ConfigLong) obj).longValue() == value;
        }
        return obj.equals(value);
    }

    @Override
    public String toString() {
        return Integer.toString(value);
    }

    @Override
    public Configurable getInstanceByString(String content) {
        return new ConfigInteger(Integer.parseInt(content));
    }

    @Override
    public int compareTo(Integer o) {
        return value.compareTo(o);
    }
}
