package top.qiguaiaaaa.geocraft.api.configs.value.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class ConfigurableHashSet<V> extends HashSet<V> {
    public ConfigurableHashSet() {
        super();
    }

    public ConfigurableHashSet(Collection<? extends V> c) {
        super(c);
    }

    public ConfigurableHashSet(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public ConfigurableHashSet(int initialCapacity) {
        super(initialCapacity);
    }

    public String[] toStringList() {
        List<String> stringList = new ArrayList<>();
        for(V v:this){
            stringList.add(v.toString());
        }
        return stringList.toArray(new String[0]);
    }
}
