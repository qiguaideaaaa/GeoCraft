package top.qiguaiaaaa.geocraft.geography.property;

import top.qiguaiaaaa.geocraft.GeoCraft;
import top.qiguaiaaaa.geocraft.api.property.UnderlyingProperty;
import top.qiguaiaaaa.geocraft.geography.state.HeatCapacityState;

import javax.annotation.Nonnull;

public class HeatCapacity extends UnderlyingProperty {
    public static final HeatCapacity HEAT_CAPACITY = new HeatCapacity();
    private HeatCapacity(){
        setRegistryName(GeoCraft.MODID,"heat_capacity");
    }
    @Nonnull
    @Override
    public HeatCapacityState getStateInstance() {
        return new HeatCapacityState();
    }
}
