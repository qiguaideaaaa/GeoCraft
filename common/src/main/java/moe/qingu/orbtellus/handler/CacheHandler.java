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

package moe.qingu.orbtellus.handler;

import moe.qingu.orbtellus.api.fluidphysics.FluidPhysicsSystem;
import moe.qingu.orbtellus.api.world.tick.TickPriority;
import moe.qingu.orbtellus.api.world.tick.scheduler.BlockTickScheduler;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

import javax.annotation.Nonnull;

/**
 * @author QGMoe
 */
public final class CacheHandler {
    public static WorldServer currentWorld;
    public static BlockTickScheduler currentBlockScheduler;
    public static FluidPhysicsSystem currentFluidPhysicsSystem;

    private CacheHandler(){}

    public static void schedule(final @Nonnull World world,
                                final @Nonnull BlockPos pos,
                                final @Nonnull Block block,
                                final int delay,
                                final @Nonnull TickPriority priority){
        if(currentBlockScheduler == null) return;
        if(currentWorld == world) currentBlockScheduler.schedule(pos, block, delay, priority);
        else BlockTickScheduler.schedule(world, pos, block, delay, priority);
    }

    public static double getGravity(final @Nonnull World world){
        return CacheHandler.currentWorld == world? CacheHandler.currentFluidPhysicsSystem.getGravity(): FluidPhysicsSystem.getSystem(world).getGravity();
    }

    public static double getLevity(final @Nonnull World world){
        return CacheHandler.currentWorld == world? CacheHandler.currentFluidPhysicsSystem.getLevity(): FluidPhysicsSystem.getSystem(world).getLevity();
    }

    public static void loadCache(final @Nonnull WorldServer world){
        currentWorld = world;
        currentBlockScheduler = BlockTickScheduler.getScheduler(world);
        currentFluidPhysicsSystem = FluidPhysicsSystem.getSystem(world);
    }

    public static void resetCache(){
        currentWorld = DimensionManager.getWorld(0);
        if(currentWorld == null) return;
        currentBlockScheduler = BlockTickScheduler.getScheduler(currentWorld);
        currentFluidPhysicsSystem = FluidPhysicsSystem.getSystem(0);
    }

    public static void clearCache(){
        currentWorld = null;
        currentBlockScheduler = null;
        currentFluidPhysicsSystem = null;
    }
}
