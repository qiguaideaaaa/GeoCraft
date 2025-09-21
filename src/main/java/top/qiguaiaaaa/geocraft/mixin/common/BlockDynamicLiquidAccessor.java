package top.qiguaiaaaa.geocraft.mixin.common;

import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

/**
 * @author QiguaiAAAA
 */
@Mixin(value = BlockDynamicLiquid.class)
public interface BlockDynamicLiquidAccessor {
    @Invoker("tryFlowInto")
    void tryFlowInto(World worldIn, BlockPos pos, IBlockState state, int level);
}
