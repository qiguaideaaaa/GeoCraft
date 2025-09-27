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
import net.minecraft.world.WorldServer;
import net.minecraftforge.fluids.Fluid;
import top.qiguaiaaaa.geocraft.configs.FluidPhysicsConfig;
import top.qiguaiaaaa.geocraft.geography.fluid_physics.task.pressure.FluidPressureSearchTaskSmallRangeRelativeResult;
import top.qiguaiaaaa.geocraft.geography.fluid_physics.task.pressure.FluidPressureSmallBFSBaseTask;
import top.qiguaiaaaa.geocraft.geography.fluid_physics.task.pressure.IFluidPressureSearchTaskResult;
import top.qiguaiaaaa.geocraft.util.math.vec.RelativeBlockPosB;
import top.qiguaiaaaa.geocraft.util.math.vec.RelativeBlockPosS;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * @author QiguaiAAAA
 */
public abstract class 小范围物理压强广搜任务 extends FluidPressureSmallBFSBaseTask implements IRealityPressureBFSTask{
    private static final RelativeBlockPosS.Mutable mutablePosForRes = new RelativeBlockPosS.Mutable();
    protected static final short TIMES_PER_SEARCH;
    protected final short maxSearchTimes;
    protected final FluidPressureSearchTaskSmallRangeRelativeResult res;
    protected short searchTimes = 0;

    static {
        TIMES_PER_SEARCH = FluidPhysicsConfig.REALITY_MAX_SEARCH_TIMES_PER_SEARCH_FOR_SMALL_RANGE_TASK.getValue().shortValue();
    }

    public 小范围物理压强广搜任务(@Nonnull Fluid fluid, @Nonnull IBlockState beginState, @Nonnull BlockPos beginPos, int searchRange) {
        super(fluid, beginState, beginPos);
        queued(beginPos);
        markVisited(beginPos);
        if(searchRange >4) throw new IllegalArgumentException("FluidPressureSmallBFSBaseTask can not handle search range larger than 511 blocks!");
        else if(searchRange == 4) maxSearchTimes = MAX_RELATIVE_POS_OFFSET;
        else maxSearchTimes = (short) IRealityPressureSearchTask.getMaxSearchTimesFromRange(searchRange);
        res = new FluidPressureSearchTaskSmallRangeRelativeResult(beginPos);
    }

    @Nonnull
    @Override
    public Collection<BlockPos> getResultCollection() {
        return res.toResultCollection();
    }

    @Override
    public void putBlockPosToResults(@Nonnull BlockPos pos) {
        res.put(mutablePosForRes.setPos(beginPos,pos));
    }

    @Override
    public String toString() {
        return String.format("[Pressure Task B][at=%s,state=%s,fluid=%s,range=%s]", beginPos.toString(),beginState,fluid.getName(),maxSearchTimes);
    }

    @Override
    public int getSearchTimes() {
        return searchTimes;
    }

    @Override
    public int getMaxSearchTimes() {
        return maxSearchTimes;
    }

    @Override
    public boolean hasFoundEnoughResults() {
        return res.size()>getBeginQuanta()+2;
    }

    @Override
    public boolean hasSearchTimeReachedMax() {
        return searchTimes>=maxSearchTimes;
    }

    @Nullable
    @Override
    public IFluidPressureSearchTaskResult search(@Nonnull WorldServer world) {
        for(int i=0;i<TIMES_PER_SEARCH;i++){
            if(isQueueEmpty()) break;
            BlockPos pos = pull();
            searchTimes++;
            if(search_Inner(world,pos)) break;
        }
        return res;
    }
}
