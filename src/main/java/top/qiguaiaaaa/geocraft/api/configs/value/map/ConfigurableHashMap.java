package top.qiguaiaaaa.geocraft.api.configs.value.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ConfigurableHashMap<K,V> extends HashMap<K,V> {
    public static final String SPLIT = "->";
    public String[] toStringList() {
        List<String> stringList = new ArrayList<>();
        for(Entry<K,V> entry:entrySet()){
            stringList.add(entry.getKey().toString()+SPLIT+entry.getValue().toString());
        }
        return stringList.toArray(new String[0]);
    }
}
