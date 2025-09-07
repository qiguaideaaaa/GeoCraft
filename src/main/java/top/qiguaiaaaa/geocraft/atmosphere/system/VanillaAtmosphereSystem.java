package top.qiguaiaaaa.geocraft.atmosphere.system;

import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import top.qiguaiaaaa.geocraft.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.geocraft.api.atmosphere.AtmosphereWorldInfo;
import top.qiguaiaaaa.geocraft.api.atmosphere.gen.IAtmosphereDataProvider;
import top.qiguaiaaaa.geocraft.api.atmosphere.storage.AtmosphereData;
import top.qiguaiaaaa.geocraft.atmosphere.VanillaAtmosphere;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class VanillaAtmosphereSystem extends QiguaiAtmosphereSystem{
    public VanillaAtmosphereSystem(WorldServer server, AtmosphereWorldInfo info, IAtmosphereDataProvider provider) {
        super(server, info, provider);
    }

    @Override
    public void onChunkUnloaded(Chunk chunk) {
        super.onChunkUnloaded(chunk);
        dataProvider.queueUnloadAtmosphereData(chunk.x,chunk.z);
    }

    @Nullable
    @Override
    protected Atmosphere generateAtmosphere(@Nullable Chunk chunk, @Nonnull AtmosphereData data) {
        if(data.isEmpty() && chunk == null) return null;
        VanillaAtmosphere atmosphere = new VanillaAtmosphere();
        atmosphere.setLocation(data.pos.x,data.pos.z);
        if(!data.isEmpty()){
            atmosphere.deserializeNBT(data.getSaveCompound());
        }
        if(chunk == null){
            atmosphere.onLoadWithoutChunk(worldInfo); //data isn't empty
        }else{
            atmosphere.onLoad(chunk,worldInfo); //data may be empty
        }
        return atmosphere;
    }
}
