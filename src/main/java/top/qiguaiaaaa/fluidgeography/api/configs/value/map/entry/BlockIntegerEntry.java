package top.qiguaiaaaa.fluidgeography.api.configs.value.map.entry;

import top.qiguaiaaaa.fluidgeography.api.configs.value.minecraft.ConfigBlock;
import top.qiguaiaaaa.fluidgeography.api.configs.value.number.ConfigInteger;

public class BlockIntegerEntry extends ConfigEntry<ConfigBlock, ConfigInteger> {
    public BlockIntegerEntry(String registryName,int value) {
        this(registryName,-1,value);
    }
    public BlockIntegerEntry(String registryName,int blockMeta,int value) {
        super(new ConfigBlock(registryName,blockMeta),new ConfigInteger(value));
    }
}
