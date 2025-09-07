package top.qiguaiaaaa.geocraft.api.configs.value.map;

import java.util.*;

public class ConfigurableLinkedHashMap<K,V> extends LinkedHashMap<K,V> {
    public static final String SPLIT = "->";
    public String[] toStringList() {
        List<String> stringList = new ArrayList<>();
        for(Map.Entry<K,V> entry:entrySet()){
            stringList.add(entry.getKey().toString()+SPLIT+entry.getValue().toString());
        }
        return stringList.toArray(new String[0]);
    }
}
