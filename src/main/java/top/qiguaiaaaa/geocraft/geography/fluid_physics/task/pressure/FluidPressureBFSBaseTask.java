package top.qiguaiaaaa.geocraft.geography.fluid_physics.task.pressure;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.Fluid;
import top.qiguaiaaaa.geocraft.util.math.vec.IVec3i;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * @author QiguaiAAAA
 */
public abstract class FluidPressureBFSBaseTask extends FluidPressureSearchBaseTask implements IFluidPressureBFSTask{
    protected final Queue<IVec3i> queue = new LinkedList<>();
    private final BlockPos.MutableBlockPos mutablePosForQueue = new BlockPos.MutableBlockPos(); //注意,仅单线程使用

    public FluidPressureBFSBaseTask(@Nonnull Fluid fluid, @Nonnull IBlockState beginState, @Nonnull BlockPos beginPos) {
        super(fluid, beginState, beginPos);
    }

    @Override
    public int getVisitedSize() {
        return getVisitedSet().size();
    }

    @Override
    public boolean isQueueEmpty() {
        return queue.isEmpty();
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Nonnull
    public BlockPos pull(){
        IVec3i relativePos = queue.poll();
        assert relativePos != null;
        return mutablePosForQueue.setPos(beginPos.getX()+relativePos.getX(),beginPos.getY()+relativePos.getY(),beginPos.getZ()+relativePos.getZ());
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Nonnull
    @Override
    public BlockPos peek() {
        IVec3i relativePos = queue.peek();
        assert relativePos != null;
        return mutablePosForQueue.setPos(beginPos.getX()+relativePos.getX(),beginPos.getY()+relativePos.getY(),beginPos.getZ()+relativePos.getZ());
    }

    @Override
    public int getQueueSize() {
        return queue.size();
    }

    abstract protected Set<?> getVisitedSet();

    @Override
    public void cancel() {
        getVisitedSet().clear();
        queue.clear();
    }

    @Override
    public void finish() {
        getVisitedSet().clear();
        this.queue.clear();
    }
}
