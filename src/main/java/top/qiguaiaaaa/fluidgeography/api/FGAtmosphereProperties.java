package top.qiguaiaaaa.fluidgeography.api;

import top.qiguaiaaaa.fluidgeography.api.atmosphere.property.GasProperty;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.property.TemperatureProperty;

public final class FGAtmosphereProperties {
    public static TemperatureProperty TEMPERATURE;
    @Deprecated
    public static TemperatureProperty GROUND_TEMPERATURE;
    public static GasProperty WATER;
    public static GasProperty STEAM;
    public static GasProperty CARBON_DIOXIDE;
}
