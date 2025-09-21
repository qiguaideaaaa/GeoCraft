package top.qiguaiaaaa.geocraft;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.Logger;
import top.qiguaiaaaa.geocraft.api.atmosphere.AtmosphereSystemRunner;
import top.qiguaiaaaa.geocraft.api.atmosphere.storage.AtmosphereRegionFileCache;
import top.qiguaiaaaa.geocraft.command.CommandAtmosphere;
import top.qiguaiaaaa.geocraft.geography.fluid_physics.FluidPressureSearchManager;
import top.qiguaiaaaa.geocraft.geography.fluid_physics.FluidUpdateManager;
import top.qiguaiaaaa.geocraft.handler.BlockUpdater;
import top.qiguaiaaaa.geocraft.world.gen.GeoCraftPostPopulatingGenerator;

@Mod(modid = GeoCraft.MODID, name = GeoCraft.NAME, version = GeoCraft.VERSION, dependencies = "required:mixinbooter;",acceptableRemoteVersions = "*",useMetadata = true)
public class GeoCraft {
    public static final String MODID = "geocraft";
    public static final String NAME = "Geo Craft";
    public static final String VERSION = "0.1";
    @SidedProxy(clientSide = "top.qiguaiaaaa.geocraft.ClientProxy",serverSide = "top.qiguaiaaaa.geocraft.CommonProxy")
    private static CommonProxy proxy;
    private static Logger logger;

    private static Thread pressureThread;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {;
        logger = event.getModLog();
        proxy.preInit(event);
        GameRegistry.registerWorldGenerator(new GeoCraftPostPopulatingGenerator(),100000);
    }
    @EventHandler
    public void init(FMLInitializationEvent event){
        proxy.init(event);
    }
    @EventHandler
    public void postInit(FMLPostInitializationEvent event){
        proxy.postInit(event);
    }
    @EventHandler
    public void onServerStarting(FMLServerStartingEvent event){
        event.registerServerCommand(new CommandAtmosphere());
        FluidPressureSearchManager pressureSearchManager = new FluidPressureSearchManager();
        pressureThread = new Thread(pressureSearchManager,FluidPressureSearchManager.class.toString());
        pressureThread.start();
    }

    @EventHandler
    public void onServerStopping(FMLServerStoppingEvent event){
        AtmosphereSystemRunner.onServerStopping(event);
    }

    @EventHandler
    public void onServerStop(FMLServerStoppedEvent event){
        AtmosphereRegionFileCache.clearRegionFileReferences();
        FluidUpdateManager.onServerStop();
        BlockUpdater.onServerStop();
        AtmosphereSystemRunner.onServerStopped(event);
        if(pressureThread != null && pressureThread.isAlive()){
            pressureThread.interrupt();
        }
    }
    public static Logger getLogger(){
        return logger;
    }
}
