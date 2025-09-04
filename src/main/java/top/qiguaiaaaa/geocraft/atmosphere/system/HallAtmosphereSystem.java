package top.qiguaiaaaa.geocraft.atmosphere.system;

import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import top.qiguaiaaaa.geocraft.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.geocraft.api.atmosphere.AtmosphereWorldInfo;
import top.qiguaiaaaa.geocraft.api.atmosphere.accessor.IAtmosphereAccessor;
import top.qiguaiaaaa.geocraft.api.atmosphere.gen.IAtmosphereDataProvider;
import top.qiguaiaaaa.geocraft.api.atmosphere.storage.AtmosphereData;
import top.qiguaiaaaa.geocraft.atmosphere.HallAtmosphere;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class HallAtmosphereSystem extends QiguaiAtmosphereSystem {
    public static final float HALL_TEMP = 400;

    public HallAtmosphereSystem(WorldServer world, AtmosphereWorldInfo worldInfo, IAtmosphereDataProvider dataProvider, IAtmosphereAccessor accessor){
        super(world,worldInfo, dataProvider,accessor);
    }

    @Nullable
    @Override
    protected Atmosphere generateAtmosphere(@Nullable Chunk chunk, @Nonnull AtmosphereData data) {
        if(data.isEmpty() && chunk == null) return null;
        HallAtmosphere atmosphere = new HallAtmosphere(HALL_TEMP);
        atmosphere.setLocation(data.pos.x,data.pos.z);
        if(!data.isEmpty()){
            atmosphere.deserializeNBT(data.getSaveCompound());
        }
        if(chunk == null){
            atmosphere.onLoadWithoutChunk(worldInfo);
        }else{
            atmosphere.onLoad(chunk,worldInfo);
        }
        return atmosphere;
    }
}
