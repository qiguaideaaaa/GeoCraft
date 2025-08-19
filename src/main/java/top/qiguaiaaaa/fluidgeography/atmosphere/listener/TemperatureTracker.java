package top.qiguaiaaaa.fluidgeography.atmosphere.listener;

import top.qiguaiaaaa.fluidgeography.api.FGInfo;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.listener.InformationLoggingTracker;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.property.TemperatureProperty;
import top.qiguaiaaaa.fluidgeography.api.util.io.FileLogger;

/**
 * 大气温度追踪器
 */
public class TemperatureTracker extends InformationLoggingTracker {
    public TemperatureTracker(FileLogger logger, int time) {
        super(logger, time);
        logger.println("Time,Lower T,Ground T");
    }
    @Override
    public void notifyListener(Atmosphere atmosphere) {
        String msg = String.format("%d,%f,%f",atmosphere.getAtmosphereWorldInfo().getWorld().getTotalWorldTime(),atmosphere.get低层大气温度()- TemperatureProperty.ICE_POINT,
                atmosphere.get地表温度()- TemperatureProperty.ICE_POINT);
        logger.println(msg);
        FGInfo.getLogger().info("track atmosphere temp ({})",msg);
        nowTime++;
        checkLoggingTime(atmosphere);
    }
}
