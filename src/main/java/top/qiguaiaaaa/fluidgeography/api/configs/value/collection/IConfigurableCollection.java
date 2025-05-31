package top.qiguaiaaaa.fluidgeography.api.configs.value.collection;

import top.qiguaiaaaa.fluidgeography.api.util.config.CollectionEquivalentContain;
import top.qiguaiaaaa.fluidgeography.api.configs.value.Configurable;
import top.qiguaiaaaa.fluidgeography.api.configs.value.ConfigurableIterableArray;

public interface IConfigurableCollection<ValueType extends Configurable> extends ConfigurableIterableArray<ValueType>,CollectionEquivalentContain<ValueType> {

    default boolean containsEquivalent(Object o){
        for (ValueType value : this) {
            if (value == null && o == null) return true;
            else if (value == null) continue;
            if (value.equals(o)) return true;
        }
        return false;
    }

    IConfigurableCollection<ValueType> getInstanceByStringArray(String[] contents);
}
