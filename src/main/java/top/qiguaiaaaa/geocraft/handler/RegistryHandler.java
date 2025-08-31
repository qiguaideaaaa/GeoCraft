package top.qiguaiaaaa.geocraft.handler;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;
import top.qiguaiaaaa.geocraft.api.GeoCraftProperties;
import top.qiguaiaaaa.geocraft.api.configs.value.geo.SimulationMode;
import top.qiguaiaaaa.geocraft.api.event.EventFactory;
import top.qiguaiaaaa.geocraft.api.property.GeographyProperty;
import top.qiguaiaaaa.geocraft.configs.SimulationConfig;
import top.qiguaiaaaa.geocraft.handler.event.AtmosphereEventHandler;
import top.qiguaiaaaa.geocraft.handler.event.MoreRealityEventHandler;
import top.qiguaiaaaa.geocraft.handler.event.VanillaEventHandler;
import top.qiguaiaaaa.geocraft.handler.event.VanillaLikeEventHandler;
import top.qiguaiaaaa.geocraft.property.*;

public final class RegistryHandler {
    public static void registerGeographyProperties(RegistryEvent.Register<GeographyProperty> event){
        IForgeRegistry<GeographyProperty> registry =event.getRegistry();
        registry.register(DefaultTemperature.TEMPERATURE);
        registry.register(DeepTemperature.DEEP_TEMPERATURE);
        registry.register(AtmosphereWater.WATER);
        registry.register(AtmosphereSteam.STEAM);
        registry.register(CarbonDioxide.CARBON_DIOXIDE);
        registry.register(AltitudeProperty.ALTITUDE);
        registry.register(HeatCapacity.HEAT_CAPACITY);
        registry.register(ReflectivityProperty.REFLECTIVITY);
        registry.register(FinalTemperature.FINAL_TEMPERATURE);

        GeoCraftProperties.FINAL_TEMPERATURE = FinalTemperature.FINAL_TEMPERATURE;
        GeoCraftProperties.TEMPERATURE = DefaultTemperature.TEMPERATURE;
        GeoCraftProperties.DEEP_TEMPERATURE = DeepTemperature.DEEP_TEMPERATURE;
        GeoCraftProperties.WATER = AtmosphereWater.WATER;
        GeoCraftProperties.CARBON_DIOXIDE = CarbonDioxide.CARBON_DIOXIDE;
        GeoCraftProperties.STEAM = AtmosphereSteam.STEAM;
        GeoCraftProperties.ALTITUDE = AltitudeProperty.ALTITUDE;
        GeoCraftProperties.HEAT_CAPACITY = HeatCapacity.HEAT_CAPACITY;
        GeoCraftProperties.REFLECTIVITY = ReflectivityProperty.REFLECTIVITY;
    }
    public static void registerEventHandler(){
        EventFactory.EVENT_BUS.register(AtmosphereEventHandler.class);
        SimulationMode mode = SimulationConfig.SIMULATION_MODE.getValue();
        switch (mode){
            case MORE_REALITY:{
                registerMoreRealityEventHandler();
                break;
            }
            case VANILLA_LIKE:{
                registerVanillaLikeEventHandler();
                break;
            }
            case VANILLA:
            default:{
                registerVanillaEventHandler();
                break;
            }
        }
    }
    private static void registerMoreRealityEventHandler(){
        MoreRealityEventHandler moreRealityEventHandler = new MoreRealityEventHandler();
        MinecraftForge.EVENT_BUS.register(moreRealityEventHandler);
        EventFactory.EVENT_BUS.register(moreRealityEventHandler);
    }
    private static void registerVanillaLikeEventHandler(){
        VanillaLikeEventHandler handler = new VanillaLikeEventHandler();
        MinecraftForge.EVENT_BUS.register(handler);
        EventFactory.EVENT_BUS.register(handler);
    }
    private static void registerVanillaEventHandler(){
        VanillaEventHandler handler = new VanillaEventHandler();
        MinecraftForge.EVENT_BUS.register(handler);
        EventFactory.EVENT_BUS.register(handler);
    }
}
