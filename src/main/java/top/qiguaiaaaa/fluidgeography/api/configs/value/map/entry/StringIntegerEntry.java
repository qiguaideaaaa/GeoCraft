package top.qiguaiaaaa.fluidgeography.api.configs.value.map.entry;

import top.qiguaiaaaa.fluidgeography.api.configs.value.base.ConfigString;
import top.qiguaiaaaa.fluidgeography.api.configs.value.number.ConfigInteger;

public class StringIntegerEntry extends ConfigEntry<ConfigString, ConfigInteger> {
    public StringIntegerEntry(String configString,int configInteger) {
        super(new ConfigString(configString),new ConfigInteger(configInteger));
    }
}
