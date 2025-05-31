package top.qiguaiaaaa.fluidgeography.api.configs.value;

import top.qiguaiaaaa.fluidgeography.api.configs.transfer.IConfigurableTransfer;

public interface ConfigurableArray<V extends Configurable> extends Configurable{
    void setTransfer(IConfigurableTransfer<V> transfer);

    IConfigurableTransfer<V> getTransfer();

    @Override
    default boolean isArray() {
        return true;
    }
    String[] toStringArray() ;
    ConfigurableArray<V> getInstanceByStringArray(String[] contents);
}
