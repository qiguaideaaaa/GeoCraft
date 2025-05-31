package top.qiguaiaaaa.fluidgeography.atmosphere;

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
import top.qiguaiaaaa.fluidgeography.api.FGAtmosphereProperties;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.AtmosphereWorldInfo;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.Underlying;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.property.*;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.state.*;
import top.qiguaiaaaa.fluidgeography.api.event.EventFactory;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.listener.IAtmosphereListener;
import top.qiguaiaaaa.fluidgeography.api.util.AtmosphereUtil;
import top.qiguaiaaaa.fluidgeography.api.util.ChunkUtil;
import top.qiguaiaaaa.fluidgeography.api.configs.AtmosphereConfig;
import top.qiguaiaaaa.fluidgeography.api.util.math.ExtendedChunkPos;

import java.util.*;

public class LowerAtmosphere implements INBTSerializable<NBTTagCompound>, Atmosphere {
    @CapabilityInject(LowerAtmosphere.class)
    public static Capability<LowerAtmosphere> LOWER_ATMOSPHERE;
    protected AtmosphereWorldInfo worldInfo = null;
    protected long tickTimes = 0;
    protected final WaterState water = FGAtmosphereProperties.WATER.getStateInstance();
    protected final TemperatureState temperature = FGAtmosphereProperties.TEMPERATURE.getStateInstance();
    protected Underlying underlying = new Underlying(Underlying.DEFAULT_HEAT_CAPACITY,0,0.9);
    protected long heatCapacity = underlying.heatCapacity;
    protected final Set<IAtmosphereState> states = new HashSet<>(Arrays.asList(water,temperature));
    protected final Set<IAtmosphereListener> listeners = new HashSet<>();
    protected final Map<EnumFacing,Triple<LowerAtmosphere,Chunk,EnumFacing>> neighbors = new EnumMap<>(EnumFacing.class);
    protected final Map<EnumFacing,Vec3d> winds = new EnumMap<>(EnumFacing.class);

    public LowerAtmosphere(){
        for(EnumFacing facing:ChunkUtil.HORIZONTALS){
            winds.put(facing,Vec3d.ZERO);
        }
    }

    public void updateTick(Chunk chunk){
        tickTimes++;
        ExtendedChunkPos chunkPos = new ExtendedChunkPos(chunk.x,chunk.z);
        neighbors.clear();;
        WorldServer world = worldInfo.getWorld();

        for(EnumFacing facing: ChunkUtil.HORIZONTALS){
            ExtendedChunkPos facingPos = chunkPos.offset(facing);
            if(!world.isAreaLoaded(facingPos.getBlock(8,64,8),1)) continue;
            Chunk neighborChunk = world.getChunk(facingPos.x,facingPos.z);
            if(!neighborChunk.isLoaded()) continue;
            LowerAtmosphere neighborAtmosphere = neighborChunk.getCapability(LowerAtmosphere.LOWER_ATMOSPHERE,null);
            if(neighborAtmosphere == null) continue;
            Triple<LowerAtmosphere,Chunk,EnumFacing> triple = new ImmutableTriple<>(neighborAtmosphere,neighborChunk,facing);
            neighbors.put(facing,triple);
        }
        // **** 计算风速 ****
        for(Triple<LowerAtmosphere,Chunk,EnumFacing> neighbor:neighbors.values()){
            EnumFacing direction = neighbor.getRight();
            Vec3d newWindSpeed = getSingleDirectionWindSpeed(neighbor.getLeft(),direction);
            winds.put(direction,newWindSpeed);
        }
        // **** 更新属性 ****
        if(!neighbors.isEmpty()){ //主动扩散
            for(Triple<LowerAtmosphere,Chunk,EnumFacing> neighbor:neighbors.values()){
                flowProperties(chunk,neighbor);
            }
        }
        //计算下垫面性质
        if(!worldInfo.isWorldClosed() &&
                tickTimes % AtmosphereConfig.ATMOSPHERE_UNDERLYING_RECALCULATE_GAP.getValue().value == 0 ){
            underlying = ChunkUtil.getUnderlying(chunk,underlying.getAverageHeight());
            heatCapacity = AtmosphereUtil.getAtmosphereHeatVolume(underlying);
        }
        //更新状态
        for(IAtmosphereState state:states){
            state.onUpdate(this,chunk);
        }
        //更新Listener
        this.updateListeners();
        //Post Event
        EventFactory.afterAtmosphereUpdate(chunk,this);
    }
    @Override
    public void initialise(Chunk chunk, AtmosphereWorldInfo info){
        this.setAtmosphereWorldInfo(info);
        underlying = ChunkUtil.getUnderlying(chunk,underlying.getAverageHeight());

        underlying.calculateAverageHeight(chunk,info);

        heatCapacity = AtmosphereUtil.getAtmosphereHeatVolume(underlying);

        initProperties(chunk);
        if(isInitialised()) return;
        resetTemperature(chunk);
    }
    protected void updateListeners(){
        for(IAtmosphereListener listener:listeners) listener.notifyListener(this);
    }
    protected void flowProperties(Chunk chunk, Triple<LowerAtmosphere,Chunk,EnumFacing> neighbor){
        for(AtmosphereProperty property: AtmospherePropertyManager.getFlowableProperties()){
            property.onAtmosphereFlow(this,chunk,neighbor.getLeft(),neighbor.getMiddle(),neighbor.getRight(),winds.get(neighbor.getRight()));
        }
    }
    protected void initProperties(Chunk chunk){
        for(AtmosphereProperty property: AtmospherePropertyManager.getProperties()){
            property.onAtmosphereInitialise(this,chunk);
        }
    }
    protected Vec3d getSingleDirectionWindSpeed(LowerAtmosphere to, EnumFacing dir){
        Vec3d wind = Vec3d.ZERO;
        for(AtmosphereProperty property: AtmospherePropertyManager.getWindEffectedProperties()){
            wind = wind.add(property.getWind(this,to,dir));
        }
        return wind;
    }

