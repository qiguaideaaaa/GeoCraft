package top.qiguaiaaaa.geocraft.geography.fluid_physics.task.pressure;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.Fluid;

import javax.annotation.Nonnull;

import java.util.Set;

import static top.qiguaiaaaa.geocraft.util.math.vec.BlockPosHelper.getRelativePos_BSI;
import static top.qiguaiaaaa.geocraft.util.math.vec.RelativeBlockPosI.Mutable.MUTABLE;

/**
 * 以{@link Long}存储坐标的BFS搜寻任务，适合搜寻范围不超过{@link #MAX_RELATIVE_POS_OFFSET}的任务
 * @author QiguaiAAAA
 */
public abstract class FluidPressureLargeBFSBaseTask extends FluidPressureBFSBaseTask implements IFluidPressureBFSTask{
    public static final int MAX_RELATIVE_POS_OFFSET = (1<<(Long.SIZE/3)-1)-1;
    protected final LongSet visited = new LongOpenHashSet();

    public FluidPressureLargeBFSBaseTask(@Nonnull Fluid fluid, @Nonnull IBlockState beginState, @Nonnull BlockPos beginPos) {
        super(fluid,beginState,beginPos);
    }

    @Override
    protected Set<?> getVisitedSet() {
        return visited;
    }

    /**
     * {@inheritDoc}
     * @param pos {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public boolean isVisited(@Nonnull BlockPos pos){
        return visited.contains(MUTABLE.setPos(beginPos,pos).toLong());
    }

    /**
     * {@inheritDoc}
     * @param pos {@inheritDoc}
     */
    @Override
    public void markVisited(@Nonnull BlockPos pos){
        visited.add(MUTABLE.setPos(beginPos,pos).toLong());
    }

    /**
     * {@inheritDoc}
     * @param pos {@inheritDoc}
     */
    @Override
    public void queued(@Nonnull BlockPos pos){
        queue.add(getRelativePos_BSI(beginPos,pos).toImmutable());
    }
}
