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

package moe.qingu.geocraft.world.scheduler.packed;

import moe.qingu.geocraft.api.util.math.vec.MBlockPos;
import moe.qingu.geocraft.configs.GeneralConfig;
import moe.qingu.geocraft.world.scheduler.ObjectLongHeaps;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import javax.annotation.Nonnull;

/**
 * @author QGMoe
 */
public final class TotalOrderPackedBlockTickScheduler extends PackedBlockTickScheduler{
    private static final PackedBlockTickDatum NULL_DATUM = new PackedBlockTickDatum();
    private static final DatumComparator COMPARATOR = new DatumComparator();
    private final MBlockPos posContainer = new MBlockPos();
    private final long[] xzs = new long[100];
    private final int[] lefts = new int[100];
    private PackedBlockTickDatum[] datumTemp = new PackedBlockTickDatum[0];
    private int tempQueueSize;

    public TotalOrderPackedBlockTickScheduler(@Nonnull final World world) {
        super(world);
    }

    @Override
    @SuppressWarnings("OctalInteger")
    public void update() {
        final long beginTime = System.currentTimeMillis(),maxTime = GeneralConfig.BLOCK_UPDATER_MAX_TIME_USAGE.getValue();
        tempQueueSize = schedules.size();
        this.datumTemp = prepareTotalUpdate(datumTemp,COMPARATOR,NULL_DATUM);
        long count = 0;
        int n;
        do {
            n = collectNext(xzs,lefts);
            for(int i=0;i<n;i++){
                posContainer.setPos((int)(xzs[i]&0xFFFF_FFFFL),(lefts[i]>>>12)&0xF,(int)(xzs[i]>>>32));
                final Block block = Block.REGISTRY.getObjectById(lefts[i]&0_7777);
                final Chunk chunk = world.getChunk(posContainer);
                chunk.markDirty();
                final IBlockState state = chunk.getBlockState(posContainer);
                if(!validator.accepts(posContainer,block,state)) continue;
                try {
                    block.updateTick(world,posContainer,state,world.rand);
                } catch (final Throwable t) {
                    throw createReport(t,posContainer,state);
                }
            }
            count += n;
            if(System.currentTimeMillis() - beginTime > maxTime) break;
        } while (n>0 && count < maxUpdateNum);
    }

    @SuppressWarnings("OctalInteger")
    public int collectNext(final @Nonnull long[] xzs, final @Nonnull int[] lefts) {
        int count = 0;
        final long worldTotalTime = world.getTotalWorldTime();
        while (tempQueueSize > 0 && count < xzs.length) {
            final long pos = volume.temp[0];
            final PackedBlockTickDatum datum = datumTemp[0];
            tempQueueSize--;
            volume.temp[0] = volume.temp[tempQueueSize];
            datumTemp[0] = datumTemp[tempQueueSize];
            if (tempQueueSize != 0) ObjectLongHeaps.downHeap(datumTemp, volume.temp, tempQueueSize, 0, COMPARATOR);
            if (datum.isEmpty()) {
                schedules.remove(pos);
                break;
            }
            final long tick = datum.queue.first();
            final long triggeredTick = datum.queue.baseTime + (tick >>> 32);
            if (Long.compareUnsigned(triggeredTick - worldTotalTime - 1L, 2147483647L) >= 0) {
                datum.lock.lock();
                try{
                    datum.queue.dequeue();
                    final long x = ((pos >>> 32 << 4) + ((tick >>> 12) & 0xFL)) & 0xFFFF_FFFL;
                    final long z = (pos & 0xFFFF_FFFFL << 4) + ((tick >>> 16) & 0xFL);
                    xzs[count] = (z << 32) | x;
                    final int y = (int) ((tick >>> 20) & 0xFFL);
                    final int blockID = (int) (tick & 0_7777L);
                    lefts[count] = (y << 12) | blockID;
                    count++;
                }finally {
                    datum.markDirty();
                    datum.lock.unlock();
                }
                if (datum.isEmpty()) {
                    schedules.remove(pos);
                    continue;
                }
                tempQueueSize++;
                volume.temp[tempQueueSize] = pos;
                datumTemp[tempQueueSize] = datum;
                ObjectLongHeaps.upHeap(datumTemp,volume.temp,tempQueueSize,tempQueueSize-1,COMPARATOR);
            }else break;
        }
        return count;
    }

    private final static class DatumComparator extends ObjectLongHeaps.BiComparator<PackedBlockTickDatum> {

        @Override
        @SuppressWarnings("OctalInteger")
        public int compare(@Nonnull final PackedBlockTickDatum d1,final long pos1, @Nonnull final PackedBlockTickDatum d2,final long pos2) {
            if(d1.isEmpty() || d2.isEmpty()) return Boolean.compare(d1.isEmpty(),d2.isEmpty());
            final long first1 = d1.queue.first();
            final long first2 = d2.queue.first();
            final long triggered1 = d1.queue.baseTime+(first1>>>32);
            final long triggered2 = d2.queue.baseTime+(first2>>>32);
            if(triggered1 != triggered2) return triggered1 - triggered2 < 0?-1:1;
            final long priority1 = (first1>>>28)&0xFL;
            final long priority2 = (first2>>>28)&0xFL;
            if(priority1 != priority2) return Long.compare(priority1,priority2);
            final long y1 = (first1>>>20)&0xFFL;
            final long y2 = (first2>>>20)&0xFFL;
            if(y1!=y2) return Long.compare(y1,y2);
            final int z1 = (int)((pos1>>>32) + (first1>>>16)&0xFL);
            final int z2 = (int)((pos2>>>32) + (first2>>>16)&0xFL);
            if(z1!=z2) return Integer.compare(z1,z2);
            final int x1 = (int)((pos1&0xFFFF_FFFFL) + (first1>>>12)&0xFL);
            final int x2 = (int)((pos2&0xFFFF_FFFFL) + (first2>>>12)&0xFL);
            if(x1!=x2) return Integer.compare(x1,x2);
            final long block1 = first1 & 0_7777L;
            final long block2 = first2 & 0_7777L;
            return Long.compare(block1,block2);
        }
    }
}
