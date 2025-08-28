package top.qiguaiaaaa.geocraft.api.configs.item.base;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import top.qiguaiaaaa.geocraft.api.configs.item.ConfigItem;

public class ConfigBoolean extends ConfigItem<Boolean> {

    public ConfigBoolean(String category, String configKey, Boolean defaultValue) {
        super(category, configKey, defaultValue);
    }

    public ConfigBoolean(String category, String configKey, Boolean defaultValue, String comment) {
        super(category, configKey, defaultValue, comment);
    }

    public ConfigBoolean(String category, String configKey, Boolean defaultValue, String comment, boolean isFinal) {
        super(category, configKey, defaultValue, comment, isFinal);
    }

    @Override
    public void load(Configuration config) {
        Property property = config.get(category,key,defaultValue,comment);
        load(property);
    }

    @Override
    protected void load(Property property) {
        this.value = property.getBoolean(defaultValue);
    }
}
