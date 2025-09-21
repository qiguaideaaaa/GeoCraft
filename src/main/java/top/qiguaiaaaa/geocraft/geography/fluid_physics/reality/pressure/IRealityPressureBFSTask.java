package top.qiguaiaaaa.geocraft.geography.fluid_physics.reality.pressure;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import top.qiguaiaaaa.geocraft.geography.fluid_physics.task.pressure.IFluidPressureBFSTask;

import javax.annotation.Nonnull;

/**
 * @author QiguaiAAAA
 */
public interface IRealityPressureBFSTask extends IFluidPressureBFSTask {
    BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
    byte getBeginQuanta();
    byte getQuantaPerBlock();
    int getSearchTimes();
    int getMaxSearchTimes();
    boolean hasFoundEnoughResults();
    boolean hasSearchTimeReachedMax();

    boolean search_Inner(@Nonnull WorldServer world, @Nonnull BlockPos pos);

    @Override
    default boolean isFinished(){
        return hasFoundEnoughResults() || hasSearchTimeReachedMax() || isQueueEmpty();
    }
}
