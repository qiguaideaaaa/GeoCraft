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

package moe.qingu.geocraft.world.scheduler.boxed;

import moe.qingu.geocraft.api.util.annotation.ThreadOnly;
import moe.qingu.geocraft.api.util.annotation.ThreadType;
import moe.qingu.geocraft.api.world.tick.IScheduledTick;
import moe.qingu.geocraft.api.world.tick.scheduler.BlockTickScheduler;
import moe.qingu.geocraft.configs.GeneralConfig;
import moe.qingu.geocraft.world.scheduler.packed.PartialOrderPackedBlockTickScheduler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

import javax.annotation.Nonnull;

/**
 * @author QGMoe
 */
public final class PartialOrderBoxedBlockTickScheduler extends BoxedBlockTickScheduler{

    public PartialOrderBoxedBlockTickScheduler(@Nonnull final World world) {
        super(world);
    }

    /**
     * 一个工具方法，返回 {@link BlockTickScheduler}，用于 {@link moe.qingu.geocraft.world.scheduler.GeoBlockTickType}
     * 避免类过早被加载
     */
    @Nonnull
    public static BlockTickScheduler create(final @Nonnull World world){
        return new PartialOrderPackedBlockTickScheduler(world);
    }

    @Override
    @ThreadOnly(ThreadType.MINECRAFT_SERVER)
    public void update() {
        final long beginTime = System.currentTimeMillis(),maxTime = GeneralConfig.BLOCK_UPDATER_MAX_TIME_USAGE.getValue();
        final long totalWorldTime = world.getTotalWorldTime();
        final IScheduledTick[] tempArr = new IScheduledTick[100];
        preparePartialUpdate();
        final int size = schedules.size();
        long count = 0;
        int i = 0;
        while (count < maxUpdateNum && i < size){
            final long pos = volume.temp[i++];
            final BoxedBlockTickDatum datum = data.get(pos);
            if(datum == null) {
                schedules.remove(pos);
                continue;
            }
            int cot = 0;
            datum.lock.lock();
            final Chunk chunk = datum.getChunk();
            final ExtendedBlockStorage[] ebs= chunk.getBlockStorageArray();
            try {
                int n;
                do {
                    n = 0;
                    while (!datum.queue.isEmpty() &&
                            n < tempArr.length &&
                            Long.compareUnsigned(datum.queue.peek().triggeredTick() - totalWorldTime -1L, 2147483647L) >= 0 //环上到期,因为延迟不可能超过Integer.MAX_VALUE,因此超出则代表时间过了
                    ) datum.set.remove(tempArr[n++] = datum.queue.poll());
                    cot += n;
                    count += n;
                    for(int j=0;j<n;j++) //noinspection DataFlowIssue
                        consume(ebs,tempArr[j]);
                } while (n>0 && count < maxUpdateNum);
            }finally {
                datum.lock.unlock();
            }
            if(cot != 0 && datum.markDirty()) dirties.add(datum);
            if(datum.queue.isEmpty()) schedules.remove(pos);
            if(System.currentTimeMillis() - beginTime > maxTime) break;
        }
    }

    private void consume(final @Nonnull ExtendedBlockStorage[] ebs,
                         final @Nonnull IScheduledTick tick){
        final BlockPos position = tick.pos();
        final @Nonnull IBlockState state = getBlockState(ebs,position);

        if(!validator.accepts(position,tick.block(),state)) return;
        try {
            state.getBlock().updateTick(world,position,state,world.rand);
        } catch (final Throwable t) {
            throw createReport(t,position,state);
        }
    }

    private @Nonnull IBlockState getBlockState(final @Nonnull ExtendedBlockStorage[] ebs,final @Nonnull BlockPos pos){
        final int y = pos.getY();
        if(y<0 || y > 255) return this.world.getBlockState(pos);
        else {
            final ExtendedBlockStorage storage = ebs[y>>4];
            if(storage == Chunk.NULL_BLOCK_STORAGE) return Blocks.AIR.getDefaultState();
            else return storage.get(pos.getX() & 0xF,y &0xF,pos.getZ() &0xF);
        }
    }
}
