package top.qiguaiaaaa.fluidgeography.api.configs.value.collection;

import top.qiguaiaaaa.fluidgeography.api.configs.value.Configurable;

import java.util.Set;

public interface IConfigurableSet<V extends Configurable> extends IConfigurableCollection<V>, Set<V> {
}
