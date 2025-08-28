package top.qiguaiaaaa.geocraft.mixin.vanilla;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraftforge.fluids.IFluidBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BlockLiquid.class)
public class BlockLiquidMixin {
    @Inject(method = "getDepth",at =@At("HEAD"),cancellable = true)
    private void getDepth(IBlockState state, CallbackInfoReturnable<Integer> cir) {
        if(state.getBlock() instanceof IFluidBlock) {
            cir.cancel();
            cir.setReturnValue(-1);
        }
    }
}
