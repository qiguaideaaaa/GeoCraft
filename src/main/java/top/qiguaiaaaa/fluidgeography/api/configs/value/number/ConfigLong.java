package top.qiguaiaaaa.fluidgeography.api.configs.value.number;

public class ConfigLong extends ConfigNumber<Long>{
    public ConfigLong(long value){
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
        return Long.toString(value);
    }

    @Override
    public ConfigLong getInstanceByString(String content) {
        return new ConfigLong(Long.parseLong(content));
    }

    @Override
    public int compareTo(Long o) {
        return value.compareTo(o);
    }
}
