package top.qiguaiaaaa.geocraft.geography.fluid_physics.task;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.Fluid;
import top.qiguaiaaaa.geocraft.util.math.vec.RelativeBlockPosS;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * 以short存储坐标的BFS搜寻任务，适合搜寻范围不超过32767的任务
 * @author QiguaiAAAA
 */
public abstract class FluidPressureSearchShortBaseTask extends FluidPressureSearchBaseTask implements IFluidPressureBFSTask{
    protected final Set<RelativeBlockPosS> visited = new HashSet<>();
    protected final Queue<RelativeBlockPosS> queue = new LinkedList<>();
    private final BlockPos.MutableBlockPos mutablePosForQueue = new BlockPos.MutableBlockPos();
    private final RelativeBlockPosS.CenteredMutable mutableRelativePos = new RelativeBlockPosS.CenteredMutable();

    public FluidPressureSearchShortBaseTask(@Nonnull Fluid fluid, @Nonnull IBlockState beginState, @Nonnull BlockPos beginPos) {
        super(fluid,beginState,beginPos);
        mutableRelativePos.setCenterPos(beginPos);
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

    /**
     * {@inheritDoc}
     * @param pos {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public boolean isVisited(@Nonnull BlockPos pos){
        mutableRelativePos.setAbsolutePos(pos);
        return visited.contains(mutableRelativePos);
    }

    /**
     * {@inheritDoc}
     * @param pos {@inheritDoc}
     */
    public void markVisited(@Nonnull BlockPos pos){
        mutableRelativePos.setAbsolutePos(pos);
        visited.add(mutableRelativePos.toImmutable());
    }

    @Override
    public boolean isQueueEmpty() {
        return queue.isEmpty();
    }

    /**
     * {@inheritDoc}
     * @param pos {@inheritDoc}
     */
    public void queued(@Nonnull BlockPos pos){
        mutableRelativePos.setAbsolutePos(pos);
        queue.add(mutableRelativePos.toImmutable());
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Nonnull
    public BlockPos pull(){
        RelativeBlockPosS relativePos = queue.poll();
        assert relativePos != null;
        return mutablePosForQueue.setPos(beginPos.getX()+relativePos.getX(),beginPos.getY()+relativePos.getY(),beginPos.getZ()+relativePos.getZ());
    }
}
