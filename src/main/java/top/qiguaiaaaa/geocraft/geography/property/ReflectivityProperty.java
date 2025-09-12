package top.qiguaiaaaa.geocraft.geography.property;

import top.qiguaiaaaa.geocraft.GeoCraft;
import top.qiguaiaaaa.geocraft.api.property.UnderlyingProperty;
import top.qiguaiaaaa.geocraft.geography.state.ReflectivityState;

import javax.annotation.Nonnull;

public class ReflectivityProperty extends UnderlyingProperty {
    public static final ReflectivityProperty REFLECTIVITY = new ReflectivityProperty();
    private ReflectivityProperty(){
        setRegistryName(GeoCraft.MODID,"reflectivity");
    }
    @Nonnull
    @Override
    public ReflectivityState getStateInstance() {
        return new ReflectivityState();
    }
}
