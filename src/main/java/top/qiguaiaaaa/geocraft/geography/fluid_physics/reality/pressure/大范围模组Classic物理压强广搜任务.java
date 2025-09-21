package top.qiguaiaaaa.geocraft.geography.fluid_physics.reality.pressure;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;
import top.qiguaiaaaa.geocraft.GeoCraft;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

/**
 * @author QiguaiAAAA
 */
public class 大范围模组Classic物理压强广搜任务 extends 大范围物理压强广搜任务 implements IRealityModClassicPressureBFSTask{
    protected final byte beginQuanta;
    protected final byte quantaPerBlock;
    protected final byte densityDir;

    大范围模组Classic物理压强广搜任务(@Nonnull Fluid fluid, @Nonnull IBlockState beginState, @Nonnull BlockPos beginPos, int searchRange, int quantaPerBlock) {
        super(fluid, beginState, beginPos,searchRange);
        beginQuanta = (byte) (quantaPerBlock-beginState.getValue(BlockFluidBase.LEVEL));
        this.quantaPerBlock = (byte) quantaPerBlock;
        this.densityDir = (byte) (fluid.getDensity()>0?1:-1);
    }

    @Override
    public byte getDensityDir() {
        return densityDir;
    }

    @Override
    public byte getBeginQuanta() {
        return beginQuanta;
    }

    @Override
    public byte getQuantaPerBlock() {
        return quantaPerBlock;
    }

    static class Debug extends 大范围模组Classic物理压强广搜任务 implements IRealityDebugPressureBFSTask.IRealityModClassicDebugPressureBFSTask {

        Debug(@Nonnull Fluid fluid, @Nonnull IBlockState beginState, @Nonnull BlockPos beginPos, int searchRange, int quantaPerBlock) {
            super(fluid, beginState, beginPos, searchRange, quantaPerBlock);
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
