package top.qiguaiaaaa.geocraft.geography.fluid_physics.task;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.Fluid;
import top.qiguaiaaaa.geocraft.util.math.vec.RelativeBlockPosB;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * 以byte存储坐标的BFS搜寻任务，适用于搜寻范围不超过127的任务
 * @author QiguaiAAAA
 */
public abstract class FluidPressureSearchByteBaseTask extends FluidPressureSearchBaseTask implements IFluidPressureBFSTask{
    protected final Set<RelativeBlockPosB> visited = new HashSet<>();
    protected final Queue<RelativeBlockPosB> queue = new LinkedList<>();
    private final BlockPos.MutableBlockPos mutablePosForQueue = new BlockPos.MutableBlockPos();
    private final RelativeBlockPosB.CenteredMutable mutableRelativePos = new RelativeBlockPosB.CenteredMutable();

    public FluidPressureSearchByteBaseTask(@Nonnull Fluid fluid, @Nonnull IBlockState beginState, @Nonnull BlockPos beginPos) {
        super(fluid, beginState, beginPos);
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
        RelativeBlockPosB relativePos = queue.poll();
        assert relativePos != null;
        return mutablePosForQueue.setPos(beginPos.getX()+relativePos.getX(),beginPos.getY()+relativePos.getY(),beginPos.getZ()+relativePos.getZ());
    }
}
