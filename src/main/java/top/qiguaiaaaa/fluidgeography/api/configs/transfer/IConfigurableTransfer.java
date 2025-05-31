package top.qiguaiaaaa.fluidgeography.api.configs.transfer;

import top.qiguaiaaaa.fluidgeography.api.configs.value.Configurable;

public interface IConfigurableTransfer<ConfigType extends Configurable> {
    ConfigType getByString(String s);
    Class<ConfigType> getTransferClass();
}
