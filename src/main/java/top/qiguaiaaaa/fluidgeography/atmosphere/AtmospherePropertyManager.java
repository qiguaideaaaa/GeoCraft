package top.qiguaiaaaa.fluidgeography.atmosphere;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import top.qiguaiaaaa.fluidgeography.FluidGeography;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.property.AtmosphereProperty;

import java.util.*;

@Mod.EventBusSubscriber
public final class AtmospherePropertyManager {
    private static boolean isLoaded = false;
    private static final ResourceLocation NAME = new ResourceLocation(FluidGeography.MODID,"atmosphere_property");
    private static IForgeRegistry<AtmosphereProperty> properties;
    private static final Set<AtmosphereProperty> windEffectedProperties = new HashSet<>();
    private static final Set<AtmosphereProperty> flowableProperties = new HashSet<>();

    @SubscribeEvent
    public static void onNewRegistryEvent(RegistryEvent.NewRegistry registry){
        if(isLoaded) return;
        properties = new RegistryBuilder<AtmosphereProperty>()
                .setName(NAME)
                .setType(AtmosphereProperty.class)
                .create();
        RegistryEvent<AtmosphereProperty> event = new RegistryEvent.Register<>(NAME,properties);
        MinecraftForge.EVENT_BUS.post(event);
        sortProperties();
        isLoaded = true;
    }

    public static Iterator<AtmosphereProperty> getPropertyIterator() {
        return properties.iterator();
    }

    public static IForgeRegistry<AtmosphereProperty> getProperties() {
        return properties;
    }

    public static Set<AtmosphereProperty> getWindEffectedProperties() {
        return windEffectedProperties;
    }
    public static Set<AtmosphereProperty> getFlowableProperties() {
        return flowableProperties;
    }

    private static void sortProperties(){
        for(AtmosphereProperty property:properties){
            if(property.haveWindEffect())
                windEffectedProperties.add(property);
            if(property.isFlowable())
                flowableProperties.add(property);
        }
    }
}
