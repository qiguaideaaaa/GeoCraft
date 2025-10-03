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
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fluids.IFluidBlock;
import top.qiguaiaaaa.geocraft.configs.GeneralConfig;
import top.qiguaiaaaa.geocraft.geography.fluid_physics.reality.pressure.小范围模组Classic物理压强单次广搜任务;
import top.qiguaiaaaa.geocraft.util.misc.ExtendedNextTickListEntry;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

import static top.qiguaiaaaa.geocraft.util.MiscUtil.getValidWorld;

/**
 * @author QiguaiAAAA
 */
public final class BlockUpdater {
    private static final Function<WorldServer,Set<ExtendedNextTickListEntry>> CREATE_SCHEDULE_LIST = k -> new LinkedHashSet<>();
    private static final Function<WorldServer,Consumer<ExtendedNextTickListEntry>> CREATE_CALC_DIS_TO_CLOSEST_PLAYER =
            k -> entry -> entry.calcDisSqToNearestPlayer(k);
    private static final Comparator<ExtendedNextTickListEntry> COMPARE_BY_DIS_TO_PLAYER =
            Comparator.comparingDouble(ExtendedNextTickListEntry::getDisSqToNearestPlayer);
    private static final Map<WorldServer,Consumer<ExtendedNextTickListEntry>> CALC_DIS_TO_CLOSEST_PLAYER_MAP = new HashMap<>();
    static final int MAX_UPDATE_NUM;
    static final Map<WorldServer, Set<ExtendedNextTickListEntry>> WORLD_SCHEDULE_MAP = new HashMap<>();
    static final List<ExtendedNextTickListEntry> READY_TO_UPDATES;

    static {
        MAX_UPDATE_NUM = GeneralConfig.BLOCK_UPDATER_MAX_UPDATES_BLOCK.getValue();
        READY_TO_UPDATES = new ArrayList<>(MAX_UPDATE_NUM/4);
    }

    public static void scheduleUpdate(World world, BlockPos pos, Block block, int delay){
        WorldServer validWorld = getValidWorld(world);
        if(validWorld == null) return;
        Set<ExtendedNextTickListEntry> schedules = WORLD_SCHEDULE_MAP.computeIfAbsent(validWorld,CREATE_SCHEDULE_LIST);
        ExtendedNextTickListEntry entry = new ExtendedNextTickListEntry(world,pos,block,delay,0);
        schedules.add(entry);
    }

    public static void onWorldTick(WorldServer world){
        final long beginTime = System.currentTimeMillis();
        final Set<ExtendedNextTickListEntry> schedules = WORLD_SCHEDULE_MAP.get(world);
        if(schedules == null){
            return;
        }
        boolean drop = false;
        Iterator<ExtendedNextTickListEntry> iterator = schedules.iterator();
        while (iterator.hasNext()) {
            ExtendedNextTickListEntry entry = iterator.next();
            if(world.getTotalWorldTime()<entry.scheduledTime) continue;
            iterator.remove();
            if(drop) break;
            drop = READY_TO_UPDATES.size()>MAX_UPDATE_NUM;
            READY_TO_UPDATES.add(entry);
        }

        if(GeneralConfig.SORT_UPDATE_TASKS_BY_DISTANCE_TO_PLAYERS.getValue()){
            READY_TO_UPDATES.forEach(CALC_DIS_TO_CLOSEST_PLAYER_MAP.computeIfAbsent(world,CREATE_CALC_DIS_TO_CLOSEST_PLAYER));
            READY_TO_UPDATES.sort(COMPARE_BY_DIS_TO_PLAYER);
        }

        final int maxTimeUsage = GeneralConfig.BLOCK_UPDATER_MAX_TIME_USAGE.getValue();
        int i = 0;
        boolean onlyDynamicFluid = false;
        for(ExtendedNextTickListEntry entry:READY_TO_UPDATES){
            IBlockState state = world.getBlockState(entry.position);
            if(state.getBlock() != entry.getBlock()) continue;
            boolean isDynamicFluid = entry.getBlock() instanceof BlockDynamicLiquid || entry.getBlock() instanceof IFluidBlock;
            if(isDynamicFluid || !onlyDynamicFluid) state.getBlock().updateTick(world,entry.position,state,world.rand);
            i++;
            if(!onlyDynamicFluid && (i&127) == 0){
                if(maxTimeUsage <0) continue;
                if(System.currentTimeMillis()-beginTime>maxTimeUsage) onlyDynamicFluid = true;
                if(!GeneralConfig.ALLOW_DYNAMIC_FLUID_UPDATE.getValue()) break;
            }
        }
        READY_TO_UPDATES.clear();
    }

    public static void onServerStop(){
        WORLD_SCHEDULE_MAP.clear();
        READY_TO_UPDATES.clear();
        CALC_DIS_TO_CLOSEST_PLAYER_MAP.clear();
    }
}
