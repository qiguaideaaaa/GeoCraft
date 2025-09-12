package top.qiguaiaaaa.geocraft.geography.property;

import net.minecraft.util.ResourceLocation;
import top.qiguaiaaaa.geocraft.GeoCraft;
import top.qiguaiaaaa.geocraft.api.property.TemperatureProperty;
import top.qiguaiaaaa.geocraft.geography.state.DefaultTemperatureState;

import javax.annotation.Nonnull;

public class DefaultTemperature extends TemperatureProperty {
    public static final DefaultTemperature TEMPERATURE = new DefaultTemperature();

    protected DefaultTemperature(){
        setRegistryName(new ResourceLocation(GeoCraft.MODID,"temperature"));
    }

    @Nonnull
    @Override
    public DefaultTemperatureState getStateInstance() {
        return new DefaultTemperatureState(TemperatureProperty.UNAVAILABLE);
    }
}
