package top.qiguaiaaaa.geocraft.geography.property;

import net.minecraft.util.ResourceLocation;
import top.qiguaiaaaa.geocraft.GeoCraft;
import top.qiguaiaaaa.geocraft.api.property.TemperatureProperty;
import top.qiguaiaaaa.geocraft.geography.state.DeepTemperatureState;

import javax.annotation.Nonnull;

public class DeepTemperature extends TemperatureProperty {
    public static final DeepTemperature DEEP_TEMPERATURE = new DeepTemperature();
    public DeepTemperature(){
        setRegistryName(new ResourceLocation(GeoCraft.MODID,"deep_temperature"));
    }
    @Nonnull
    @Override
    public DeepTemperatureState getStateInstance() {
        return new DeepTemperatureState(TemperatureProperty.UNAVAILABLE);
    }
}
