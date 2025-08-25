package top.qiguaiaaaa.fluidgeography.api.util;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.storage.WorldInfo;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.property.TemperatureProperty;
import top.qiguaiaaaa.fluidgeography.api.util.math.Degree;

public final class AtmosphereUtil {
    public static double getSunEnergyPerChunk(WorldInfo worldInfo){
        return Math.sin(getSunHeight(worldInfo).getRadian())* FinalFactors.每大气刻单位区块获得能量;
    }
    /**
     * 获取太阳高度角
     * @return 太阳高度角,单位为rad
     */
    public static Degree getSunHeight(WorldInfo worldInfo){
        long dayTime= (worldInfo.getWorldTime()+6000)%24000;
        if(dayTime<6000 || dayTime>18000) return new Degree(0);
        return new Degree((Math.PI*((6000-Math.abs(12000-dayTime))/6000.0d))/2.0,true);
    }
    /**
     * 根据太阳高度角和方位角计算太阳方向向量
     * 注意：这个向量是从太阳指向地面的方向
     *
     * @param 高度角 太阳光线与水平面的夹角
     * @param 方位角 太阳相对于正北的方向角（0°为正北，90°为正东，180°为正南，270°为正西）
     * @return 单位方向向量
     */
    public static Vec3d calculateSunDirection(Degree 高度角, Degree 方位角) {
        double sunH = 高度角.getRadian();
        double direction = 方位角.getRadian();

        // X: 东-西方向（东为正）
        // Z: 北-南方向（南为正）
        // Y: 垂直方向（上为正）
        double x = Math.sin(direction) * Math.cos(sunH);
        double z = -Math.cos(direction) * Math.cos(sunH);
        double y = -Math.sin(sunH);

        return new Vec3d(x, y, z).normalize();
    }

    /**
     * 使用Tetens公式计算指定温度下的饱和水汽压
     * @param temperature 温度
     * @return 饱和水汽压，单位为帕 Pa
     */
    public static double 计算饱和水汽压(double temperature){
        return 610.78 * Math.exp((17.27 * TemperatureProperty.toCelsiusFromKelvin(temperature)) / (TemperatureProperty.toCelsiusFromKelvin(temperature) + 237.3));
    }

    public static final class FinalFactors {
        public static final double 大气单元边长 = 16.0;
        public static final double 大气单元底面积 = 大气单元边长 * 大气单元边长;
        public static final double 低层大气厚度 = 2000.0;
        public static final int 对流层顶海拔 =17000;
        public static final int 太阳常数 = 1361;
        public static final double 斯特藩_玻尔兹曼常数 = 5.670374419e-8; // 斯特藩-玻尔兹曼常数 W·m^-2·K^-4
        public static final double 每大气刻单位区块获得能量 = 太阳常数 *大气单元底面积*216;
        public static final double 每大气刻损失能量常数 = 斯特藩_玻尔兹曼常数*大气单元底面积*216;
        public static final int WATER_MELT_LATENT_HEAT_PER_QUANTA = 41750000;
        public static final int WATER_EVAPORATE_LATENT_HEAT_PER_QUANTA = 283250000;

        public static final double 质量消光系数_云 = 5000.0;
        public static final double 质量消光系数_气体 = 1e-5;
        public static final double 质量消光系数_水汽 = 0.03;
        // 标准大气常数（对流层内）
        public static final double 海平面气压 = 101325.0;    // Pa
        public static final double 重力加速度 = 9.80665;      // m/s²
        public static final double 对流层温度直减率 = 0.0065;       // K/m
        public static final double 干绝热温度直减率 = 0.0098;
        public static final double 气体常数  = 8.314462618;  // FE/(mol·K)
        public static final double 干空气比热容 = 287; // FE/(kg·K)
        public static final double 干空气摩尔质量 = 0.0289644;    // kg/mol
        public static final double 湿空气比热容 = 461.5;
        public static final double 水摩尔质量 = 0.01801528;
        public static final double 水汽长波吸收系数 = 0.1;
        public static final double 液态水长波吸收系数 = 0.2;
        public static final double 温室气体浓度 = 400.0/1000000;
        public static final double 温室气体吸收系数 = 0.0004;

    }
}
