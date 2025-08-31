package top.qiguaiaaaa.geocraft.property;

import net.minecraft.util.ResourceLocation;
import top.qiguaiaaaa.geocraft.GeoCraft;
import top.qiguaiaaaa.geocraft.api.property.TemperatureProperty;
import top.qiguaiaaaa.geocraft.state.DeepTemperatureState;

public class DeepTemperature extends TemperatureProperty {
    public static final DeepTemperature DEEP_TEMPERATURE = new DeepTemperature();
    public DeepTemperature(){
        setRegistryName(new ResourceLocation(GeoCraft.MODID,"deep_temperature"));
    }
    @Override
    public DeepTemperatureState getStateInstance() {
        return new DeepTemperatureState(TemperatureProperty.UNAVAILABLE);
    }
}
