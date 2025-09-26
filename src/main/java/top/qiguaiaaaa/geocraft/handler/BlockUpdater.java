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

package top.qiguaiaaaa.geocraft.handler;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import org.apache.commons.lang3.tuple.Triple;
import top.qiguaiaaaa.geocraft.util.misc.ExtendedNextTickListEntry;

import java.util.*;
import java.util.function.Function;

import static top.qiguaiaaaa.geocraft.util.MiscUtil.getValidWorld;

/**
 * @author QiguaiAAAA
 */
public final class BlockUpdater {
    private static final Function<WorldServer,LinkedList<ExtendedNextTickListEntry>> CREATE_SCHEDULE_LIST = k -> new LinkedList<>();
    static final int MAX_UPDATE_NUM = 65536*4;
    static final Map<WorldServer, List<ExtendedNextTickListEntry>> WORLD_SCHEDULE_MAP = new HashMap<>();
    static final List<ExtendedNextTickListEntry> READY_TO_UPDATES = new ArrayList<>(MAX_UPDATE_NUM/4);

    public static void scheduleUpdate(World world, BlockPos pos, Block block, int delay){
        if(delay == 0){
            READY_TO_UPDATES.add(new ExtendedNextTickListEntry(world,pos,block,delay,0));
            return;
        }
        WorldServer validWorld = getValidWorld(world);
        if(validWorld == null) return;
        List<ExtendedNextTickListEntry> schedules = WORLD_SCHEDULE_MAP.computeIfAbsent(validWorld,CREATE_SCHEDULE_LIST);
        schedules.add(new ExtendedNextTickListEntry(world,pos,block,delay,0));
    }

    public static void onWorldTick(WorldServer world){
        final List<ExtendedNextTickListEntry> schedules = WORLD_SCHEDULE_MAP.get(world);
        if(schedules == null) return;
        int size = schedules.size();
        boolean drop = false;
        for(int i=0,j=0;i<size && j <size;i++){
            ExtendedNextTickListEntry entry = schedules.get(j);
            if(world.getTotalWorldTime()<entry.scheduledTime){
                j++;
                continue;
            }
            schedules.remove(j);
            if(drop) continue;
            drop = READY_TO_UPDATES.size()>MAX_UPDATE_NUM;
            READY_TO_UPDATES.add(entry);
        }
        for(ExtendedNextTickListEntry entry:READY_TO_UPDATES){
            IBlockState state = world.getBlockState(entry.position);
            if(state.getBlock() != entry.getBlock()) continue;
            state.getBlock().updateTick(world,entry.position,state,world.rand);
        }
        READY_TO_UPDATES.clear();
    }

    public static void onServerStop(){
        WORLD_SCHEDULE_MAP.clear();
        READY_TO_UPDATES.clear();
    }
}
