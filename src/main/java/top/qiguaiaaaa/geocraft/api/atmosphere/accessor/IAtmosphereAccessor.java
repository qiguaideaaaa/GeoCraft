package top.qiguaiaaaa.geocraft.api.atmosphere.accessor;

import net.minecraft.util.math.BlockPos;
import top.qiguaiaaaa.geocraft.api.atmosphere.system.IAtmosphereSystem;

public interface IAtmosphereAccessor {
    IAtmosphereSystem getSystem();
    boolean isAtmosphereLoaded(BlockPos pos,int radis);
    double getTemperature(BlockPos pos);
    double getPressure(BlockPos pos);
    double getWaterPressure(BlockPos pos);
}
