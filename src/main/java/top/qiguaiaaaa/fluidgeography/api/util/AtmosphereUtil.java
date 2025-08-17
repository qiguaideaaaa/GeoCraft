package top.qiguaiaaaa.fluidgeography.api.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.AtmosphereSystemManager;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.Underlying;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.property.AtmosphereTemperature;
import top.qiguaiaaaa.fluidgeography.api.configs.AtmosphereConfig;
import top.qiguaiaaaa.fluidgeography.api.util.math.Altitude;

public final class AtmosphereUtil {
    public static final int SUN_CONSTANT = 1361;
    public static final double 每大气刻损失能量常数 = 5.67*(Math.pow(10,-8))*256*216;
    public static final double 每大气刻单位区块获得能量 = SUN_CONSTANT *256*216;
    public static final int WATER_MELT_LATENT_HEAT_PER_QUANTA = 41750000;
    public static final int WATER_EVAPORATE_LATENT_HEAT_PER_QUANTA = 283250000;
    public static final double WATER_MOLAR_MASS = 0.01801528;
    public static double getSunEnergyPerChunk(WorldInfo worldInfo){
        return Math.sin(getSunHeight(worldInfo))* 每大气刻单位区块获得能量;
    }
    /**
     * 获取太阳高度角
     * @return 太阳高度角,单位为rad
     */
    public static double getSunHeight(WorldInfo worldInfo){
        long dayTime= (worldInfo.getWorldTime()+6000)%24000;
        if(dayTime<6000 || dayTime>18000) return 0;
        return (Math.PI*((6000-Math.abs(12000-dayTime))/6000.0d))/2.0;
    }

    /**
     * 获取太阳高度角，单位为角度
     * @return 太阳高度角，单位为角度
     */
    public static double getSunHeightDegree(WorldInfo worldInfo){
        return getSunHeight(worldInfo)/Math.PI*180;
    }
    public static double getHeatEnergyRadiationLoss(Atmosphere atmosphere,double 太阳辐射透过率){
        Underlying underlying = atmosphere.get下垫面();
        double groundRadiation = 每大气刻损失能量常数 *Math.pow(atmosphere.get低层大气温度(),4) *underlying.平均发射率;
//        double 地面温度 = atmosphere.get地表温度();
//
//        // 地面长波辐射 (W/m²)
//        double 地面长波辐射 = 每大气刻损失能量常数 * Math.pow(地面温度, 4) * underlying.平均发射率;
//
//        // 云层和大气的回辐射 (假设低层温度代表云温)
//        double 低层大气温度 = atmosphere.get低层大气温度();
//        double 云层发射率 = 1-太阳辐射透过率;
//        double 云层回辐射 = 每大气刻损失能量常数 * Math.pow(低层大气温度, 4) * 云层发射率;
//
//        // 大气回辐射（晴天时主要来源）
//        double 大气发射率 = get大气发射率(atmosphere);
//        double 大气回辐射 = 每大气刻损失能量常数 * Math.pow(低层大气温度, 4) * 大气发射率 * (1.0 - 云层发射率);
//
//        // 地面净辐射损失 = 地面辐射 - (云回辐射 + 大气回辐射)
//        double 地面净辐射损失 = 地面长波辐射 - (云层回辐射 + 大气回辐射);
//
//        // 地面辐射损失率系数（游戏调参用）
//        double 地面辐射损失系数 = AtmosphereConfig.GROUND_RADIATION_LOSS_RATE.getValue().value;
//
//        return 地面净辐射损失 * 地面辐射损失系数;
        return (groundRadiation *AtmosphereConfig.GROUND_RADIATION_LOSS_RATE.getValue().value
                +groundRadiation*(1 - AtmosphereConfig.GROUND_RADIATION_LOSS_RATE.getValue().value)*get大气发射率(atmosphere)*0.4)
                *太阳辐射透过率;
    }

    /**
     * 获取大气发射率
     */
    public static double get大气发射率(Atmosphere atmosphere){
        double 水汽压 = get大气水汽压(atmosphere)*0.01; // hPa
        double 云量 = 1- get大气透过率(atmosphere,atmosphere.getAtmosphereWorldInfo().getWorld().getWorldInfo());
        double 海拔 = atmosphere.get下垫面().get地面平均海拔().get物理海拔();

        // 基础发射率
        double 发射率 = 0.74 + 0.0049 * 水汽压;

        // 云修正
        发射率 *= (1 + 0.22 * 云量);

        // 海拔修正
        double 尺度高度 = 8000.0;
        double 海拔因子 = Math.exp(-海拔 / 尺度高度);
        发射率 *= 海拔因子;

        // 限制范围
        return MathHelper.clamp(发射率, 0.01, 1.0);
        //return MathHelper.clamp(0.75*Math.pow((get大气水汽压(atmosphere)*0.01)/atmosphere.get低层大气温度(),0.166),0.01,1.0);
    }

