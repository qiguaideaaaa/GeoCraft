package top.qiguaiaaaa.geocraft.geography.fluid_physics;

import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.Fluid;

import javax.annotation.Nonnull;

/**
 * @author QiguaiAAAA
 */
public abstract class FluidUpdateBaseTask implements IFluidUpdateTask{
    protected final Fluid fluid;
    protected final BlockPos pos;

    public FluidUpdateBaseTask(@Nonnull Fluid fluid,@Nonnull BlockPos pos) {
        this.fluid = fluid;
        this.pos = pos;
    }

    @Nonnull
    @Override
    public BlockPos getPos() {
        return pos;
    }

    @Nonnull
    @Override
    public Fluid getFluid() {
        return fluid;
    }
}
