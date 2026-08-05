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

package moe.qingu.orbtellus.world.scheduler.boxed;

import it.unimi.dsi.fastutil.longs.LongIterator;
import moe.qingu.orbtellus.api.util.annotation.ThreadOnly;
import moe.qingu.orbtellus.api.util.annotation.ThreadType;
import moe.qingu.orbtellus.api.world.tick.IScheduledTick;
import moe.qingu.orbtellus.api.world.tick.scheduler.BlockTickScheduler;
import moe.qingu.orbtellus.world.scheduler.GeoBlockTickType;
import net.minecraft.world.World;
import net.minecraft.world.天圆地方$WorldUtil;

import javax.annotation.Nonnull;
import java.util.PriorityQueue;

/**
 * @author QGMoe
 */
public final class TotalOrderBoxedBlockTickScheduler extends BoxedBlockTickScheduler{
    private final PriorityQueue<IScheduledTick> queue = new PriorityQueue<>();

    public TotalOrderBoxedBlockTickScheduler(@Nonnull final World world) {
        super(world);
    }

    /**
     * 一个工具方法，返回 {@link BlockTickScheduler}，用于 {@link GeoBlockTickType}
     * 避免类过早被加载
     */
    @Nonnull
    public static BlockTickScheduler create(final @Nonnull World world){
        return new TotalOrderBoxedBlockTickScheduler(world);
    }

    @Override
    @ThreadOnly(ThreadType.MINECRAFT_SERVER)
    public void update() {
        final long totalWorldTime = world.getTotalWorldTime();
        long count = 0;
        final LongIterator iterator = schedules.iterator();
        while (count < maxUpdateNum && iterator.hasNext()){
            final long pos = iterator.nextLong();
            final BoxedBlockTickDatum datum = data.get(pos);
            if(datum == null) {
                iterator.remove();
                continue;
            }
            switch (天圆地方$WorldUtil.ensureAreaTickable(world,datum.getChunk())){
                case -1:{
                    iterator.remove();
                    data.remove(pos);
                } case 0: continue; //-1 & 0 都continue
            }
            int cot = 0;
            datum.lock.lock();
            try {
                IScheduledTick tick;
                while (!datum.queue.isEmpty() &&
                        Long.compareUnsigned(datum.queue.peek().triggeredTick() - totalWorldTime -1L, 2147483647L) >= 0 //环上到期,因为延迟不可能超过Integer.MAX_VALUE,因此超出则代表时间过了
                ){
                    datum.set.remove(tick = datum.queue.poll());
                    queue.add(tick);
                    cot++;
                }
                count += cot;
            }finally {
                datum.lock.unlock();
            }
            if(cot != 0 && datum.markDirty()) dirties.add(datum);
            if(datum.queue.isEmpty()) iterator.remove();
        }
        consumeByTotalOrder(queue);
    }
}
