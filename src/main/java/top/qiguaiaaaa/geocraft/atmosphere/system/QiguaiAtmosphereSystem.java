package top.qiguaiaaaa.geocraft.atmosphere.system;

import net.minecraft.world.WorldServer;
import top.qiguaiaaaa.geocraft.GeoCraft;
import top.qiguaiaaaa.geocraft.api.atmosphere.AtmosphereWorldInfo;
import top.qiguaiaaaa.geocraft.api.atmosphere.accessor.IAtmosphereAccessor;
import top.qiguaiaaaa.geocraft.api.atmosphere.gen.IAtmosphereDataProvider;
import top.qiguaiaaaa.geocraft.api.atmosphere.storage.AtmosphereData;
import top.qiguaiaaaa.geocraft.api.atmosphere.system.BaseAtmosphereSystem;
import top.qiguaiaaaa.geocraft.api.setting.GeoAtmosphereSetting;
import top.qiguaiaaaa.geocraft.atmosphere.QiguaiAtmosphere;

import java.util.Collection;

public abstract class QiguaiAtmosphereSystem extends BaseAtmosphereSystem {
    protected final WorldServer world;
    public QiguaiAtmosphereSystem(WorldServer server, AtmosphereWorldInfo info, IAtmosphereDataProvider provider, IAtmosphereAccessor accessor) {
        super(info, provider,accessor);
        this.world = server;
        worldInfo.setSystem(this);
    }

    @Override
    public void updateTick() {
        if(stopped) return;
        updateAtmospheres();
        dataProvider.tick();
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
                    if(!atmosphere.isInitialised()) continue;
                    atmosphere.updateTick(null);
                }else{
                    if(!atmosphere.isInitialised()){
                        if(!data.isEmpty()) atmosphere.deserializeNBT(data.getSaveCompound());
                        atmosphere.onLoad(data.getChunk(), worldInfo);
                    }
                    if(atmosphere.isInitialised()){
                        atmosphere.updateTick(data.getChunk());
                        data.saveAtmosphere();
                        if(atmosphere.tickTime()%4 == 3){
                            dataProvider.saveAtmosphereData(data.pos.x,data.pos.z);
                        }
                    }
                }
            }catch (Throwable e){
                GeoCraft.getLogger().error("AtmosphereSystem {} meet an error while updating atmosphere at ChunkPos({},{}) which started at BlockPos({},{}).",world.provider.getDimension()
                        ,data.pos.x,data.pos.z,data.pos.getXStart(),data.pos.getZStart());
                if(GeoAtmosphereSetting.isEnableDetailedLogging()){
                    GeoCraft.getLogger().error("Atmosphere detailed:{}",atmosphere);
                    GeoCraft.getLogger().error(e);
                }
            }

        }
    }
}
