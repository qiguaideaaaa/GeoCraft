package top.qiguaiaaaa.fluidgeography.util.registry;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * 一个不会被Forge记录的Registry
 */
public class ServerOnlyRegistry<V extends IForgeRegistryEntry<V>> implements IForgeRegistry<V> {
    private final Class<V> superType;
    private final Map<ResourceLocation, V> entries = new HashMap<>();

    public ServerOnlyRegistry(Class<V> superType) {
        this.superType = superType;
    }

    @Override
    public Class<V> getRegistrySuperType() {
        return superType;
    }

    @Override
    public void register(V value) {
        if (value.getRegistryName() == null) {
            throw new IllegalArgumentException("Registry entry must have a name!");
        }
        entries.put(value.getRegistryName(),value);
    }

    @SafeVarargs
    @Override
    public final void registerAll(V... values) {
        for (V v : values) {
            register(v);
        }
    }

    @Override
    public boolean containsKey(ResourceLocation key) {
        return entries.containsKey(key);
    }

    @Override
    public boolean containsValue(V value) {
        return entries.containsValue(value);
    }

    @Nullable
    @Override
    public V getValue(ResourceLocation key) {
        return entries.get(key);
    }

    @Nullable
    @Override
    public ResourceLocation getKey(V value) {
        for (Map.Entry<ResourceLocation, V> e : entries.entrySet()) {
            if (e.getValue() == value) {
                return e.getKey();
            }
        }
        return null;
    }

    @Nonnull
    @Override
    public Set<ResourceLocation> getKeys() {
        return Collections.unmodifiableSet(entries.keySet());
    }

    @Nonnull
    @Override
    public List<V> getValues() {
        return new ArrayList<>(entries.values());
    }

    @Nonnull
    @Override
    public Set<Map.Entry<ResourceLocation, V>> getEntries() {
        return Collections.unmodifiableSet(entries.entrySet());
    }

    @Override
    public <T> T getSlaveMap(ResourceLocation slaveMapName, Class<T> type) {
        return null;
    }

    @Override
    public Iterator<V> iterator() {
        return entries.values().iterator();
    }
}
