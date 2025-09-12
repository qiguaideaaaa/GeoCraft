package top.qiguaiaaaa.geocraft.api.configs.value.collection;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * 一个用于列表配置项的配置类型
 * @param <V> 列表存储的对象类型，存储的对象应当覆写{@link Object#toString()}
 */
public class ConfigurableHashSet<V> extends HashSet<V> {
    /**
     * @see HashSet#HashSet()
     */
    public ConfigurableHashSet() {
        super();
    }

    /**
     * @see HashSet#HashSet(Collection) 
     */
    public ConfigurableHashSet(@Nonnull Collection<? extends V> c) {
        super(c);
    }

    /**
     * @see HashSet#HashSet(int, float)  
     */
    public ConfigurableHashSet(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    /**
     * @see HashSet#HashSet(int)
     */
    public ConfigurableHashSet(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * 将列表序列化为String列表
     * @return 一个String列表，表示该列表的内容
     */
    @Nonnull
    public String[] toStringList() {
        final List<String> stringList = new ArrayList<>();
        for(V v:this){
            stringList.add(v.toString());
        }
        return stringList.toArray(new String[0]);
    }
}
