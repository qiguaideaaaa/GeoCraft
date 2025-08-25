package top.qiguaiaaaa.fluidgeography.api.atmosphere.property;

import top.qiguaiaaaa.fluidgeography.api.atmosphere.state.TemperatureState;

public abstract class TemperatureProperty extends AtmosphereProperty{
    public static final int BOILED_POINT = 373;
    public static final int ICE_POINT = 273;

    public TemperatureProperty(boolean windEffect, boolean flowable) {
        super(windEffect, flowable);
    }

    @Override
    public abstract TemperatureState getStateInstance() ;
    public static double toCelsiusFromKelvin(double temperature){
        return temperature-TemperatureProperty.ICE_POINT;
    }
}
