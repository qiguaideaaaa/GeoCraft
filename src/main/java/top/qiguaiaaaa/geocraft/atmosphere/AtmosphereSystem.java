package top.qiguaiaaaa.geocraft.atmosphere;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import top.qiguaiaaaa.geocraft.api.GEOInfo;
import top.qiguaiaaaa.geocraft.api.atmosphere.AtmosphereWorldInfo;
import top.qiguaiaaaa.geocraft.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.geocraft.api.atmosphere.IAtmosphereSystem;
import top.qiguaiaaaa.geocraft.api.property.GeoAtmosphereProperty;
import top.qiguaiaaaa.geocraft.api.event.EventFactory;
import top.qiguaiaaaa.geocraft.util.BaseUtil;
import top.qiguaiaaaa.geocraft.util.WaterUtil;

import javax.annotation.Nullable;
import java.util.*;

import static top.qiguaiaaaa.geocraft.api.util.AtmosphereUtil.FinalFactors.WATER_MELT_LATENT_HEAT_PER_QUANTA;

public class AtmosphereSystem implements IAtmosphereSystem {
    protected final WorldServer world;
    protected final AtmosphereWorldInfo atmosphereWorldInfo;
    protected boolean stopped = false;
    protected AtmosphereSystem(WorldServer world, AtmosphereWorldInfo worldInfo){
        this.world = world;
        this.atmosphereWorldInfo = worldInfo;
    }
    @Override
    public void updateTick(){
        if(stopped) return;
        Collection<Chunk> loadedChunks = world.getChunkProvider().getLoadedChunks();
        for (Chunk chunk:loadedChunks) {
            if(world.getWorldTime()%60 != Math.abs(chunk.x+chunk.z)%60) continue;
            DefaultAtmosphere atmosphere = chunk.getCapability(DefaultAtmosphere.DEFAULT_ATMOSPHERE,null);
            if(atmosphere == null) continue;
            if(!atmosphere.isInitialised()) atmosphere.initialise(chunk,atmosphereWorldInfo);
            try{
                if(atmosphere.isInitialised())  atmosphere.updateTick(chunk);
            }catch (Exception e){
                GEOInfo.getLogger().error("AtmosphereSystem {} meet an error while updating atmosphere at ChunkPos({},{}) which started at BlockPos({},{}).",world.provider.getDimension()
                ,chunk.x,chunk.z,chunk.getPos().getXStart(),chunk.getPos().getZStart());
                if(GeoAtmosphereProperty.isEnableDetailedLogging()){
                    GEOInfo.getLogger().error("Atmosphere detailed:{}",atmosphere);
                    GEOInfo.getLogger().error(e);
                }
            }

        }
        Iterator<Chunk> persistentChunkIterator = world.getPersistentChunkIterable(world.getPlayerChunkMap().getChunkIterator());
        while (persistentChunkIterator.hasNext()){
            Chunk chunk = persistentChunkIterator.next();
            updateBlocks(chunk);
        }
    }

    @Override
    public void setStop(boolean status) {
        stopped = status;
    }

    @Override
    public boolean isStopped() {
        return stopped;
    }

    /**
     * 处理下雨等事件
     */
    protected void updateBlocks(Chunk chunk){
        DefaultAtmosphere atmosphere = chunk.getCapability(DefaultAtmosphere.DEFAULT_ATMOSPHERE,null);
        if(atmosphere == null || !atmosphere.isInitialised()) return;
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

    /**
     * 获取某一处的大气
     * @param pos 位置
     * @return 大气
     */
    @Nullable
    @Override
    public Atmosphere getAtmosphere(BlockPos pos){
        if(!world.isAreaLoaded(pos,1)) return null;
        Chunk chunk = world.getChunk(pos);
        return getAtmosphere(chunk);
    }

    @Nullable
    @Override
    public Atmosphere getAtmosphere(Chunk chunk) {
        if(!chunk.isLoaded()) return null;
        Atmosphere atmosphere = chunk.getCapability(DefaultAtmosphere.DEFAULT_ATMOSPHERE,null);
        if(atmosphere != null && atmosphere.isInitialised()) return atmosphere;
        return null;
    }

    @Override
    public AtmosphereWorldInfo getAtmosphereWorldInfo() {
        return atmosphereWorldInfo;
    }
}