    /**
     * 获得大气水汽压
     * @return 大气水汽压，单位帕 Pa
     */
    public static double get大气水汽压(Atmosphere atmosphere){
        return atmosphere.get水量()*FinalFactors.气体常数*atmosphere.get低层大气温度()/(WATER_MOLAR_MASS*256*getAtmosphereHeight(atmosphere.get下垫面()));
    }
    public static double getAtmosphereHeight(Underlying underlying){
        return FinalFactors.对流层顶海拔-underlying.get地面平均海拔().get物理海拔();
    }
    public static double getWaterEvaporatePossibility(Atmosphere atmosphere){
        if(atmosphere.get低层大气温度()>= AtmosphereTemperature.BOILED_POINT) return 1;
        double possibility = 1.0d;
        possibility *= Math.pow(1.07,atmosphere.get低层大气温度() - AtmosphereTemperature.ICE_POINT)/Math.pow(1.07,100);
        possibility *= Math.sqrt(1-Math.pow(atmosphere.get水量()/1024.0,2)/1000.0);
        possibility *= 0.5;
        return possibility;
    }
    public static double getRainPossibility(Atmosphere atmosphere, BlockPos pos){
        float temp = atmosphere.get温度(pos,true);
        if(temp>= AtmosphereTemperature.BOILED_POINT) return 0;
        if(temp<= AtmosphereTemperature.ICE_POINT-100) return 1;
        double strong = atmosphere.getRainStrong();
        return strong/(strong+16384);
    }

    /**
     * 获得大气透过率
     * @param atmosphere 大气
     * @param worldInfo 世界信息
     * @return 大气透过率，云越薄越趋近于1
     */
    public static double get大气透过率(Atmosphere atmosphere, WorldInfo worldInfo) {
        double strong = atmosphere.getRainStrong(); // 0 = 无云, ~100 = 极强降雨
        double sunHeight = getSunHeight(worldInfo);

        double sunSin = Math.sin(sunHeight);
        sunSin = Math.max(sunSin, 0.0001); // 避免日出日落趋近于0

        // 基础光学厚度（云量贡献）
        // 在无雨时, strong=0 → τ接近0, 在大雨时 τ很大
        double 云量贡献;
        if (worldInfo.isRaining()) {
            云量贡献 = 0.3 + (strong / (strong + 5.0)) * 3.0; // 厚云 + 雨滴
        } else {
            云量贡献 = (strong / (strong + 30.0)) * 2.0; // 稀薄云层
        }

        // 路径长度修正: 太阳越低，光路径越长 → 削弱更多
        double pathFactor = 1.0 / sunSin;

        // Beer–Lambert 定律: 透过率 = exp(-τ * 路径系数)
        double transmittance = Math.exp(-云量贡献 * pathFactor);

        // 限制范围在 [0,1]
        return Math.max(0.0, Math.min(1.0, transmittance));
    }
    public static double get低层大气密度(double 物理海拔) {
        if (物理海拔 < 0) 物理海拔 = 0;
        if (物理海拔 > 11000) 物理海拔 = 11000;
        double 指数 = (FinalFactors.重力加速度 * FinalFactors.干空气摩尔质量) / (FinalFactors.气体常数 * FinalFactors.对流层温度直减率) - 1.0;
        // ρ = ρ0 * (1 - L*h/T0)^(g*M/(R*L) - 1)
        return FinalFactors.海平面密度 * Math.pow(1.0 - FinalFactors.对流层温度直减率 * 物理海拔 / FinalFactors.海平面温度, 指数);
    }
    public static double get低层大气平均密度(double 地表海拔){
        return (get低层大气密度(地表海拔)+get低层大气密度(地表海拔+FinalFactors.低层大气厚度))/2;
    }

    /**
     * 获取低层大气热容,不包含大气中水的热容
     * @return 热容,单位FE/(kg·K)
     */
    public static long get低层大气热容(double 地面物理平均海拔){
        return (long) (FinalFactors.低层大气单元体积*
                get低层大气密度(地面物理平均海拔)*
                FinalFactors.干空气比热容);
    }
    public static double 大气吸收系数(double 大气厚度, double 大气密度, double 温室气体含量){
        double 吸收系数 = (1 + 温室气体含量 * 0.1)*1.5e-4;
        double f = 1-0.5 * Math.exp(-吸收系数 * 大气密度 * 大气厚度);
        return MathHelper.clamp(f, 0.0, 1.0);
    }

