package top.qiguaiaaaa.geocraft.api.configs.value.map.entry;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import top.qiguaiaaaa.geocraft.api.configs.value.map.ConfigurableLinkedHashMap;
import top.qiguaiaaaa.geocraft.api.configs.value.minecraft.ConfigurableBlockProperty;
import top.qiguaiaaaa.geocraft.api.configs.value.minecraft.ConfigurableBlockState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 用于在{@link ConfigurableLinkedHashMap}中便利表示一个方块状态-整数对
 * @author QiguaiAAAA
 */
public class BlockIntegerEntry extends ConfigEntry<ConfigurableBlockState, Integer> {
    /**
     * 创建一个选中某个方块所有数据值的配置
     * @param registryName 方块注册名
     * @param value 值
     */
    public BlockIntegerEntry(@Nonnull String registryName, int value) {
        this(registryName,-1,value);
    }

    /**
     * 创建一个选中某个方块指定数据值的配置
     * @param registryName 方块注册名
     * @param blockMeta 指定数据值，不能小于-1。等于-1时相当于选中所有数据值
     * @param value 值
     */
    public BlockIntegerEntry(@Nonnull String registryName,int blockMeta,int value) {
        super(new ConfigurableBlockState(registryName,blockMeta),value);
    }

    /**
     * 创建一个选中某个方块指定状态的配置
     * @param registryName 方块注册名
     * @param value 值
     * @param properties 指定属性。属性值若为*则表示选中该属性的所有可能。
     */
    public BlockIntegerEntry(@Nonnull String registryName,int value,@Nullable ConfigurableBlockProperty... properties) {
        super(new ConfigurableBlockState(registryName,properties),value);
    }

    /**
     * @see #BlockIntegerEntry(String, int, int) 
     */
    public BlockIntegerEntry(@Nonnull ResourceLocation registryName,int blockMeta,int value){
        super(new ConfigurableBlockState(registryName,blockMeta),value);
    }

    /**
     * @see #BlockIntegerEntry(String, int, ConfigurableBlockProperty...)
     */
    public BlockIntegerEntry(@Nonnull ResourceLocation registryName,int value,@Nullable ConfigurableBlockProperty... properties){
        super(new ConfigurableBlockState(registryName,properties),value);
    }

    /**
     * 创建一个选中指定方块状态的配置
     * @param state 指定方块状态
     * @param value 配置值
     */
    public BlockIntegerEntry(@Nonnull IBlockState state,int value){
        super(new ConfigurableBlockState(state),value);
    }
}
