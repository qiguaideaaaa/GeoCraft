package top.qiguaiaaaa.geocraft.api.configs.item.number;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import top.qiguaiaaaa.geocraft.api.configs.item.ConfigItem;

public class ConfigLong extends ConfigItem<Long> {
    public ConfigLong(String category, String configKey, Long defaultValue) {
        super(category, configKey, defaultValue);
    }

    public ConfigLong(String category, String configKey, Long defaultValue, String comment) {
        super(category, configKey, defaultValue, comment);
    }

    public ConfigLong(String category, String configKey, Long defaultValue, String comment, boolean isFinal) {
        super(category, configKey, defaultValue, comment, isFinal);
    }

    @Override
    public void load(Configuration config) {
        Property property = config.get(category,key,defaultValue,comment);
        load(property);
    }

    @Override
    protected void load(Property property) {
        this.value = property.getLong(defaultValue);
    }
}