    public static float getFreezePossibility(Atmosphere atmosphere, BlockPos pos){
        float temp = atmosphere.get温度(pos,false);
        if(temp>= AtmosphereTemperature.ICE_POINT) return 0;
        if(temp< AtmosphereTemperature.ICE_POINT-100) return 1;
        float diff = AtmosphereTemperature.ICE_POINT-temp;
        return (diff/100)*0.94f+0.06f;
    }
    public static boolean isWater(World world, BlockPos pos) {
        return world.getBlockState(pos).getMaterial() == Material.WATER;
    }

    /**
     * 是否能够在指定位置放置雪，注意不是降雪，该方法只会检查亮度条件
     * @param world 世界
     * @param pos 位置
     * @return 如果能，则返回true
     */
    public static boolean canPlaceSnow(World world, BlockPos pos) {
        if (pos.getY() >= 0 && pos.getY() < 256 && world.getLightFor(EnumSkyBlock.BLOCK, pos) < 10) {
            return Blocks.SNOW_LAYER.canPlaceBlockAt(world, pos);
        }
        return false;
    }

    /**
     * 是否能够在指定位置降雪
     * @param world 世界
     * @param pos 位置
     * @param checkLight 是否检查亮度
     * @return 如果能，则返回true
     */
    public static boolean canSnowAt(World world,BlockPos pos, boolean checkLight) {
        Atmosphere atmosphere = AtmosphereSystemManager.getAtmosphere(world, pos);
        if(atmosphere == null) return world.canSnowAtBody(pos,checkLight);
        if(atmosphere.get水量()< FluidUtil.ONE_IN_EIGHT_OF_BUCKET_VOLUME) return false;
        if (atmosphere.get温度(pos,true) >= AtmosphereTemperature.ICE_POINT) {
            return false;
        } else if (!checkLight) {
            return true;
        } else {
            if (pos.getY() >= 0 && pos.getY() < 256 && world.getLightFor(EnumSkyBlock.BLOCK, pos) < 10) {
                IBlockState state = world.getBlockState(pos);

                return state.getBlock().isAir(state, world, pos) && Blocks.SNOW_LAYER.canPlaceBlockAt(world, pos);
            }

            return false;
        }
    }

    /**
     * 指定位置是否能够凝结水成冰
     * @param world 世界
     * @param pos 位置
     * @param neighborWaterCheck 是否检查周边水方块
     * @return 如果能，则返回true
     */
    public static boolean canWaterFreeze(World world,BlockPos pos, boolean neighborWaterCheck) {
        Atmosphere atmosphere = AtmosphereSystemManager.getAtmosphere(world, pos);
        if(atmosphere == null) return world.canBlockFreezeBody(pos,neighborWaterCheck);

        if (atmosphere.get温度(pos,false) < AtmosphereTemperature.ICE_POINT) {
            if (pos.getY() < 0 || pos.getY() >= 256)
                return false;
            IBlockState state = world.getBlockState(pos);
            Block block = state.getBlock();

            if ((block == Blocks.WATER || block == Blocks.FLOWING_WATER) && state.getValue(BlockLiquid.LEVEL) == 0) {
                if (!neighborWaterCheck) {
                    return true;
                }

                boolean isWaterSurrounded = isWater(world, pos.west()) && isWater(world, pos.east()) && isWater(world, pos.north()) && isWater(world, pos.south());

                return !isWaterSurrounded;
            }

        }
        return false;
    }
    public static final class FinalFactors {
        public static final int 对流层顶海拔 =17000;
        public static final double 斯特藩_玻尔兹曼常数 = 5.670374419e-8; // 斯特藩-玻尔兹曼常数 W·m^-2·K^-4
        public static final double 低层大气厚度 = 2000.0;
        public static final double 大气单元边长 = 16.0;
        public static final double 大气单元底面积 = 大气单元边长 * 大气单元边长; // 256 m²
        public static final double 低层大气单元体积 = 低层大气厚度 * 大气单元底面积; // 2000*16*16

        // 质量吸收系数
        public static final double κ_低层 = 1.6e-4;   // m²/kg
        public static final double κ_高层 = 8.0e-5;   // m²/kg

        public static final double 云发射率映射斜率 = 2.0; // 越大则同样云量产生更高发射率

        // 标准大气常数（对流层内）
        public static final double 海平面温度 = 288.15;      // K
        public static final double 海平面气压 = 101325.0;    // Pa
        public static final double 海平面密度 = 1.225;       // kg/m³
        public static final double 重力加速度 = 9.80665;      // m/s²
        public static final double 对流层温度直减率 = 0.0065;       // K/m
        public static final double 气体常数  = 8.314462618;  // FE/(mol·K)
        public static final double 干空气比热容 = 287; // FE/(kg·K)
        public static final double 干空气摩尔质量 = 0.0289644;    // kg/mol
    }
}
