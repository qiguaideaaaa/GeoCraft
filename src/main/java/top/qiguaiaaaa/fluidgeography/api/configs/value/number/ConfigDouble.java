package top.qiguaiaaaa.fluidgeography.api.configs.value.number;

public class ConfigDouble extends ConfigNumber<Double>{
    public ConfigDouble(double value){
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
        return Double.toString(value);
    }

    @Override
    public ConfigDouble getInstanceByString(String content) {
        return new ConfigDouble(Double.parseDouble(content));
    }

    @Override
    public int compareTo(Double o) {
        return value.compareTo(o);
    }
}
