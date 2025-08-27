package top.qiguaiaaaa.fluidgeography.atmosphere;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import top.qiguaiaaaa.fluidgeography.FluidGeography;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.property.AtmosphereProperty;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.property.GeographyProperty;
import top.qiguaiaaaa.fluidgeography.util.registry.ServerOnlyRegistryBuilder;

import java.util.*;

@Mod.EventBusSubscriber
public final class GeographyPropertyManager {
    private static boolean isLoaded = false;
    private static final ResourceLocation NAME = new ResourceLocation(FluidGeography.MODID,"geography_property");
    private static IForgeRegistry<GeographyProperty> properties;
    private static final Set<AtmosphereProperty> atmosphereProperties = new HashSet<>();
    private static final Set<AtmosphereProperty> windEffectedProperties = new HashSet<>();
    private static final Set<AtmosphereProperty> flowableProperties = new HashSet<>();

    @SubscribeEvent
    public static void onNewRegistryEvent(RegistryEvent.NewRegistry registry){
        if(isLoaded) return;
        properties = new ServerOnlyRegistryBuilder<GeographyProperty>()
                .setType(GeographyProperty.class)
                .create();
        RegistryEvent<GeographyProperty> event = new RegistryEvent.Register<>(NAME,properties);
        MinecraftForge.EVENT_BUS.post(event);
        sortProperties();
        isLoaded = true;
    }

    public static IForgeRegistry<GeographyProperty> getProperties() {
        return properties;
    }
    public static Set<AtmosphereProperty> getAtmosphereProperties(){return atmosphereProperties;}

    public static Set<AtmosphereProperty> getWindEffectedProperties() {
        return windEffectedProperties;
    }
    public static Set<AtmosphereProperty> getFlowableProperties() {
        return flowableProperties;
    }

    private static void sortProperties(){
        for(GeographyProperty property:properties){
            if(property instanceof AtmosphereProperty){
                AtmosphereProperty atmosphereProperty = (AtmosphereProperty) property;
                atmosphereProperties.add(atmosphereProperty);
                if(atmosphereProperty.haveWindEffect())
                    windEffectedProperties.add(atmosphereProperty);
                if(atmosphereProperty.isFlowable())
                    flowableProperties.add(atmosphereProperty);
            }

        }
    }
}
