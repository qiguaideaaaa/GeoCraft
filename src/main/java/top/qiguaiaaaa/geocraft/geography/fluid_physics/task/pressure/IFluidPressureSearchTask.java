package top.qiguaiaaaa.geocraft.geography.fluid_physics.task.pressure;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fluids.Fluid;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

/**
 * @author QiguaiAAAA
 */
public interface IFluidPressureSearchTask {
    @Nonnull
    Fluid getFluid();
    @Nonnull
    BlockPos getBeginPos();
    @Nonnull
    IBlockState getBeginState();
    @Nullable
    Collection<BlockPos> search(@Nonnull WorldServer world);
    void cancel();
    void finish();
    boolean isFinished();
    boolean isEqualState(@Nonnull IBlockState curState);
}
