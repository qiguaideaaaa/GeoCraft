package top.qiguaiaaaa.fluidgeography.api.configs.item;

import top.qiguaiaaaa.fluidgeography.api.util.config.CollectionEquivalentContain;
import top.qiguaiaaaa.fluidgeography.api.configs.value.collection.ConfigHashSet;
import top.qiguaiaaaa.fluidgeography.api.configs.value.Configurable;
import top.qiguaiaaaa.fluidgeography.api.configs.value.collection.IConfigurableCollection;

import java.util.Collection;
import java.util.Iterator;

public class CollectionConfigItem<ValueType extends Configurable,CollectionType extends IConfigurableCollection<ValueType>> extends ConfigItem<IConfigurableCollection<ValueType>>
        implements CollectionEquivalentContain<ValueType> {
    public CollectionConfigItem(String category, String configKey, CollectionType defaultValue) {
        super(category, configKey, defaultValue);
    }

    public CollectionConfigItem(String category, String configKey, CollectionType defaultValue, String comment) {
        super(category, configKey, defaultValue, comment);
    }

    public CollectionConfigItem(String category, String configKey, CollectionType defaultValue, String comment, boolean isFinal) {
        super(category, configKey, defaultValue, comment, isFinal);
    }

    @Override
    public IConfigurableCollection<ValueType> getValue() {
        if(isFinal){
            return new ConfigHashSet<>(value);
        }
        return value;
    }

    @Override
    public IConfigurableCollection<ValueType> getDefaultValue() {
        return new ConfigHashSet<>(value);
    }

    @Override
    public int size() {
        return value.size();
    }

    @Override
    public boolean isEmpty() {
        return value.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return value.contains(o);
    }

    @Override
    public boolean containsEquivalent(Object o) {
        return value.containsEquivalent(o);
    }

    @Override
    public Iterator<ValueType> iterator() {
        return value.iterator();
    }

    @Override
    public Object[] toArray() {
        return value.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return value.toArray(a);
    }

    @Override
    public boolean add(ValueType type) {
        if(isFinal()) return false;
        return value.add(type);
    }

    @Override
    public boolean remove(Object o) {
        if(isFinal()) return false;
        return value.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return value.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends ValueType> c) {
        if(isFinal())return false;
        return value.addAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        if(isFinal()) return false;
        return value.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        if(isFinal()) return false;
        return value.retainAll(c);
    }

    @Override
    public void clear() {
        if(isFinal()) return;
        value.clear();
    }
}
