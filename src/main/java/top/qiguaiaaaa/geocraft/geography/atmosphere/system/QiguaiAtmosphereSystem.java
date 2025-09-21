package top.qiguaiaaaa.geocraft.geography.atmosphere.system;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import top.qiguaiaaaa.geocraft.GeoCraft;
import top.qiguaiaaaa.geocraft.api.atmosphere.AtmosphereWorldInfo;
import top.qiguaiaaaa.geocraft.api.atmosphere.accessor.AverageAtmosphereAccessor;
import top.qiguaiaaaa.geocraft.api.atmosphere.accessor.IAtmosphereAccessor;
import top.qiguaiaaaa.geocraft.api.atmosphere.gen.IAtmosphereDataProvider;
import top.qiguaiaaaa.geocraft.api.atmosphere.storage.AtmosphereData;
import top.qiguaiaaaa.geocraft.api.atmosphere.system.BaseAtmosphereSystem;
import top.qiguaiaaaa.geocraft.api.setting.GeoAtmosphereSetting;
import top.qiguaiaaaa.geocraft.geography.atmosphere.QiguaiAtmosphere;

import javax.annotation.Nonnull;
import java.util.Collection;

public abstract class QiguaiAtmosphereSystem extends BaseAtmosphereSystem {
    protected final WorldServer world;
    public QiguaiAtmosphereSystem(WorldServer server, AtmosphereWorldInfo info, IAtmosphereDataProvider provider) {
        super(info, provider);
        this.world = server;
        worldInfo.setSystem(this);
    }

    @Override
    public void updateTick() {
        if(stopped) return;
        updateAtmospheres();
        dataProvider.tick();
    }

    @Override
    public IAtmosphereAccessor getAccessor(@Nonnull BlockPos pos, boolean notAir) {
        AtmosphereData data = dataProvider.getLoadedAtmosphereData(pos.getX()>>4,pos.getZ()>>4);
        if(data == null) return null;
        if(data.getAtmosphere() == null) return null;
        if(!data.getAtmosphere().isLoaded()) return null;
        return getAccessor(data,pos,notAir);
    }

    public IAtmosphereAccessor getAccessor(@Nonnull AtmosphereData data,@Nonnull BlockPos pos, boolean notAir){
        return new AverageAtmosphereAccessor(this,data,pos,notAir);
    }

    protected void updateAtmospheres(){
        Collection<AtmosphereData> dataList = dataProvider.getLoadedAtmosphereDataCollection();
        for (AtmosphereData data:dataList) {
            if(world.getWorldTime()%60 != Math.abs(data.pos.x+data.pos.z)%60) continue;
            QiguaiAtmosphere atmosphere = (QiguaiAtmosphere) data.getAtmosphere();
            if(atmosphere == null){
                if(data.isEmpty() && !data.isUnloadQueued()){
                    dataProvider.queueUnloadAtmosphereData(data.pos.x,data.pos.z);
                    continue;
                }
                data.setAtmosphere(generateAtmosphere(data.getChunk(),data));
                continue;
            }

            try{
                if(data.getChunk() == null){
                    if(!atmosphere.isLoaded()) continue;
                    atmosphere.updateTick(null);
                }else{
                    if(!atmosphere.isLoaded()){
                        if(!data.isEmpty()) atmosphere.deserializeNBT(data.getSaveCompound());
                        atmosphere.onLoad(data.getChunk(), worldInfo);
                    }
                    if(atmosphere.isLoaded()){
                        atmosphere.updateTick(data.getChunk());
                        if(atmosphere.tickTime()%4 == 3){
                            data.saveAtmosphere();
                        }
                    }
                }
            }catch (Throwable e){
                GeoCraft.getLogger().error("AtmosphereSystem {} meet an error while updating atmosphere at ChunkPos({},{}) which started at BlockPos({},{}).",world.provider.getDimension()
                        ,data.pos.x,data.pos.z,data.pos.getXStart(),data.pos.getZStart());
                if(GeoAtmosphereSetting.isEnableDetailedLogging()){
                    GeoCraft.getLogger().error("Atmosphere detailed:{}",atmosphere);
                    GeoCraft.getLogger().error("Error:",e);
                }
            }

        }
    }
}
