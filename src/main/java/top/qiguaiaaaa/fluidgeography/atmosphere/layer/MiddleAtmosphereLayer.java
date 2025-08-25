package top.qiguaiaaaa.fluidgeography.atmosphere.layer;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.Chunk;
import org.apache.commons.lang3.tuple.Triple;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.layer.UnderlyingLayer;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.property.AtmosphereProperty;
import top.qiguaiaaaa.fluidgeography.api.util.AtmosphereUtil;
import top.qiguaiaaaa.fluidgeography.api.util.math.Altitude;
import top.qiguaiaaaa.fluidgeography.atmosphere.AtmospherePropertyManager;
import top.qiguaiaaaa.fluidgeography.atmosphere.DefaultAtmosphere;

import java.util.Map;

import static top.qiguaiaaaa.fluidgeography.api.atmosphere.Atmosphere.TEMPERATURE_NOISE;
import static top.qiguaiaaaa.fluidgeography.atmosphere.layer.GroundAtmosphereLayer.温度过渡区间长度;
import static top.qiguaiaaaa.fluidgeography.atmosphere.layer.GroundAtmosphereLayer.温度过渡开始高度;

public class MiddleAtmosphereLayer extends QiguaiAtmosphereLayer {
    public static final int 相对起始高度 = GroundAtmosphereLayer.厚度,厚度=32,第二温度过渡开始高度=相对起始高度+38,第二温度过渡区间长度=8;
    public MiddleAtmosphereLayer(DefaultAtmosphere atmosphere) {
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
        if(相对海拔< 温度过渡开始高度+ 温度过渡区间长度 && lowerLayer != null){
            double 过渡区开始温度 = lowerLayer.getTemperature(new BlockPos(pos.getX(),地面海拔+ 温度过渡开始高度,pos.getZ()),false);
            double 过渡区结束温度 = temperature.get();
            temp = (过渡区结束温度-过渡区开始温度)/温度过渡区间长度*(相对海拔-温度过渡开始高度)+过渡区开始温度;
        }else if(相对海拔>第二温度过渡开始高度 && upperLayer != null){
            double 高度差 = Altitude.to物理高度(相对海拔);
            double 过渡区开始温度 = temperature.get() - AtmosphereUtil.FinalFactors.对流层温度直减率 * 高度差;
            double 过渡区结束温度 = upperLayer.getTemperature().get();
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
        return 厚度;
    }

    @Override
    public String getTagName() {
        return "ma";
    }

    @Override
    public Vec3d 计算上风速() {
        if(upperLayer == null) return Vec3d.ZERO;
        double 散度垂直运动贡献 = -散度()*Altitude.to物理高度(getDepth())*0.1*0.01;
        double 垂直对流运动贡献 = 计算垂直对流速度();
        return new Vec3d(0,散度垂直运动贡献+垂直对流运动贡献,0);
    }

    @Override
    public Vec3d 计算下风速() {
        if(lowerLayer == null || lowerLayer instanceof UnderlyingLayer) return Vec3d.ZERO;
        double 散度垂直运动贡献 = 散度()*Altitude.to物理高度(getDepth())*0.1*0.01;
        return new Vec3d(0,散度垂直运动贡献,0);
    }

    protected void 热量对流(double 垂直风速){
        double 传输量 = 平均密度 * AtmosphereUtil.FinalFactors.干空气比热容 * AtmosphereUtil.FinalFactors.大气单元底面积 * 垂直风速 *
                (temperature.get()-upperLayer.getTemperature().get()) *
                216;
        temperature.add热量(-传输量,heatCapacity);
        upperLayer.putHeat(传输量,null);
    }

    @Override
    protected void 对流() {
        if(upperLayer == null) return;
        double 实际垂直风速 = winds.get(EnumFacing.UP).add(((DefaultAtmosphere)atmosphere).get下风(upperLayer)).y;
        热量对流(实际垂直风速);
        for(AtmosphereProperty property: AtmospherePropertyManager.getFlowableProperties()){
            property.onConvect(this,upperLayer,实际垂直风速);
        }
    }

    @Override
    public void tick(Chunk chunk, Map<EnumFacing, Triple<Atmosphere, Chunk, EnumFacing>> neighbors) {
        super.tick(chunk, neighbors);
        ((DefaultAtmosphere)atmosphere).set下风(this);
    }
}
