package top.qiguaiaaaa.fluidgeography.api.configs.value.collection;

import top.qiguaiaaaa.fluidgeography.api.configs.value.Configurable;
import top.qiguaiaaaa.fluidgeography.api.configs.transfer.BaseTransfer;
import top.qiguaiaaaa.fluidgeography.api.configs.transfer.IConfigurableTransfer;

import java.util.*;
public class ConfigHashSet<ValueType extends Configurable> extends HashSet<ValueType> implements IConfigurableSet<ValueType> {
    protected IConfigurableTransfer<ValueType> transfer;

    public ConfigHashSet(IConfigurableTransfer<ValueType> transfer){
        this.transfer = transfer;
    }
    @SafeVarargs
    public ConfigHashSet(ValueType... configurables){
        this(Arrays.asList(configurables));
    }
    public ConfigHashSet(Collection<ValueType> configurables){
        super(configurables);
        if(size()>0) transfer = new BaseTransfer<>(configurables.iterator().next());
        else throw new IllegalArgumentException();
    }
    @Override
    public void setTransfer(IConfigurableTransfer<ValueType> transfer) {
        if(transfer == null) return;
        this.transfer = transfer;
    }
    @Override
    public IConfigurableTransfer<ValueType> getTransfer() {
        return transfer;
    }

    /**
     * 请使用toConfigurableByStringArray
     */
    @Override
    public ConfigHashSet<ValueType> getInstanceByString(String content) {
        ValueType value = transfer.getByString(content);
        if(value == null) return new ConfigHashSet<>(transfer);
        return new ConfigHashSet<>(value);
    }
    @Override
    public ConfigHashSet<ValueType> getInstanceByStringArray(String[] contents) {
        List<ValueType> values = new ArrayList<>();
        for(String s:contents){
            if(s == null) continue;
            ValueType val = transfer.getByString(s);
            if(val != null) values.add(val);
        }
        if(values.isEmpty()){
            return new ConfigHashSet<>(transfer);
        }
        return new ConfigHashSet<>(values);
    }
}
