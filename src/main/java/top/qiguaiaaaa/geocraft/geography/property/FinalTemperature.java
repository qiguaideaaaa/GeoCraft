package top.qiguaiaaaa.geocraft.geography.property;

import top.qiguaiaaaa.geocraft.GeoCraft;
import top.qiguaiaaaa.geocraft.api.property.TemperatureProperty;
import top.qiguaiaaaa.geocraft.api.state.TemperatureState;
import top.qiguaiaaaa.geocraft.geography.state.FinalTemperatureState;

import javax.annotation.Nonnull;

public class FinalTemperature extends TemperatureProperty{
    public static final FinalTemperature FINAL_TEMPERATURE = new FinalTemperature();
    private FinalTemperature(){
        setRegistryName(GeoCraft.MODID,"final_temperature");
    }
    @Nonnull
    @Override
    public TemperatureState getStateInstance() {
        return new FinalTemperatureState(TemperatureProperty.STANDARD_TEMP);
    }
}
