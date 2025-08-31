package top.qiguaiaaaa.geocraft.property;

import top.qiguaiaaaa.geocraft.GeoCraft;
import top.qiguaiaaaa.geocraft.api.property.UnderlyingProperty;
import top.qiguaiaaaa.geocraft.api.util.math.Altitude;
import top.qiguaiaaaa.geocraft.state.AltitudeState;

public class AltitudeProperty extends UnderlyingProperty {
    public static final double UNAVAILABLE = -100000;
    public static final AltitudeProperty ALTITUDE = new AltitudeProperty();
    private AltitudeProperty(){
        setRegistryName(GeoCraft.MODID,"altitude");
    }
    @Override
    public AltitudeState getStateInstance() {
        return new AltitudeState(new Altitude(UNAVAILABLE));
    }
}
