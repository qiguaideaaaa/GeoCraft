package top.qiguaiaaaa.fluidgeography.atmosphere.listener;

import top.qiguaiaaaa.fluidgeography.api.FGInfo;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.listener.InformationLoggingTracker;
import top.qiguaiaaaa.fluidgeography.api.util.io.FileLogger;

/**
 * 大气水量追踪器
 */
public class WaterTracker extends InformationLoggingTracker {
    public WaterTracker(FileLogger logger, int time) {
        super(logger, time);
    }

    @Override
    public void notifyListener(Atmosphere atmosphere) {
        String msg = String.format("%d,%d",atmosphere.getAtmosphereWorldInfo().getWorld().getTotalWorldTime(),atmosphere.get水量());
        logger.println(msg);
        FGInfo.getLogger().info("track atmosphere water ({} mB)",msg);
        nowTime++;
        this.checkLoggingTime(atmosphere);
    }
}
