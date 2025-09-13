package top.qiguaiaaaa.geocraft.geography.atmosphere.layer.surface;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.Chunk;
import org.apache.commons.lang3.tuple.Triple;
import top.qiguaiaaaa.geocraft.GeoCraft;
import top.qiguaiaaaa.geocraft.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.geocraft.api.property.AtmosphereProperty;
import top.qiguaiaaaa.geocraft.api.setting.GeoAtmosphereSetting;
import top.qiguaiaaaa.geocraft.api.util.AtmosphereUtil;
import top.qiguaiaaaa.geocraft.api.util.math.Altitude;
import top.qiguaiaaaa.geocraft.geography.atmosphere.SurfaceAtmosphere;
import top.qiguaiaaaa.geocraft.geography.property.GeographyPropertyManager;

import javax.annotation.Nonnull;
import java.util.Map;

import static top.qiguaiaaaa.geocraft.api.atmosphere.Atmosphere.TEMPERATURE_NOISE;

public class MiddleAtmosphereLayer extends SurfaceAtmosphereLayer {
    public static final int 默认相对起始高度 = GroundAtmosphereLayer.厚度, 最大厚度 =68,最高高度=300,第二温度过渡区间长度=12;
    protected double 第二温度过渡开始相对高度,厚度;
    public MiddleAtmosphereLayer(SurfaceAtmosphere atmosphere) {
        super(atmosphere);
        相对起始高度 = 默认相对起始高度;
        起始高度 = atmosphere.getUnderlying().getAltitude().get()+ 相对起始高度;
        厚度 = Math.min(最大厚度,最高高度-起始高度);
        第二温度过渡开始相对高度 = 相对起始高度+厚度-第二温度过渡区间长度/2.0;
    }

    @Override
    public void 更新高度缓存() {
        super.更新高度缓存();
        厚度 = Math.min(最大厚度,最高高度-起始高度);
        第二温度过渡开始相对高度 = 相对起始高度+厚度-第二温度过渡区间长度/2.0;
    }

    @Override
    public float getTemperature(@Nonnull BlockPos pos, boolean notAir) {
        double 地面海拔 = atmosphere.getUnderlying().getAltitude().get();
        double 相对海拔 = pos.getY()-地面海拔;
        if(相对海拔< 相对起始高度 -0.1){
            if(isLowerLayerValid) return low.getTemperature(pos,notAir);
            return temperature.get();
        }
        if(相对海拔>getTopY()){
            if(isUpperLayerValid) return up.getTemperature(pos,notAir);
            return getTemperature(new BlockPos(pos.getX(),地面海拔+getDepth()-0.01,pos.getZ()),notAir);
        }
        double temp;
        if(相对海拔< GroundAtmosphereLayer.温度过渡开始高度+ GroundAtmosphereLayer.温度过渡区间长度 && isLowerLayerValid){
            double 过渡区开始温度 = low.getTemperature(new BlockPos(pos.getX(),地面海拔+ GroundAtmosphereLayer.温度过渡开始高度,pos.getZ()),false);
            double 过渡区结束温度 = temperature.get();
            temp = (过渡区结束温度-过渡区开始温度)/ GroundAtmosphereLayer.温度过渡区间长度*(相对海拔- GroundAtmosphereLayer.温度过渡开始高度)+过渡区开始温度;
        }else if(相对海拔> 第二温度过渡开始相对高度 && isUpperLayerValid){
            double 高度差 = Altitude.to物理高度(相对海拔);
            double 过渡区开始温度 = temperature.get() - AtmosphereUtil.Constants.对流层温度直减率 * 高度差;
            double 过渡区结束温度 = upperLayer.getTemperature().get();
            temp = (过渡区结束温度-过渡区开始温度)/第二温度过渡区间长度*(相对海拔- 第二温度过渡开始相对高度)+过渡区开始温度;
        } else {
            double 高度差 = Altitude.to物理高度(相对海拔- 相对起始高度);
            temp = temperature.get() - AtmosphereUtil.Constants.对流层温度直减率 * 高度差;
        }
        float noise = (float)(TEMPERATURE_NOISE.getValue((float)pos.getX() / 8.0f, (float)pos.getZ() / 8.0f) * 4.0d);
        return (float) Math.max(temp - noise*0.05,3);
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
                        垂直风速*216/Altitude.to物理高度(厚度)*
                                diff/(upperLayer.getDepth()+厚度)*2
                                *Math.min(平均密度/1.2,1),
                        -Math.abs(diff/3),Math.abs(diff/3));
        if(((SurfaceAtmosphere)atmosphere).isDebug())
            GeoCraft.getLogger().info("{} flow heat {} FE to UP ({} K changed),wind = {}",
                    getTagName(),传输量,-传输量/heatCapacity,垂直风速);
        temperature.addHeat(-传输量,heatCapacity);
        upperLayer.putHeat(传输量,null);
    }

    @Override
    protected void 对流() {
        if(!isUpperLayerValid) return;
        double 实际垂直风速 = winds.get(EnumFacing.UP).add(((SurfaceAtmosphere)atmosphere).getDownWind(up)).y;
        热量对流(Math.abs(实际垂直风速));
        for(AtmosphereProperty property: GeographyPropertyManager.getFlowableProperties()){
            property.onConvect(this,up,实际垂直风速);
        }
    }

    @Override
    protected double[] 对外长波辐射() {
        final double 变化上限 = temperature.get()/2*heatCapacity;
        double 总量 = 长波发射率 * AtmosphereUtil.Constants.斯特藩_玻尔兹曼常数 *
                Math.pow(temperature.get(), 4) *
                AtmosphereUtil.Constants.大气单元底面积* GeoAtmosphereSetting.getSimulationGap() /1500 * Altitude.to物理高度(getDepth());
        总量 = MathHelper.clamp(总量,-变化上限,变化上限);
        return new double[]{总量*0.7,总量*0.3};
    }

    @Override
    public void onLoadWithoutChunk() {
        if(!temperature.isInitialised()){
            if(lowerLayer == null || !lowerLayer.isInitialise()){
                temperature.set(280);
            }else{
                temperature.set((float) (lowerLayer.getTemperature().get()-
                        Altitude.to物理高度(lowerLayer.getDepth())* AtmosphereUtil.Constants.对流层温度直减率));
            }
        }
        super.onLoadWithoutChunk();
    }

    @Override
    public void tick(Chunk chunk, @Nonnull Map<EnumFacing, Triple<Atmosphere, Chunk, EnumFacing>> neighbors, int x, int z) {
        super.tick(chunk, neighbors,x,z);
        ((SurfaceAtmosphere)atmosphere).setDownWind(this);
    }
}
