package top.qiguaiaaaa.geocraft.geography.state;

import top.qiguaiaaaa.geocraft.api.property.TemperatureProperty;
import top.qiguaiaaaa.geocraft.api.state.TemperatureState;
import top.qiguaiaaaa.geocraft.geography.property.FinalTemperature;

import javax.annotation.Nonnull;

public class FinalTemperatureState extends TemperatureState {
    public FinalTemperatureState(float temp) {
        super(temp);
    }

    @Override
    public void add(double temp) {}

    @Override
    public void addHeat(double Q, double heatCapacity) {}

    @Override
    public boolean toBeSavedIntoNBT() {
        return false;
    }

    @Override
    public boolean toBeLoadedFromNBT() {
        return false;
    }

    @Override
    public boolean isInitialised() {
        return true;
    }

    @Nonnull
    @Override
    public String getNBTTagKey() {
        return "ftemp";
    }

    @Nonnull
    @Override
    public TemperatureProperty getProperty() {
        return FinalTemperature.FINAL_TEMPERATURE;
    }
}
