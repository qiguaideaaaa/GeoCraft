/*
 * Copyright 2025 QiguaiAAAA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * 版权所有 2025 QiguaiAAAA
 * 根据Apache许可证第2.0版（“本许可证”）许可；
 * 除非符合本许可证的规定，否则你不得使用此文件。
 * 你可以在此获取本许可证的副本：
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * 除非所适用法律要求或经书面同意，在本许可证下分发的软件是“按原样”分发的，
 * 没有任何形式的担保或条件，不论明示或默示。
 * 请查阅本许可证了解有关本许可证下许可和限制的具体要求。
 * 中文译文来自开放原子开源基金会，非官方译文，如有疑议请以英文原文为准
 */

package top.qiguaiaaaa.geocraft.geography.fluid_physics.reality.pressure;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.Fluid;
import top.qiguaiaaaa.geocraft.geography.fluid_physics.task.pressure.FluidPressureSmallBFSBaseTask;

import javax.annotation.Nonnull;

/**
 * @author QiguaiAAAA
 */
public final class RealityPressureTaskBuilder {
    @Nonnull
    public static IRealityVanillaPressureSearchTask createVanillaTask(@Nonnull Fluid fluid, @Nonnull IBlockState beginState, @Nonnull BlockPos beginPos, int searchRange){
        int maxSearchTimes = IRealityPressureSearchTask.getMaxSearchTimesFromRange(searchRange);
        if(maxSearchTimes<= 小范围物理压强广搜任务.TIMES_PER_SEARCH){
            return new 小范围原版物理压强单次广搜任务(fluid, beginState, beginPos, searchRange);
        }
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
