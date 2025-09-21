package top.qiguaiaaaa.geocraft.api.atmosphere;

import net.minecraft.world.WorldServer;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import top.qiguaiaaaa.geocraft.api.atmosphere.system.IAtmosphereSystem;
import top.qiguaiaaaa.geocraft.api.event.EventFactory;
import top.qiguaiaaaa.geocraft.api.event.atmosphere.AtmosphereGenerateEvent;

import static top.qiguaiaaaa.geocraft.api.atmosphere.AtmosphereSystemManager.*;

@Mod.EventBusSubscriber
public class AtmosphereSystemRunner {
    static {
        EventFactory.EVENT_BUS.register(AtmosphereSystemRunner.class);
    }
    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event){
        if(event.phase == TickEvent.Phase.START) return;
        IAtmosphereSystem system = getAtmosphereSystem(event.world);
        if(system != null){
            system.updateTick();
        }
    }

    @SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load event){
        WorldServer server = getValidWorld(event.getWorld());
        if(server == null) return;
        IAtmosphereSystem system = getAtmosphereSystem(server);
        if(system == null) system = EventFactory.onAtmosphereSystemCreate(server);
        if(system == null) return;
        atmosphereSystems.put(server,system);
    }

    @SubscribeEvent
    public static void onWorldSave(WorldEvent.Save event){
        IAtmosphereSystem system = getAtmosphereSystem(event.getWorld());
        if(system == null) return;
        system.onWorldSave();
    }

    public static void onServerStopping(FMLServerStoppingEvent event){
        for(IAtmosphereSystem system:atmosphereSystems.values()){
            if(system == null) continue;
            system.onServerStopping(event);
        }
    }

    public static void onServerStopped(FMLServerStoppedEvent event){
        for(IAtmosphereSystem system:atmosphereSystems.values()){
            if(system == null) continue;
            system.onServerStopped(event);
        }
        atmosphereSystems.clear();
    }

    @SubscribeEvent
    public static void onPreAtmosphereGenerate(AtmosphereGenerateEvent.Pre event){
        IAtmosphereSystem system = getAtmosphereSystem(event.getWorld());
        if(system == null) return;
        system.onChunkGenerated(event.getChunk());
    }
    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event){
        IAtmosphereSystem system = getAtmosphereSystem(event.getWorld());
        if(system == null)return;
        if(!event.getChunk().isTerrainPopulated()) return;
        system.onChunkLoaded(event.getChunk());
    }

    @SubscribeEvent
    public void onChunkUnLoad(ChunkEvent.Unload event){
        IAtmosphereSystem system = getAtmosphereSystem(event.getWorld());
        if(system == null) return;
        system.onChunkUnloaded(event.getChunk());
    }
}
