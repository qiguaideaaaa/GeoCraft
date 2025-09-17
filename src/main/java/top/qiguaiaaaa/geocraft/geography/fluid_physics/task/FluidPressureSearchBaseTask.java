package top.qiguaiaaaa.geocraft.geography.fluid_physics.task;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.Fluid;
import top.qiguaiaaaa.geocraft.geography.fluid_physics.IFluidPressureSearchTask;

import javax.annotation.Nonnull;

/**
 * @author QiguaiAAAA
 */
public abstract class FluidPressureSearchBaseTask implements IFluidPressureSearchTask {
    protected final Fluid fluid;
    protected final IBlockState beginState;
    protected final BlockPos beginPos;
    public FluidPressureSearchBaseTask(@Nonnull Fluid fluid, @Nonnull IBlockState beginState, @Nonnull BlockPos beginPos) {
        this.fluid = fluid;
        this.beginState = beginState;
        this.beginPos = beginPos;
    }

    @Nonnull
    @Override
    public Fluid getFluid() {
        return fluid;
    }

    @Nonnull
    @Override
    public BlockPos getBeginPos() {
        return beginPos;
    }

    @Nonnull
    @Override
    public IBlockState getBeginState() {
        return beginState;
    }

    @Override
    public boolean isEqualState(IBlockState curState) {
        return curState == beginState;
    }
}
