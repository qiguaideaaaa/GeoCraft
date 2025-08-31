package top.qiguaiaaaa.geocraft.api.property;

import net.minecraftforge.fluids.Fluid;
import top.qiguaiaaaa.geocraft.api.state.FluidState;

public abstract class FluidProperty extends AtmosphereProperty {
    protected final Fluid fluid;
    public FluidProperty(Fluid gasFluid, boolean windEffect, boolean flowable) {
        super(windEffect, flowable);
        this.fluid = gasFluid;
    }

    @Override
    public abstract FluidState getStateInstance();
    /**
     * 获取气体对应的Forge流体
     * @return Forge流体
     */
    public Fluid getFluid() {
        return fluid;
    }
}
