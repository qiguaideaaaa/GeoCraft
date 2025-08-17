package top.qiguaiaaaa.fluidgeography.api.atmosphere.model;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.injection.At;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.state.AtmosphereStates;
import top.qiguaiaaaa.fluidgeography.api.util.math.Altitude;

/**
 * 大气模型
 * 大气系统基于模型而运作。通过提供不同的大气模型，可以实现不同的大气行为。
 */
public interface IAtmosphereModel {
    AtmosphereStates run(Atmosphere atmosphere, AtmosphereStates states, Chunk chunk);

    /**
     * 获取指定大气的近地面气压
     * @param atmosphere 大气
     * @return 气压，单位Pa
     */
    double getPressure(Atmosphere atmosphere);

    /**
     * 获取大气指定海拔的气压
     * @param atmosphere 大气
     * @param altitude 海拔
     * @return 气压,单位Pa
     */
    double getPressure(Atmosphere atmosphere, Altitude altitude);

    /**
     * 获取大气指定海拔的温度
     * @param atmosphere 大气
     * @param altitude 海拔
     * @return 温度
     */
    float getTemperature(Atmosphere atmosphere,Altitude altitude);

    /**
     * 获取区块的初始温度
     * @param chunk 区块
     * @return 初始温度
     */
    float getInitTemperature(Atmosphere atmosphere, Chunk chunk);

    /**
     * 获取大气指定位置的风速
     * @param atmosphere 大气
     * @param pos 方块位置,为游戏位置
     * @return 风速向量
     */
    Vec3d getWind(Atmosphere atmosphere, BlockPos pos);
}
