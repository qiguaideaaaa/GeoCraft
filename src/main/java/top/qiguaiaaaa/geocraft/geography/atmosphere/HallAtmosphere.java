package top.qiguaiaaaa.geocraft.geography.atmosphere;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import top.qiguaiaaaa.geocraft.GeoCraft;
import top.qiguaiaaaa.geocraft.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.geocraft.api.atmosphere.layer.AtmosphereLayer;
import top.qiguaiaaaa.geocraft.api.atmosphere.layer.Layer;
import top.qiguaiaaaa.geocraft.api.atmosphere.layer.UnderlyingLayer;
import top.qiguaiaaaa.geocraft.api.atmosphere.system.IAtmosphereSystem;
import top.qiguaiaaaa.geocraft.api.event.EventFactory;
import top.qiguaiaaaa.geocraft.api.util.math.ExtendedChunkPos;
import top.qiguaiaaaa.geocraft.geography.atmosphere.layer.hall.ClosedAtmosphereLayer;
import top.qiguaiaaaa.geocraft.geography.atmosphere.layer.hall.ConstantUnderlying;
import top.qiguaiaaaa.geocraft.util.ChunkUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;

public class HallAtmosphere extends QiguaiAtmosphere {
    protected float constTemperature;
    protected final ConstantUnderlying underlying = new ConstantUnderlying(this);
    protected final ClosedAtmosphereLayer atmosphereLayer = new ClosedAtmosphereLayer(this);
    public HallAtmosphere(float temp){
        this.constTemperature = temp;
        layers.add(underlying);
        layers.add(atmosphereLayer);
        underlying.getTemperature().set(constTemperature);
        atmosphereLayer.getTemperature().set(constTemperature);
        underlying.setUpperLayer(atmosphereLayer);
        atmosphereLayer.setLowerLayer(underlying);
    }

    @Override
    public float getTemperature(@Nonnull BlockPos pos, boolean notAir) {
        return constTemperature;
    }

    @Nullable
    @Override
    public AtmosphereLayer getBottomAtmosphereLayer() {
        return atmosphereLayer;
    }

    @Nonnull
    @Override
    public UnderlyingLayer getUnderlying() {
        return underlying;
    }

    @Override
    public double getCloudExponent() {
        return 0;
    }

    @Override
    public void updateTick(@Nullable Chunk chunk) {
        tickTimes++;
        if(debug) GeoCraft.getLogger().info("{} {} Atmosphere updated {}",x,z,tickTimes);
        ExtendedChunkPos chunkPos = new ExtendedChunkPos(x,z);
        final Map<EnumFacing, Triple<Atmosphere,Chunk,EnumFacing>> neighbors = new EnumMap<>(EnumFacing.class);
        final WorldServer world = worldInfo.getWorld();
        final IAtmosphereSystem system = worldInfo.getSystem();
        for(EnumFacing facing: ChunkUtil.HORIZONTALS){
            ExtendedChunkPos facingPos = chunkPos.offset(facing);
            if(!system.isAtmosphereLoaded(facingPos)) continue;
            Chunk neighborChunk = null;
            if(world.isAreaLoaded(facingPos.getBlock(8,64,8),1)){
                neighborChunk = world.getChunk(facingPos.x,facingPos.z);
            }
            Atmosphere neighborAtmosphere = system.getAtmosphere(facingPos.x,facingPos.z);
            if(neighborAtmosphere == null) continue;
            Triple<Atmosphere,Chunk,EnumFacing> triple = new ImmutableTriple<>(neighborAtmosphere,neighborChunk,facing);
            neighbors.put(facing,triple);
        }
        //从下往上依次更新
        for(Layer layer:layers){
            if(debug) GeoCraft.getLogger().info("{} {} Atmosphere is updating layer {} ,i = {}",x,z,layer.getTagName(),layers.indexOf(layer));
            layer.tick(chunk,neighbors,x,z);
        }
        if(debug) GeoCraft.getLogger().info("{} {} Atmosphere updated successfully, now status:\n {}",x,z,this.toString());
        //更新Listener
        this.updateListeners();
        //Post Event
        EventFactory.postAtmosphereUpdate(chunk,this,x,z);
    }
}
