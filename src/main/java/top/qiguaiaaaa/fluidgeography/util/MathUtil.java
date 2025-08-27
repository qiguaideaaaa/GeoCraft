package top.qiguaiaaaa.fluidgeography.util;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import top.qiguaiaaaa.fluidgeography.api.util.math.Degree;

public final class MathUtil {
    public static Degree 计算与水平面夹角(Vec3d vec){
        double len = vec.length();
        double rad = Math.asin(Math.abs(vec.y)/len);
        return new Degree(rad,true);
    }

    /**
     * 获取以指定水平方向为正方向的情况下，对应速度的包括方向的大小
     * @param speed 原速度
     * @param dir 指定正方向
     * @return 速度大小，正方向为dir指定方向。需要注意，绝对值不会是原速度在水平方向上的投影，而是其包括了竖直方向分量的实际大小。除非其在水平方向上没有速度，这种情况下一定为0.
     */
    public static double 获得带水平正负方向的速度(Vec3d speed, EnumFacing dir){
        double b = new Vec3d(dir.getDirectionVec()).dotProduct(speed);
        if(b == 0) return 0;
        return speed.length()*(b>0?1:-1);
    }
}
