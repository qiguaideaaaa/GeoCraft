package top.qiguaiaaaa.fluidgeography.mixin.reality.mod.ic2;

import ic2.core.util.PumpUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.qiguaiaaaa.fluidgeography.util.FluidSearchUtil;
import top.qiguaiaaaa.fluidgeography.api.util.FluidUtil;

import java.util.Optional;

import static top.qiguaiaaaa.fluidgeography.api.configs.SimulationConfig.IC2PumpFluidSearchMaxIterations;
import static top.qiguaiaaaa.fluidgeography.api.configs.SimulationConfig.fluidsNotToSimulate;

@Mixin(value = PumpUtil.class,remap = false)
public class PumpUtilMixin {
    @Inject(method = "searchFluidSource",at = @At("HEAD"),cancellable = true,remap = false)
    private static void searchFluidSource(World world, BlockPos startPos, CallbackInfoReturnable<BlockPos> cir) {
        cir.cancel();
        cir.setReturnValue(null);
        Optional<BlockPos> optionalBlockPos = FluidSearchUtil.findFluid(world,startPos,false,true,IC2PumpFluidSearchMaxIterations.getValue().value);
        if(!optionalBlockPos.isPresent()) return;
        Fluid fluid = FluidUtil.getFluid(world.getBlockState(optionalBlockPos.get()));
        if(fluidsNotToSimulate.containsEquivalent(fluid)){
            optionalBlockPos = FluidSearchUtil.findSource(world,optionalBlockPos.get(),fluid,false,false,8,0);
        }
        if(!optionalBlockPos.isPresent()) return;
        cir.setReturnValue(optionalBlockPos.get());
    }
}
