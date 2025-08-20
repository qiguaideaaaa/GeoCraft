package top.qiguaiaaaa.fluidgeography.api.atmosphere.property;

import net.minecraftforge.fluids.Fluid;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.state.GasState;

public abstract class GasProperty extends AtmosphereProperty{
    protected final Fluid gas;
    public GasProperty(Fluid gasFluid,boolean windEffect, boolean flowable) {
        super(windEffect, flowable);
        this.gas = gasFluid;
    }

    @Override
    public abstract GasState getStateInstance();
    /**
     * 获取气体对应的Forge流体
     * @return Forge流体
     */
    public Fluid getGas() {
        return gas;
    }
}
