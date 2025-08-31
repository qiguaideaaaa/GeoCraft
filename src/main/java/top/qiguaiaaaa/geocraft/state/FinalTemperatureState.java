package top.qiguaiaaaa.geocraft.state;

import top.qiguaiaaaa.geocraft.api.property.TemperatureProperty;
import top.qiguaiaaaa.geocraft.api.state.TemperatureState;
import top.qiguaiaaaa.geocraft.property.FinalTemperature;

public class FinalTemperatureState extends TemperatureState {
    public FinalTemperatureState(float temp) {
        super(temp);
    }

    @Override
    public void add(double temp) {}

    @Override
    public void add热量(double Q, double 热容) {}

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

    @Override
    public String getNBTTagKey() {
        return "ftemp";
    }

    @Override
    public TemperatureProperty getProperty() {
        return FinalTemperature.FINAL_TEMPERATURE;
    }
}
