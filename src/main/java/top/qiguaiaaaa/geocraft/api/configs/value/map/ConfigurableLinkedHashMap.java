package top.qiguaiaaaa.geocraft.api.configs.value.map;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * 用于在配置中表示一个映射表
 * @param <K> 键的类型，需要实现{@link Object#toString()}
 * @param <V> 值的类型，需要实现{@link Object#toString()}
 * @author QiguaiAAAA
 */
public class ConfigurableLinkedHashMap<K,V> extends LinkedHashMap<K,V> {
    public static final String SPLIT = "->";

    /**
     * 将该Map序列化为一个字符串列表
     * @return 序列化后的字符串列表
     */
    @Nonnull
    public String[] toStringList() {
        List<String> stringList = new ArrayList<>();
        for(Map.Entry<K,V> entry:entrySet()){
            stringList.add(entry.getKey().toString()+SPLIT+entry.getValue().toString());
        }
        return stringList.toArray(new String[0]);
    }
}
