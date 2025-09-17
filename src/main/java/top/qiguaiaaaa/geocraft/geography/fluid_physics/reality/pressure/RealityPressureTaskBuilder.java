package top.qiguaiaaaa.geocraft.geography.fluid_physics.reality.pressure;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.Fluid;
import top.qiguaiaaaa.geocraft.geography.fluid_physics.task.IFluidPressureBFSTask;

import javax.annotation.Nonnull;

/**
 * @author QiguaiAAAA
 */
public final class RealityPressureTaskBuilder {
    @Nonnull
    public static IFluidPressureBFSTask createVanillaTask(@Nonnull Fluid fluid, @Nonnull IBlockState beginState, @Nonnull BlockPos beginPos, int searchRange){
        if(searchRange<2)
            return new ByteRealityVanillaPressureSearchTask(fluid, beginState, beginPos, searchRange);
        return new ShortRealityVanillaPressureSearchTask(fluid, beginState, beginPos, searchRange);
    }

    @Nonnull
    public static IFluidPressureBFSTask createVanillaTask_Debug(@Nonnull Fluid fluid, @Nonnull IBlockState beginState, @Nonnull BlockPos beginPos, int searchRange){
        return new ShortRealityVanillaPressureSearchTask.Debug(fluid, beginState, beginPos, searchRange);
    }

    @Nonnull
    public static IFluidPressureBFSTask createModClassicTask(@Nonnull Fluid fluid,@Nonnull IBlockState beginState,@Nonnull BlockPos beginPos,int searchRange,int quantaPerBlock){
        if(searchRange<2)
            return new ByteRealityModClassicPressureSearchTask(fluid, beginState, beginPos, searchRange, quantaPerBlock);
        return new ShortRealityModClassicPressureSearchTask(fluid, beginState, beginPos, searchRange, quantaPerBlock);
    }
}
