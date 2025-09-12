package top.qiguaiaaaa.geocraft.api.configs.item.base;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import top.qiguaiaaaa.geocraft.api.configs.item.ConfigItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * {@link Boolean}配置项
 */
public class ConfigBoolean extends ConfigItem<Boolean> {
    /**
     * @see #ConfigBoolean(String, String, boolean, String, boolean)
     */
    public ConfigBoolean(@Nonnull String category,@Nonnull String configKey, boolean defaultValue) {
        super(category, configKey, defaultValue);
    }

    /**
     * @see #ConfigBoolean(String, String, boolean, String, boolean)
     */
    public ConfigBoolean(@Nonnull String category,@Nonnull String configKey, boolean defaultValue,@Nullable String comment) {
        super(category, configKey, defaultValue, comment);
    }

    /**
     * 创建一个Boolean类型配置项
     * @param category 配置所在目录
     * @param configKey 配置的key
     * @param defaultValue 配置的默认值
     * @param comment 配置的注释
     * @param isFinal 配置是否在初始化后不可更改
     */
    public ConfigBoolean(@Nonnull String category,@Nonnull String configKey, boolean defaultValue,@Nullable String comment, boolean isFinal) {
        super(category, configKey, defaultValue, comment, isFinal);
    }

    /**
     * {@inheritDoc}
     * @param config {@inheritDoc}
     */
    @Override
    public void load(@Nonnull Configuration config) {
        Property property = config.get(category,key,defaultValue,comment);
        load(property);
    }

    /**
     * {@inheritDoc}
     * @param property {@inheritDoc}
     */
    @Override
    protected void load(@Nonnull Property property) {
        this.value = property.getBoolean(defaultValue);
    }
}
