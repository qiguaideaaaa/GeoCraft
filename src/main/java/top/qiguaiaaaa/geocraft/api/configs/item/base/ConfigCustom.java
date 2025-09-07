package top.qiguaiaaaa.geocraft.api.configs.item.base;

import net.minecraftforge.common.config.Property;
import top.qiguaiaaaa.geocraft.api.configs.item.ConfigItem;

import java.util.function.Function;

/**
 * 提供更多自定义的配置项目,需要传入一个方法用于获取值的实例
 * @param <V> 值类型,注意要实现{@link Object#toString()}以写入配置文件
 */
public class ConfigCustom<V> extends ConfigItem<V> {
    protected final Function<String,V> parser;
    public ConfigCustom(String category, String configKey, V defaultValue,Function<String,V> parser) {
        this(category,configKey,defaultValue,null,parser);
    }

    public ConfigCustom(String category, String configKey, V defaultValue, String comment,Function<String,V> parser) {
        this(category,configKey,defaultValue,comment,parser,false);
    }

    public ConfigCustom(String category, String configKey, V defaultValue, String comment, Function<String,V> parser,boolean isFinal) {
        super(category, configKey, defaultValue, comment, isFinal);
        this.parser = parser;
    }

    @Override
    protected void load(Property property) {
        this.value = parser.apply(property.getString());
        if(this.value == null) this.value = defaultValue;
    }
}
