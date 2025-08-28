package top.qiguaiaaaa.geocraft.api.atmosphere.layer;

import top.qiguaiaaaa.geocraft.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.geocraft.api.atmosphere.property.FluidProperty;
import top.qiguaiaaaa.geocraft.api.atmosphere.state.FluidState;
import top.qiguaiaaaa.geocraft.api.atmosphere.state.GeographyState;

import javax.annotation.Nullable;

public abstract class BaseAtmosphereLayer extends BaseLayer implements AtmosphereLayer{

    public BaseAtmosphereLayer(Atmosphere atmosphere) {
        super(atmosphere);
    }

    @Nullable
    @Override
    public FluidState getGas(FluidProperty property) {
        final GeographyState state = states.get(property);
        if(state instanceof FluidState) return (FluidState) state;
        return null;
    }
}
