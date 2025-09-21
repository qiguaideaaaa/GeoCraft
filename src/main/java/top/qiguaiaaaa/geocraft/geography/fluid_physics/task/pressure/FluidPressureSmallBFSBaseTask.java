package top.qiguaiaaaa.geocraft.geography.fluid_physics.task.pressure;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.Fluid;

import javax.annotation.Nonnull;

import java.util.Set;

import static top.qiguaiaaaa.geocraft.util.math.vec.BlockPosHelper.getRelativePos_BS;
import static top.qiguaiaaaa.geocraft.util.math.vec.RelativeBlockPosS.Mutable.MUTABLE;

/**
 * 以{@link Integer}存储坐标的BFS搜寻任务，适用于搜寻范围不超过{@link #MAX_RELATIVE_POS_OFFSET}的任务
 * @author QiguaiAAAA
 */
public abstract class FluidPressureSmallBFSBaseTask extends FluidPressureBFSBaseTask{
    public static final int MAX_RELATIVE_POS_OFFSET = (1<<(Integer.SIZE/3)-1)-1;
    protected final IntSet visited = new IntOpenHashSet();

    public FluidPressureSmallBFSBaseTask(@Nonnull Fluid fluid, @Nonnull IBlockState beginState, @Nonnull BlockPos beginPos) {
        super(fluid, beginState, beginPos);
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
        return visited.contains(MUTABLE.setPos(beginPos,pos).toInt());
    }

    /**
     * {@inheritDoc}
     * @param pos {@inheritDoc}
     */
    @Override
    public void markVisited(@Nonnull BlockPos pos){
        visited.add(MUTABLE.setPos(beginPos,pos).toInt());
    }

    /**
     * {@inheritDoc}
     * @param pos {@inheritDoc}
     */
    @Override
    public void queued(@Nonnull BlockPos pos){
        queue.add(getRelativePos_BS(beginPos,pos).toImmutable());
    }
}
