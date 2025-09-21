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

package top.qiguaiaaaa.geocraft.geography.atmosphere.system;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import top.qiguaiaaaa.geocraft.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.geocraft.api.atmosphere.AtmosphereWorldInfo;
import top.qiguaiaaaa.geocraft.api.atmosphere.accessor.IAtmosphereAccessor;
import top.qiguaiaaaa.geocraft.api.atmosphere.gen.IAtmosphereDataProvider;
import top.qiguaiaaaa.geocraft.api.atmosphere.layer.AtmosphereLayer;
import top.qiguaiaaaa.geocraft.api.atmosphere.storage.AtmosphereData;
import top.qiguaiaaaa.geocraft.api.event.EventFactory;
import top.qiguaiaaaa.geocraft.geography.atmosphere.SurfaceAtmosphere;
import top.qiguaiaaaa.geocraft.geography.atmosphere.accessor.SurfaceAtmosphereAccessor;
import top.qiguaiaaaa.geocraft.util.BaseUtil;
import top.qiguaiaaaa.geocraft.util.ChunkUtil;
import top.qiguaiaaaa.geocraft.util.WaterUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Iterator;

import static top.qiguaiaaaa.geocraft.api.util.AtmosphereUtil.Constants.WATER_MELT_LATENT_HEAT_PER_QUANTA;

/**
 * 主世界大气系统
 */
public class SurfaceAtmosphereSystem extends QiguaiAtmosphereSystem {
    public SurfaceAtmosphereSystem(WorldServer world, AtmosphereWorldInfo worldInfo, IAtmosphereDataProvider dataProvider){
        super(world,worldInfo, dataProvider);
    }

    @Override
    public void updateTick(){
        if(stopped) return;
        updateAtmospheres();
        Iterator<Chunk> persistentChunkIterator = world.getPersistentChunkIterable(world.getPlayerChunkMap().getChunkIterator());
        while (persistentChunkIterator.hasNext()){
            Chunk chunk = persistentChunkIterator.next();
            updateBlocks(chunk);
        }

        dataProvider.tick();
    }

    @Override
    public IAtmosphereAccessor getAccessor(@Nonnull AtmosphereData data, @Nonnull BlockPos pos, boolean notAir) {
        return new SurfaceAtmosphereAccessor(this,data,pos,notAir);
    }

    /**
     * 处理下雨等事件
     */
    protected void updateBlocks(Chunk chunk){
        AtmosphereData data = dataProvider.getLoadedAtmosphereData(chunk.x,chunk.z);
        if(data == null) return;
        Atmosphere atmosphere = data.getAtmosphere();
        if(atmosphere ==null) return;
        if(data.getChunk() == null){
            this.onChunkLoaded(chunk); //若大气处于无区块加载状态,先处理大气
        }

        if(!atmosphere.isLoaded()) return;
        int x = chunk.x * 16;
        int z = chunk.z * 16;
        int rand = world.rand.nextInt();
        BlockPos randPos = world.getPrecipitationHeight(new BlockPos(x + (rand & 15), 0, z + (rand >> 8 & 15)));
        BlockPos pos = randPos.down();

        boolean isRaining = world.isRaining();

        if (!world.isAreaLoaded(pos, 1)) return;

        IAtmosphereAccessor freezeAccessor = getAccessor(data,pos,true);

        double rainPossibility = isRaining?WaterUtil.getRainPossibility(atmosphere,randPos):0;
        double freezePossibility = WaterUtil.getFreezePossibility(freezeAccessor);

        if (BaseUtil.getRandomResult(world.rand,freezePossibility) && WaterUtil.canWaterFreeze(world,pos,true)) {
            world.setBlockState(pos, Blocks.ICE.getDefaultState());
            freezeAccessor.putHeatToUnderlying(WATER_MELT_LATENT_HEAT_PER_QUANTA*8);
        }

        if(!BaseUtil.getRandomResult(world.rand,rainPossibility)){
            return;
        }

        IBlockState newState = EventFactory.onAtmosphereRainAndSnow(chunk,atmosphere,randPos,rainPossibility);
        if(newState != null){
            world.setBlockState(randPos,newState);
        }

        world.getBlockState(pos).getBlock().fillWithRain(world, pos);
    }

    @Nullable
    @Override
    protected Atmosphere generateAtmosphere(@Nullable Chunk chunk, @Nonnull AtmosphereData data) {
        if(data.isEmpty() && chunk == null) return null;
        SurfaceAtmosphere atmosphere = new SurfaceAtmosphere();
        atmosphere.setLocation(data.pos.x,data.pos.z);
        boolean isFirstGenerated = false;
        if(!data.isEmpty()){
            atmosphere.deserializeNBT(data.getSaveCompound());
        }else isFirstGenerated = true;
        if(chunk == null){
            atmosphere.onLoadWithoutChunk(worldInfo); //data isn't empty
        }else{
            atmosphere.onLoad(chunk,worldInfo); //data may be empty
            if(isFirstGenerated) populateAtmosphere(atmosphere,chunk);
        }
        return atmosphere;
    }

    protected void populateAtmosphere(SurfaceAtmosphere atmosphere,Chunk chunk){
        AtmosphereLayer layer = atmosphere.getBottomAtmosphereLayer();
        if(layer == null) return;
        Biome mainBiome = ChunkUtil.getMainBiome(chunk);
        layer.addSteam(null,(int) mainBiome.getRainfall()*4000);
        layer.addWater(null,(int) mainBiome.getRainfall()*1000);
    }
}
