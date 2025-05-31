package top.qiguaiaaaa.fluidgeography.api.configs.value.map;

import top.qiguaiaaaa.fluidgeography.api.configs.value.Configurable;
import top.qiguaiaaaa.fluidgeography.api.configs.value.map.entry.ConfigEntry;
import top.qiguaiaaaa.fluidgeography.api.configs.transfer.BaseTransfer;
import top.qiguaiaaaa.fluidgeography.api.configs.transfer.IConfigurableTransfer;

import java.util.*;

public class ConfigMap<K extends Configurable,V extends Configurable> extends AbstractMap<K,V> implements IConfigurableMap<K,V> {
    protected final Set<Entry<K,V>> entrySet = new HashSet<>();
    protected IConfigurableTransfer<ConfigEntry<K,V>> transfer;
    public ConfigMap(IConfigurableTransfer<ConfigEntry<K,V>> transfer){
        this.transfer = transfer;
    }
    @SafeVarargs
    public ConfigMap(ConfigEntry<K,V>... entries){
        this(Arrays.asList(entries));
    }
    public ConfigMap(List<ConfigEntry<K,V>> entries){
        this(entries,entries.size()>0?new BaseTransfer<>(entries.get(0)):null);
    }
    @SafeVarargs
    public ConfigMap(IConfigurableTransfer<ConfigEntry<K,V>> transfer, ConfigEntry<K,V>... entries){
        this(Arrays.asList(entries),transfer);
    }
    public ConfigMap(List<ConfigEntry<K,V>> entries, IConfigurableTransfer<ConfigEntry<K,V>> transfer){
        if(transfer == null) throw new IllegalArgumentException();
        entrySet.addAll(entries);
        this.transfer = transfer;
    }
    @Override
    public void setTransfer(IConfigurableTransfer<ConfigEntry<K, V>> transfer) {
        this.transfer = transfer;
    }

    @Override
    public IConfigurableTransfer<ConfigEntry<K, V>> getTransfer() {
        return transfer;
    }

    @Override
    public ConfigMap<K,V> getInstanceByStringArray(String[] contents) {
        List<ConfigEntry<K,V>> entries = new ArrayList<>();
        for(String s:contents){
            if(s == null) continue;
            ConfigEntry<K,V> entry = transfer.getByString(s);
            if(entry != null) entries.add(entry);
        }
        if(entries.isEmpty()){
            return new ConfigMap<>(transfer);
        }
        return new ConfigMap<>(entries,transfer);
    }

    @Override
    public ConfigMap<K,V> getInstanceByString(String content) {
        ConfigEntry<K,V> value = transfer.getByString(content);
        if(value == null) return new ConfigMap<>(transfer);
        return new ConfigMap<>(transfer,value);
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return entrySet;
    }
}