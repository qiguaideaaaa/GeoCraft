package top.qiguaiaaaa.geocraft.handler;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;
import top.qiguaiaaaa.geocraft.api.GEOProperties;
import top.qiguaiaaaa.geocraft.api.atmosphere.property.*;
import top.qiguaiaaaa.geocraft.atmosphere.DefaultAtmosphere;
import top.qiguaiaaaa.geocraft.atmosphere.DefaultAtmosphereStorage;
import top.qiguaiaaaa.geocraft.configs.SimulationConfig;
import top.qiguaiaaaa.geocraft.api.event.EventFactory;
import top.qiguaiaaaa.geocraft.api.simulation.SimulationMode;
import top.qiguaiaaaa.geocraft.atmosphere.property.AtmosphereSteam;
import top.qiguaiaaaa.geocraft.atmosphere.property.CarbonDioxide;
import top.qiguaiaaaa.geocraft.atmosphere.property.AtmosphereWater;
import top.qiguaiaaaa.geocraft.atmosphere.property.DefaultTemperature;

public final class RegistryHandler {
    public static void registerGeographyProperties(RegistryEvent.Register<GeographyProperty> event){
        IForgeRegistry<GeographyProperty> registry =event.getRegistry();
        registry.register(DefaultTemperature.TEMPERATURE);
        registry.register(AtmosphereWater.WATER);
        registry.register(AtmosphereSteam.STEAM);
        registry.register(CarbonDioxide.CARBON_DIOXIDE);

        GEOProperties.TEMPERATURE = DefaultTemperature.TEMPERATURE;
        GEOProperties.WATER = AtmosphereWater.WATER;
        GEOProperties.CARBON_DIOXIDE = CarbonDioxide.CARBON_DIOXIDE;
        GEOProperties.STEAM = AtmosphereSteam.STEAM;
    }
    public static void registerEventHandler(){
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
    public static void registerCapability(){
        CapabilityManager.INSTANCE.register(DefaultAtmosphere.class,new DefaultAtmosphereStorage(), DefaultAtmosphere::new);
    }
}
