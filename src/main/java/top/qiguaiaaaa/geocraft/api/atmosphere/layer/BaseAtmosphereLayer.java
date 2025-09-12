package top.qiguaiaaaa.geocraft.api.atmosphere.layer;

import top.qiguaiaaaa.geocraft.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.geocraft.api.property.FluidProperty;
import top.qiguaiaaaa.geocraft.api.state.FluidState;
import top.qiguaiaaaa.geocraft.api.state.GeographyState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 一个基本的大气抽象层级，实现了{@link FluidState}的存取
 */
public abstract class BaseAtmosphereLayer extends BaseLayer implements AtmosphereLayer{

    public BaseAtmosphereLayer(@Nonnull Atmosphere atmosphere) {
        super(atmosphere);
    }

    @Nullable
    @Override
    public FluidState getGas(@Nonnull FluidProperty property) {
        final GeographyState state = states.get(property);
        if(state instanceof FluidState) return (FluidState) state;
        return null;
    }
}
