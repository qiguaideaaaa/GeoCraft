package top.qiguaiaaaa.geocraft.state;

import top.qiguaiaaaa.geocraft.api.property.TemperatureProperty;
import top.qiguaiaaaa.geocraft.api.state.TemperatureState;
import top.qiguaiaaaa.geocraft.property.DeepTemperature;

/**
 * 地下10m处温度
 */
public class DeepTemperatureState extends TemperatureState {
    public DeepTemperatureState(float temp) {
        super(temp);
    }

    @Override
    public boolean isInitialised() {
        return temperature > 0 && !Float.isInfinite(temperature);
    }

    @Override
    public String getNBTTagKey() {
        return "dtemp";
    }

    @Override
    public TemperatureProperty getProperty() {
        return DeepTemperature.DEEP_TEMPERATURE;
    }
}
