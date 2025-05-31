package top.qiguaiaaaa.fluidgeography.mixin.reality.mod.ic2;

import ic2.core.util.LiquidUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.qiguaiaaaa.fluidgeography.api.util.FluidUtil;

import static top.qiguaiaaaa.fluidgeography.api.configs.SimulationConfig.fluidsNotToSimulate;

@Mixin(value = LiquidUtil.class,remap = false)
public class LiquidUtilMixin {
    @Inject(method = "drainBlock",at = @At("HEAD"),cancellable = true,remap = false)
    private static void drainBlock(World world, BlockPos pos, boolean simulate, CallbackInfoReturnable<FluidStack> cir) {
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        if (block instanceof IFluidBlock) {
            cir.cancel();
            IFluidBlock liquid = (IFluidBlock)block;
            if (liquid.canDrain(world, pos)) {
                cir.setReturnValue(liquid.drain(world, pos, !simulate));
                return;
            }
        } else if (block instanceof BlockLiquid) {
            FluidStack fluid = null;
            if (block != Blocks.WATER && block != Blocks.FLOWING_WATER) {
                if (block == Blocks.LAVA || block == Blocks.FLOWING_LAVA) {
                    if(fluidsNotToSimulate.containsEquivalent(FluidRegistry.LAVA)) return;
                    fluid = new FluidStack(FluidRegistry.LAVA, FluidUtil.getFluidQuanta(world,pos,state)*FluidUtil.ONE_IN_EIGHT_OF_BUCKET_VOLUME);
                }
            } else {
                if(fluidsNotToSimulate.containsEquivalent(FluidRegistry.WATER)) return;
                fluid = new FluidStack(FluidRegistry.WATER, FluidUtil.getFluidQuanta(world,pos,state)*FluidUtil.ONE_IN_EIGHT_OF_BUCKET_VOLUME);
            }
            cir.cancel();
            if(fluid != null && !simulate){
                world.setBlockToAir(pos);
            }
            cir.setReturnValue(fluid);
            return;
        }
        cir.cancel();
        cir.setReturnValue(null);
    }
}
