package top.qiguaiaaaa.geocraft.geography.atmosphere.layer.hall;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.chunk.Chunk;
import org.apache.commons.lang3.tuple.Triple;
import top.qiguaiaaaa.geocraft.GeoCraft;
import top.qiguaiaaaa.geocraft.api.GeoCraftProperties;
import top.qiguaiaaaa.geocraft.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.geocraft.api.atmosphere.raypack.HeatPack;
import top.qiguaiaaaa.geocraft.api.state.TemperatureState;
import top.qiguaiaaaa.geocraft.geography.atmosphere.QiguaiAtmosphere;
import top.qiguaiaaaa.geocraft.geography.atmosphere.layer.QiguaiAtmosphereLayer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Random;

public class ClosedAtmosphereLayer extends QiguaiAtmosphereLayer {
    protected static final Random random = new Random();
    protected TemperatureState temperature = GeoCraftProperties.FINAL_TEMPERATURE.getStateInstance();
    public ClosedAtmosphereLayer(QiguaiAtmosphere atmosphere) {
        super(atmosphere);
        this.heatCapacity = 1e10;
        states.put(GeoCraftProperties.FINAL_TEMPERATURE,temperature);
    }

    @Override
    public Vec3d 计算水平风速分量(Atmosphere to, EnumFacing dir) {
        return new Vec3d(dir.getDirectionVec()).scale(random.nextDouble()*4-2).add(super.计算水平风速分量(to,dir));
    }

    @Override
    protected void 对流() {}

    @Override
    protected double[] 对外长波辐射() {
        return new double[]{0,0};
    }

    @Override
    public double getPressure(@Nonnull BlockPos pos) {
        return 2e5;
    }

    @Override
    public float getTemperature(@Nonnull BlockPos pos, boolean notAir) {
        return temperature.get();
    }

    @Override
    public void tick(@Nullable Chunk chunk, @Nonnull Map<EnumFacing, Triple<Atmosphere, Chunk, EnumFacing>> neighbors, int x, int z) {
        for(Triple<Atmosphere,Chunk,EnumFacing> neighbor:neighbors.values()){
            EnumFacing direction = neighbor.getRight();
            Vec3d newWindSpeed = 计算水平风速分量(neighbor.getLeft(),direction);
            if(((QiguaiAtmosphere)atmosphere).isDebug()) GeoCraft.getLogger().info("{} calculated wind {} as {} ",getTagName(),direction.name(),newWindSpeed);
            winds.put(direction,newWindSpeed);
        }
        for(Triple<Atmosphere,Chunk,EnumFacing> neighbor:neighbors.values()){
            大气平流(chunk,neighbor);
        }
    }

    @Override
    public void sendHeat(@Nonnull HeatPack pack, @Nullable Vec3d direction) {}

    @Override
    public double getBeginY() {
        return lowerLayer!=null?lowerLayer.getTopY():63;
    }

    @Override
    public double getDepth() {
        return 256;
    }

    @Override
    public TemperatureState getTemperature() {
        return temperature;
    }

    @Override
    public String getTagName() {
        return "ca";
    }

    @Override
    public void putHeat(double quanta, @Nullable BlockPos pos) {}

    @Override
    public void sendHeat(@Nonnull HeatPack pack, @Nullable EnumFacing direction) {}

    @Override
    public void sendHeat(@Nonnull HeatPack pack, @Nullable Vec3i direction) {}

    @Override
    public double drawHeat(double quanta, @Nullable BlockPos pos) {
        return quanta;
    }
}
