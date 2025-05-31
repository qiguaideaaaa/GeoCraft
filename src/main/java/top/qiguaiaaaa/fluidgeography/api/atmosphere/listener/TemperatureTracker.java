package top.qiguaiaaaa.fluidgeography.api.atmosphere.listener;

import top.qiguaiaaaa.fluidgeography.api.FGInfo;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.fluidgeography.api.util.io.FileLogger;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.property.AtmosphereTemperature;

/**
 * 大气温度追踪器
 */
public class TemperatureTracker extends InformationLoggingTracker {
    public TemperatureTracker(FileLogger logger, int time) {
        super(logger, time);
    }
    @Override
    public void notifyListener(Atmosphere atmosphere) {
        String msg = String.format("%d,%f",atmosphere.getAtmosphereWorldInfo().getWorld().getTotalWorldTime(),atmosphere.getTemperature()- AtmosphereTemperature.ICE_POINT);
        logger.println(msg);
        FGInfo.getLogger().info("track atmosphere temp ({})",msg);
        nowTime++;
        checkLoggingTime(atmosphere);
    }
}
