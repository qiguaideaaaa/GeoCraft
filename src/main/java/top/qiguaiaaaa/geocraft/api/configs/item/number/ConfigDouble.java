package top.qiguaiaaaa.geocraft.api.configs.item.number;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import top.qiguaiaaaa.geocraft.api.configs.item.ConfigItem;

public class ConfigDouble extends ConfigItem<Double> {

    public ConfigDouble(String category, String configKey, Double defaultValue) {
        super(category, configKey, defaultValue);
    }

    public ConfigDouble(String category, String configKey, Double defaultValue, String comment) {
        super(category, configKey, defaultValue, comment);
    }

    public ConfigDouble(String category, String configKey, Double defaultValue, String comment, boolean isFinal) {
        super(category, configKey, defaultValue, comment, isFinal);
    }

    @Override
    public void load(Configuration config) {
        Property property = config.get(category,key,defaultValue,comment);
        load(property);
    }

    @Override
    protected void load(Property property) {
        this.value = property.getDouble(defaultValue);
    }
}
