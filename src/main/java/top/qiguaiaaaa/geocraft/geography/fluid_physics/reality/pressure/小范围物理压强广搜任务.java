package top.qiguaiaaaa.geocraft.geography.fluid_physics.reality.pressure;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fluids.Fluid;
import top.qiguaiaaaa.geocraft.geography.fluid_physics.task.pressure.FluidPressureSmallBFSBaseTask;
import top.qiguaiaaaa.geocraft.util.math.vec.RelativeBlockPosB;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author QiguaiAAAA
 */
public abstract class 小范围物理压强广搜任务 extends FluidPressureSmallBFSBaseTask implements IRealityPressureBFSTask{
    protected static final short TIMES_PER_SEARCH = 128;
    protected final short maxSearchTimes;
    protected final Set<BlockPos> res = new LinkedHashSet<>();
    protected short searchTimes = 0;

    @Override
    public void queued(@Nonnull BlockPos pos) {
        if(maxSearchTimes<=Byte.MAX_VALUE){
            queue.add(RelativeBlockPosB.Mutable.MUTABLE.setPos(beginPos,pos).toImmutable());
            return;
        }
        super.queued(pos);
    }

    public 小范围物理压强广搜任务(@Nonnull Fluid fluid, @Nonnull IBlockState beginState, @Nonnull BlockPos beginPos, int searchRange) {
        super(fluid, beginState, beginPos);
        queued(beginPos);
        markVisited(beginPos);
        if(searchRange >4) throw new IllegalArgumentException("FluidPressureSmallBFSBaseTask can not handle search range larger than 511 blocks!");
        else if(searchRange == 4) maxSearchTimes = MAX_RELATIVE_POS_OFFSET;
        else maxSearchTimes = (short) (1<<(searchRange+5));
    }

    @Nonnull
    @Override
    public Collection<BlockPos> getResultCollection() {
        return res;
    }

    @Override
    public void putBlockPosToResults(@Nonnull BlockPos pos) {
        res.add(pos.toImmutable());
    }

    @Override
    public String toString() {
        return String.format("[Pressure Task B][at=%s,state=%s,fluid=%s,range=%s]", beginPos.toString(),beginState,fluid.getName(),maxSearchTimes);
    }

    @Override
    public int getSearchTimes() {
        return searchTimes;
    }

    @Override
    public int getMaxSearchTimes() {
        return maxSearchTimes;
    }

    @Override
    public boolean hasFoundEnoughResults() {
        return res.size()>getBeginQuanta()+2;
    }

    @Override
    public boolean hasSearchTimeReachedMax() {
        return searchTimes>=maxSearchTimes;
    }

    @Nullable
    @Override
    public Collection<BlockPos> search(@Nonnull WorldServer world) {
        for(int i=0;i<TIMES_PER_SEARCH;i++){
            if(isQueueEmpty()) break;
            BlockPos pos = pull();
            searchTimes++;
            if(search_Inner(world,pos))
                return res;
        }
        return res;
    }
}
