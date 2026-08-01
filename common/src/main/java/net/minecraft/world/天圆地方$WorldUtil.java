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

package net.minecraft.world;

import net.minecraft.world.chunk.Chunk;

import javax.annotation.Nonnull;

/**
 * @author QGMoe
 */
public final class 天圆地方$WorldUtil {

    private 天圆地方$WorldUtil(){}

    public static int ensureAreaTickable(final @Nonnull World world, final int chunkX, final int chunkZ){
        if(!world.isChunkLoaded(chunkX,chunkZ,true)) return -1;
        return isNeighbourLoaded(world, chunkX, chunkZ) ? 1:0;
    }

    public static int ensureAreaTickable(final @Nonnull World world, final @Nonnull Chunk chunk){
        if(!chunk.isLoaded()) return -1;
        return isNeighbourLoaded(world, chunk.x, chunk.z)?1:0;
    }

    public static boolean isNeighbourLoaded(final @Nonnull World world,final int chunkX,final int chunkZ){
        return world.isChunkLoaded(chunkX-1,chunkZ-1,true) &&
                world.isChunkLoaded(chunkX,chunkZ-1,true) &&
                world.isChunkLoaded(chunkX+1,chunkZ-1,true) &&
                world.isChunkLoaded(chunkX-1,chunkZ,true) &&
                world.isChunkLoaded(chunkX+1,chunkZ,true) &&
                world.isChunkLoaded(chunkX-1,chunkZ+1,true) &&
                world.isChunkLoaded(chunkX,chunkZ+1,true) &&
                world.isChunkLoaded(chunkX+1,chunkZ+1,true);
    }
}
