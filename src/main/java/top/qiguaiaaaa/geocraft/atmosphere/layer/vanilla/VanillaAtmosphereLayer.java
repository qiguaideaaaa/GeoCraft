package top.qiguaiaaaa.geocraft.atmosphere.layer.vanilla;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import org.apache.commons.lang3.tuple.Triple;
import top.qiguaiaaaa.geocraft.GeoCraft;
import top.qiguaiaaaa.geocraft.api.GeoCraftProperties;
import top.qiguaiaaaa.geocraft.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.geocraft.api.atmosphere.layer.BaseAtmosphereLayer;
import top.qiguaiaaaa.geocraft.api.atmosphere.raypack.HeatPack;
import top.qiguaiaaaa.geocraft.api.property.AtmosphereProperty;
import top.qiguaiaaaa.geocraft.api.state.FluidState;
import top.qiguaiaaaa.geocraft.api.state.GeographyState;
import top.qiguaiaaaa.geocraft.api.state.TemperatureState;
import top.qiguaiaaaa.geocraft.api.util.AtmosphereUtil;
import top.qiguaiaaaa.geocraft.api.util.math.Altitude;
import top.qiguaiaaaa.geocraft.atmosphere.QiguaiAtmosphere;
import top.qiguaiaaaa.geocraft.atmosphere.VanillaAtmosphere;
import top.qiguaiaaaa.geocraft.property.GeographyPropertyManager;
import top.qiguaiaaaa.geocraft.state.DefaultTemperatureState;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Random;

public class VanillaAtmosphereLayer extends BaseAtmosphereLayer {
    protected static final Random random = new Random();
    protected Vec3d wind;
    protected TemperatureState temperature = GeoCraftProperties.TEMPERATURE.getStateInstance();
    public VanillaAtmosphereLayer(VanillaAtmosphere atmosphere) {
        super(atmosphere);
        wind = Vec3d.ZERO;
        states.put(GeoCraftProperties.TEMPERATURE,temperature);
    }

    @Override
    public boolean addSteam(@Nullable BlockPos pos, int amount) {
        return true;
    }

    @Override
    public boolean addWater(@Nullable BlockPos pos, int amount) {
        return true;
    }

    @Override
    public double getPressure(BlockPos pos) {
       return AtmosphereUtil.FinalFactors.海平面气压 *
                Math.exp(
                        -AtmosphereUtil.FinalFactors.干空气摩尔质量 *
                                AtmosphereUtil.FinalFactors.重力加速度 *
                                Altitude.get物理海拔(pos.getY()) /
                                (AtmosphereUtil.FinalFactors.气体常数 * getTemperature(pos,false))
                );
    }

    @Override
    public double getWaterPressure(BlockPos pos) {
        return atmosphere.getWaterPressure(pos);
    }

    @Override
    public double getWaterPressure() {
        return getWaterPressure(new BlockPos(0,getBeginY(),0));
    }

    @Override
    public float getTemperature(BlockPos pos, boolean notAir) {
        if(atmosphere.getAtmosphereWorldInfo().getWorld().isBlockLoaded(pos)){
            Biome curBiome = atmosphere.getAtmosphereWorldInfo().getWorld().getBiome(pos);
            return DefaultTemperatureState.toRealTemperature(curBiome.getTemperature(pos));
        }
        return DefaultTemperatureState.toRealTemperature(((VanillaAtmosphere)atmosphere).getBiome().getTemperature(pos));
    }

    @Override
    public Vec3d getWind(BlockPos pos) {
        return wind;
    }

    @Nullable
    @Override
    public FluidState getSteam() {
        return null;
    }

    public Vec3d 计算水平风速分量(Atmosphere to, EnumFacing dir) {
        Vec3d wind = new Vec3d(dir.getDirectionVec()).scale(random.nextDouble()*4-2);
        for(AtmosphereProperty property: GeographyPropertyManager.getWindEffectedProperties()){
            wind= wind.add(property.getWind(this,to,dir));
        }
        return wind;
    }

    @Override
    public void onLoad(Chunk chunk) {
        onLoadWithoutChunk();
        temperature.set(DefaultTemperatureState.calculateBaseTemperature(chunk,atmosphere.getUnderlying()));
    }

    @Override
    public void onLoadWithoutChunk() {
        for(GeographyState state:states.values())
            if(!state.isInitialised())
                state.initialise(this);
    }

    @Override
    public void tick(@Nullable Chunk chunk, Map<EnumFacing, Triple<Atmosphere, Chunk, EnumFacing>> neighbors, int x, int z) {
        wind = Vec3d.ZERO;
        for(Triple<Atmosphere,Chunk,EnumFacing> neighbor:neighbors.values()){
            EnumFacing direction = neighbor.getRight();
            Vec3d newWindSpeed = 计算水平风速分量(neighbor.getLeft(),direction);
            if(((QiguaiAtmosphere)atmosphere).isDebug()) GeoCraft.getLogger().info("{} calculated wind {} as {} ",getTagName(),direction.name(),newWindSpeed);
            wind = wind.add(newWindSpeed);
        }
        for(Triple<Atmosphere,Chunk,EnumFacing> neighbor:neighbors.values()){
            for(AtmosphereProperty property: GeographyPropertyManager.getFlowableProperties()){
                property.onFlow(this,chunk,neighbor.getLeft(),neighbor.getMiddle(),neighbor.getRight(),wind);
            }
        }
    }

    @Override
    public void sendHeat(HeatPack pack, @Nullable Vec3d direction) {}

    @Override
    public double getBeginY() {
        return lowerLayer!=null?lowerLayer.getTopY():63;
    }

    @Override
    public double getDepth() {
        return 4096;
    }

    @Override
    public TemperatureState getTemperature() {
        return temperature;
    }

    @Nullable
    @Override
    public FluidState getWater() {
        return null;
    }

    @Override
    public double getHeatCapacity() {
        return 1e10;
    }

    @Override
    public String getTagName() {
        return "va";
    }

    @Override
    public boolean isSerializable() {
        return true;
    }

    @Override
    public void putHeat(double quanta, @Nullable BlockPos pos) {}

    @Override
    public double drawHeat(double quanta, @Nullable BlockPos pos) {
        return quanta;
    }
}
