package top.qiguaiaaaa.geocraft.geography.atmosphere;

import net.minecraft.init.Biomes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import top.qiguaiaaaa.geocraft.GeoCraft;
import top.qiguaiaaaa.geocraft.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.geocraft.api.atmosphere.AtmosphereWorldInfo;
import top.qiguaiaaaa.geocraft.api.atmosphere.layer.AtmosphereLayer;
import top.qiguaiaaaa.geocraft.api.atmosphere.layer.Layer;
import top.qiguaiaaaa.geocraft.api.atmosphere.layer.UnderlyingLayer;
import top.qiguaiaaaa.geocraft.api.atmosphere.system.IAtmosphereSystem;
import top.qiguaiaaaa.geocraft.api.event.EventFactory;
import top.qiguaiaaaa.geocraft.api.util.math.ExtendedChunkPos;
import top.qiguaiaaaa.geocraft.geography.atmosphere.layer.vanilla.VanillaAtmosphereLayer;
import top.qiguaiaaaa.geocraft.geography.atmosphere.layer.vanilla.VanillaUnderlying;
import top.qiguaiaaaa.geocraft.geography.state.DefaultTemperatureState;
import top.qiguaiaaaa.geocraft.util.ChunkUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;

public class VanillaAtmosphere extends QiguaiAtmosphere {
    protected Biome biome = Biomes.PLAINS;
    protected VanillaUnderlying underlying = new VanillaUnderlying(this);
    protected VanillaAtmosphereLayer atmosphereLayer = new VanillaAtmosphereLayer(this);
    public VanillaAtmosphere(){
        layers.add(underlying);
        layers.add(atmosphereLayer);
        underlying.setUpperLayer(atmosphereLayer);
        atmosphereLayer.setLowerLayer(underlying);
    }
    @Override
    public float getTemperature(@Nonnull BlockPos pos, boolean notAir) {
        Biome curBiome = biome;
        if(worldInfo.getWorld().isBlockLoaded(pos)){
            curBiome = worldInfo.getWorld().getBiome(pos);
        }
        return DefaultTemperatureState.toRealTemperature(curBiome.getTemperature(pos));
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
        if(worldInfo.getWorld().getWorldInfo().isThundering()) return 100;
        if(worldInfo.getWorld().getWorldInfo().isRaining()) return 50;
        return biome.getRainfall()*10;
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

    @Override
    public boolean addSteam(int addAmount, @Nonnull BlockPos pos) {
        return true;
    }

    @Override
    public boolean addWater(int amount, @Nonnull BlockPos pos) {
        return true;
    }

    @Override
    public int drainWater(int amount, @Nonnull BlockPos pos, boolean test) {
        if(amount<0) return 0;
        Biome curBiome = biome;
        if(worldInfo.getWorld().isBlockLoaded(pos)){
            curBiome = worldInfo.getWorld().getBiome(pos);
        }
        return (int) Math.min(amount,curBiome.getRainfall()*50000);
    }

    @Override
    public void putHeat(double Q, BlockPos pos) {}

    @Override
    public double getWaterPressure(@Nonnull BlockPos pos) {
        if(worldInfo.getWorld().isBlockLoaded(pos)){
            Biome curBiome = worldInfo.getWorld().getBiome(pos);
            return curBiome.getRainfall()*3000;
        }
        return biome.getRainfall()*3000;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = super.serializeNBT();
        compound.setInteger("Biome",Biome.getIdForBiome(biome));
        return compound;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        int biomeId = nbt.getInteger("Biome");
        if(biomeId>=0) biome = Biome.getBiomeForId(biomeId);
        if(biome == null) biome = Biomes.PLAINS;
        super.deserializeNBT(nbt);
    }

    public Biome getBiome(){
        return biome;
    }

    @Override
    public void onLoad(@Nonnull Chunk chunk, @Nonnull AtmosphereWorldInfo info) {
        biome = ChunkUtil.getMainBiome(chunk);
        super.onLoad(chunk, info);
    }
}
