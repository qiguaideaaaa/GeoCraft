package top.qiguaiaaaa.fluidgeography.api.configs.value.map.entry;

import top.qiguaiaaaa.fluidgeography.api.configs.value.Configurable;

import java.util.Map;

public class ConfigEntry<Key extends Configurable,Value extends Configurable> implements Configurable, Map.Entry<Key, Value> {
    protected Key key;
    protected Value value;
    public ConfigEntry(Key key,Value value){
        this.key = key;
        this.value = value;
    }
    @SuppressWarnings("unchecked")
    @Override
    public Configurable getInstanceByString(String content) {
        String[] spilt = content.trim().split(";",2);
        if(spilt.length<2) return null;
        try{
            Key k = (Key) key.getInstanceByString(spilt[0]);
            Value v = (Value) value.getInstanceByString(spilt[1]);
            if(k == null || v == null) return null;
            return new ConfigEntry<>(k,v);
        }catch (Throwable t){
            return null;
        }
    }

    @Override
    public Key getKey() {
        return key;
    }

    @Override
    public Value getValue() {
        return value;
    }

    @Override
    public Value setValue(Value value) {
        return value;
    }

    @Override
    public String toString() {
        return key.toString()+";"+value.toString();
    }
}
