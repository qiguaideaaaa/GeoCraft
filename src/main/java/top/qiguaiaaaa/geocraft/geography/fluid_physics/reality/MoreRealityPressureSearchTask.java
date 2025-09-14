package top.qiguaiaaaa.geocraft.geography.fluid_physics.reality;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.Fluid;
import top.qiguaiaaaa.geocraft.geography.fluid_physics.FluidPressureSearchBaseTask;

import javax.annotation.Nonnull;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author QiguaiAAAA
 */
public abstract class MoreRealityPressureSearchTask extends FluidPressureSearchBaseTask {
    protected final int maxSearchTimes;
    protected final Set<BlockPos> res = new LinkedHashSet<>();
    protected int searchTimes = 0;
    public MoreRealityPressureSearchTask(@Nonnull Fluid fluid,@Nonnull IBlockState beginState,@Nonnull BlockPos beginPos, int searchRange) {
        super(fluid, beginState, beginPos);
        queue.add(beginPos);
        visited.add(beginPos);
        maxSearchTimes = 1<<(searchRange+5);
    }

    @Override
    public boolean isFinished() {
        return (!res.isEmpty() && searchTimes> maxSearchTimes>>2 ) || (res.size()>8) || queue.isEmpty() || searchTimes> maxSearchTimes;
    }
}
