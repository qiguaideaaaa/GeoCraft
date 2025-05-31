package top.qiguaiaaaa.fluidgeography.api.configs.value.minecraft;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import top.qiguaiaaaa.fluidgeography.api.util.FluidUtil;
import top.qiguaiaaaa.fluidgeography.api.configs.value.base.ConfigNameWrapper;

public class ConfigFluid extends ConfigNameWrapper {
    public ConfigFluid(String fluidName) {
        super(fluidName);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
        if(obj instanceof Fluid)
            return ((Fluid) obj).getName().equals(value);
        if(obj instanceof BlockLiquid)
            return equals(FluidUtil.getFluid((Block) obj));
        return super.equals(obj);
    }

    @Override
    public ConfigFluid getInstanceByString(String content) {
        if(content == null || content.trim().isEmpty()) return null;
        return new ConfigFluid(content);
    }

    public Fluid getFluid(){
        return FluidRegistry.getFluid(value);
    }

    public boolean isFluidExist(){
        return getFluid() != null;
    }
}
