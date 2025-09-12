package top.qiguaiaaaa.geocraft.api.configs.item.base;

import net.minecraftforge.common.config.Property;
import top.qiguaiaaaa.geocraft.api.configs.item.ConfigItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Function;

/**
 * 提供更多自定义的配置项目,需要传入一个方法用于获取值的实例
 * @param <V> 值类型,注意要实现{@link Object#toString()}以写入配置文件
 */
public class ConfigCustom<V> extends ConfigItem<V> {
    protected final Function<String,V> parser; //转换器，将字符串反序列化为对应配置值
    /**
     * @see #ConfigCustom(String, String, Object, String, Function, boolean)
     */
    public ConfigCustom(@Nonnull String category,@Nonnull String configKey,@Nonnull V defaultValue,@Nonnull Function<String,V> parser) {
        this(category,configKey,defaultValue,null,parser);
    }

    /**
     * @see #ConfigCustom(String, String, Object, String, Function, boolean)
     */
    public ConfigCustom(@Nonnull String category, @Nonnull String configKey, @Nonnull V defaultValue, @Nullable String comment,@Nonnull Function<String,V> parser) {
        this(category,configKey,defaultValue,comment,parser,false);
    }

    /**
     * 创建一个配置项
     * @param category 配置所在目录
     * @param configKey 配置的key
     * @param defaultValue 配置的默认值，不应为null，因为会调用{@link Object#toString()}
     * @param comment 配置的注释
     * @param parser 反序列化器，用于将字符串反序列化为对应配置值
     * @param isFinal 配置是否在初始化后不可更改
     */
    public ConfigCustom(@Nonnull String category,@Nonnull String configKey,@Nonnull V defaultValue,@Nullable String comment,@Nonnull Function<String,V> parser,boolean isFinal) {
        super(category, configKey, defaultValue, comment, isFinal);
        this.parser = parser;
    }

    /**
     * {@inheritDoc}
     * @param property {@inheritDoc}
     */
    @Override
    protected void load(@Nonnull Property property) {
        this.value = parser.apply(property.getString());
        if(this.value == null) this.value = defaultValue;
    }
}
