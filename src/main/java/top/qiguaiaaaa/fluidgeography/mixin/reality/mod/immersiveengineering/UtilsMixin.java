package top.qiguaiaaaa.fluidgeography.mixin.reality.mod.immersiveengineering;

import blusunrize.immersiveengineering.common.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.qiguaiaaaa.fluidgeography.api.util.FluidUtil;

import static top.qiguaiaaaa.fluidgeography.api.configs.SimulationConfig.fluidsNotToSimulate;
@Mixin(value = Utils.class,remap = false)
public class UtilsMixin {
    @Inject(method = "drainFluidBlock",at= @At("HEAD"),cancellable = true,remap = false)
    private static void drainFluidBlock(World world, BlockPos pos, boolean doDrain, CallbackInfoReturnable<FluidStack> cir) {
        Block b = world.getBlockState(pos).getBlock();
        Fluid f = FluidRegistry.lookupFluidForBlock(b);

        if(f!=null) {
            if(fluidsNotToSimulate.containsEquivalent(f)) return;
            if(b instanceof IFluidBlock) {
                if(((IFluidBlock)b).canDrain(world, pos))
                    cir.setReturnValue(((IFluidBlock)b).drain(world, pos, doDrain));
                else
                    cir.setReturnValue(null);
            } else {
                int meta = b.getMetaFromState(world.getBlockState(pos));
                int quanta = 8-meta;
                if(doDrain)
                    world.setBlockToAir(pos);
                cir.setReturnValue(new FluidStack(f, quanta* FluidUtil.ONE_IN_EIGHT_OF_BUCKET_VOLUME));
            }
        } else cir.setReturnValue(null);
        cir.cancel();
    }
}
