package top.qiguaiaaaa.fluidgeography;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.Logger;
import top.qiguaiaaaa.fluidgeography.api.FGInfo;
import top.qiguaiaaaa.fluidgeography.command.CommandAtmosphere;

@Mod(modid = FluidGeography.MODID, name = FluidGeography.NAME, version = FluidGeography.VERSION, dependencies = "required:mixinbooter;")
public class FluidGeography
{
    public static final String MODID = "fluidgeography";
    public static final String NAME = "Fluid Geography";
    public static final String VERSION = "0.1";
    @SidedProxy(clientSide = "top.qiguaiaaaa.fluidgeography.ClientProxy",serverSide = "top.qiguaiaaaa.fluidgeography.CommonProxy")
    private static CommonProxy proxy;
    private static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {;
        logger = event.getModLog();
        proxy.preInit(event);
        FGInfo.setLogger(logger);
        FGInfo.setModId(MODID);
        FGInfo.setModVersion(VERSION);
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
    public void serverStarting(FMLServerStartingEvent event){
        event.registerServerCommand(new CommandAtmosphere());
    }
    public static Logger getLogger(){
        return logger;
    }
}
