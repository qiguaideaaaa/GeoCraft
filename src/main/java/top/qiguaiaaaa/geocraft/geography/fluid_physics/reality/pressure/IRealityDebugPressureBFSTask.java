package top.qiguaiaaaa.geocraft.geography.fluid_physics.reality.pressure;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import top.qiguaiaaaa.geocraft.GeoCraft;
import top.qiguaiaaaa.geocraft.api.util.debug.IDebug;
import top.qiguaiaaaa.geocraft.geography.fluid_physics.task.pressure.IFluidPressureBFSTask;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

/**
 * @author QiguaiAAAA
 */
public interface IRealityDebugPressureBFSTask extends IDebug,IRealityPressureBFSTask {

    default void isEqualState_Debug(@Nonnull IBlockState curState) {
        GeoCraft.getLogger().info("{} stopped because source state changed to {}",this,curState);
    }

    default void cancel_Debug() {
        GeoCraft.getLogger().info("{} is cancelled",this);
    }

    default void putBlockPosToResults_Debug(@Nonnull BlockPos pos) {
        GeoCraft.getLogger().info("{} found a possible result {}",this,pos);
    }

    default void queued_Debug(@Nonnull BlockPos pos) {
        GeoCraft.getLogger().info("{} queued pos {}",this,pos);
    }

    default void markVisited_Debug(@Nonnull BlockPos pos) {
        GeoCraft.getLogger().info("{} mark {} as visited",this,pos);
    }

    default void finish_Debug() {
        GeoCraft.getLogger().info("{} finished:",this);
        for(BlockPos pos:getResultCollection()){
            GeoCraft.getLogger().info("{}",pos);
        }
    }

    default void canSearchInto_Debug(@Nonnull WorldServer world,@Nonnull BlockPos pos,@Nonnull int[] dir) {
        GeoCraft.getLogger().info("{} checked {} via dir ({},{},{}) and is sure it can be searched into.",
                this,pos,dir[0],dir[1],dir[2]);
    }

    default void search_Debug(@Nonnull WorldServer world) {
        GeoCraft.getLogger().info("{} is being running",this);
    }

    @Override
    default boolean isFinished() {
        if(IRealityPressureBFSTask.super.isFinished()){
            GeoCraft.getLogger().info("{} is finished with visited poses {} , queued poses {}, res poses {}, search times {}, max search times {}",
                    this,getVisitedSize(),getQueueSize(),getResultCollection().size(),getSearchTimes(),getMaxSearchTimes());
            return true;
        }
        return false;
    }

    interface IRealityVanillaDebugPressureBFSTask extends IRealityVanillaPressureBFSTask,IRealityDebugPressureBFSTask{
        @Override
        default boolean isEqualState(@Nonnull IBlockState curState) {
            if(IRealityVanillaPressureBFSTask.super.isEqualState(curState)){
                return true;
            }
            isEqualState_Debug(curState);
            return false;
        }

        @Override
        default boolean canSearchInto(@Nonnull WorldServer world, @Nonnull BlockPos pos, int[] dir) {
            if(IRealityVanillaPressureBFSTask.super.canSearchInto(world, pos, dir)){
                canSearchInto_Debug(world,pos,dir);
                return true;
            }
            return false;
        }
    }

    interface IRealityModClassicDebugPressureBFSTask extends IRealityModClassicPressureBFSTask,IRealityDebugPressureBFSTask{
        @Override
        default boolean isEqualState(@Nonnull IBlockState curState) {
            if(IRealityModClassicPressureBFSTask.super.isEqualState(curState)){
                return true;
            }
            isEqualState_Debug(curState);
            return false;
        }

        @Override
        default boolean canSearchInto(@Nonnull WorldServer world, @Nonnull BlockPos pos, int[] dir) {
            if(IRealityModClassicPressureBFSTask.super.canSearchInto(world, pos, dir)){
                canSearchInto_Debug(world,pos,dir);
                return true;
            }
            return false;
        }
    }
}
