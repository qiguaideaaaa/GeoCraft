package top.qiguaiaaaa.geocraft.geography.fluid_physics.reality.pressure;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.Fluid;
import top.qiguaiaaaa.geocraft.geography.fluid_physics.task.FluidPressureSearchByteBaseTask;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author QiguaiAAAA
 */
public abstract class ByteRealityPressureSearchTask extends FluidPressureSearchByteBaseTask {
    protected static final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
    protected final byte maxSearchTimes;
    protected final Set<BlockPos> res = new LinkedHashSet<>();
    protected byte searchTimes = 0;

    public ByteRealityPressureSearchTask(@Nonnull Fluid fluid, @Nonnull IBlockState beginState, @Nonnull BlockPos beginPos, int searchRange) {
        super(fluid, beginState, beginPos);
        queued(beginPos);
        markVisited(beginPos);
        if(searchRange >=2) throw new IllegalArgumentException("RealityPressureSearchByteTask can not handle search range larger than 127!");
        else maxSearchTimes = (byte) (1<<(searchRange+5));
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
