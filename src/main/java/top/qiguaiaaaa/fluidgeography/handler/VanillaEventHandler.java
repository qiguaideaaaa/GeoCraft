package top.qiguaiaaaa.fluidgeography.handler;

import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.fluidgeography.api.event.atmosphere.AtmosphereUpdateEvent;
import top.qiguaiaaaa.fluidgeography.api.event.block.StaticLiquidUpdateEvent;
import top.qiguaiaaaa.fluidgeography.api.util.FluidUtil;
import top.qiguaiaaaa.fluidgeography.simulation.VanillaSimulationCore;
import top.qiguaiaaaa.fluidgeography.util.WaterUtil;

public final class VanillaEventHandler {
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
    }
}
