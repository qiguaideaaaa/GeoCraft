package top.qiguaiaaaa.geocraft.geography.atmosphere.system;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import top.qiguaiaaaa.geocraft.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.geocraft.api.atmosphere.AtmosphereWorldInfo;
import top.qiguaiaaaa.geocraft.api.atmosphere.accessor.IAtmosphereAccessor;
import top.qiguaiaaaa.geocraft.api.atmosphere.gen.IAtmosphereDataProvider;
import top.qiguaiaaaa.geocraft.api.atmosphere.storage.AtmosphereData;
import top.qiguaiaaaa.geocraft.geography.atmosphere.HallAtmosphere;
import top.qiguaiaaaa.geocraft.api.atmosphere.accessor.DirectAtmosphereAccessor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class HallAtmosphereSystem extends QiguaiAtmosphereSystem {
    public static final float HALL_TEMP = 400;

    public HallAtmosphereSystem(WorldServer world, AtmosphereWorldInfo worldInfo, IAtmosphereDataProvider dataProvider){
        super(world,worldInfo, dataProvider);
    }

    @Override
    public IAtmosphereAccessor getAccessor(@Nonnull AtmosphereData data, @Nonnull BlockPos pos, boolean notAir) {
        return new DirectAtmosphereAccessor(this,data,pos,notAir);
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
