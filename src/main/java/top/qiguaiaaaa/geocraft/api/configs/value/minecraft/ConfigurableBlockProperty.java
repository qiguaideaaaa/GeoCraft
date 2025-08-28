package top.qiguaiaaaa.geocraft.api.configs.value.minecraft;

import net.minecraft.block.properties.IProperty;

import java.util.Objects;

public class ConfigurableBlockProperty {
    public final String name,value;

    public ConfigurableBlockProperty(String name, String value) {
        this.name = name.toLowerCase().trim();
        this.value = value.toLowerCase().trim();
    }
    public ConfigurableBlockProperty(IProperty<?> property,Comparable<?> value){
        this(property.getName(),value.toString());
    }

    @Override
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
