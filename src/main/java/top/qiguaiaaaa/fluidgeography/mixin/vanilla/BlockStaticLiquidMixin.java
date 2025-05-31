package top.qiguaiaaaa.fluidgeography.mixin.vanilla;

import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.qiguaiaaaa.fluidgeography.api.event.EventFactory;
import top.qiguaiaaaa.fluidgeography.util.mixinapi.FluidSettable;

import java.util.Random;

import static top.qiguaiaaaa.fluidgeography.api.configs.SimulationConfig.fluidsNotToSimulate;

@Mixin(value = BlockStaticLiquid.class)
public class BlockStaticLiquidMixin implements FluidSettable {
    private Fluid thisFluid;

    @Inject(method = "updateTick",at = @At("RETURN"))
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand, CallbackInfo ci) {
        if(fluidsNotToSimulate.containsEquivalent(thisFluid)) return;
        IBlockState newState = EventFactory.afterBlockLiquidStaticUpdate(thisFluid,worldIn,pos,state);
        if(newState != null){
            worldIn.setBlockState(pos,newState);
        }
    }

    @Override
    public void setCorrespondingFluid(Fluid fluid) {
        this.thisFluid = fluid;
    }
}
