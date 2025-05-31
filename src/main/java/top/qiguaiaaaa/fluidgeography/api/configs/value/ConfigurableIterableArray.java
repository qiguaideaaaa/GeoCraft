package top.qiguaiaaaa.fluidgeography.api.configs.value;

import java.util.ArrayList;
import java.util.List;

public interface ConfigurableIterableArray<V extends Configurable> extends ConfigurableArray<V>,Iterable<V>{
    default String[] toStringArray() {
        List<String> strings = new ArrayList<>();
        for(V val:this){
            if(val == null) continue;
            strings.add(val.toString());
        }
        return strings.toArray(new String[0]);
    }
}
