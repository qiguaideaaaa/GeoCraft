package top.qiguaiaaaa.fluidgeography.api.atmosphere;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.util.INBTSerializable;
import top.qiguaiaaaa.fluidgeography.api.FGAtmosphereProperties;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.property.AtmosphereProperty;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.property.GasProperty;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.property.TemperatureProperty;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.state.GasState;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.state.IAtmosphereState;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.state.TemperatureState;
import top.qiguaiaaaa.fluidgeography.api.util.AtmosphereUtil;
import top.qiguaiaaaa.fluidgeography.api.util.ChunkUtil;

import javax.annotation.Nullable;
import java.util.*;

public class AtmosphereStates implements INBTSerializable<NBTTagCompound> {
    protected final Atmosphere atmosphere;
    protected final Map<EnumFacing, Vec3d> winds = new EnumMap<>(EnumFacing.class);
    protected final GasState water = FGAtmosphereProperties.WATER.getStateInstance();
    protected final TemperatureState 低层大气温度 = FGAtmosphereProperties.LOWER_ATMOSPHERE_TEMPERATURE.getStateInstance();
    protected final TemperatureState 地表温度 = FGAtmosphereProperties.GROUND_TEMPERATURE.getStateInstance();
    protected Underlying 下垫面;
    protected long 低层大气热容;
    protected final Map<AtmosphereProperty,IAtmosphereState> states = new HashMap<>();

    public AtmosphereStates(Atmosphere atmosphere,Underlying 下垫面){
        this.atmosphere = atmosphere;
        this.下垫面 = 下垫面;
        低层大气热容 =AtmosphereUtil.get低层大气热容(下垫面.get地面平均海拔().get物理海拔());
        for(EnumFacing facing: ChunkUtil.HORIZONTALS){
            winds.put(facing,Vec3d.ZERO);
        }
        states.put(FGAtmosphereProperties.WATER,water);
        states.put(FGAtmosphereProperties.GROUND_TEMPERATURE,地表温度);
        states.put(FGAtmosphereProperties.LOWER_ATMOSPHERE_TEMPERATURE,低层大气温度);
    }
    public Underlying get下垫面() {
        return 下垫面;
    }

    public void set下垫面(Underlying 下垫面) {
        this.下垫面 = 下垫面;
    }

    public long get低层大气热容() {
        return 低层大气热容+ (long) (water.getAmount()*4.2);
    }
    public void update低层大气热容(){
        低层大气热容 = AtmosphereUtil.get低层大气热容(下垫面.get地面平均海拔().get物理海拔());
    }

    public TemperatureState get低层大气温度() {
        return 低层大气温度;
    }
    public TemperatureState get地表温度(){
        return 地表温度;
    }
    public GasState getWaterState(){
        return water;
    }

    public Map<EnumFacing, Vec3d> getWinds() {
        return winds;
    }
    public Vec3d getWindSpeed(EnumFacing direction){
        return winds.get(direction);
    }
    @Nullable
    public GasState getGasState(GasProperty property){
        final IAtmosphereState state = states.get(property);
        if(state instanceof GasState) return (GasState) state;
        return null;
    }
    @Nullable
    public TemperatureState getTemperatureState(TemperatureProperty property){
        final IAtmosphereState state = states.get(property);
        if(state instanceof TemperatureState) return (TemperatureState) state;
        return null;
    }
    @Nullable
    public IAtmosphereState getState(AtmosphereProperty property){
        return states.get(property);
    }

    /**
     * 添加大气状态
     * @param property 大气属性
     * @return 如果存在旧状态,则返回.否则返回Null
     */
    public IAtmosphereState addState(AtmosphereProperty property){
        IAtmosphereState oldState = getState(property);
        IAtmosphereState newState = property.getStateInstance();
        states.put(property,newState);
        newState.initialise(atmosphere);
        return oldState;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        NBTTagCompound compound = new NBTTagCompound();
        for(IAtmosphereState state:states.values()){
            compound.setTag(state.getNBTTagKey(),state.serializeNBT());
        }
        return compound;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        for(IAtmosphereState state:states.values()){
            state.deserializeNBT(nbt.getTag(state.getNBTTagKey()));
        }
    }
}
