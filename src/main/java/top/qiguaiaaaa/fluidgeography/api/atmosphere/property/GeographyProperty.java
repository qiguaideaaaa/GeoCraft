package top.qiguaiaaaa.fluidgeography.api.atmosphere.property;

import net.minecraftforge.registries.IForgeRegistryEntry;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.state.GeographyState;

public abstract class GeographyProperty extends IForgeRegistryEntry.Impl<GeographyProperty> {
    /**
     * 获取对应状态的Instance
     * @return 一个符合该属性的状态
     */
    public abstract GeographyState getStateInstance();
}
