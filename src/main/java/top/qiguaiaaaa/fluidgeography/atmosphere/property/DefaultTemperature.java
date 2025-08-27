package top.qiguaiaaaa.fluidgeography.atmosphere.property;

import net.minecraft.util.ResourceLocation;
import top.qiguaiaaaa.fluidgeography.api.FGInfo;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.property.TemperatureProperty;
import top.qiguaiaaaa.fluidgeography.atmosphere.state.DefaultTemperatureState;

public class DefaultTemperature extends TemperatureProperty {
    public static final DefaultTemperature TEMPERATURE = new DefaultTemperature();

    protected DefaultTemperature(){
        setRegistryName(new ResourceLocation(FGInfo.getModId(),"temperature"));
    }

    @Override
    public DefaultTemperatureState getStateInstance() {
        return new DefaultTemperatureState(-100);
    }
}
