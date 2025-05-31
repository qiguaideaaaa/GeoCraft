package top.qiguaiaaaa.fluidgeography.mixin.common;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = BlockLiquid.class)
public interface BlockLiquidAccessor {
    @Invoker("getDepth")
    int getDepth(IBlockState state);
}
