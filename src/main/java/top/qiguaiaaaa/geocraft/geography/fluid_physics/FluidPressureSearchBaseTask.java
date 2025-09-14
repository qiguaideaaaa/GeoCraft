package top.qiguaiaaaa.geocraft.geography.fluid_physics;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.Fluid;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * @author QiguaiAAAA
 */
public abstract class FluidPressureSearchBaseTask implements IFluidPressureSearchTask {
    protected final Fluid fluid;
    protected final IBlockState beginState;
    protected final BlockPos beginPos;
    protected final Set<BlockPos> visited = new HashSet<>();
    protected final Queue<BlockPos> queue = new LinkedList<>();

    public FluidPressureSearchBaseTask(@Nonnull Fluid fluid,@Nonnull IBlockState beginState,@Nonnull BlockPos beginPos) {
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
    public void cancel() {
        visited.clear();
        queue.clear();
    }

    @Override
    public void finish() {
        this.visited.clear();
        this.queue.clear();
    }
}
