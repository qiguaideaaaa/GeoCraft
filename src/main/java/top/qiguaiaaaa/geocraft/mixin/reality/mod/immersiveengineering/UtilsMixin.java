package top.qiguaiaaaa.geocraft.mixin.reality.mod.immersiveengineering;

import blusunrize.immersiveengineering.common.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.qiguaiaaaa.geocraft.api.property.GeoFluidProperty;
import top.qiguaiaaaa.geocraft.api.util.FluidUtil;
@Mixin(value = Utils.class,remap = false)
public class UtilsMixin {
    @Inject(method = "drainFluidBlock",at= @At("HEAD"),cancellable = true,remap = false)
    private static void drainFluidBlock(World world, BlockPos pos, boolean doDrain, CallbackInfoReturnable<FluidStack> cir) {
        Block b = world.getBlockState(pos).getBlock();
        Fluid f = FluidRegistry.lookupFluidForBlock(b);

        if(f!=null) {
            if(!GeoFluidProperty.isFluidToBePhysical(f)) return;
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
