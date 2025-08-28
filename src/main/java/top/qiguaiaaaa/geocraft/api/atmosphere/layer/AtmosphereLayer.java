package top.qiguaiaaaa.geocraft.api.atmosphere.layer;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import top.qiguaiaaaa.geocraft.api.atmosphere.property.FluidProperty;
import top.qiguaiaaaa.geocraft.api.atmosphere.state.FluidState;

import javax.annotation.Nullable;

public interface AtmosphereLayer extends Layer {

    /**
     * 向该层大气添加气态形式的水
     * @param pos 添加位置
     * @param amount 水量，
     * @return 是否成功
     */
    boolean addSteam(@Nullable BlockPos pos,int amount);

    /**
     * 向该层大气添加液态形式的水
     * @param pos 添加位置
     * @param amount 水量，单位mB
     * @return 是否成功
     */
    boolean addWater(@Nullable BlockPos pos,int amount);

    /**
     * 获得某地的气压
     * @param pos 某地
     * @return 大气压，单位帕 Pa
     */
    double getPressure(BlockPos pos);

    /**
     * 获得某位置的大气水汽压
     * @return 大气水汽压，单位帕 Pa
     */
    double getWaterPressure(BlockPos pos);

    /**
     * 获取整体水汽压
     * @return 大气水汽压,单位帕 Pa
     */
    double getWaterPressure();

    /**
     * 获取大气温度
     * @param pos 某处
     * @return 大气温度
     */
    @Override
    default float getTemperature(BlockPos pos){
        return getTemperature(pos,false);
    }

    /**
     * 获得某处的温度
     * @param pos 位置
     * @param notAir 如果需要获取非气体温度，则为true。否则返回气体温度。
     * @return 温度
     */
    float getTemperature(BlockPos pos,boolean notAir);

    /**
     * 获得某地的风,注意地面层是不会有风的
     * @param pos 位置
     * @return 代表风速的三维向量。若没有风或无风则返回零向量。
     */
    Vec3d getWind(BlockPos pos);

    /**
     * 获取大气成分状态
     * @param property 大气成分
     * @return 对应的大气状态
     */
    @Nullable
    FluidState getGas(FluidProperty property);

    /**
     * 获取该大气层的气态水水量状态
     * @return 气态水水量状态,不一定存在
     */
    @Nullable
    FluidState getSteam();
}
