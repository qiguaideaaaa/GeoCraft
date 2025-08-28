package top.qiguaiaaaa.geocraft.atmosphere.layer;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.Chunk;
import org.apache.commons.lang3.tuple.Triple;
import top.qiguaiaaaa.geocraft.api.GEOInfo;
import top.qiguaiaaaa.geocraft.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.geocraft.api.atmosphere.property.AtmosphereProperty;
import top.qiguaiaaaa.geocraft.api.property.GeoAtmosphereProperty;
import top.qiguaiaaaa.geocraft.api.util.AtmosphereUtil;
import top.qiguaiaaaa.geocraft.api.util.math.Altitude;
import top.qiguaiaaaa.geocraft.atmosphere.GeographyPropertyManager;
import top.qiguaiaaaa.geocraft.atmosphere.DefaultAtmosphere;

import java.util.Map;

import static top.qiguaiaaaa.geocraft.api.atmosphere.Atmosphere.TEMPERATURE_NOISE;
import static top.qiguaiaaaa.geocraft.atmosphere.layer.GroundAtmosphereLayer.温度过渡区间长度;
import static top.qiguaiaaaa.geocraft.atmosphere.layer.GroundAtmosphereLayer.温度过渡开始高度;

public class MiddleAtmosphereLayer extends QiguaiAtmosphereLayer {
    public static final int 相对起始高度 = GroundAtmosphereLayer.厚度,厚度=68,第二温度过渡开始高度=相对起始高度+62,第二温度过渡区间长度=12;
    public MiddleAtmosphereLayer(DefaultAtmosphere atmosphere) {
        super(atmosphere);
        this.起始高度 = atmosphere.getUnderlying().getAltitude().get()+相对起始高度;
    }

    protected double 起始高度;

    @Override
    public void 更新缓存() {
        this.起始高度 = atmosphere.getUnderlying().getAltitude().get()+相对起始高度;
        super.更新缓存();
    }

    @Override
    public float getTemperature(BlockPos pos, boolean notAir) {
        double 地面海拔 = atmosphere.getUnderlying().getAltitude().get();
        double 相对海拔 = pos.getY()-地面海拔;
        if(相对海拔< 相对起始高度-0.1){
            if(isLowerLayerValid) return low.getTemperature(pos,notAir);
            return temperature.get();
        }
        if(相对海拔>getTopY()){
            if(isUpperLayerValid) return up.getTemperature(pos,notAir);
            return getTemperature(new BlockPos(pos.getX(),地面海拔+getDepth()-0.01,pos.getZ()),notAir);
        }
        double temp;
        if(相对海拔< 温度过渡开始高度+ 温度过渡区间长度 && isLowerLayerValid){
            double 过渡区开始温度 = low.getTemperature(new BlockPos(pos.getX(),地面海拔+ 温度过渡开始高度,pos.getZ()),false);
            double 过渡区结束温度 = temperature.get();
            temp = (过渡区结束温度-过渡区开始温度)/温度过渡区间长度*(相对海拔-温度过渡开始高度)+过渡区开始温度;
        }else if(相对海拔>第二温度过渡开始高度 && isUpperLayerValid){
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
        if(!isUpperLayerValid) return Vec3d.ZERO;
        double 散度垂直运动贡献 = -散度()*Altitude.to物理高度(getDepth())*0.1*0.001;
        double 垂直对流运动贡献 = 计算垂直对流速度();
        return new Vec3d(0,散度垂直运动贡献+垂直对流运动贡献,0);
    }

    @Override
    public Vec3d 计算下风速() {
        if(!isLowerLayerValid) return Vec3d.ZERO;
        double 散度垂直运动贡献 = 散度()*Altitude.to物理高度(getDepth())*0.1*0.001;
        return new Vec3d(0,散度垂直运动贡献,0);
    }

    protected void 热量对流(double 垂直风速){
        double upTemp = upperLayer.getTemperature(new BlockPos(0,upperLayer.getBeginY()+upperLayer.getDepth()/2,0));
        double diff = 中心温度-upTemp;
        double 传输量 = Math.min(heatCapacity,upperLayer.getHeatCapacity()) *
                MathHelper.clamp(
                    MathHelper.clamp(垂直风速*216/Altitude.to物理高度(厚度),-1.0/12,1.0/12)*
                    diff/(upperLayer.getDepth()+厚度/2.0)
                    *Math.min(平均密度/1.2,1),
                -Math.abs(diff/3),Math.abs(diff/3));
        if(((DefaultAtmosphere)atmosphere).isDebug())
            GEOInfo.getLogger().info("{} flow heat {} FE to UP ({} K changed),wind = {}",
                    getTagName(),传输量,-传输量/heatCapacity,垂直风速);
        temperature.add热量(-传输量,heatCapacity);
        upperLayer.putHeat(传输量,null);
    }

    @Override
    protected void 对流() {
        if(!isUpperLayerValid) return;
        double 实际垂直风速 = winds.get(EnumFacing.UP).add(((DefaultAtmosphere)atmosphere).getDownWind(up)).y;
        热量对流(Math.abs(实际垂直风速));
        for(AtmosphereProperty property: GeographyPropertyManager.getFlowableProperties()){
            property.onConvect(this,up,实际垂直风速);
        }
    }

    @Override
    protected double[] 对外长波辐射() {
        double 总量 = 长波发射率 * AtmosphereUtil.FinalFactors.斯特藩_玻尔兹曼常数 *
                Math.pow(temperature.get(), 4) *
                AtmosphereUtil.FinalFactors.大气单元底面积* GeoAtmosphereProperty.getSimulationGap() /1000 * Altitude.to物理高度(getDepth());
        return new double[]{总量*0.6,总量*0.4};
    }

    @Override
    public void initialise(Chunk chunk) {
        if(!temperature.isInitialised()){
            if(lowerLayer == null || !lowerLayer.isInitialise()){
                temperature.set(280);
            }else{
                temperature.set((float) (lowerLayer.getTemperature().get()-
                        Altitude.to物理高度(lowerLayer.getDepth())* AtmosphereUtil.FinalFactors.对流层温度直减率));
            }
        }
        super.initialise(chunk);
    }

    @Override
    public void tick(Chunk chunk, Map<EnumFacing, Triple<Atmosphere, Chunk, EnumFacing>> neighbors) {
        super.tick(chunk, neighbors);
        ((DefaultAtmosphere)atmosphere).setDownWind(this);
    }
}
