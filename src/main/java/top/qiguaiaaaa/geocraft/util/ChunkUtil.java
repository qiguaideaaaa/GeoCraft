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

package top.qiguaiaaaa.geocraft.util;

import net.minecraft.init.Biomes;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fluids.Fluid;
import top.qiguaiaaaa.geocraft.api.util.FluidUtil;
import top.qiguaiaaaa.geocraft.util.math.vec.BlockPosI;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class ChunkUtil {
    public static final List<EnumFacing> HORIZONTALS = Collections.unmodifiableList(Arrays.asList(
            EnumFacing.WEST, EnumFacing.EAST, EnumFacing.NORTH, EnumFacing.SOUTH));

    public static int getSameLiquidDepth(Chunk chunk,int x,int y,int z, Fluid fluid, int maxDepth){
        int ans = 0;
        while (FluidUtil.getFluid(chunk.getBlockState(x,y,z))== fluid){
            ans++;
            if(ans >maxDepth) break;
            if(y>0) y--;
        }
        return ans;
    }

    public static int getNeighborsLightFor(World world, EnumSkyBlock type, BlockPos pos) {
        if (!world.provider.hasSkyLight() && type == EnumSkyBlock.SKY) {
            return 0;
        }

        BlockPosI.Mutable mutable = new BlockPosI.Mutable(pos);
        if (pos.getY() < 0) {
            mutable.setPos(pos.getX(), 0, pos.getZ());
        }

        if (!world.isValid(mutable)) {
            return type.defaultLightValue;
        } else if (!world.isBlockLoaded(mutable)) {
            return type.defaultLightValue;
        }
        int light = 0;
        for(EnumFacing facing:EnumFacing.values()){
            light = Math.max(light,world.getLightFor(EnumSkyBlock.SKY,mutable.offsetM(facing)));
        }
        return light;
    }

    public static Biome getMainBiome(Chunk chunk){
        byte[] biomes = chunk.getBiomeArray();
        short[] frequency = new short[biomes.length+256];
        for (int biome : biomes) {
            if(biome<0) biome = biome+256;
            frequency[biome+256]++;
        }
        int maxPosition = 0,maxFrequency = -1;
        for(int i=0;i< frequency.length;i++){
            if(frequency[i]>maxFrequency){
                maxFrequency = frequency[i];
                maxPosition = i;
            }
        }
        return Biome.getBiome(maxPosition-256, Biomes.PLAINS);
    }
}
