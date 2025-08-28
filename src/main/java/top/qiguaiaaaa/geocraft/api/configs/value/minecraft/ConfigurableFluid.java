package top.qiguaiaaaa.geocraft.api.configs.value.minecraft;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class ConfigurableFluid {
    protected String name;
    public ConfigurableFluid(String fluidName) {
        this.name = fluidName.trim();
    }
    public ConfigurableFluid(Fluid fluid){
        this.name = fluid.getName();
    }

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

    @Override
    public String toString() {
        return name;
    }
}
