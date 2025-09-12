package top.qiguaiaaaa.geocraft.api.atmosphere.accessor;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import top.qiguaiaaaa.geocraft.api.atmosphere.layer.AtmosphereLayer;
import top.qiguaiaaaa.geocraft.api.atmosphere.layer.Layer;
import top.qiguaiaaaa.geocraft.api.atmosphere.layer.UnderlyingLayer;
import top.qiguaiaaaa.geocraft.api.atmosphere.raypack.HeatPack;
import top.qiguaiaaaa.geocraft.api.atmosphere.storage.AtmosphereData;
import top.qiguaiaaaa.geocraft.api.atmosphere.system.IAtmosphereSystem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 一个最简单的{@link IAtmosphereAccessor}实现，所有数据均没有经过平滑处理
 */
public class DirectAtmosphereAccessor extends AbstractAtmosphereAccessor {

    public DirectAtmosphereAccessor(@Nonnull IAtmosphereSystem system, @Nonnull AtmosphereData data, @Nonnull BlockPos pos, boolean notAir) {
        super(system, data, pos, notAir);
    }

    @Override
    public double getTemperature() {
        checkAtmosphereDataLoaded();
        return data.getAtmosphere().getTemperature(pos,notAir);
    }

    @Override
    public double getPressure() {
        checkAtmosphereDataLoaded();
        return data.getAtmosphere().getPressure(pos);
    }

    @Override
    public double getWaterPressure() {
        checkAtmosphereDataLoaded();
        return data.getAtmosphere().getWaterPressure(pos);
    }

    @Nonnull
    @Override
    public Vec3d getWind() {
        checkAtmosphereDataLoaded();
        return data.getAtmosphere().getWind(pos);
    }

    @Override
    public void putHeatToAtmosphere(double amount) {
        checkAtmosphereDataLoaded();
        data.getAtmosphere().putHeat(amount,pos);
    }

    @Override
    public void putHeatToUnderlying(double amount) {
        checkAtmosphereDataLoaded();
        data.getAtmosphere().getUnderlying().putHeat(amount,pos);
    }

    @Override
    public void putHeatToCurrentLayer(double amount) {
        checkAtmosphereDataLoaded();
        Layer layer = data.getAtmosphere().getLayer(pos);
        if(layer == null) return;
        layer.putHeat(amount,pos);
    }

    @Override
    public double drawHeatFromAtmosphere(double amount) {
        checkAtmosphereDataLoaded();
        Layer layer = data.getAtmosphere().getLayer(pos);
        if(layer == null) return 0;
        if(layer instanceof AtmosphereLayer){
            return layer.drawHeat(amount,pos);
        }
        data.getAtmosphere().putHeat(-amount,pos);
        return amount;
    }

    @Override
    public double drawHeatFromUnderlying(double amount) {
        checkAtmosphereDataLoaded();
        UnderlyingLayer underlying = data.getAtmosphere().getUnderlying();
        return underlying.drawHeat(amount,pos);
    }

    @Override
    public double drawHeatFromCurrentLayer(double amount) {
        checkAtmosphereDataLoaded();
        Layer layer = data.getAtmosphere().getLayer(pos);
        if(layer == null) return 0;
        return layer.drawHeat(amount,pos);
    }

    @Override
    public void sendHeat(@Nonnull HeatPack pack,@Nullable EnumFacing direction) {
        checkAtmosphereDataLoaded();
        Layer layer = data.getAtmosphere().getLayer(pos);
        if(layer == null) return;
        layer.sendHeat(pack,direction);
    }

    @Override
    public void sendHeat(@Nonnull HeatPack pack,@Nullable Vec3d directionVec) {
        checkAtmosphereDataLoaded();
        Layer layer = data.getAtmosphere().getLayer(pos);
        if(layer == null) return;
        layer.sendHeat(pack,directionVec);
    }

    @Override
    public void sendHeat(@Nonnull HeatPack pack,@Nullable Vec3i directionVec) {
        checkAtmosphereDataLoaded();
        Layer layer = data.getAtmosphere().getLayer(pos);
        if(layer == null) return;
        layer.sendHeat(pack,directionVec);
    }
}
