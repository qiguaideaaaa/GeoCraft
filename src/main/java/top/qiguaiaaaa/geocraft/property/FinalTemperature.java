package top.qiguaiaaaa.geocraft.property;

import top.qiguaiaaaa.geocraft.GeoCraft;
import top.qiguaiaaaa.geocraft.api.property.TemperatureProperty;
import top.qiguaiaaaa.geocraft.api.state.TemperatureState;
import top.qiguaiaaaa.geocraft.state.FinalTemperatureState;

public class FinalTemperature extends TemperatureProperty{
    public static final FinalTemperature FINAL_TEMPERATURE = new FinalTemperature();
    private FinalTemperature(){
        setRegistryName(GeoCraft.MODID,"final_temperature");
    }
    @Override
    public TemperatureState getStateInstance() {
        return new FinalTemperatureState(TemperatureProperty.STANDARD_TEMP);
    }
}
