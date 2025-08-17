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

public final class RegistryHandler {
    public static void registerAtmosphereProperties(RegistryEvent.Register<AtmosphereProperty> event){
        IForgeRegistry<AtmosphereProperty> registry =event.getRegistry();
        registry.register(GroundTemperature.GROUND_TEMPERATURE);
        registry.register(AtmosphereTemperature.TEMPERATURE);
        registry.register(AtmosphereWater.WATER);
        registry.register(AtmosphereCarbonDioxide.CARBON_DIOXIDE);

        FGAtmosphereProperties.GROUND_TEMPERATURE = GroundTemperature.GROUND_TEMPERATURE;
        FGAtmosphereProperties.TEMPERATURE = AtmosphereTemperature.TEMPERATURE;
        FGAtmosphereProperties.WATER = AtmosphereWater.WATER;
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
