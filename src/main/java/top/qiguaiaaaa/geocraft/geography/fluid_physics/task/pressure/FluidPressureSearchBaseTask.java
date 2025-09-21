package top.qiguaiaaaa.geocraft.geography.fluid_physics.task.pressure;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.Fluid;

import javax.annotation.Nonnull;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author QiguaiAAAA
 */
public abstract class FluidPressureSearchBaseTask implements IFluidPressureSearchTask {
    private static final AtomicInteger ID = new AtomicInteger(); //减少Hash计算开销
    protected final Fluid fluid;
    protected final IBlockState beginState;
    protected final BlockPos beginPos;
    private final int hashID;
    public FluidPressureSearchBaseTask(@Nonnull Fluid fluid, @Nonnull IBlockState beginState, @Nonnull BlockPos beginPos) {
        this.fluid = fluid;
        this.beginState = beginState;
        this.beginPos = beginPos;
        hashID = ID.getAndIncrement();
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
    public boolean isEqualState(@Nonnull IBlockState curState) {
        return curState == beginState;
    }

    @Override
    public int hashCode() {
        return hashID;
    }
}
