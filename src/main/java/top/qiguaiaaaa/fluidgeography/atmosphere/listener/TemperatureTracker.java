package top.qiguaiaaaa.fluidgeography.atmosphere.listener;

import net.minecraft.util.math.BlockPos;
import top.qiguaiaaaa.fluidgeography.api.FGInfo;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.listener.InformationLoggingTracker;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.property.TemperatureProperty;
import top.qiguaiaaaa.fluidgeography.api.util.io.FileLogger;

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
    public void notifyListener(Atmosphere atmosphere) {
        double temp = atmosphere.getAtmosphereTemperature(pos);
        String msg = String.format("%d,%f,%f",atmosphere.getAtmosphereWorldInfo().getWorld().getTotalWorldTime(), TemperatureProperty.toCelsiusFromKelvin(temp),
                atmosphere.getUnderlying().getTemperature().getCelsius());
        logger.println(msg);
        FGInfo.getLogger().info("track atmosphere temp ({})",msg);
        nowTime++;
        checkLoggingTime(atmosphere);
    }
}
