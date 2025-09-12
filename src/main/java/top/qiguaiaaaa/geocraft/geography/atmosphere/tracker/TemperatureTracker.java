package top.qiguaiaaaa.geocraft.geography.atmosphere.tracker;

import net.minecraft.util.math.BlockPos;
import top.qiguaiaaaa.geocraft.GeoCraft;
import top.qiguaiaaaa.geocraft.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.geocraft.api.property.TemperatureProperty;
import top.qiguaiaaaa.geocraft.api.atmosphere.tracker.InformationLoggingTracker;
import top.qiguaiaaaa.geocraft.api.util.io.FileLogger;

/**
 * 大气温度追踪器
 */
public class TemperatureTracker extends InformationLoggingTracker {
    BlockPos pos;
    public TemperatureTracker(FileLogger logger, BlockPos pos, int time) {
        super(logger, time);
        this.pos = pos;
        logger.println("Time,Lower T,Ground T");
    }
    @Override
    public void notify(Atmosphere atmosphere) {
        double temp = atmosphere.getAtmosphereTemperature(pos);
        String msg = String.format("%d,%f,%f",atmosphere.getAtmosphereWorldInfo().getWorld().getTotalWorldTime(), TemperatureProperty.toCelsiusFromKelvin(temp),
                atmosphere.getUnderlying().getTemperature().getCelsius());
        logger.println(msg);
        GeoCraft.getLogger().info("track atmosphere temp ({})",msg);
        nowTime++;
        checkLoggingTime(atmosphere);
    }
}
