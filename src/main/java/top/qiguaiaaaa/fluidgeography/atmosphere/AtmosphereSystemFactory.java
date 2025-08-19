package top.qiguaiaaaa.fluidgeography.atmosphere;

import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.AtmosphereSystemManager;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.AtmosphereWorldInfo;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.AtmosphereWorldType;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.IAtmosphereSystem;
import top.qiguaiaaaa.fluidgeography.api.configs.AtmosphereConfig;
import top.qiguaiaaaa.fluidgeography.atmosphere.model.NewAtmosphereModel;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public final class AtmosphereSystemFactory {
    @SubscribeEvent
    public static void onWorldUpdate(TickEvent.WorldTickEvent event){
        if(event.phase == TickEvent.Phase.START) return;
        WorldServer server = getValidWorld(event.world);
        if(server == null) return;
        IAtmosphereSystem system = AtmosphereSystemManager.getAtmosphereSystem(server);
        if(system == null){
            system = createAtmosphereSystem(server);
            AtmosphereSystemManager.addAtmosphereSystem(server,system);
        }
        system.updateTick();
    }
    public static AtmosphereSystem createAtmosphereSystem(WorldServer world){
        AtmosphereWorldInfo info = new AtmosphereWorldInfo(world,new NewAtmosphereModel());
        int dimension = world.provider.getDimension();
        if(AtmosphereConfig.CLOSED_DIMENSIONS.containsEquivalent(dimension)){
            info.setType(AtmosphereWorldType.CLOSED);
        }else if(AtmosphereConfig.CONSTANT_TEMP_DIMENSIONS.containsEquivalent(dimension)){
            info.setType(AtmosphereWorldType.TEMP_CONSTANT);
        }else{
            info.setType(AtmosphereWorldType.NORMAL);
        }
        return new AtmosphereSystem(world,info);
    }
    @Nullable
    private static WorldServer getValidWorld(World world){
        if(world.isRemote || (!(world instanceof WorldServer))) return null;
        return (WorldServer) world;
    }
}
