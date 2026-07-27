/*
 * Copyright 2026 QGMoe
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
 * 版权所有 2026 QGMoe
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

package 清汩萌.天圆地方.世界;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.storage.SaveHandlerMP;
import net.minecraft.world.storage.WorldInfo;
import org.junit.jupiter.api.Assertions;

import javax.annotation.Nonnull;

/**
 * @author QGMoe
 */
public abstract class 模拟世界 extends World {
    protected 模拟世界(final @Nonnull WorldInfo info,
                       final @Nonnull WorldProvider providerIn,
                       final @Nonnull Profiler profilerIn,
                       final boolean client) {
        super(new SaveHandlerMP(), info, providerIn, profilerIn, client);
    }

    @Override
    public void neighborChanged(final @Nonnull BlockPos pos, final @Nonnull Block blockIn, final @Nonnull BlockPos fromPos) {
        if (!this.isRemote) {
            final @Nonnull IBlockState iblockstate = this.getBlockState(pos);

            try {
                iblockstate.neighborChanged(this, pos, blockIn, fromPos);
            } catch (final Throwable throwable) {
                Assertions.fail(throwable);
            }
        }
    }

    @Override
    public void observedNeighborChanged(final @Nonnull BlockPos pos,
                                        final @Nonnull Block changedBlock,
                                        final @Nonnull BlockPos changedBlockPos) {
        if (!this.isRemote) {
            final @Nonnull IBlockState iblockstate = this.getBlockState(pos);

            try {
                iblockstate.getBlock().observedNeighborChange(iblockstate, this, pos, changedBlock, changedBlockPos);
            } catch (final Throwable throwable) {
                Assertions.fail(throwable);
            }
        }
    }

    @Override
    public void markBlocksDirtyVertical(final int x,final int z,final int y1,final int y2) {
        // do nothing
    }

    @Override
    public void markBlockRangeForRenderUpdate(final @Nonnull BlockPos rangeMin, final @Nonnull BlockPos rangeMax) {
        // do nothing
    }

    @Override
    public void markBlockRangeForRenderUpdate(final int x1,final int y1,final int z1,final int x2,final int y2,final int z2) {
        // do nothing
    }
}
