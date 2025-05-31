package top.qiguaiaaaa.fluidgeography.api.configs.transfer;

import top.qiguaiaaaa.fluidgeography.api.configs.value.Configurable;
import top.qiguaiaaaa.fluidgeography.api.configs.value.base.ConfigString;

public class BaseTransfer<ValueType extends Configurable> implements IConfigurableTransfer<ValueType> {
    public static final BaseTransfer<ConfigString> STRING_TRANSFER = new BaseTransfer<>(new ConfigString(""));
    protected ValueType configurable;
    public BaseTransfer(ValueType c){
        configurable = c;
    }
    @SuppressWarnings("unchecked")
    @Override
    public ValueType getByString(String s) {
        return (ValueType) configurable.getInstanceByString(s);
    }
    @SuppressWarnings("unchecked")
    @Override
    public Class<ValueType> getTransferClass() {
        return (Class<ValueType>) configurable.getClass();
    }
}
