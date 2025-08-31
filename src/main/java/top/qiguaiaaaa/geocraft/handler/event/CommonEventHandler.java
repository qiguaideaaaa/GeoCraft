package top.qiguaiaaaa.geocraft.handler.event;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import top.qiguaiaaaa.geocraft.api.property.GeographyProperty;
import top.qiguaiaaaa.geocraft.handler.RegistryHandler;

@Mod.EventBusSubscriber
public final class CommonEventHandler {

    @SubscribeEvent
    public static void onRegisterAtmosphereProperty(RegistryEvent.Register<GeographyProperty> event){
        RegistryHandler.registerGeographyProperties(event);
    }

}
