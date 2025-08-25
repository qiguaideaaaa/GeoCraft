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
    public static double 获得带水平正负方向的速度(Vec3d speed, EnumFacing dir){
        return speed.length()*(new Vec3d(dir.getDirectionVec()).dotProduct(speed)>0?1:-1);
    }
}
