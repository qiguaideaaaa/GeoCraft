package top.qiguaiaaaa.geocraft.geography.fluid_physics.reality.pressure;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.Fluid;

import javax.annotation.Nonnull;

/**
 * @author QiguaiAAAA
 */
public final class RealityPressureTaskBuilder {
    @Nonnull
    public static IRealityVanillaPressureBFSTask createVanillaTask(@Nonnull Fluid fluid, @Nonnull IBlockState beginState, @Nonnull BlockPos beginPos, int searchRange){
        if(searchRange<5)
            return new 小范围原版物理压强广搜任务(fluid, beginState, beginPos, searchRange);
        return new 大范围原版物理压强广搜任务(fluid, beginState, beginPos, searchRange);
    }

    @Nonnull
    public static IRealityDebugPressureBFSTask.IRealityVanillaDebugPressureBFSTask createVanillaTask_Debug(@Nonnull Fluid fluid, @Nonnull IBlockState beginState, @Nonnull BlockPos beginPos, int searchRange){
        if(searchRange<5)
            return new 小范围原版物理压强广搜任务.Debug(fluid, beginState, beginPos, searchRange);
        return new 大范围原版物理压强广搜任务.Debug(fluid, beginState, beginPos, searchRange);
    }

    @Nonnull
    public static IRealityModClassicPressureBFSTask createModClassicTask(@Nonnull Fluid fluid,@Nonnull IBlockState beginState,@Nonnull BlockPos beginPos,int searchRange,int quantaPerBlock){
        if(searchRange<5)
            return new 小范围模组Classic物理压强广搜任务(fluid, beginState, beginPos, searchRange, quantaPerBlock);
        return new 大范围模组Classic物理压强广搜任务(fluid, beginState, beginPos, searchRange, quantaPerBlock);
    }

    @Nonnull
    public static IRealityDebugPressureBFSTask.IRealityModClassicDebugPressureBFSTask createModClassicTask_Debug(@Nonnull Fluid fluid, @Nonnull IBlockState beginState, @Nonnull BlockPos beginPos, int searchRange, int quantaPerBlock){
        if(searchRange<5)
            return new 小范围模组Classic物理压强广搜任务.Debug(fluid, beginState, beginPos, searchRange, quantaPerBlock);
        return new 大范围模组Classic物理压强广搜任务.Debug(fluid, beginState, beginPos, searchRange, quantaPerBlock);
    }
}
