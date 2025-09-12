package top.qiguaiaaaa.geocraft.api.configs.item.base;

import net.minecraftforge.common.config.Property;
import top.qiguaiaaaa.geocraft.api.configs.item.ConfigItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * {@link String}配置项
 */
public class ConfigString extends ConfigItem<String> {
    /**
     * @see #ConfigString(String, String, String, String, boolean)
     */
    public ConfigString(@Nonnull String category,@Nonnull String configKey,@Nonnull String defaultValue) {
        super(category, configKey, defaultValue);
    }

    /**
     * @see #ConfigString(String, String, String, String, boolean)
     */
    public ConfigString(@Nonnull String category,@Nonnull String configKey,@Nonnull String defaultValue,@Nullable String comment) {
        super(category, configKey, defaultValue, comment);
    }

    /**
     * 创建一个配置项
     * @param category 配置所在目录
     * @param configKey 配置的key
     * @param defaultValue 配置的默认值，不应为null，因为会调用{@link Object#toString()}
     * @param comment 配置的注释
     * @param isFinal 配置是否在初始化后不可更改
     */
    public ConfigString(@Nonnull String category,@Nonnull String configKey,@Nonnull String defaultValue,@Nullable String comment, boolean isFinal) {
        super(category, configKey, defaultValue, comment, isFinal);
    }

    /**
     * {@inheritDoc}
     * @param property {@inheritDoc}
     */
    @Override
    protected void load(@Nonnull Property property) {
        this.value = property.getString();
    }
}
