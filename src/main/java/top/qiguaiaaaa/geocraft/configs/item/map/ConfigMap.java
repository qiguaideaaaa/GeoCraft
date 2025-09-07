package top.qiguaiaaaa.geocraft.configs.item.map;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import top.qiguaiaaaa.geocraft.GeoCraft;
import top.qiguaiaaaa.geocraft.api.configs.value.map.ConfigurableLinkedHashMap;
import top.qiguaiaaaa.geocraft.api.configs.value.map.entry.ConfigEntry;
import top.qiguaiaaaa.geocraft.api.configs.item.ConfigItem;

import java.util.function.Function;

public class ConfigMap<K,V> extends ConfigItem<ConfigurableLinkedHashMap<K,V>> {

    protected final Function<String,K> parserK;
    protected final Function<String,V> parserV;

    @SafeVarargs
    public ConfigMap(String category, String configKey, Function<String,K> parserK, Function<String,V> parserV, ConfigEntry<K,V>... entries) {
        this(category,configKey,null,parserK,parserV,entries);
    }

    @SafeVarargs
    public ConfigMap(String category, String configKey, String comment, Function<String,K> parserK, Function<String,V> parserV, ConfigEntry<K,V>... entries) {
        this(category,configKey,comment,parserK,parserV,false,entries);
    }

    @SafeVarargs
    public ConfigMap(String category, String configKey, String comment, Function<String,K> parserK, Function<String,V> parserV, boolean isFinal, ConfigEntry<K,V>... entries) {
        super(category, configKey, new ConfigurableLinkedHashMap<>(), comment, isFinal);
        this.parserK = parserK;
        this.parserV = parserV;
        for(ConfigEntry<K,V> entry:entries){
            if(entry == null) continue;
            defaultValue.put(entry.getKey(),entry.getValue());
        }
    }

    @Override
    public void load(Configuration config) {
        Property property = config.get(category,key,defaultValue.toStringList(),comment);
        load(property);
    }

    @Override
    protected void load(Property property) {
        value = new ConfigurableLinkedHashMap<>();
        String[] strings = property.getStringList();
        for(String content:strings){
            String[] spilt = content.trim().split(ConfigurableLinkedHashMap.SPLIT,2);
            if(spilt.length<2){
                GeoCraft.getLogger().warn("loading configuration {} error: {} is not valid key-value pair",category,content);
                continue;
            }
            try{
                K k = parserK.apply(spilt[0]);
                V v = parserV.apply(spilt[1]);
                if(k == null || v == null) continue;
                this.value.put(k,v);
            }catch (Throwable e){
                GeoCraft.getLogger().warn("loading configuration {} in {} error",content,category);
                GeoCraft.getLogger().warn("Error Detailed:",e);
            }
        }
    }
}