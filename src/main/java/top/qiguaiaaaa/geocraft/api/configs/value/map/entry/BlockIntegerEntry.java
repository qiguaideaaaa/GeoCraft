package top.qiguaiaaaa.geocraft.api.configs.value.map.entry;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import top.qiguaiaaaa.geocraft.api.configs.value.minecraft.ConfigurableBlockProperty;
import top.qiguaiaaaa.geocraft.api.configs.value.minecraft.ConfigurableBlockState;

public class BlockIntegerEntry extends ConfigEntry<ConfigurableBlockState, Integer> {
    public BlockIntegerEntry(String registryName,int value) {
        this(registryName,-1,value);
    }
    public BlockIntegerEntry(String registryName,int blockMeta,int value) {
        super(new ConfigurableBlockState(registryName,blockMeta),value);
    }
    public BlockIntegerEntry(String registryName, int value, ConfigurableBlockProperty... properties) {
        super(new ConfigurableBlockState(registryName,properties),value);
    }
    public BlockIntegerEntry(ResourceLocation registryName,int blockMeta,int value){
        super(new ConfigurableBlockState(registryName,blockMeta),value);
    }
    public BlockIntegerEntry(ResourceLocation registryName,int value,ConfigurableBlockProperty... properties){
        super(new ConfigurableBlockState(registryName,properties),value);
    }
    public BlockIntegerEntry(IBlockState state,int value){
        super(new ConfigurableBlockState(state),value);
    }
}
