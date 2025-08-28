package top.qiguaiaaaa.geocraft.configs.item.collection;

import net.minecraftforge.common.config.Property;
import top.qiguaiaaaa.geocraft.GeoCraft;
import top.qiguaiaaaa.geocraft.api.configs.item.ConfigItem;
import top.qiguaiaaaa.geocraft.api.configs.value.collection.ConfigurableHashSet;

import java.util.function.Function;

public class ConfigSet<ValueType> extends ConfigItem<ConfigurableHashSet<ValueType>> {
    protected final Function<String,ValueType> parser;

    public ConfigSet(String category, String configKey, ConfigurableHashSet<ValueType> defaultValue, Function<String,ValueType> parser) {
        this(category, configKey, defaultValue,null,parser);
    }

    public ConfigSet(String category, String configKey, ConfigurableHashSet<ValueType> defaultValue, String comment, Function<String,ValueType> parser) {
        this(category,configKey,defaultValue,comment,parser,false);
    }

    public ConfigSet(String category, String configKey, ConfigurableHashSet<ValueType> defaultValue, String comment, Function<String,ValueType> parser, boolean isFinal) {
        super(category, configKey, defaultValue, comment, isFinal);
        this.parser = parser;
    }

    @Override
    protected void load(Property property) {
        value = new ConfigurableHashSet<>();
        String[] strings = property.getStringList();
        for(String string:strings){
            try {
                ValueType loadedVal = parser.apply(string);
                value.add(loadedVal);
            }catch (Throwable e){
                GeoCraft.getLogger().warn("loading configuration {} in {} error",string,category);
                GeoCraft.getLogger().warn("Error Detailed:",e);
            }
        }
    }
}
