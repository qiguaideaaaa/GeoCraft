package top.qiguaiaaaa.geocraft.api.configs.item.number;


import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import top.qiguaiaaaa.geocraft.api.configs.item.ConfigItem;

public class ConfigInteger extends ConfigItem<Integer> {

    public ConfigInteger(String category, String configKey, Integer defaultValue) {
        super(category, configKey, defaultValue);
    }

    public ConfigInteger(String category, String configKey, Integer defaultValue, String comment) {
        super(category, configKey, defaultValue, comment);
    }

    public ConfigInteger(String category, String configKey, Integer defaultValue, String comment, boolean isFinal) {
        super(category, configKey, defaultValue, comment, isFinal);
    }

    @Override
    public void load(Configuration config) {
        Property property = config.get(category,key,defaultValue,comment);
        load(property);
    }

    @Override
    protected void load(Property property) {
        this.value = property.getInt(defaultValue);
    }
}
