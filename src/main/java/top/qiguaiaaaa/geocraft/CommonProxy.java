package top.qiguaiaaaa.geocraft;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import top.qiguaiaaaa.geocraft.configs.ConfigInit;
import top.qiguaiaaaa.geocraft.configs.ConfigurationLoader;
import top.qiguaiaaaa.geocraft.handler.FluidHandler;
import top.qiguaiaaaa.geocraft.handler.RegistryHandler;
import top.qiguaiaaaa.geocraft.handler.event.MoreRealityEventHandler;
import top.qiguaiaaaa.geocraft.api.configs.value.geo.SimulationMode;
import top.qiguaiaaaa.geocraft.util.BaseUtil;
import top.qiguaiaaaa.geocraft.util.MixinUtil;

import java.io.File;
import static top.qiguaiaaaa.geocraft.configs.SimulationConfig.SIMULATION_MODE;

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
        }else if(SIMULATION_MODE.getValue() == SimulationMode.VANILLA_LIKE){

        }
    }

    private static void initConfig(){
        ConfigInit.initConfigs();
        isConfigInitialised = true;
    }
}
