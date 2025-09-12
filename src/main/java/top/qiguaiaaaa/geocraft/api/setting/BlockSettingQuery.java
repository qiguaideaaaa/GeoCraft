package top.qiguaiaaaa.geocraft.api.setting;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import top.qiguaiaaaa.geocraft.api.configs.value.minecraft.ConfigurableBlockState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 一个工具类，用于便利方块的某个配置值的查询
 * @param <V> 配置值类型
 * @author QiguaiAAAA
 */
public class BlockSettingQuery<V> {
    protected final Map<IBlockState, V> cache = new ConcurrentHashMap<>();
    protected final Map<ConfigurableBlockState, V> configsProperty = new HashMap<>();
    protected final Map<ConfigurableBlockState, V> configsMeta = new HashMap<>();
    protected final Map<ConfigurableBlockState, V> configsBlock = new HashMap<>();
    protected V defaultValue = null;

    /**
     * 创建一个方块配置查询工具
     * @param defaultValue 默认值
     */
    public BlockSettingQuery(V defaultValue){
        setDefaultValue(defaultValue);
    }


    /**
     * 查询指定IBlockState的设置值
     * @param state 要查询的IBlockState
     * @return 查询到的属性值，如果没有找到则返回默认值
     */
    public @Nonnull V querySettingValue(@Nonnull IBlockState state) {
        if (cache.containsKey(state)) {
            return cache.get(state);
        }
        V result = queryByFullProperties(state);
        if (result != null) {
            cache.put(state, result);
            return result;
        }

        result = queryByMeta(state);
        if (result != null) {
            cache.put(state, result);
            return result;
        }

        result = queryByRegistryName(state);
        if (result != null) {
            cache.put(state, result);
            return result;
        }

        // 5. 返回默认值
        cache.put(state, defaultValue);
        return defaultValue;
    }

    /**
     * 基于完整属性匹配查询
     */
    @Nullable
    protected V queryByFullProperties(@Nonnull IBlockState state) {
        for (Map.Entry<ConfigurableBlockState, V> entry : configsProperty.entrySet()) {
            ConfigurableBlockState configState = entry.getKey();

            if (configState.match(state)) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * 基于meta值查询
     */
    @Nullable
    protected V queryByMeta(@Nonnull IBlockState state) {
        for (Map.Entry<ConfigurableBlockState, V> entry : configsMeta.entrySet()) {
            ConfigurableBlockState configState = entry.getKey();

            if (configState.match(state)) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * 仅基于registryName查询
     */
    @Nullable
    protected V queryByRegistryName(@Nonnull IBlockState state) {
        Block block = state.getBlock();
        ResourceLocation registryName = block.getRegistryName();
        if (registryName == null) return null;

        for (Map.Entry<ConfigurableBlockState, V> entry : configsBlock.entrySet()) {
            ConfigurableBlockState configState = entry.getKey();

            if (configState.match(state)) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * 设置默认值
     * @param v 默认值
     */
    public void setDefaultValue(V v) {
        this.defaultValue = v;
    }

    /**
     * 获取默认值
     * @return 默认值
     */
    public V getDefaultValue() {
        return defaultValue;
    }

    /**
     * 添加配置
     * @param state 方块状态配置
     * @param value 值
     */
    public void addConfiguration(ConfigurableBlockState state, V value) {
        if(state == null) return;
        if(state.meta<-2) return;
        if(state.meta == -2) configsProperty.put(state,value);
        else if(state.meta == -1) configsBlock.put(state,value);
        else configsMeta.put(state,value);
    }

    /**
     * 移除配置
     * @param state 方块状态配置
     */
    public void removeConfiguration(ConfigurableBlockState state) {
        configsProperty.remove(state);
        configsMeta.remove(state);
        configsBlock.remove(state);
    }

    /**
     * 清除缓存
     */
    public void clearCache() {
        cache.clear();
    }
}
