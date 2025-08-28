package top.qiguaiaaaa.geocraft.handler;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import top.qiguaiaaaa.geocraft.GeoCraft;
import top.qiguaiaaaa.geocraft.api.atmosphere.property.GeographyProperty;
import top.qiguaiaaaa.geocraft.atmosphere.DefaultAtmosphereProvider;

@Mod.EventBusSubscriber
public final class CommonEventHandler {

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Chunk> event){
        DefaultAtmosphereProvider defaultAtmosphereProvider = new DefaultAtmosphereProvider();
        World world = event.getObject().getWorld();
        if(world.isRemote) return;
        event.addCapability(new ResourceLocation(GeoCraft.MODID,"atmosphere"), defaultAtmosphereProvider);
    }

    @SubscribeEvent
    public static void onRegisterAtmosphereProperty(RegistryEvent.Register<GeographyProperty> event){
        RegistryHandler.registerGeographyProperties(event);
    }

}
