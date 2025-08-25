package top.qiguaiaaaa.fluidgeography.simulation;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.AtmosphereSystemManager;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.fluidgeography.api.util.AtmosphereUtil;
import top.qiguaiaaaa.fluidgeography.api.util.FluidUtil;
import top.qiguaiaaaa.fluidgeography.util.BaseUtil;
import top.qiguaiaaaa.fluidgeography.util.WaterUtil;

import java.util.Random;

public class VanillaSimulationCore {
    public static void evaporateWater(World world, BlockPos pos, Random rand){
        if(world.getLightFor(EnumSkyBlock.SKY,pos) == 0) return;
        Atmosphere atmosphere = AtmosphereSystemManager.getAtmosphere(world,pos);
        if(atmosphere == null) return;
        if(!BaseUtil.getRandomResult(rand, WaterUtil.getWaterEvaporatePossibility(atmosphere,pos))){
            return;
        }
        if(!atmosphere.addSteam(FluidUtil.ONE_IN_EIGHT_OF_BUCKET_VOLUME,pos)) return;
        atmosphere.putHeat(-(AtmosphereUtil.FinalFactors.WATER_EVAPORATE_LATENT_HEAT_PER_QUANTA),pos);
    }

}
