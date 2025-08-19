package top.qiguaiaaaa.fluidgeography.api.atmosphere.property;

import top.qiguaiaaaa.fluidgeography.api.atmosphere.state.GasState;

public abstract class GasProperty extends AtmosphereProperty{
    public GasProperty(boolean windEffect, boolean flowable) {
        super(windEffect, flowable);
    }

    @Override
    public abstract GasState getStateInstance();
}
