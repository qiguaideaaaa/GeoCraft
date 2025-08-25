package top.qiguaiaaaa.fluidgeography.handler;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;
import top.qiguaiaaaa.fluidgeography.api.FGAtmosphereProperties;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.property.*;
import top.qiguaiaaaa.fluidgeography.atmosphere.DefaultAtmosphere;
import top.qiguaiaaaa.fluidgeography.atmosphere.DefaultAtmosphereStorage;
import top.qiguaiaaaa.fluidgeography.api.configs.SimulationConfig;
import top.qiguaiaaaa.fluidgeography.api.event.EventFactory;
import top.qiguaiaaaa.fluidgeography.api.simulation.SimulationMode;
import top.qiguaiaaaa.fluidgeography.atmosphere.property.AtmosphereSteam;
import top.qiguaiaaaa.fluidgeography.atmosphere.property.CarbonDioxide;
import top.qiguaiaaaa.fluidgeography.atmosphere.property.AtmosphereWater;
import top.qiguaiaaaa.fluidgeography.atmosphere.property.DefaultTemperature;

public final class RegistryHandler {
    public static void registerAtmosphereProperties(RegistryEvent.Register<AtmosphereProperty> event){
        IForgeRegistry<AtmosphereProperty> registry =event.getRegistry();
        registry.register(DefaultTemperature.TEMPERATURE);
        registry.register(AtmosphereWater.WATER);
        registry.register(AtmosphereSteam.STEAM);
        registry.register(CarbonDioxide.CARBON_DIOXIDE);

        FGAtmosphereProperties.TEMPERATURE = DefaultTemperature.TEMPERATURE;
        FGAtmosphereProperties.WATER = AtmosphereWater.WATER;
        FGAtmosphereProperties.CARBON_DIOXIDE = CarbonDioxide.CARBON_DIOXIDE;
        FGAtmosphereProperties.STEAM = AtmosphereSteam.STEAM;
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
