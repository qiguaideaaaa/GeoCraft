package top.qiguaiaaaa.fluidgeography.atmosphere;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.INBTSerializable;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import top.qiguaiaaaa.fluidgeography.api.FGInfo;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.AtmosphereSystemManager;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.BaseAtmosphere;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.layer.AtmosphereLayer;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.layer.Layer;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.listener.IAtmosphereListener;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.property.TemperatureProperty;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.raypack.HeatPack;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.state.FluidState;
import top.qiguaiaaaa.fluidgeography.api.event.EventFactory;
import top.qiguaiaaaa.fluidgeography.api.util.AtmosphereUtil;
import top.qiguaiaaaa.fluidgeography.api.util.ChunkUtil;
import top.qiguaiaaaa.fluidgeography.api.util.math.Altitude;
import top.qiguaiaaaa.fluidgeography.api.util.math.Degree;
import top.qiguaiaaaa.fluidgeography.api.util.math.ExtendedChunkPos;
import top.qiguaiaaaa.fluidgeography.atmosphere.debug.DebugHeatPack;
import top.qiguaiaaaa.fluidgeography.atmosphere.layer.*;
import top.qiguaiaaaa.fluidgeography.atmosphere.state.DefaultTemperatureState;

import javax.annotation.Nullable;
import java.util.*;

import static top.qiguaiaaaa.fluidgeography.api.util.AtmosphereUtil.getSunEnergyPerChunk;

public class DefaultAtmosphere extends BaseAtmosphere implements INBTSerializable<NBTTagCompound> {
    @CapabilityInject(DefaultAtmosphere.class)
    public static Capability<DefaultAtmosphere> DEFAULT_ATMOSPHERE;
    protected boolean debug = false;
    //下垫面
    protected Underlying underlying = new Underlying(this);
    //** 大气层 **//
    protected GroundAtmosphereLayer groundAtmosphereLayer = new GroundAtmosphereLayer(this);
    protected MiddleAtmosphereLayer middleAtmosphereLayer = new MiddleAtmosphereLayer(this);
    protected HighAtmosphereLayer highAtmosphereLayer = new HighAtmosphereLayer(this);
    protected final Map<AtmosphereLayer,Vec3d> downWinds = new HashMap<>();

