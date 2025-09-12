package top.qiguaiaaaa.geocraft.api.configs.value.map.entry;

import top.qiguaiaaaa.geocraft.api.configs.value.map.ConfigurableLinkedHashMap;

import javax.annotation.Nonnull;
import java.util.Map;

/**
 * 用于{@link ConfigurableLinkedHashMap}，表示一个配置的键值对
 * @param <Key> 键，应该要实现{@link Object#toString()}
 * @param <Value> 值，应该要实现{@link Object#toString()}
 * @author QiguaiAAAA
 */
public class ConfigEntry<Key,Value> implements Map.Entry<Key, Value> {
    protected Key key;
    protected Value value;

    /**
     * @see Map.Entry
     */
    public ConfigEntry(@Nonnull Key key,@Nonnull Value value){
        this.key = key;
        this.value = value;
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public Key getKey() {
        return key;
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public Value getValue() {
        return value;
    }

    /**
     * {@inheritDoc}
     * @param value new value to be stored in this entry
     * @return {@inheritDoc}
     */
    @Override
    public Value setValue(Value value) {
        return value;
    }
}
