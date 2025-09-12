package top.qiguaiaaaa.geocraft.mixin.common.item;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemGlassBottle;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.qiguaiaaaa.geocraft.api.event.EventFactory;
import top.qiguaiaaaa.geocraft.mixin.common.ItemAccessor;

import java.util.List;

@Mixin(value = ItemGlassBottle.class)
public class ItemGlassBottleMixin {
    /**
     * @reason 插入事件
     */
    @Inject(method = "onItemRightClick",at = @At("HEAD"),cancellable = true)
    public void onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn, CallbackInfoReturnable<ActionResult<ItemStack>> cir) {
        cir.cancel();
        List<EntityAreaEffectCloud> list = worldIn.getEntitiesWithinAABB(EntityAreaEffectCloud.class, playerIn.getEntityBoundingBox().grow(2.0D), cloud -> cloud != null && cloud.isEntityAlive() && cloud.getOwner() instanceof EntityDragon);
        ItemStack itemstack = playerIn.getHeldItem(handIn);

        if (!list.isEmpty()) {
            ActionResult<ItemStack> ret = EventFactory.onGlassBottleUseOnAreaEffectCloud(playerIn,itemstack,worldIn,list); // 事件插入
            if(ret != null){
                cir.setReturnValue(ret);
                return;
            }

            EntityAreaEffectCloud entityareaeffectcloud = list.get(0);
            entityareaeffectcloud.setRadius(entityareaeffectcloud.getRadius() - 0.5F);
            worldIn.playSound(null, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.ITEM_BOTTLE_FILL_DRAGONBREATH, SoundCategory.NEUTRAL, 1.0F, 1.0F);
            cir.setReturnValue(new ActionResult<>(EnumActionResult.SUCCESS, this.turnBottleIntoItem(itemstack, playerIn, new ItemStack(Items.DRAGON_BREATH))));
            return;
        }
        RayTraceResult raytraceresult = ((ItemAccessor)this).rayTrace(worldIn, playerIn, true);
        // 事件插入
        ActionResult<ItemStack> ret = EventFactory.onGlassBottleUseOnFluid(playerIn,itemstack,worldIn,raytraceresult);
        if(ret != null){
            cir.setReturnValue(ret);
            return;
        }
        //事件结束
        cir.setReturnValue(new ActionResult<>(EnumActionResult.PASS, itemstack));
        if (raytraceresult == null)
            return;
        if (raytraceresult.typeOfHit != RayTraceResult.Type.BLOCK)
            return;
        BlockPos blockpos = raytraceresult.getBlockPos();

        if (!worldIn.isBlockModifiable(playerIn, blockpos) || !playerIn.canPlayerEdit(blockpos.offset(raytraceresult.sideHit), raytraceresult.sideHit, itemstack))
            return;

        if (worldIn.getBlockState(blockpos).getMaterial() == Material.WATER) {
            worldIn.playSound(playerIn, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0F, 1.0F);
            cir.setReturnValue(new ActionResult<>(EnumActionResult.SUCCESS, this.turnBottleIntoItem(itemstack, playerIn, PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionTypes.WATER))));
        }
    }
    @Shadow
    protected ItemStack turnBottleIntoItem(ItemStack p_185061_1_, EntityPlayer player, ItemStack stack) {
        return new ItemStack(Items.APPLE,1);
    }
}
