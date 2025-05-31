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

public final class AtmosphereUtil {
    public static final int AIR_SPECIFIC_HEAT_CAPACITY = 1000;
    public static final int SUN_CONSTANT = 1361;
    public static final long CHUNK_ATMOSPHERE_SPECIFIC_HEAT_CAPACITY = 256*192*AIR_SPECIFIC_HEAT_CAPACITY;
    public static final double ATMOSPHERE_ENERGY_RADIATION_LOSS_CONSTANT = 5.67*(Math.pow(10,-8))*256*216;
    public static final double CHUNK_SUN_ENERGY_GAIN_CONSTANT = SUN_CONSTANT *256*216;
    public static final int WATER_MELT_LATENT_HEAT_PER_QUANTA = 41750000;
    public static final int WATER_EVAPORATE_LATENT_HEAT_PER_QUANTA = 283250000;
    public static final double WATER_MOLAR_MASS = 0.01801528;
    public static final double GAS_CONSTANT = 8.31446261815324;
    public static double getSunEnergyPerChunk(WorldInfo worldInfo){
        return Math.sin(getSunHeight(worldInfo))* CHUNK_SUN_ENERGY_GAIN_CONSTANT;
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
    public static double getHeatEnergyRadiationLoss(Atmosphere atmosphere,double cloudInsulationEffect){
        Underlying underlying = atmosphere.getUnderlying();
        double groundRadiation = ATMOSPHERE_ENERGY_RADIATION_LOSS_CONSTANT *Math.pow(atmosphere.getTemperature(),4) *underlying.averageEmissivity;
        return (groundRadiation *AtmosphereConfig.GROUND_RADIATION_LOSS_RATE.getValue().value
                +groundRadiation*(1 - AtmosphereConfig.GROUND_RADIATION_LOSS_RATE.getValue().value)*getAtmosphereEmissivity(atmosphere)*0.4)
                *cloudInsulationEffect;
    }

    /**
     * 获取大气发射率
     */
    public static double getAtmosphereEmissivity(Atmosphere atmosphere){
        return MathHelper.clamp(0.75*Math.pow((getAtmosphereWaterPressure(atmosphere)*0.01)/atmosphere.getTemperature(),0.166),0.01,1.0);
    }

    /**
     * 获得大气水汽压
     * @return 大气水汽压，单位帕 Pa
     */
    public static double getAtmosphereWaterPressure(Atmosphere atmosphere){
        return atmosphere.getWaterAmount()*GAS_CONSTANT*atmosphere.getTemperature()/(WATER_MOLAR_MASS*256*getAtmosphereHeight(atmosphere.getUnderlying()));
    }
    public static long getAtmosphereHeatVolume(Underlying underlying){
        return (long) (CHUNK_ATMOSPHERE_SPECIFIC_HEAT_CAPACITY*(getAtmosphereHeight(underlying)/192))+underlying.heatCapacity;
    }
    public static double getAtmosphereHeight(Underlying underlying){
        return Math.min(1000,12000-underlying.getAverageHeight()*20);
    }
    public static double getWaterEvaporatePossibility(Atmosphere atmosphere){
        if(atmosphere.getTemperature()>= AtmosphereTemperature.BOILED_POINT) return 1;
        double possibility = 1.0d;
        possibility *= Math.pow(1.07,atmosphere.getTemperature() - AtmosphereTemperature.ICE_POINT)/Math.pow(1.07,100);
        possibility *= Math.sqrt(1-Math.pow(atmosphere.getWaterAmount()/1024.0,2)/1000.0);
        possibility *= 0.5;
        return possibility;
    }
    public static double getRainPossibility(Atmosphere atmosphere, BlockPos pos){
        float temp = atmosphere.getTemperature(pos);
        if(temp>= AtmosphereTemperature.BOILED_POINT) return 0;
        if(temp<= AtmosphereTemperature.ICE_POINT-100) return 1;
        double strong = atmosphere.getRainStrong();
        return strong/(strong+1024);
    }
    public static double getCloudInsulationEffect(Atmosphere atmosphere, WorldInfo worldInfo){
        double strong = atmosphere.getRainStrong();
        if(worldInfo.isRaining()){
            return 1-((strong/(strong+5))*0.6+0.1);
        }
        return 1-((strong)/(strong+100));
    }
    public static float getFreezePossibility(Atmosphere atmosphere, BlockPos pos){
        float temp = atmosphere.getTemperature(pos);
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
        if(atmosphere.getWaterAmount()< FluidUtil.ONE_IN_EIGHT_OF_BUCKET_VOLUME) return false;
        if (atmosphere.getTemperature(pos) >= AtmosphereTemperature.ICE_POINT) {
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

        if (atmosphere.getTemperature(pos) < AtmosphereTemperature.ICE_POINT) {
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
}
