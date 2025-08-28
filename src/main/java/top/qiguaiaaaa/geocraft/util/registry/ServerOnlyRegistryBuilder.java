package top.qiguaiaaaa.geocraft.util.registry;

import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class ServerOnlyRegistryBuilder<T extends IForgeRegistryEntry<T>> {
    private Class<T> registryType;

    public ServerOnlyRegistryBuilder<T> setType(Class<T> type)
    {
        this.registryType = type;
        return this;
    }

    public IForgeRegistry<T> create()
    {
        return new ServerOnlyRegistry<>(registryType);
    }
}
