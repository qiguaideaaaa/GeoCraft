package top.qiguaiaaaa.fluidgeography.atmosphere.listener;

import top.qiguaiaaaa.fluidgeography.api.FGInfo;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.layer.AtmosphereLayer;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.layer.UnderlyingLayer;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.listener.InformationLoggingTracker;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.state.GasState;
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
        int totalAmount = 0;
        AtmosphereLayer layer = atmosphere.getBottomLayer();

        while (layer != null){
            if(layer instanceof UnderlyingLayer){
                layer = layer.getUpperLayer();
                continue;
            }
            GasState water = layer.getWater();
            GasState steam = layer.getSteam();
            if(water != null) totalAmount += water.getAmount();
            if(steam != null) totalAmount += steam.getAmount();
            layer = layer.getUpperLayer();
        }
        String msg = String.format("%d,%d",atmosphere.getAtmosphereWorldInfo().getWorld().getTotalWorldTime(),totalAmount);
        logger.println(msg);
        FGInfo.getLogger().info("track atmosphere water ({} mB)",msg);
        nowTime++;
        this.checkLoggingTime(atmosphere);
    }
}
