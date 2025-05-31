package top.qiguaiaaaa.fluidgeography.api.configs.value.map;

import top.qiguaiaaaa.fluidgeography.api.configs.value.Configurable;
import top.qiguaiaaaa.fluidgeography.api.configs.value.map.entry.ConfigEntry;
import top.qiguaiaaaa.fluidgeography.api.configs.value.ConfigurableArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface IConfigurableMap<Key extends Configurable,Value extends Configurable> extends ConfigurableArray<ConfigEntry<Key,Value>>, Map<Key,Value> {
    default String[] toStringArray() {
        List<String> strings = new ArrayList<>();
        for(Entry<Key,Value> val:this.entrySet()){
            if(val == null) continue;
            strings.add(val.toString());
        }
        return strings.toArray(new String[0]);
    }
}
