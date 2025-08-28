package top.qiguaiaaaa.geocraft.api.atmosphere.property;

import top.qiguaiaaaa.geocraft.api.atmosphere.state.TemperatureState;

public abstract class TemperatureProperty extends GeographyProperty {
    public static final int BOILED_POINT = 373;
    public static final int ICE_POINT = 273;
    public static final int MIN = 3;
    public static final int UNAVAILABLE = -100;

    @Override
    public abstract TemperatureState getStateInstance() ;
    public static double toCelsiusFromKelvin(double temperature){
        return temperature-TemperatureProperty.ICE_POINT;
    }
}
