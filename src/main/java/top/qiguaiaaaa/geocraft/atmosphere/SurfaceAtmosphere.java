package top.qiguaiaaaa.geocraft.atmosphere;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import top.qiguaiaaaa.geocraft.GeoCraft;
import top.qiguaiaaaa.geocraft.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.geocraft.api.atmosphere.layer.AtmosphereLayer;
import top.qiguaiaaaa.geocraft.api.atmosphere.layer.Layer;
import top.qiguaiaaaa.geocraft.api.atmosphere.raypack.HeatPack;
import top.qiguaiaaaa.geocraft.api.atmosphere.system.IAtmosphereSystem;
import top.qiguaiaaaa.geocraft.api.event.EventFactory;
import top.qiguaiaaaa.geocraft.api.property.TemperatureProperty;
import top.qiguaiaaaa.geocraft.api.state.FluidState;
import top.qiguaiaaaa.geocraft.api.util.AtmosphereUtil;
import top.qiguaiaaaa.geocraft.api.util.math.Altitude;
import top.qiguaiaaaa.geocraft.api.util.math.Degree;
import top.qiguaiaaaa.geocraft.api.util.math.ExtendedChunkPos;
import top.qiguaiaaaa.geocraft.atmosphere.debug.DebugHeatPack;
import top.qiguaiaaaa.geocraft.atmosphere.layer.*;
import top.qiguaiaaaa.geocraft.atmosphere.layer.surface.*;
import top.qiguaiaaaa.geocraft.state.DefaultTemperatureState;
import top.qiguaiaaaa.geocraft.util.ChunkUtil;

import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import static top.qiguaiaaaa.geocraft.api.util.AtmosphereUtil.getSunEnergyPerChunk;

public class SurfaceAtmosphere extends QiguaiAtmosphere {
    //下垫面
    protected Underlying underlying = new Underlying(this);
    //** 大气层 **//
    protected GroundAtmosphereLayer groundAtmosphereLayer = new GroundAtmosphereLayer(this);
    protected MiddleAtmosphereLayer middleAtmosphereLayer = new MiddleAtmosphereLayer(this);
    protected HighAtmosphereLayer highAtmosphereLayer = new HighAtmosphereLayer(this);
    protected final Map<AtmosphereLayer,Vec3d> downWinds = new HashMap<>();

    public SurfaceAtmosphere(){
        layers.add(underlying);
        layers.add(groundAtmosphereLayer);
        layers.add(middleAtmosphereLayer);
        layers.add(highAtmosphereLayer);
        underlying.setUpperLayer(groundAtmosphereLayer);
        groundAtmosphereLayer.setLowerLayer(underlying);
        groundAtmosphereLayer.setUpperLayer(middleAtmosphereLayer);
        middleAtmosphereLayer.setLowerLayer(groundAtmosphereLayer);
        middleAtmosphereLayer.setUpperLayer(highAtmosphereLayer);
        highAtmosphereLayer.setLowerLayer(middleAtmosphereLayer);
    }

