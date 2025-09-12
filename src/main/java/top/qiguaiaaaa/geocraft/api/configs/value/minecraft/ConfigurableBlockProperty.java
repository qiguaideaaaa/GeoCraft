package top.qiguaiaaaa.geocraft.api.configs.value.minecraft;

import net.minecraft.block.properties.IProperty;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * 用于在配置中表示一个方块属性状态
 * @author QiguaiAAAA
 */
public class ConfigurableBlockProperty {
    public final String name,value;

    /**
     * 从名称和值表示一个方块属性
     * @param name 名称
     * @param value 值
     */
    public ConfigurableBlockProperty(@Nonnull String name,@Nonnull String value) {
        this.name = name.toLowerCase().trim();
        this.value = value.toLowerCase().trim();
    }

    /**
     * 从一个方块属性实例表示一个方块属性
     * @param property 方块属性
     * @param value 属性的值
     */
    public ConfigurableBlockProperty(@Nonnull IProperty<?> property,Comparable<?> value){
        this(property.getName(),value.toString());
    }

    /**
     * 将该表示序列化为字符串
     * @return 序列化后的字符串，表示一个方块属性状态
     */
    @Override
    @Nonnull
    public String toString() {
        return name+'='+value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConfigurableBlockProperty)) return false;
        ConfigurableBlockProperty that = (ConfigurableBlockProperty) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }
}
