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
import top.qiguaiaaaa.fluidgeography.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.AtmosphereSystemManager;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.AtmosphereWorldInfo;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.layer.AtmosphereLayer;
import top.qiguaiaaaa.fluidgeography.atmosphere.layer.*;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.listener.IAtmosphereListener;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.property.AtmosphereProperty;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.raypack.HeatPack;
import top.qiguaiaaaa.fluidgeography.api.event.EventFactory;
import top.qiguaiaaaa.fluidgeography.api.util.AtmosphereUtil;
import top.qiguaiaaaa.fluidgeography.api.util.ChunkUtil;
import top.qiguaiaaaa.fluidgeography.api.util.math.Degree;
import top.qiguaiaaaa.fluidgeography.api.util.math.ExtendedChunkPos;
import top.qiguaiaaaa.fluidgeography.atmosphere.state.DefaultTemperatureState;

import java.util.*;

import static top.qiguaiaaaa.fluidgeography.api.util.AtmosphereUtil.getSunEnergyPerChunk;

public class DefaultAtmosphere implements INBTSerializable<NBTTagCompound>, Atmosphere {
    @CapabilityInject(DefaultAtmosphere.class)
    public static Capability<DefaultAtmosphere> LOWER_ATMOSPHERE;
    protected AtmosphereWorldInfo worldInfo = null;
    protected long tickTimes = 0;
    //** 大气层 **//
    protected Underlying underlying = new Underlying(this);
    protected GroundAtmosphereLayer groundAtmosphereLayer = new GroundAtmosphereLayer(this);
    protected MiddleAtmosphereLayer middleAtmosphereLayer = new MiddleAtmosphereLayer(this);
    protected HighAtmosphereLayer highAtmosphereLayer = new HighAtmosphereLayer(this);
    protected final List<AtmosphereLayer> layers = new ArrayList<>();
    protected final Map<AtmosphereLayer,Vec3d> 下风 = new HashMap<>();
    protected final Set<IAtmosphereListener> listeners = new HashSet<>();

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

        //太阳辐射从太空射入
        if(!worldInfo.isWorldClosed()){
            getTopLayer().sendHeat(
                    new HeatPack(HeatPack.HeatType.SHORT_WAVE,getSunEnergyPerChunk(worldInfo.getWorld().getWorldInfo())),
                    AtmosphereUtil.calculateSunDirection(AtmosphereUtil.getSunHeight(worldInfo.getWorld().getWorldInfo()), Degree.ZERO));
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
        for(AtmosphereLayer layer:layers){
            layer.tick(chunk,neighbors);
        }
        //更新Listener
        this.updateListeners();
        //Post Event
        EventFactory.afterAtmosphereUpdate(chunk,this);
    }

    public void set下风(QiguaiAtmosphereLayer layer){
        下风.put(layer,layer.getWind(EnumFacing.DOWN));
    }

    public Vec3d get下风(AtmosphereLayer layer){
        Vec3d res = 下风.get(layer);
        return res == null?Vec3d.ZERO:res;
    }

    @Override
    public void initialise(Chunk chunk, AtmosphereWorldInfo info){
        this.setAtmosphereWorldInfo(info);
        for(AtmosphereLayer layer:layers){
            layer.initialise(chunk);
        }
        initProperties(chunk);
        if(isInitialised()) return;
        重置温度(chunk);
    }
    protected void updateListeners(){
        for(IAtmosphereListener listener:listeners) listener.notifyListener(this);
    }
    protected void initProperties(Chunk chunk){
        for(AtmosphereProperty property: AtmospherePropertyManager.getProperties()){
            property.onAtmosphereInitialise(this,chunk);
        }
    }

