package top.qiguaiaaaa.geocraft.api.atmosphere.accessor;

import net.minecraft.util.math.BlockPos;

public interface IAtmosphereAccessor {
    boolean isAtmosphereLoaded(BlockPos pos,int radis);
    double getTemperature(BlockPos pos);
    double getPressure(BlockPos pos);
    double getWaterPressure(BlockPos pos);
}
