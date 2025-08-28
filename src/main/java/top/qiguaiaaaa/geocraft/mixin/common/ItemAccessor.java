package top.qiguaiaaaa.geocraft.mixin.common;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = Item.class)
public interface ItemAccessor {
    @Invoker("rayTrace")
    RayTraceResult rayTrace(World worldIn, EntityPlayer playerIn, boolean useLiquids);
}
