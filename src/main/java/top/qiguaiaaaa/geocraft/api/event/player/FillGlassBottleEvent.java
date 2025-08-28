package top.qiguaiaaaa.geocraft.api.event.player;

import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

@Cancelable
@Event.HasResult
public class FillGlassBottleEvent extends PlayerEvent {
    private final ItemStack current;
    private final World world;
    private ItemStack result;

    public FillGlassBottleEvent(EntityPlayer player, @Nonnull ItemStack current, World world) {
        super(player);
        this.current = current;
        this.world = world;
    }
    @Nonnull
    public ItemStack getEmptyGlassBottle(){
        return current;
    }
    public World getWorld(){
        return world;
    }
    public ItemStack getFilledGlassBottle(){
        return result;
    }
    public void setFilledGlassBottle(@Nonnull ItemStack glassBottle){
        this.result = glassBottle;
    }
    @Cancelable
    @HasResult
    public static class FillGlassBottleOnAreaEffectCloudEvent extends FillGlassBottleEvent{
        private final List<EntityAreaEffectCloud> entityAreaEffectClouds;

        public FillGlassBottleOnAreaEffectCloudEvent(EntityPlayer player, @Nonnull ItemStack current, World world, @Nonnull List<EntityAreaEffectCloud> entityList) {
            super(player, current, world);
            entityAreaEffectClouds = entityList;
        }
        public List<EntityAreaEffectCloud> getEntityListClicked(){
            return entityAreaEffectClouds;
        }
    }
    @Cancelable
    @HasResult
    public static class FillGlassBottleOnFluidEvent extends FillGlassBottleEvent{
        @Nullable
        private final RayTraceResult rayTraceResult;

        public FillGlassBottleOnFluidEvent(EntityPlayer player, @Nonnull ItemStack current, World world, @Nullable RayTraceResult rayTraceResult) {
            super(player, current, world);
            this.rayTraceResult = rayTraceResult;
        }
        @Nullable
        public RayTraceResult getRayTraceResult(){
            return rayTraceResult;
        }
    }
}
