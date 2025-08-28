package top.qiguaiaaaa.geocraft.api.configs.value.map.entry;

import java.util.Map;

public class ConfigEntry<Key,Value> implements Map.Entry<Key, Value> {
    protected Key key;
    protected Value value;
    public ConfigEntry(Key key,Value value){
        this.key = key;
        this.value = value;
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
}
