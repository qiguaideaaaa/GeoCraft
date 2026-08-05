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

package moe.qingu.orbtellus.world.scheduler;

import it.unimi.dsi.fastutil.Arrays;
import it.unimi.dsi.fastutil.Swapper;
import it.unimi.dsi.fastutil.ints.IntComparator;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;

import javax.annotation.Nonnull;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author QGMoe
 */
public final class ChunkyScheduledData<T> {
    public final Long2ObjectOpenHashMap<T> data = new Long2ObjectOpenHashMap<>();
    public final ConcurrentLinkedQueue<T> dirties = new ConcurrentLinkedQueue<>();
    public final LongOpenHashSet schedules = new LongOpenHashSet();
    private final DistanceComparator sorter = new DistanceComparator();
    public long[] temp = new long[0];
    public double[] tempDist;

    public void sortTempByPlayers(final @Nonnull World world,final int size){
        final EntityPlayerMP[] players = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers()
                .stream()
                .filter(p -> p.getServerWorld() == world)
                .toArray(EntityPlayerMP[]::new);
        if(players.length == 0) return;
        if(tempDist == null || size > tempDist.length) tempDist = new double[size];
        for(int i =0;i<size;i++){
            final long pos = temp[i];
            final int x = (int) (pos & 0xFFFF_FFFFL) << 4;
            final int z = (int) (pos >>> 32) << 4;
            double minDist = Double.POSITIVE_INFINITY;
            for(final EntityPlayerMP player:players){
                final double dx = player.posX - x;
                final double dz = player.posZ - z;
                minDist = Math.min(minDist,dx*dx+dz*dz);
            }
            tempDist[i] = minDist;
        }
        Arrays.quickSort(0, size, this.sorter, this.sorter);
    }

    protected final class DistanceComparator implements IntComparator, Swapper {
        @Override
        public int compare(final int k1,final int k2) {
            return Double.compare(tempDist[k1],tempDist[k2]);
        }

        @Override
        public int compare(final @Nonnull Integer o1, final @Nonnull Integer o2) {
            return compare(o1.intValue(),o2.intValue());
        }

        @Override
        public void swap(final int a,final int b) {
            final long tempChunk = temp[a];
            temp[a] = temp[b];
            temp[b] = tempChunk;
            final double tempDis = tempDist[a];
            tempDist[a] = tempDist[b];
            tempDist[b] = tempDis;
        }
    }
}
