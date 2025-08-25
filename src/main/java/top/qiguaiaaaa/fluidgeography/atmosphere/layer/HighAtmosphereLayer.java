package top.qiguaiaaaa.fluidgeography.atmosphere.layer;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.Chunk;
import org.apache.commons.lang3.tuple.Triple;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.layer.UnderlyingLayer;
import top.qiguaiaaaa.fluidgeography.api.util.AtmosphereUtil;
import top.qiguaiaaaa.fluidgeography.api.util.math.Altitude;
import top.qiguaiaaaa.fluidgeography.atmosphere.DefaultAtmosphere;

import java.util.Map;

import static top.qiguaiaaaa.fluidgeography.api.atmosphere.Atmosphere.TEMPERATURE_NOISE;
import static top.qiguaiaaaa.fluidgeography.atmosphere.layer.MiddleAtmosphereLayer.第二温度过渡区间长度;
import static top.qiguaiaaaa.fluidgeography.atmosphere.layer.MiddleAtmosphereLayer.第二温度过渡开始高度;

public class HighAtmosphereLayer extends QiguaiAtmosphereLayer{
    public static final int 相对起始高度 = MiddleAtmosphereLayer.相对起始高度+MiddleAtmosphereLayer.厚度,顶端物理高度=16896;
    public HighAtmosphereLayer(DefaultAtmosphere atmosphere) {
        super(atmosphere);
        this.起始高度 = atmosphere.getUnderlying().getAltitude().get()+相对起始高度;
    }

    protected double 起始高度;

    @Override
    public void 更新缓存() {
        super.更新缓存();
        this.起始高度 = atmosphere.getUnderlying().getAltitude().get()+相对起始高度;
    }

    @Override
    public float getTemperature(BlockPos pos, boolean notAir) {
        double 地面海拔 = atmosphere.getUnderlying().getAltitude().get();
        double 相对海拔 = pos.getY()-地面海拔;
        if(相对海拔< 相对起始高度-0.1){
            if(lowerLayer == null) return temperature.get();
            return lowerLayer.getTemperature(pos,notAir);
        }
        if(相对海拔>getTopY()){
            if(upperLayer == null) return getTemperature(new BlockPos(pos.getX(),地面海拔+getDepth()-0.01,pos.getZ()),notAir);
            return upperLayer.getTemperature(pos,notAir);
        }
        double temp;
        if(相对海拔< 第二温度过渡开始高度+第二温度过渡区间长度 && lowerLayer != null){
            double 过渡区开始温度 = lowerLayer.getTemperature(new BlockPos(pos.getX(),地面海拔+ 第二温度过渡开始高度,pos.getZ()),false);
            double 过渡区结束温度 = temperature.get();
            temp = (过渡区结束温度-过渡区开始温度)/第二温度过渡区间长度*(相对海拔-第二温度过渡开始高度)+过渡区开始温度;
        } else {
            double 高度差 = Altitude.to物理高度(相对海拔-相对起始高度);
            temp = temperature.get() - AtmosphereUtil.FinalFactors.对流层温度直减率 * 高度差;
        }
        float noise = (float)(TEMPERATURE_NOISE.getValue((float)pos.getX() / 8.0f, (float)pos.getZ() / 8.0f) * 4.0d);
        return (float) Math.max(temp - noise*0.05,3);
    }

    @Override
    public double getBeginY() {
        return 起始高度;
    }

    @Override
    public double getDepth() {
        return Altitude.get游戏海拔(顶端物理高度)-起始高度;
    }

    @Override
    public String getTagName() {
        return "ha";
    }

    @Override
    public Vec3d 计算上风速() {
        return Vec3d.ZERO;
    }

    @Override
    public Vec3d 计算下风速() {
        if(lowerLayer == null || lowerLayer instanceof UnderlyingLayer) return Vec3d.ZERO;
        double 散度垂直运动贡献 = 散度()*Altitude.to物理高度(getDepth())*0.1*0.01;
        return new Vec3d(0,散度垂直运动贡献,0);
    }

    @Override
    protected void 对流() {}

    @Override
    public void tick(Chunk chunk, Map<EnumFacing, Triple<Atmosphere, Chunk, EnumFacing>> neighbors) {
        super.tick(chunk, neighbors);
        ((DefaultAtmosphere)atmosphere).set下风(this);
    }
}
