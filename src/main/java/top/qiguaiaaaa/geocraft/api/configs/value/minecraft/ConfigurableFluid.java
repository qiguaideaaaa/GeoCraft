package top.qiguaiaaaa.geocraft.api.configs.value.minecraft;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 用于在配置中表示一个流体
 * @author QiguaiAAAA
 */
public class ConfigurableFluid {
    protected String name;

    /**
     * 从流体名称中创建一个流体表示
     * @param fluidName 流体名称
     */
    public ConfigurableFluid(@Nonnull String fluidName) {
        this.name = fluidName.trim();
    }

    /**
     * 从流体实例创建一个流体表示
     * @param fluid 流体实例
     */
    public ConfigurableFluid(@Nonnull Fluid fluid){
        this.name = fluid.getName();
    }

    /**
     * 获取该流体表示对应的流体实例
     * @return 流体实例，可能为null
     */
    @Nullable
    public Fluid getFluid(){
        return FluidRegistry.getFluid(name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) return true;
        if(obj instanceof String) return name.equals(obj);
        if(obj instanceof Fluid) return name.equals(((Fluid)obj).getName());
        if(!(obj instanceof ConfigurableFluid)) return false;
        return name.equals(((ConfigurableFluid)obj).name);
    }

    /**
     * 将该流体表示序列化为字符串
     * @return 序列化后的字符串
     */
    @Nonnull
    @Override
    public String toString() {
        return name;
    }
}