    @Override
    public void updateTick(@Nullable Chunk chunk){
        tickTimes++;
        if(debug) GeoCraft.getLogger().info("{} {} Atmosphere updated {}",x,z,tickTimes);

        //太阳辐射从太空射入
        HeatPack pack = debug?new DebugHeatPack(HeatPack.HeatType.SHORT_WAVE,getSunEnergyPerChunk(worldInfo.getWorld().getWorldInfo())):
        new HeatPack(HeatPack.HeatType.SHORT_WAVE,getSunEnergyPerChunk(worldInfo.getWorld().getWorldInfo()));
        getTopLayer().sendHeat(
                pack, AtmosphereUtil.calculateSunDirection(AtmosphereUtil.getSunHeight(worldInfo.getWorld().getWorldInfo()), Degree.ZERO));
        //处理相邻大气
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

    /**
     * 将大气温度设置为初始值
     * @param chunk 大气所在区块
     */
    public void 重置温度(Chunk chunk){
        underlying.updateAltitude(chunk);
        groundAtmosphereLayer.getTemperature().set(DefaultTemperatureState.calculateBaseTemperature(chunk,underlying));
        underlying.getTemperature().set(groundAtmosphereLayer.getTemperature());
        middleAtmosphereLayer.getTemperature().set(
                (float) (groundAtmosphereLayer.getTemperature().get()-
                        Altitude.to物理高度(groundAtmosphereLayer.getDepth())* AtmosphereUtil.FinalFactors.对流层温度直减率)
        );
        highAtmosphereLayer.getTemperature().set((float) (
                groundAtmosphereLayer.getTemperature().get()-
                        Altitude.to物理高度(groundAtmosphereLayer.getDepth()+middleAtmosphereLayer.getDepth())* AtmosphereUtil.FinalFactors.对流层温度直减率)
        );
    }

    //******************
    // Getter And Setter
    //******************
    public void setDownWind(QiguaiAtmosphereLayer layer){
        downWinds.put(layer,layer.getWind(EnumFacing.DOWN));
    }

    public Vec3d getDownWind(AtmosphereLayer layer){
        Vec3d res = downWinds.get(layer);
        return res == null?Vec3d.ZERO:res;
    }

    @Override
    public Underlying getUnderlying() {
        return underlying;
    }

    @Nullable
    @Override
    public AtmosphereLayer getBottomAtmosphereLayer() {
        return groundAtmosphereLayer;
    }

    @Override
    public double getCloudExponent(){
        double 水汽量 = 0;
        for(Layer layer:layers){
            if(!(layer instanceof AtmosphereLayer)) continue;
            FluidState state = layer.getWater();
            if(state == null) continue;
            水汽量 += state.getAmount();
        }

        // 使用平滑公式: 强度 = max强度 * (1 - exp(-超量/比例因子))
        // 这样刚超饱和时雨不大，超量很多时趋近最大强度
        double 比例因子 = 5000.0; // 控制强度增长速度
        double 最大强度 = 100.0; // 对应极端暴雨

        return 最大强度 * (1.0 - Math.exp(-水汽量 / 比例因子));
    }

    //************
    // Override
    //************

    @Override
    public float getTemperature(BlockPos pos, boolean notAir){
        if(notAir) return underlying.getTemperature(pos);
        AtmosphereLayer layer = getAtmosphereLayer(pos);
        if(layer == null) return TemperatureProperty.UNAVAILABLE;
        return layer.getTemperature(pos, false);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("[Atmosphere Information]\n")
                .append("DEBUG = ").append(debug).append("\n")
                .append("Layer num = ").append(layers.size()).append("\n")
                .append("Ticks = ").append(tickTimes).append("\n")
                .append("World In = ").append(worldInfo.getWorld().provider.getDimension()).append("\n")
                .append("[Layer Infomation]\n");
        for(Layer layer:layers){
            builder.append(layer.getBeginY()).append(" --> ").append(layer.getBeginY()+layer.getDepth()).append("  : ").append(layer.getTagName()).append("\n")
                    .append("Temperature ").append(layer.getTemperature()).append(" K\n")
                    .append("Water ").append(layer.getWater()==null?"Not Found":layer.getWater()).append(" mB\n")
                    .append("Heat Capacity ").append(layer.getHeatCapacity()).append(" FE/(Layer * K)\n");
            if(layer instanceof AtmosphereLayer){
                AtmosphereLayer aLayer = (AtmosphereLayer) layer;
                builder.append("Steam ").append(aLayer.getSteam()==null?"Not Found":aLayer.getSteam()).append(" mB\n")
                        .append("Water Pressure ").append(aLayer.getWaterPressure()).append(" Pa\n");
            }
            if(layer instanceof SurfaceAtmosphereLayer){
                SurfaceAtmosphereLayer surfaceLayer = (SurfaceAtmosphereLayer) layer;
                builder.append("Density ").append(surfaceLayer.getDensity()).append(" kg/m^3\n")
                        .append("Pressure ").append(surfaceLayer.getPressure()).append(" Pa\n");
                for(EnumFacing facing:EnumFacing.values()){
                    builder.append("[").append(facing.name()).append("] ").append(((SurfaceAtmosphereLayer) layer).getWind(facing)).append("\n");
                }
            }
        }
        return builder.toString();
    }
}
