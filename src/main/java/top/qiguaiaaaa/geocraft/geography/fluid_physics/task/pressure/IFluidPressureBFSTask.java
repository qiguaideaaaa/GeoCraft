package top.qiguaiaaaa.geocraft.geography.fluid_physics.task.pressure;

import net.minecraft.util.math.BlockPos;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * @author QiguaiAAAA
 */
public interface IFluidPressureBFSTask extends IFluidPressureSearchTask {
    /**
     * 指定位置是否已经被访问
     * @param pos 位置
     * @return 若已被访问,则返回true
     */
    boolean isVisited(@Nonnull BlockPos pos);

    /**
     * 标记指定位置被访问
     * @param pos 被访问的位置
     */
    void markVisited(@Nonnull BlockPos pos);
    int getVisitedSize();

    boolean isQueueEmpty();

    /**
     * 将指定位置加入队列
     * @param pos 位置
     */
    void queued(@Nonnull BlockPos pos);

    /**
     * 取出队列中的第一个位置，会从队列中移除第一个位置
     * @return 第一个位置
     */
    @Nonnull
    BlockPos pull();

    /**
     * 获取队列中的第一个位置，不会在队列中将其移除
     * @return 第一个位置
     */
    @Nonnull
    BlockPos peek();

    int getQueueSize();

    /**
     * 获取结果集合
     * @return 一个BlockPos集合
     */
    @Nonnull
    Collection<BlockPos> getResultCollection();

    default void putBlockPosToResults(@Nonnull BlockPos pos){
        getResultCollection().add(pos.toImmutable());
    }
}