    //************
    // Override
    //************

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        for(AtmosphereLayer layer:layers){
            compound.setTag(layer.getTagName(),layer.serializeNBT());
        }
        return compound;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        for(AtmosphereLayer layer:layers){
            NBTBase base = nbt.getTag(layer.getTagName());
            if(!(base instanceof NBTTagCompound)) throw new IllegalArgumentException("NBT of Atmosphere Layer "+layer.getTagName()+" isn't a valid compound tag!");
            layer.deserializeNBT((NBTTagCompound) base);
        }
    }
    //******************
    // Getter And Setter
    //******************
    @Override
    public boolean addSteam(int addAmount, BlockPos pos){
        return getLayer(pos).addSteam(pos,addAmount);
    }

    @Override
    public boolean addWater(int amount, BlockPos pos) {
        return getAtmosphereLayer(pos).addWater(pos,amount);
    }

    @Override
    public void putHeat(double Q, BlockPos pos){
        getAtmosphereLayer(pos).putHeat(Q,pos);
    }

    @Override
    public void addListener(IAtmosphereListener listener){
        if(listener == null) return;
        listeners.add(listener);
    }
    @Override
    public void removeListener(IAtmosphereListener listener){
        listeners.remove(listener);
    }

    @Override
    public void setAtmosphereWorldInfo(AtmosphereWorldInfo worldInfo) {
        if(worldInfo == null) return;
        this.worldInfo = worldInfo;
    }
    /**
     * 将大气温度设置为初始值
     * @param chunk 大气所在区块
     */
    public void 重置温度(Chunk chunk){
        underlying.updateAltitude(chunk);
        groundAtmosphereLayer.getTemperature().set(DefaultTemperatureState.calculateBaseTemperature(chunk,underlying));
        underlying.getTemperature().set(groundAtmosphereLayer.getTemperature());
    }
    @Override
    public Vec3d getWind(BlockPos pos){
        return getLayer(pos).getWind(pos);
    }
    @Override
    public AtmosphereWorldInfo getAtmosphereWorldInfo() {
        return worldInfo;
    }
    @Override
    public long tickTime() {
        return tickTimes;
    }

    @Override
    public AtmosphereLayer getLayer(BlockPos pos) {
        AtmosphereLayer res = null;
        for (AtmosphereLayer layer : layers) {
            if (pos.getY() < layer.getBeginY()) break;
            res = layer;
        }
        if(res == null) return getBottomLayer();
        return res;
    }

    public AtmosphereLayer getAtmosphereLayer(BlockPos pos){
        AtmosphereLayer res = getLayer(pos);
        if(res == underlying) res = underlying.getUpperLayer();
        return res;
    }

    @Override
    public AtmosphereLayer getTopLayer() {
        return layers.get(layers.size()-1);
    }

    @Override
    public AtmosphereLayer getBottomLayer() {
        return layers.get(0);
    }

    @Override
    public double getRainStrong(){
        double 水汽量 = get水量(); // 单位: m³（换算成液态水体积）
        double 饱和值 = 3000.0; // 阈值, 超过才可能下雨，可根据温度调整

        if (水汽量 <= 饱和值) {
            return 0.0;
        }

        // 超出饱和部分
        double 超量 = 水汽量 - 饱和值;

        // 使用平滑公式: 强度 = max强度 * (1 - exp(-超量/比例因子))
        // 这样刚超饱和时雨不大，超量很多时趋近最大强度
        double 比例因子 = 5000.0; // 控制强度增长速度
        double 最大强度 = 100.0; // 对应极端暴雨

        double 强度 = 最大强度 * (1.0 - Math.exp(-超量 / 比例因子));

        return 强度;
    }
    @Override
    public Underlying getUnderlying() {
        return underlying;
    }
    @Override
    public int get水量() {
        return groundAtmosphereLayer.getWater().getAmount();
    }

    @Override
    public double getWaterPressure(BlockPos pos) {
        return getLayer(pos).getWaterPressure(pos);
    }
    /**
     * 获取大气指定位置的气压
     * @param pos 位置
     * @return 气压,单位Pa
     */
    @Override
    public double getPressure(BlockPos pos) {
        return getLayer(pos).getPressure(pos);
    }

    @Override
    public float getTemperature(BlockPos pos, boolean notAir){
        double 平均海拔 = underlying.getAltitude().get();
        if(notAir && pos.getY()-平均海拔<=10) return underlying.getTemperature().get();
        return getAtmosphereLayer(pos).getTemperature(pos,notAir);
    }

    @Override
    public Set<IAtmosphereListener> getListeners() {
        return new HashSet<>(listeners);
    }

    @Override
    public boolean isInitialised(){
        for(AtmosphereLayer layer:layers){
            if(!layer.isInitialise()) return false;
        }
        return true;
    }
}
