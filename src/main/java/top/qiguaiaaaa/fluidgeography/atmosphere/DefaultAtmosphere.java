package top.qiguaiaaaa.fluidgeography.atmosphere;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.INBTSerializable;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.AtmosphereWorldInfo;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.Underlying;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.property.*;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.state.*;
import top.qiguaiaaaa.fluidgeography.api.event.EventFactory;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.listener.IAtmosphereListener;
import top.qiguaiaaaa.fluidgeography.api.util.ChunkUtil;
import top.qiguaiaaaa.fluidgeography.api.util.math.Altitude;

import java.util.*;

public class DefaultAtmosphere implements INBTSerializable<NBTTagCompound>, Atmosphere {
    @CapabilityInject(DefaultAtmosphere.class)
    public static Capability<DefaultAtmosphere> LOWER_ATMOSPHERE;
    protected AtmosphereWorldInfo worldInfo = null;
    protected long tickTimes = 0;
    protected final AtmosphereStates states = new AtmosphereStates(this,new Underlying(Underlying.默认热容,0,0.9));
    protected final Set<IAtmosphereListener> listeners = new HashSet<>();

    public DefaultAtmosphere(){
        for(EnumFacing facing:ChunkUtil.HORIZONTALS){
            states.getWinds().put(facing,Vec3d.ZERO);
        }
    }

    public void updateTick(Chunk chunk){
        tickTimes++;
        this.worldInfo.getModel().run(this, states,chunk);
        //更新Listener
        this.updateListeners();
        //Post Event
        EventFactory.afterAtmosphereUpdate(chunk,this);
    }
    @Override
    public void initialise(Chunk chunk, AtmosphereWorldInfo info){
        this.setAtmosphereWorldInfo(info);
        this.states.set下垫面(ChunkUtil.getUnderlying(chunk, states.get下垫面().get地面平均海拔()));

        this.states.get下垫面().更新平均海拔(chunk,info);

        states.update低层大气热容();

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
        return states.serializeNBT();
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        states.deserializeNBT(nbt);
    }
    //******************
    // Getter And Setter
    //******************
    @Override
    public boolean add水量(int addAmount){
        return states.getWaterState().addAmount(addAmount);
    }
    @Override
    public void add低层大气温度(double temp){
        states.get低层大气温度().add(temp);
    }

    @Override
    public void add地表温度(double temp) {
        states.get地表温度().add(temp);
    }

    @Override
    public void add低层大气热量(double Q){
        states.get低层大气温度().add热量(Q,states.get低层大气热容());
    }

    @Override
    public void add地表热量(double Q) {
        states.get地表温度().add热量(Q,states.get下垫面().热容);
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
    public void set水量(int waterAmount) {
        states.getWaterState().setAmount(waterAmount);
    }
    @Override
    public void set低层大气温度(float temperature) {
        this.states.get低层大气温度().set(temperature);
    }

    @Override
    public void set地表温度(float temperature) {
        this.states.get地表温度().set(temperature);
    }

    @Override
    public void setAtmosphereWorldInfo(AtmosphereWorldInfo worldInfo) {
        if(worldInfo == null) return;
        this.worldInfo = worldInfo;
    }
    @Override
    public void 重置温度(Chunk chunk){
        states.get下垫面().更新平均海拔(chunk,worldInfo);
        states.get低层大气温度().set(worldInfo.getModel().getInitTemperature(this,chunk));
        states.get地表温度().set(states.get低层大气温度());
    }
    @Override
    public Vec3d getWindSpeed(EnumFacing direction){
        return states.getWindSpeed(direction);
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
    public long get低层大气热容() {
        return states.get低层大气热容();
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
    public Underlying get下垫面() {
        return states.get下垫面().copy();
    }
    @Override
    public int get水量() {
        return states.getWaterState().getAmount();
    }
    @Override
    public float get低层大气温度() {
        return states.get低层大气温度().get();
    }
    @Override
    public float get温度(BlockPos pos,boolean isAir){
        if(pos.getY()<= states.get下垫面().get地面平均海拔().get()){
            if(isAir) return get低层大气温度();
            else return get地表温度();
        }
        float temp = worldInfo.getModel().getTemperature(this,new Altitude(pos.getY()));
        float noise = (float)(TEMPERATURE_NOISE.getValue((float)pos.getX() / 8.0f, (float)pos.getZ() / 8.0f) * 4.0d);
        return (float) Math.max(temp - noise*0.05,3);
    }

    @Override
    public float get地表温度() {
        return states.get地表温度().get();
    }

    @Override
    public Set<IAtmosphereListener> getListeners() {
        return new HashSet<>(listeners);
    }

    @Override
    public AtmosphereStates getStates() {
        return states;
    }

    @Override
    public boolean isInitialised(){
        return this.states.get低层大气温度().get() >= 0 && worldInfo != null;
    }
}
