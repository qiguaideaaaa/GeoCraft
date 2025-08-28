package top.qiguaiaaaa.geocraft.handler;

import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import top.qiguaiaaaa.geocraft.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.geocraft.api.configs.value.minecraft.ConfigurableFluid;
import top.qiguaiaaaa.geocraft.api.event.atmosphere.AtmosphereUpdateEvent;
import top.qiguaiaaaa.geocraft.api.event.block.StaticLiquidUpdateEvent;
import top.qiguaiaaaa.geocraft.api.property.GeoFluidProperty;
import top.qiguaiaaaa.geocraft.api.util.FluidUtil;
import top.qiguaiaaaa.geocraft.simulation.VanillaLikeSimulationCore;
import top.qiguaiaaaa.geocraft.simulation.VanillaSimulationCore;
import top.qiguaiaaaa.geocraft.util.BaseUtil;
import top.qiguaiaaaa.geocraft.util.WaterUtil;

import static top.qiguaiaaaa.geocraft.configs.SimulationConfig.fluidsNotToSimulateInVanillaLike;

public final class VanillaLikeEventHandler{
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void afterStaticWaterUpdate(StaticLiquidUpdateEvent.After event){
        if(event.getLiquid() != FluidRegistry.WATER) return;
        World worldIn = event.getWorld();
        BlockPos pos = event.getPos();
        VanillaSimulationCore.evaporateWater(worldIn,pos, worldIn.rand);
    }
    @SubscribeEvent
    public void onAtmosphereRainAndSnow(AtmosphereUpdateEvent.RainAndSnow event){
        Atmosphere atmosphere = event.getAtmosphere();
        World world = event.getWorld();
        BlockPos randPos = event.getRandPos();
        if (WaterUtil.canSnowAt(world,randPos, true)) {
            atmosphere.drainWater(FluidUtil.ONE_IN_EIGHT_OF_BUCKET_VOLUME,randPos,false);
            event.setResult(Event.Result.ALLOW);
            event.setState(Blocks.SNOW_LAYER.getDefaultState());
        }
        if(!BaseUtil.getRandomResult(world.rand,event.getRainPossibility())) return;
        if(VanillaLikeSimulationCore.canRainAt(world,randPos.down())){
            atmosphere.drainWater(Fluid.BUCKET_VOLUME,randPos,false);
            //因为不是更新指定的位置,所以不设置结果
            world.setBlockState(randPos.down(),Blocks.FLOWING_WATER.getDefaultState());
        }
    }

    public static void onPostInit(FMLPostInitializationEvent event){
        for(ConfigurableFluid fluid:fluidsNotToSimulateInVanillaLike.getValue()){
            if(fluid == null) continue;
            GeoFluidProperty.setFluidToBePhysical(fluid.toString(),false);
        }
    }
}
