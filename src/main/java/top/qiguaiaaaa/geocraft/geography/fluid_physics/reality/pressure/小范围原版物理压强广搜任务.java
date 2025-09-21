package top.qiguaiaaaa.geocraft.geography.fluid_physics.reality.pressure;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fluids.Fluid;
import top.qiguaiaaaa.geocraft.GeoCraft;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

/**
 * @see IRealityVanillaPressureBFSTask
 * @author QiguaiAAAA
 */
public class 小范围原版物理压强广搜任务 extends 小范围物理压强广搜任务 implements IRealityVanillaPressureBFSTask{
    protected final byte beginQuanta;

    小范围原版物理压强广搜任务(@Nonnull Fluid fluid, @Nonnull IBlockState beginState, @Nonnull BlockPos beginPos, int searchRange) {
        super(fluid, beginState, beginPos, searchRange);
        beginQuanta = (byte) (8-beginState.getValue(BlockLiquid.LEVEL));
    }

    @Override
    public byte getBeginQuanta() {
        return beginQuanta;
    }

    static class Debug extends 小范围原版物理压强广搜任务 implements IRealityDebugPressureBFSTask.IRealityVanillaDebugPressureBFSTask {
        Debug(@Nonnull Fluid fluid, @Nonnull IBlockState beginState, @Nonnull BlockPos beginPos, int searchRange) {
            super(fluid, beginState, beginPos, searchRange);
            GeoCraft.getLogger().info("{} is created",this);
        }

        @Override
        public void cancel() {
            super.cancel();
            cancel_Debug();
        }

        @Override
        public void putBlockPosToResults(@Nonnull BlockPos pos) {
            super.putBlockPosToResults(pos);
            putBlockPosToResults_Debug(pos);
        }

        @Override
        public void queued(@Nonnull BlockPos pos) {
            super.queued(pos);
            queued_Debug(pos);
        }

        @Override
        public void markVisited(@Nonnull BlockPos pos) {
            super.markVisited(pos);
            markVisited_Debug(pos);
        }

        @Override
        public void finish() {
            super.finish();
            finish_Debug();
        }

        @Nonnull
        @Override
        public BlockPos pull() {
            GeoCraft.getLogger().info("{} pulled {}",this,peek());
            return super.pull();
        }

        @Nullable
        @Override
        public Collection<BlockPos> search(@Nonnull WorldServer world) {
            search_Debug(world);
            return super.search(world);
        }
    }
}
