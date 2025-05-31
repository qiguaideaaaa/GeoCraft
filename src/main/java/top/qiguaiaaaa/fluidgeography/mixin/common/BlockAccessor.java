package top.qiguaiaaaa.fluidgeography.mixin.common;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = Block.class)
public interface BlockAccessor {
    @Accessor("material")
    Material getMaterial();
}
