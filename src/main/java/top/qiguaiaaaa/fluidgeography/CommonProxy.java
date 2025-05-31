package top.qiguaiaaaa.fluidgeography;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import top.qiguaiaaaa.fluidgeography.config.ConfigInit;
import top.qiguaiaaaa.fluidgeography.config.ConfigurationLoader;
import top.qiguaiaaaa.fluidgeography.handler.FluidHandler;
import top.qiguaiaaaa.fluidgeography.handler.RegistryHandler;
import top.qiguaiaaaa.fluidgeography.handler.MoreRealityEventHandler;
import top.qiguaiaaaa.fluidgeography.api.simulation.SimulationMode;
import top.qiguaiaaaa.fluidgeography.util.BaseUtil;
import top.qiguaiaaaa.fluidgeography.util.MixinUtil;

import java.io.File;
import static top.qiguaiaaaa.fluidgeography.api.configs.SimulationConfig.SIMULATION_MODE;

public class CommonProxy {
    private static boolean isConfigInitialised = false;
    public static void earlyInit(){
        initConfig();
        File suggestedConfigurationFile = BaseUtil.getSuggestedConfigurationFile();
        if(suggestedConfigurationFile==null){
            return;
        }
        ConfigurationLoader.init(suggestedConfigurationFile);
        ConfigurationLoader.load();
    }
    public void preInit(FMLPreInitializationEvent event) {
        RegistryHandler.registerCapability();
        if(ConfigurationLoader.isInitialised()) return;
        if(!isConfigInitialised) initConfig();
        ConfigurationLoader.init(event.getSuggestedConfigurationFile());
        ConfigurationLoader.load();
    }
    public void init(FMLInitializationEvent event) {
        RegistryHandler.registerEventHandler();
    }


    public void postInit(FMLPostInitializationEvent event) {
        MixinUtil.linkLiquidWithFluid();
        FluidHandler.initRegisteredFluids();
        if(SIMULATION_MODE.getValue() == SimulationMode.MORE_REALITY){
            MoreRealityEventHandler.onPostInit(event);
        }
    }

    private static void initConfig(){
        ConfigInit.initConfigs();
        isConfigInitialised = true;
    }
}
