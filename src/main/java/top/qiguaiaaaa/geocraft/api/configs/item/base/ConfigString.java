package top.qiguaiaaaa.geocraft.api.configs.item.base;

import net.minecraftforge.common.config.Property;
import top.qiguaiaaaa.geocraft.api.configs.item.ConfigItem;

public class ConfigString extends ConfigItem<String> {

    public ConfigString(String category, String configKey, String defaultValue) {
        super(category, configKey, defaultValue);
    }

    public ConfigString(String category, String configKey, String defaultValue, String comment) {
        super(category, configKey, defaultValue, comment);
    }

    public ConfigString(String category, String configKey, String defaultValue, String comment, boolean isFinal) {
        super(category, configKey, defaultValue, comment, isFinal);
    }

    @Override
    protected void load(Property property) {
        this.value = property.getString();
    }
}
