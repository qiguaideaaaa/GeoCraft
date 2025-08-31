package top.qiguaiaaaa.geocraft.property;

import top.qiguaiaaaa.geocraft.GeoCraft;
import top.qiguaiaaaa.geocraft.api.property.UnderlyingProperty;
import top.qiguaiaaaa.geocraft.state.HeatCapacityState;

public class HeatCapacity extends UnderlyingProperty {
    public static final HeatCapacity HEAT_CAPACITY = new HeatCapacity();
    private HeatCapacity(){
        setRegistryName(GeoCraft.MODID,"heat_capacity");
    }
    @Override
    public HeatCapacityState getStateInstance() {
        return new HeatCapacityState();
    }
}
