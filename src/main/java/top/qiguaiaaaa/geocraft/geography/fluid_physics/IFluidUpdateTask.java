package top.qiguaiaaaa.geocraft.geography.fluid_physics;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;

import javax.annotation.Nonnull;
import java.util.Random;

/**
 * 一个流体更新任务
 * @author QiguaiAAAA
 */
public interface IFluidUpdateTask {
    void onUpdate(@Nonnull World world,@Nonnull IBlockState state,@Nonnull Random rand);
    @Nonnull
    Block getBlock();
    @Nonnull
    BlockPos getPos();
    @Nonnull
    Fluid getFluid();
}
