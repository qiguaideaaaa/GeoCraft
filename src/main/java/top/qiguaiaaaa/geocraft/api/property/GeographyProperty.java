package top.qiguaiaaaa.geocraft.api.property;

import net.minecraftforge.registries.IForgeRegistryEntry;
import top.qiguaiaaaa.geocraft.api.state.GeographyState;

public abstract class GeographyProperty extends IForgeRegistryEntry.Impl<GeographyProperty> {
    /**
     * 获取对应状态的Instance
     * @return 一个符合该属性的状态
     */
    public abstract GeographyState getStateInstance();
}
