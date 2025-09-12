package top.qiguaiaaaa.geocraft.geography.state;

import top.qiguaiaaaa.geocraft.api.property.TemperatureProperty;
import top.qiguaiaaaa.geocraft.api.state.TemperatureState;
import top.qiguaiaaaa.geocraft.geography.property.DeepTemperature;

import javax.annotation.Nonnull;

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

    @Nonnull
    @Override
    public String getNBTTagKey() {
        return "dtemp";
    }

    @Nonnull
    @Override
    public TemperatureProperty getProperty() {
        return DeepTemperature.DEEP_TEMPERATURE;
    }
}
