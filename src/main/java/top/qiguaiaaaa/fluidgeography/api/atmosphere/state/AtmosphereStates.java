package top.qiguaiaaaa.fluidgeography.api.atmosphere.state;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.util.INBTSerializable;
import top.qiguaiaaaa.fluidgeography.api.FGAtmosphereProperties;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.Underlying;
import top.qiguaiaaaa.fluidgeography.api.util.AtmosphereUtil;
import top.qiguaiaaaa.fluidgeography.api.util.ChunkUtil;

import java.util.*;

public class AtmosphereStates implements INBTSerializable<NBTTagCompound> {
    protected final Atmosphere atmosphere;
    protected final Map<EnumFacing, Vec3d> winds = new EnumMap<>(EnumFacing.class);
    protected final WaterState water = FGAtmosphereProperties.WATER.getStateInstance();
    protected final TemperatureState 低层大气温度 = FGAtmosphereProperties.TEMPERATURE.getStateInstance();
    protected final GroundTemperatureState 地表温度 = FGAtmosphereProperties.GROUND_TEMPERATURE.getStateInstance();
    protected Underlying 下垫面;
    protected long 低层大气热容;
    protected final Set<IAtmosphereState> states = new HashSet<>(Arrays.asList(water, 低层大气温度,地表温度));

    public AtmosphereStates(Atmosphere atmosphere,Underlying 下垫面){
        this.atmosphere = atmosphere;
        this.下垫面 = 下垫面;
        低层大气热容 =AtmosphereUtil.get低层大气热容(下垫面.get地面平均海拔().get物理海拔());
        for(EnumFacing facing: ChunkUtil.HORIZONTALS){
            winds.put(facing,Vec3d.ZERO);
        }
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
    public GroundTemperatureState get地表温度(){
        return 地表温度;
    }
    public WaterState getWaterState(){
        return water;
    }

    public Map<EnumFacing, Vec3d> getWinds() {
        return winds;
    }
    public Vec3d getWindSpeed(EnumFacing direction){
        return winds.get(direction);
    }

    @Deprecated
    public Set<IAtmosphereState> getStates() {
        return states;
    }

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
}