    public DefaultAtmosphere(){
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

    public void updateTick(Chunk chunk){
        tickTimes++;
        if(debug) FGInfo.getLogger().info("{} {} Atmosphere updated {}",chunk.x,chunk.z,tickTimes);

        //太阳辐射从太空射入
        if(!worldInfo.isWorldClosed()){
            HeatPack pack = debug?new DebugHeatPack(HeatPack.HeatType.SHORT_WAVE,getSunEnergyPerChunk(worldInfo.getWorld().getWorldInfo())):
            new HeatPack(HeatPack.HeatType.SHORT_WAVE,getSunEnergyPerChunk(worldInfo.getWorld().getWorldInfo()));
            getTopLayer().sendHeat(
                    pack, AtmosphereUtil.calculateSunDirection(AtmosphereUtil.getSunHeight(worldInfo.getWorld().getWorldInfo()), Degree.ZERO));
        }
        //处理相邻大气
        ExtendedChunkPos chunkPos = new ExtendedChunkPos(chunk.x,chunk.z);
        final Map<EnumFacing, Triple<Atmosphere,Chunk,EnumFacing>> neighbors = new EnumMap<>(EnumFacing.class);
        final WorldServer world = worldInfo.getWorld();
        for(EnumFacing facing: ChunkUtil.HORIZONTALS){
            ExtendedChunkPos facingPos = chunkPos.offset(facing);
            if(!world.isAreaLoaded(facingPos.getBlock(8,64,8),1)) continue;
            Chunk neighborChunk = world.getChunk(facingPos.x,facingPos.z);
            Atmosphere neighborAtmosphere = AtmosphereSystemManager.getAtmosphere(neighborChunk);
            if(neighborAtmosphere == null) continue;
            Triple<Atmosphere,Chunk,EnumFacing> triple = new ImmutableTriple<>(neighborAtmosphere,neighborChunk,facing);
            neighbors.put(facing,triple);
        }
        //从下往上依次更新
        for(Layer layer:layers){
            if(debug) FGInfo.getLogger().info("{} {} Atmosphere is updating layer {} ,i = {}",chunk.x,chunk.z,layer.getTagName(),layers.indexOf(layer));
            layer.tick(chunk,neighbors);
        }
        if(debug) FGInfo.getLogger().info("{} {} Atmosphere updated successfully, now status:\n {}",chunk.x,chunk.z,this.toString());
        //更新Listener
        this.updateListeners();
        //Post Event
        EventFactory.afterAtmosphereUpdate(chunk,this);
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

    protected void updateListeners(){
        for(IAtmosphereListener listener:listeners) listener.notifyListener(this);
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

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean isDebug() {
        return debug;
    }

    @Override
    public boolean addSteam(int addAmount, BlockPos pos){
        return getAtmosphereLayer(pos).addSteam(pos,addAmount);
    }

    @Override
    public boolean addWater(int amount, BlockPos pos) {
        return getAtmosphereLayer(pos).addWater(pos,amount);
    }

    @Override
    public int drainWater(int amount, BlockPos pos, boolean test) {
        if(amount<0) return 0;
        int realAmount = 0;
        for(Layer layer = getAtmosphereLayer(pos);layer != null;layer=layer.getUpperLayer()){
            if(!(layer instanceof AtmosphereLayer)) continue;
            FluidState state = layer.getWater();
            if(state == null) continue;
            int realAmountLayer = Math.min(amount,state.getAmount());
            amount -= realAmountLayer;
            realAmount += realAmountLayer;
            if(!test) state.addAmount(-realAmountLayer);
            if(amount <=0) break;
        }
        return realAmount;
    }

    @Override
    public void putHeat(double Q, BlockPos pos){
        getAtmosphereLayer(pos).putHeat(Q,pos);
    }

    @Override
    public Layer getLayer(BlockPos pos) {
        Layer res = null;
        for (Layer layer : layers) {
            if (pos.getY() < layer.getBeginY()) break;
            res = layer;
        }
        return res;
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

    public AtmosphereLayer getAtmosphereLayer(BlockPos pos){
        Layer res = getLayer(pos);
        while (!(res instanceof AtmosphereLayer)){
            if(res == null) return getBottomAtmosphereLayer();
            res = res.getUpperLayer();
        }
        return (AtmosphereLayer) res;
    }

    @Override
    public double getRainStrong(){
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
    public Vec3d getWind(BlockPos pos){
        AtmosphereLayer layer = getAtmosphereLayer(pos);
        if(layer == null) return Vec3d.ZERO;
        return layer.getWind(pos);
    }

    @Override
    public float getTemperature(BlockPos pos, boolean notAir){
        if(notAir) return underlying.getTemperature(pos);
        AtmosphereLayer layer = getAtmosphereLayer(pos);
        if(layer == null) return TemperatureProperty.UNAVAILABLE;
        return layer.getTemperature(pos, false);
    }

    @Override
    public double getPressure(BlockPos pos) {
        AtmosphereLayer layer = getAtmosphereLayer(pos);
        if(layer == null) return 0;
        return layer.getPressure(pos);
    }

    @Override
    public double getWaterPressure(BlockPos pos) {
        AtmosphereLayer layer = getAtmosphereLayer(pos);
        if(layer == null) return 0;
        return layer.getWaterPressure(pos);
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
            if(layer instanceof QiguaiAtmosphereLayer){
                QiguaiAtmosphereLayer qiguai = (QiguaiAtmosphereLayer) layer;
                builder.append("Density ").append(qiguai.getDensity()).append(" kg/m^3\n")
                        .append("Pressure ").append(qiguai.getPressure()).append(" Pa\n");
                for(EnumFacing facing:EnumFacing.values()){
                    builder.append("[").append(facing.name()).append("] ").append(((QiguaiAtmosphereLayer) layer).getWind(facing)).append("\n");
                }
            }
        }
        return builder.toString();
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        for(Layer layer:layers){
            compound.setTag(layer.getTagName(),layer.serializeNBT());
        }
        return compound;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        for(Layer layer:layers){
            NBTBase base = nbt.getTag(layer.getTagName());
            if(!(base instanceof NBTTagCompound)) throw new IllegalArgumentException("NBT of Atmosphere Layer "+layer.getTagName()+" isn't a valid compound tag!");
            layer.deserializeNBT((NBTTagCompound) base);
        }
    }
}
