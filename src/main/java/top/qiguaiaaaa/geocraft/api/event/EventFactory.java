package top.qiguaiaaaa.geocraft.api.event;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import top.qiguaiaaaa.geocraft.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.geocraft.api.atmosphere.system.IAtmosphereSystem;
import top.qiguaiaaaa.geocraft.api.event.atmosphere.AtmosphereSystemEvent;
import top.qiguaiaaaa.geocraft.api.event.block.StaticLiquidUpdateEvent;
import top.qiguaiaaaa.geocraft.api.event.atmosphere.AtmosphereUpdateEvent;
import top.qiguaiaaaa.geocraft.api.event.player.FillGlassBottleEvent;
import top.qiguaiaaaa.geocraft.api.event.player.FillGlassBottleEvent.FillGlassBottleOnAreaEffectCloudEvent;
import top.qiguaiaaaa.geocraft.api.event.player.FillGlassBottleEvent.FillGlassBottleOnFluidEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public final class EventFactory {
    public static final EventBus EVENT_BUS = new EventBus();

    public static ActionResult<ItemStack> onGlassBottleUseOnAreaEffectCloud(@Nonnull EntityPlayer player, @Nonnull ItemStack itemStack, World world, @Nonnull List<EntityAreaEffectCloud> entityList){
        FillGlassBottleEvent event = new FillGlassBottleOnAreaEffectCloudEvent(player,itemStack,world,entityList);
        return processOnGlassBottleUseEvent(itemStack,player,event);
    }
    public static ActionResult<ItemStack> onGlassBottleUseOnFluid(@Nonnull EntityPlayer player, @Nonnull ItemStack itemStack, World world, @Nullable RayTraceResult rayTraceResult){
        FillGlassBottleEvent event = new FillGlassBottleOnFluidEvent(player,itemStack,world,rayTraceResult);
        return processOnGlassBottleUseEvent(itemStack,player,event);
    }
    public static IBlockState onAtmosphereRainAndSnow(@Nonnull Chunk chunk, @Nonnull Atmosphere atmosphere, @Nonnull BlockPos randPos, double rainPossibility){
        AtmosphereUpdateEvent.RainAndSnow event = new AtmosphereUpdateEvent.RainAndSnow(chunk,atmosphere,randPos,rainPossibility);
        EVENT_BUS.post(event);
        if(event.getResult() == Result.ALLOW){
            return event.getState();
        }
        return null;
    }
    public static void postAtmosphereUpdate(@Nullable Chunk chunk, @Nonnull Atmosphere atmosphere, int x,int z){
        AtmosphereUpdateEvent.PostAtmosphereUpdateEvent event = new AtmosphereUpdateEvent.PostAtmosphereUpdateEvent(chunk,atmosphere,x,z);
        EVENT_BUS.post(event);
    }
    public static IBlockState afterBlockLiquidStaticUpdate(@Nonnull Fluid fluid, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state){
        StaticLiquidUpdateEvent.After event = new StaticLiquidUpdateEvent.After(fluid,world,pos,state);
        EVENT_BUS.post(event);
        if(event.getResult() == Result.ALLOW){
            return event.getNewState();
        }
        return null;
    }

    public static IAtmosphereSystem onAtmosphereSystemCreate(@Nonnull WorldServer server){
        AtmosphereSystemEvent.AtmosphereSystemCreateEvent event = new AtmosphereSystemEvent.AtmosphereSystemCreateEvent(server);
        EVENT_BUS.post(event);
        if(event.isCanceled()) return null;
        return event.getSystem();
    }

    private static ActionResult<ItemStack> processOnGlassBottleUseEvent(ItemStack itemStack,EntityPlayer player,FillGlassBottleEvent event){
        if(EVENT_BUS.post(event)) return new ActionResult<>(EnumActionResult.PASS,itemStack);
        if(event.getResult() == Result.ALLOW){
            itemStack.shrink(1);

            if (itemStack.isEmpty()) {
                return new ActionResult<>(EnumActionResult.SUCCESS,event.getFilledGlassBottle());
            } else {
                if (!player.inventory.addItemStackToInventory(event.getFilledGlassBottle())) {
                    player.dropItem(event.getFilledGlassBottle(), false);
                }

                return new ActionResult<>(EnumActionResult.SUCCESS,itemStack);
            }
        }
        return null;
    }
}
