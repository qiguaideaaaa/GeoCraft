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

import moe.qingu.geocraft.api.util.annotation.ThreadOnly;
import moe.qingu.geocraft.api.util.annotation.ThreadType;
import moe.qingu.geocraft.api.util.math.vec.MBlockPos;
import moe.qingu.geocraft.api.world.tick.scheduler.BlockTickScheduler;
import moe.qingu.geocraft.configs.GeneralConfig;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author QGMoe
 */
public final class PartialOrderPackedBlockTickScheduler extends PackedBlockTickScheduler{
    private final Consumer consumer = new Consumer();
    private final long[] tempArr = new long[100];

    public PartialOrderPackedBlockTickScheduler(@Nonnull final World world) {
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
    public void update(){
        final long beginTime = System.currentTimeMillis(),maxTime = GeneralConfig.BLOCK_UPDATER_MAX_TIME_USAGE.getValue();
        final long totalWorldTime = world.getTotalWorldTime();
        preparePartialUpdate();
        final int size = schedules.size();
        long count = 0;
        int i = 0;
        while (count < maxUpdateNum && i < size){
            final long pos = volume.temp[i++];
            final PackedBlockTickDatum datum = data.get(pos);
            if(datum == null) {
                schedules.remove(pos);
                continue;
            }
            final int z = (int) (pos>>Integer.SIZE);
            final int x = (int) pos;
            int cot = 0;
            final Chunk chunk = world.getChunk(x,z);
            consumer.ebs = chunk.getBlockStorageArray();
            consumer.baseX = chunk.x <<4;
            consumer.baseZ = chunk.z <<4;
            datum.lock.lock();
            try {
                int n;
                do {
                    n = datum.queue.forNext(totalWorldTime,consumer,tempArr,datum.lock);
                    cot += n;
                    count += n;
                } while (n>0 && count < maxUpdateNum);
            }finally {
                datum.lock.unlock();
            }
            if(cot != 0 && datum.markDirty()){
                chunk.markDirty();
                dirties.add(datum);
            }
            if(datum.queue.isEmpty()) schedules.remove(pos);
            if(System.currentTimeMillis() - beginTime > maxTime) break;
        }
        consumer.ebs = null;
    }

    private final class Consumer extends PackedBlockTickConsumer {
        private final MBlockPos posContainer = new MBlockPos();
        private int baseX;
        private int baseZ;
        private ExtendedBlockStorage[] ebs;

        @Override
        public void consume(final int x,final int y,final int z, @Nonnull final Block block) {
            final @Nullable ExtendedBlockStorage storage = ebs[y>>4];
            if(storage != Chunk.NULL_BLOCK_STORAGE){
                final IBlockState state = storage.get(x,y & 0xF,z);
                posContainer.setPos(baseX + x, y, baseZ + z);
                if(!validator.accepts(posContainer,block,state)) return;
                final World world = PartialOrderPackedBlockTickScheduler.this.world;
                try {
                    block.updateTick(world,posContainer,state,world.rand);
                } catch (final Throwable t) {
                    throw createReport(t,posContainer,state);
                }
            }
        }
    }
}
