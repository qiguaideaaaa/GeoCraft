package top.qiguaiaaaa.geocraft.api.property;

import net.minecraftforge.registries.IForgeRegistryEntry;
import top.qiguaiaaaa.geocraft.api.state.GeographyState;

import javax.annotation.Nonnull;

/**
 * 地理属性
 * @author QiguaiAAAA
 */
public abstract class GeographyProperty extends IForgeRegistryEntry.Impl<GeographyProperty> {
    /**
     * 获取对应状态的Instance
     * @return 一个符合该属性的状态
     */
    @Nonnull
    public abstract GeographyState getStateInstance();
}
