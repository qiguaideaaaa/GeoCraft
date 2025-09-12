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

/**
 * 当玩家装瓶的时候触发<br/>
 * 通过Mixin实现
 * @author QiguaiAAAA
 */
@Cancelable
@Event.HasResult
public class FillGlassBottleEvent extends PlayerEvent {
    private final ItemStack current;
    private final World world;
    private ItemStack result;

    public FillGlassBottleEvent(@Nonnull EntityPlayer player, @Nonnull ItemStack current,@Nonnull World world) {
        super(player);
        this.current = current;
        this.world = world;
    }

    /**
     * 获取装之前的瓶子
     * @return 装之前的瓶子
     */
    @Nonnull
    public ItemStack getEmptyGlassBottle(){
        return current;
    }
    @Nonnull
    public World getWorld(){
        return world;
    }

    /**
     * 获取设置的填充满后的瓶子<br/>
     * 当Result被设置为{@link Result#ALLOW}时，该方法不应返回null，否则会抛出{@link NullPointerException}
     * @return 设置的填满后的瓶子
     */
    @Nullable
    public ItemStack getFilledGlassBottle(){
        return result;
    }

    /**
     * 设置填充满后的瓶子
     * @param glassBottle 填满后的瓶子
     */
    public void setFilledGlassBottle(@Nonnull ItemStack glassBottle){
        this.result = glassBottle;
    }

    /**
     * 当玩家在效果云装瓶的时候发布
     */
    @Cancelable
    @HasResult
    public static class FillGlassBottleOnAreaEffectCloudEvent extends FillGlassBottleEvent{
        private final List<EntityAreaEffectCloud> entityAreaEffectClouds;

        public FillGlassBottleOnAreaEffectCloudEvent(@Nonnull EntityPlayer player, @Nonnull ItemStack current,@Nonnull World world, @Nonnull List<EntityAreaEffectCloud> entityList) {
            super(player, current, world);
            entityAreaEffectClouds = entityList;
        }
        @Nonnull
        public List<EntityAreaEffectCloud> getEntityListClicked(){
            return entityAreaEffectClouds;
        }
    }

    /**
     * 当玩家在装流体的时候发布
     */
    @Cancelable
    @HasResult
    public static class FillGlassBottleOnFluidEvent extends FillGlassBottleEvent{
        @Nullable
        private final RayTraceResult rayTraceResult;

        public FillGlassBottleOnFluidEvent(@Nonnull EntityPlayer player, @Nonnull ItemStack current, World world, @Nullable RayTraceResult rayTraceResult) {
            super(player, current, world);
            this.rayTraceResult = rayTraceResult;
        }
        @Nullable
        public RayTraceResult getRayTraceResult(){
            return rayTraceResult;
        }
    }
}