    //************
    // Override
    //************

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        for(IAtmosphereState state:states){
            compound.setTag(state.getNBTTagKey(),state.serializeNBT());
        }
        return compound;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        for(IAtmosphereState state:states){
            state.deserializeNBT(nbt.getTag(state.getNBTTagKey()));
        }
    }
    //******************
    // Getter And Setter
    //******************
    @Override
    public boolean addWaterAmount(int addAmount){
        return water.addAmount(addAmount);
    }
    @Override
    public void addTemperature(double temp){
        temperature.addTemperature(temp);
    }
    @Override
    public void addHeatQuantity(double Q){
        temperature.addHeatQuantity(Q,heatCapacity);
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
    public void setWaterAmount(int waterAmount) {
        water.setAmount(waterAmount);
    }
    @Override
    public void setTemperature(float temperature) {
        this.temperature.setTemperature(temperature);
    }

    @Override
    public void setAtmosphereWorldInfo(AtmosphereWorldInfo worldInfo) {
        if(worldInfo == null) return;
        this.worldInfo = worldInfo;
    }
    @Override
    public void resetTemperature(Chunk chunk){
        underlying.calculateAverageHeight(chunk,worldInfo);
        temperature.recalculate(chunk,underlying);
    }
    @Override
    public Vec3d getWindSpeed(EnumFacing direction){
        return winds.get(direction);
    }
    @Override
    public Vec3d getWindSpeed(BlockPos pos){
        int x = pos.getX() & 15;
        int z = pos.getZ() & 15;
        double weightS = z/16.0,
                weightN = 1-weightS,
                weightE = x/16.0,
                weightW = 1-weightE;
        return winds.get(EnumFacing.SOUTH).scale(weightS)
                .add(winds.get(EnumFacing.NORTH).scale(weightN))
                .add(winds.get(EnumFacing.EAST).scale(weightE))
                .add(winds.get(EnumFacing.WEST).scale(weightW));
    }
    @Override
    public AtmosphereWorldInfo getAtmosphereWorldInfo() {
        return worldInfo;
    }
    @Override
    public long getTickTimes() {
        return tickTimes;
    }
    @Override
    public long getHeatCapacity() {
        return heatCapacity;
    }
    @Override
    public double getRainStrong(){
        return getWaterAmount()/5000.0;
    }
    @Override
    public Underlying getUnderlying() {
        return underlying.copy();
    }
    @Override
    public int getWaterAmount() {
        return water.getAmount();
    }
    @Override
    public float getTemperature() {
        return temperature.getTemperature();
    }
    @Override
    public float getTemperature(BlockPos pos){
        if(pos.getY()<=underlying.getAverageHeight()) return getTemperature();
        double diff = pos.getY()-underlying.getAverageHeight();
        float noise = (float)(TEMPERATURE_NOISE.getValue((float)pos.getX() / 8.0f, (float)pos.getZ() / 8.0f) * 4.0d);
        return (float) Math.max(getTemperature() - (noise+diff)*0.05,3);
    }
    @Override
    public float getTemperatureBase(Chunk chunk) {
        return TemperatureState.calculateBaseTemperature(chunk,underlying);
    }

    @Override
    public Set<IAtmosphereListener> getListeners() {
        return new HashSet<>(listeners);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends IAtmosphereState> T getState(AtmosphereProperty property){
        for(IAtmosphereState state:states){
            if(state.getProperty().equals(property)){
                return (T) state;
            }
        }
        return null;
    }
    @Override
    public IAtmosphereState createState(AtmosphereProperty property){
        IAtmosphereState state = getState(property);
        if(state != null) return state;
        states.add(state = property.getStateInstance());
        return state;
    }
    @Override
    public boolean isInitialised(){
        return this.temperature.getTemperature() >= 0 && worldInfo != null;
    }
}
