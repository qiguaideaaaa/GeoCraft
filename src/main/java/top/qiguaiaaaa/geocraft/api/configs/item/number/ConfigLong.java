package top.qiguaiaaaa.geocraft.api.configs.item.number;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import top.qiguaiaaaa.geocraft.api.configs.item.ConfigItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * {@link Long}配置项
 */
public class ConfigLong extends ConfigItem<Long> {
    /**
     * @see #ConfigLong(String, String, long, String, boolean)
     */
    public ConfigLong(@Nonnull String category,@Nonnull String configKey, long defaultValue) {
        super(category, configKey, defaultValue);
    }

    /**
     * @see #ConfigLong(String, String, long, String, boolean)
     */
    public ConfigLong(@Nonnull String category,@Nonnull String configKey, long defaultValue,@Nullable String comment) {
        super(category, configKey, defaultValue, comment);
    }

    /**
     * 创建一个Long类型配置项
     * @param category 配置所在目录
     * @param configKey 配置的key
     * @param defaultValue 配置的默认值
     * @param comment 配置的注释
     * @param isFinal 配置是否在初始化后不可更改
     */
    public ConfigLong(@Nonnull String category,@Nonnull String configKey, long defaultValue,@Nullable String comment, boolean isFinal) {
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
        this.value = property.getLong(defaultValue);
    }
}
