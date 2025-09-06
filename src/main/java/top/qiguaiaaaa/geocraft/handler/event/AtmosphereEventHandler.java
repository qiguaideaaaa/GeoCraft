package top.qiguaiaaaa.geocraft.handler.event;

import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import top.qiguaiaaaa.geocraft.api.atmosphere.AtmosphereWorldInfo;
import top.qiguaiaaaa.geocraft.api.atmosphere.gen.DefaultAtmosphereDataProvider;
import top.qiguaiaaaa.geocraft.api.atmosphere.storage.DefaultAtmosphereDataLoader;
import top.qiguaiaaaa.geocraft.api.atmosphere.system.IAtmosphereSystem;
import top.qiguaiaaaa.geocraft.api.event.atmosphere.AtmosphereSystemEvent;
import top.qiguaiaaaa.geocraft.atmosphere.system.HallAtmosphereSystem;
import top.qiguaiaaaa.geocraft.atmosphere.system.SurfaceAtmosphereSystem;

import java.io.File;

public class AtmosphereEventHandler {
    @SubscribeEvent
    public static void createAtmosphereSystem(AtmosphereSystemEvent.AtmosphereSystemCreateEvent event){
        WorldServer server= event.getWorld();
        AtmosphereWorldInfo info = new AtmosphereWorldInfo(server);
        IAtmosphereSystem system = null;

        WorldProvider provider = server.provider;
        int dimension = provider.getDimension();
        String saveFolder = provider.getSaveFolder();
        if(saveFolder == null) saveFolder = "DIM"+provider.getDimension();


        if(dimension==0){
            DefaultAtmosphereDataLoader loader = new DefaultAtmosphereDataLoader(new File(server.getSaveHandler().getWorldDirectory(),saveFolder));
            system = new SurfaceAtmosphereSystem(server,info,new DefaultAtmosphereDataProvider(server,loader));

        }else if(dimension == -1){
            DefaultAtmosphereDataLoader loader = new DefaultAtmosphereDataLoader(new File(server.getSaveHandler().getWorldDirectory(),saveFolder));
            system = new HallAtmosphereSystem(server,info,new DefaultAtmosphereDataProvider(server,loader));
        }

        event.setSystem(system);
    }
}
