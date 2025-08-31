package top.qiguaiaaaa.geocraft.property;

import net.minecraft.util.ResourceLocation;
import top.qiguaiaaaa.geocraft.GeoCraft;
import top.qiguaiaaaa.geocraft.api.property.TemperatureProperty;
import top.qiguaiaaaa.geocraft.state.DefaultTemperatureState;

public class DefaultTemperature extends TemperatureProperty {
    public static final DefaultTemperature TEMPERATURE = new DefaultTemperature();

    protected DefaultTemperature(){
        setRegistryName(new ResourceLocation(GeoCraft.MODID,"temperature"));
    }

    @Override
    public DefaultTemperatureState getStateInstance() {
        return new DefaultTemperatureState(TemperatureProperty.UNAVAILABLE);
    }
}
