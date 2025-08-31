package top.qiguaiaaaa.geocraft.property;

import top.qiguaiaaaa.geocraft.GeoCraft;
import top.qiguaiaaaa.geocraft.api.property.UnderlyingProperty;
import top.qiguaiaaaa.geocraft.state.ReflectivityState;

public class ReflectivityProperty extends UnderlyingProperty {
    public static final ReflectivityProperty REFLECTIVITY = new ReflectivityProperty();
    private ReflectivityProperty(){
        setRegistryName(GeoCraft.MODID,"reflectivity");
    }
    @Override
    public ReflectivityState getStateInstance() {
        return new ReflectivityState();
    }
}
