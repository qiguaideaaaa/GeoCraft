package top.qiguaiaaaa.geocraft.simulation;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import top.qiguaiaaaa.geocraft.api.atmosphere.AtmosphereSystemManager;
import top.qiguaiaaaa.geocraft.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.geocraft.api.atmosphere.accessor.IAtmosphereAccessor;
import top.qiguaiaaaa.geocraft.api.util.AtmosphereUtil;
import top.qiguaiaaaa.geocraft.api.util.FluidUtil;
import top.qiguaiaaaa.geocraft.util.BaseUtil;
import top.qiguaiaaaa.geocraft.util.WaterUtil;

import java.util.Random;

public class VanillaSimulationCore {
    public static void evaporateWater(World world, BlockPos pos, Random rand){
        int light = world.getLightFor(EnumSkyBlock.SKY,pos);
        if(light<= 0) return;
        IAtmosphereAccessor accessor = AtmosphereSystemManager.getAtmosphereAccessor(world,pos,true);
        if(accessor == null) return;
        Atmosphere atmosphere = accessor.getAtmosphereHere();
        if(atmosphere == null) return;
        accessor.setSkyLight(light);

        int amount = (int) MathHelper.clamp(WaterUtil.getWaterEvaporateAmount(atmosphere,pos),0,1000);
        if(amount == 0) return;
        if(!atmosphere.addSteam(amount,pos)) return;
        accessor.drawHeatFromUnderlying(AtmosphereUtil.FinalFactors.WATER_EVAPORATE_LATENT_HEAT_PER_QUANTA*(double)amount/FluidUtil.ONE_IN_EIGHT_OF_BUCKET_VOLUME);
    }

}
