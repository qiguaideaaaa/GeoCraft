package top.qiguaiaaaa.fluidgeography.mixin.reality;

import net.minecraft.block.BlockLiquid;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.capability.wrappers.BlockLiquidWrapper;
import net.minecraftforge.fluids.capability.wrappers.FluidBlockWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import top.qiguaiaaaa.fluidgeography.util.wrappers.PhysicsBlockLiquidWrapper;
import top.qiguaiaaaa.fluidgeography.util.wrappers.PhysicsFluidBlockWrapper;

import static top.qiguaiaaaa.fluidgeography.api.configs.SimulationConfig.fluidsNotToSimulate;
import static top.qiguaiaaaa.fluidgeography.api.configs.SimulationConfig.fluidsWhoseBucketsBehavesAsVanillaBuckets;
@Mixin(value = FluidUtil.class,remap = false)
public class ForgeFluidUtilMixin {
    @Redirect(method = "getFluidBlockHandler",
            at= @At(value = "NEW",
                    target = "net/minecraftforge/fluids/capability/wrappers/FluidBlockWrapper"),remap = false)
    private static FluidBlockWrapper getFluidBlockHandlerMod(IFluidBlock fluidBlock, World world, BlockPos blockPos) {
        if(fluidsWhoseBucketsBehavesAsVanillaBuckets.containsEquivalent(fluidBlock.getFluid())
        || fluidsNotToSimulate.containsEquivalent(fluidBlock.getFluid())) return new FluidBlockWrapper(fluidBlock,world,blockPos);
        return new PhysicsFluidBlockWrapper(fluidBlock, world, blockPos);
    }
    @Redirect(method = "getFluidBlockHandler",
            at=@At(value = "NEW",
                    target = "net/minecraftforge/fluids/capability/wrappers/BlockLiquidWrapper"),remap = false)
    private static BlockLiquidWrapper getFluidBlockHandlerVanilla(BlockLiquid blockLiquid, World world, BlockPos blockPos) {
        if(fluidsNotToSimulate.containsEquivalent(blockLiquid)) return new BlockLiquidWrapper(blockLiquid,world,blockPos);
        return new PhysicsBlockLiquidWrapper(blockLiquid,world,blockPos);
    }
}
