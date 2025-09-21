package top.qiguaiaaaa.geocraft.api.atmosphere.system;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import top.qiguaiaaaa.geocraft.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.geocraft.api.atmosphere.AtmosphereWorldInfo;
import top.qiguaiaaaa.geocraft.api.atmosphere.gen.IAtmosphereDataProvider;
import top.qiguaiaaaa.geocraft.api.atmosphere.storage.AtmosphereData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

public abstract class BaseAtmosphereSystem implements IAtmosphereSystem{
    protected final AtmosphereWorldInfo worldInfo;
    protected final IAtmosphereDataProvider dataProvider;
    protected boolean stopped = false;

    public BaseAtmosphereSystem(AtmosphereWorldInfo info, IAtmosphereDataProvider provider) {
        this.worldInfo = info;
        this.dataProvider = provider;
    }

    @Override
    public void onChunkGenerated(@Nonnull Chunk chunk) {
        AtmosphereData data = dataProvider.provideAtmosphereData(chunk.x,chunk.z);
        data.setChunk(chunk);
        data.setAtmosphere(generateAtmosphere(chunk,data));
    }

    @Override
    public void onChunkLoaded(@Nonnull Chunk chunk) {
        AtmosphereData data = dataProvider.provideAtmosphereData(chunk.x,chunk.z);
        data.setChunk(chunk);
        if(data.getAtmosphere() == null){
            data.setAtmosphere(generateAtmosphere(chunk,data));
        }
    }

    @Override
    public void onChunkUnloaded(@Nonnull Chunk chunk) {
        AtmosphereData data = dataProvider.getLoadedAtmosphereData(chunk.x,chunk.z);
        if(data == null) return;
        data.saveAtmosphere();
        data.setChunk(null);
    }

    @Override
    public void onWorldSave() {
        dataProvider.saveAllAtmosphereData();
    }

    @Override
    public void onServerStopping(@Nonnull FMLServerStoppingEvent event) {
        Collection<AtmosphereData> dataList = dataProvider.getLoadedAtmosphereDataCollection();
        for(AtmosphereData data:dataList){
            if(data.getAtmosphere() == null) continue;
            Atmosphere atmosphere = data.getAtmosphere();
            if(atmosphere.isLoaded()){
                data.saveAtmosphere();
            }
        }
        onWorldSave();
    }

    @Override
    public boolean isAtmosphereLoaded(@Nonnull ChunkPos pos) {
        Atmosphere atmosphere =  getAtmosphere(pos.x, pos.z);
        return atmosphere != null && atmosphere.isLoaded();
    }

    @Override
    public void setStop(boolean status) {
        stopped = status;
    }

    @Override
    public boolean isStopped() {
        return stopped;
    }

    @Nonnull
    @Override
    public IAtmosphereDataProvider getDataProvider() {
        return dataProvider;
    }

    @Nullable
    @Override
    public Atmosphere getAtmosphere(@Nonnull BlockPos pos) {
        return getAtmosphere(pos.getX()>>4,pos.getZ()>>4);
    }

    @Nullable
    @Override
    public Atmosphere getAtmosphere(@Nonnull Chunk chunk) {
        return getAtmosphere(chunk.x,chunk.z);
    }

    @Override
    public Atmosphere getAtmosphere(int x,int z) {
        AtmosphereData data = dataProvider.getLoadedAtmosphereData(x,z);
        if(data == null) return null;
        Atmosphere atmosphere = data.getAtmosphere();
        if(atmosphere != null && atmosphere.isLoaded()) return atmosphere;
        return null;
    }

    @Nonnull
    @Override
    public AtmosphereWorldInfo getAtmosphereWorldInfo() {
        return worldInfo;
    }

    @Nullable
    protected abstract Atmosphere generateAtmosphere(@Nullable Chunk chunk,@Nonnull AtmosphereData data);
}
