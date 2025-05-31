package top.qiguaiaaaa.fluidgeography.api.configs.item;

import top.qiguaiaaaa.fluidgeography.api.configs.value.Configurable;
import top.qiguaiaaaa.fluidgeography.api.configs.value.map.ConfigMap;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class MapConfigItem<K extends Configurable,V extends Configurable> extends ConfigItem<ConfigMap<K,V>> implements Map<K,V> {
    public MapConfigItem(String category, String configKey, ConfigMap<K, V> defaultValue) {
        super(category, configKey, defaultValue);
    }

    public MapConfigItem(String category, String configKey, ConfigMap<K, V> defaultValue, String comment) {
        super(category, configKey, defaultValue, comment);
    }

    public MapConfigItem(String category, String configKey, ConfigMap<K, V> defaultValue, String comment, boolean isFinal) {
        super(category, configKey, defaultValue, comment, isFinal);
    }

    @Override
    public int size() {
        return value.size();
    }

    @Override
    public boolean isEmpty() {
        return value.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return value.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return this.value.containsValue(value);
    }

    @Override
    public V get(Object key) {
        return this.value.get(key);
    }

    @Override
    public V put(K key, V value) {
        return this.value.put(key,value);
    }

    @Override
    public V remove(Object key) {
        return this.value.remove(key);
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        this.value.putAll(m);
    }

    @Override
    public void clear() {
        this.value.clear();
    }

    @Override
    public Set<K> keySet() {
        return this.value.keySet();
    }

    @Override
    public Collection<V> values() {
        return this.value.values();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return this.value.entrySet();
    }
}
