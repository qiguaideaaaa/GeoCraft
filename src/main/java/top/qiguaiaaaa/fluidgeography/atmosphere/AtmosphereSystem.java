package top.qiguaiaaaa.fluidgeography.atmosphere;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.AtmosphereWorldInfo;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.IAtmosphereSystem;
import top.qiguaiaaaa.fluidgeography.api.event.EventFactory;
import top.qiguaiaaaa.fluidgeography.api.util.AtmosphereUtil;
import top.qiguaiaaaa.fluidgeography.util.BaseUtil;

import javax.annotation.Nullable;
import java.util.*;

import static top.qiguaiaaaa.fluidgeography.api.util.AtmosphereUtil.WATER_MELT_LATENT_HEAT_PER_QUANTA;

public class AtmosphereSystem implements IAtmosphereSystem {
    protected final WorldServer world;
    protected final AtmosphereWorldInfo atmosphereWorldInfo;
    protected AtmosphereSystem(WorldServer world, AtmosphereWorldInfo worldInfo){
        this.world = world;
        this.atmosphereWorldInfo = worldInfo;
    }
    @Override
    public void updateTick(){
        Collection<Chunk> loadedChunks = world.getChunkProvider().getLoadedChunks();
        for (Chunk chunk:loadedChunks) {
            if(world.getWorldTime()%60 != Math.abs(chunk.x+chunk.z)%60) continue;
            LowerAtmosphere atmosphere = chunk.getCapability(LowerAtmosphere.LOWER_ATMOSPHERE,null);
            if(atmosphere == null) continue;
            if(!atmosphere.isInitialised()) atmosphere.initialise(chunk,atmosphereWorldInfo);
            if(atmosphere.isInitialised())  atmosphere.updateTick(chunk);
        }
        Iterator<Chunk> persistentChunkIterator = world.getPersistentChunkIterable(world.getPlayerChunkMap().getChunkIterator());
        while (persistentChunkIterator.hasNext()){
            Chunk chunk = persistentChunkIterator.next();
            updateBlocks(chunk);
        }
    }
    protected void updateBlocks(Chunk chunk){
        LowerAtmosphere atmosphere = chunk.getCapability(LowerAtmosphere.LOWER_ATMOSPHERE,null);
        if(atmosphere == null || !atmosphere.isInitialised()) return;
        int x = chunk.x * 16;
        int z = chunk.z * 16;
        int rand = world.rand.nextInt();
        BlockPos randPos = world.getPrecipitationHeight(new BlockPos(x + (rand & 15), 0, z + (rand >> 8 & 15)));
        BlockPos pos = randPos.down();

        boolean isRaining = world.isRaining();

        if (!world.isAreaLoaded(pos, 1)) return;

        double rainPossibility = isRaining?AtmosphereUtil.getRainPossibility(atmosphere,randPos):0;
        float freezePossibility = AtmosphereUtil.getFreezePossibility(atmosphere,randPos);

        if (BaseUtil.getRandomResult(world.rand,freezePossibility) && AtmosphereUtil.canWaterFreeze(world,pos,true)) {
            world.setBlockState(pos, Blocks.ICE.getDefaultState());
            atmosphere.addHeatQuantity(WATER_MELT_LATENT_HEAT_PER_QUANTA*8);
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
    public Atmosphere getAtmosphere(BlockPos pos){
        if(!world.isAreaLoaded(pos,1)) return null;
        Chunk chunk = world.getChunk(pos);
        Atmosphere atmosphere = chunk.getCapability(LowerAtmosphere.LOWER_ATMOSPHERE,null);
        if(atmosphere != null && atmosphere.isInitialised()) return atmosphere;
        return null;
    }
    @Override
    public AtmosphereWorldInfo getAtmosphereWorldInfo() {
        return atmosphereWorldInfo;
    }
}
