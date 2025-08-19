package top.qiguaiaaaa.fluidgeography.atmosphere.property;

import net.minecraft.util.ResourceLocation;
import top.qiguaiaaaa.fluidgeography.api.FGInfo;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.property.TemperatureProperty;
import top.qiguaiaaaa.fluidgeography.atmosphere.state.GroundTemperatureState;

public class GroundTemperature extends TemperatureProperty {
    public static final GroundTemperature GROUND_TEMPERATURE = new GroundTemperature();

    public GroundTemperature() {
        super(false,false);
        setRegistryName(new ResourceLocation(FGInfo.getModId(),"ground_temperature"));
    }

    @Override
    public GroundTemperatureState getStateInstance() {
        return new GroundTemperatureState(300);
    }
}
