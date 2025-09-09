package top.qiguaiaaaa.geocraft.handler.event;

import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import top.qiguaiaaaa.geocraft.GeoCraft;
import top.qiguaiaaaa.geocraft.api.atmosphere.AtmosphereWorldInfo;
import top.qiguaiaaaa.geocraft.api.atmosphere.gen.DefaultAtmosphereDataProvider;
import top.qiguaiaaaa.geocraft.api.atmosphere.storage.DefaultAtmosphereDataLoader;
import top.qiguaiaaaa.geocraft.api.atmosphere.system.IAtmosphereSystem;
import top.qiguaiaaaa.geocraft.api.configs.value.geo.AtmosphereSystemType;
import top.qiguaiaaaa.geocraft.api.event.atmosphere.AtmosphereSystemEvent;
import top.qiguaiaaaa.geocraft.atmosphere.system.HallAtmosphereSystem;
import top.qiguaiaaaa.geocraft.atmosphere.system.SurfaceAtmosphereSystem;
import top.qiguaiaaaa.geocraft.atmosphere.system.VanillaAtmosphereSystem;
import top.qiguaiaaaa.geocraft.configs.AtmosphereConfig;

import java.io.File;

public class AtmosphereEventHandler {
    @SubscribeEvent
    public static void createAtmosphereSystem(AtmosphereSystemEvent.Create event){
        WorldServer server= event.getWorld();
        WorldProvider provider = server.provider;
        int dimension = provider.getDimension();
        AtmosphereSystemType type = AtmosphereConfig.ATMOSPHERE_SYSTEM_TYPES.getValue().get(dimension);
        if(type == null || type == AtmosphereSystemType.NO_ATMOSPHERE_SYSTEM || type == AtmosphereSystemType.THIRD_PARTY_ATMOSPHERE_SYSTEM) return;
        AtmosphereWorldInfo info = new AtmosphereWorldInfo(server);
        IAtmosphereSystem system = null;


        String saveFolder = provider.getSaveFolder();
        if(saveFolder == null) saveFolder = "DIM"+provider.getDimension();


        if(type == AtmosphereSystemType.SURFACE_ATMOSPHERE_SYSTEM){
            DefaultAtmosphereDataLoader loader = new DefaultAtmosphereDataLoader(new File(server.getSaveHandler().getWorldDirectory(),saveFolder));
            system = new SurfaceAtmosphereSystem(server,info,new DefaultAtmosphereDataProvider(server,loader));
        }else if(type == AtmosphereSystemType.HALL_ATMOSPHERE_SYSTEM){
            DefaultAtmosphereDataLoader loader = new DefaultAtmosphereDataLoader(new File(server.getSaveHandler().getWorldDirectory(),saveFolder));
            system = new HallAtmosphereSystem(server,info,new DefaultAtmosphereDataProvider(server,loader));
        }else if(type == AtmosphereSystemType.VANILLA_ATMOSPHERE_SYSTEM){
            DefaultAtmosphereDataLoader loader = new DefaultAtmosphereDataLoader(new File(server.getSaveHandler().getWorldDirectory(),saveFolder));
            system = new VanillaAtmosphereSystem(server,info,new DefaultAtmosphereDataProvider(server,loader));
        }

        GeoCraft.getLogger().info("Dimension DIM{} is using atmosphere system type {}",dimension,type);

        event.setSystem(system);
    }
}
