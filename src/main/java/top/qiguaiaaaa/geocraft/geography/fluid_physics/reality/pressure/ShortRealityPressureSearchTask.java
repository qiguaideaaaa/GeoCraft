package top.qiguaiaaaa.geocraft.geography.fluid_physics.reality.pressure;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.Fluid;
import top.qiguaiaaaa.geocraft.geography.fluid_physics.task.FluidPressureSearchShortBaseTask;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author QiguaiAAAA
 */
public abstract class ShortRealityPressureSearchTask extends FluidPressureSearchShortBaseTask {
    protected static final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
    protected final short maxSearchTimes;
    protected final Set<BlockPos> res = new LinkedHashSet<>();
    protected short searchTimes = 0;
    public ShortRealityPressureSearchTask(@Nonnull Fluid fluid, @Nonnull IBlockState beginState, @Nonnull BlockPos beginPos, int searchRange) {
        super(fluid, beginState, beginPos);
        queued(beginPos);
        markVisited(beginPos);
        maxSearchTimes = (short) (1<<(searchRange+5));
    }

    @Override
    public boolean isFinished() {
        return (!res.isEmpty() && searchTimes> maxSearchTimes>>2 ) || (res.size()>8) || queue.isEmpty() || searchTimes> maxSearchTimes;
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
}
