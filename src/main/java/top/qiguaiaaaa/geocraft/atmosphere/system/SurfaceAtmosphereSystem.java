package top.qiguaiaaaa.geocraft.atmosphere.system;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import top.qiguaiaaaa.geocraft.GeoCraft;
import top.qiguaiaaaa.geocraft.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.geocraft.api.atmosphere.AtmosphereWorldInfo;
import top.qiguaiaaaa.geocraft.api.atmosphere.accessor.IAtmosphereAccessor;
import top.qiguaiaaaa.geocraft.api.atmosphere.gen.IAtmosphereDataProvider;
import top.qiguaiaaaa.geocraft.api.atmosphere.layer.AtmosphereLayer;
import top.qiguaiaaaa.geocraft.api.atmosphere.storage.AtmosphereData;
import top.qiguaiaaaa.geocraft.api.atmosphere.system.BaseAtmosphereSystem;
import top.qiguaiaaaa.geocraft.api.event.EventFactory;
import top.qiguaiaaaa.geocraft.api.setting.GeoAtmosphereSetting;
import top.qiguaiaaaa.geocraft.atmosphere.SurfaceAtmosphere;
import top.qiguaiaaaa.geocraft.util.BaseUtil;
import top.qiguaiaaaa.geocraft.util.ChunkUtil;
import top.qiguaiaaaa.geocraft.util.WaterUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Iterator;

import static top.qiguaiaaaa.geocraft.api.util.AtmosphereUtil.FinalFactors.WATER_MELT_LATENT_HEAT_PER_QUANTA;

/**
 * 主世界大气系统
 */
public class SurfaceAtmosphereSystem extends QiguaiAtmosphereSystem {
    public SurfaceAtmosphereSystem(WorldServer world, AtmosphereWorldInfo worldInfo, IAtmosphereDataProvider dataProvider, IAtmosphereAccessor accessor){
        super(world,worldInfo, dataProvider,accessor);
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

        if(!atmosphere.isInitialised()) return;
        int x = chunk.x * 16;
        int z = chunk.z * 16;
        int rand = world.rand.nextInt();
        BlockPos randPos = world.getPrecipitationHeight(new BlockPos(x + (rand & 15), 0, z + (rand >> 8 & 15)));
        BlockPos pos = randPos.down();

        boolean isRaining = world.isRaining();

        if (!world.isAreaLoaded(pos, 1)) return;

        double rainPossibility = isRaining?WaterUtil.getRainPossibility(atmosphere,randPos):0;
        double freezePossibility = WaterUtil.getFreezePossibility(atmosphere,randPos);

        if (BaseUtil.getRandomResult(world.rand,freezePossibility) && WaterUtil.canWaterFreeze(world,pos,true)) {
            world.setBlockState(pos, Blocks.ICE.getDefaultState());
            atmosphere.getUnderlying().putHeat(WATER_MELT_LATENT_HEAT_PER_QUANTA*8,pos);
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
