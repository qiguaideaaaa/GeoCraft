package top.qiguaiaaaa.geocraft.atmosphere.layer;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.Chunk;
import top.qiguaiaaaa.geocraft.api.GEOInfo;
import top.qiguaiaaaa.geocraft.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.geocraft.api.atmosphere.property.AtmosphereProperty;
import top.qiguaiaaaa.geocraft.api.property.GeoAtmosphereProperty;
import top.qiguaiaaaa.geocraft.api.util.AtmosphereUtil;
import top.qiguaiaaaa.geocraft.api.util.math.Altitude;
import top.qiguaiaaaa.geocraft.atmosphere.GeographyPropertyManager;
import top.qiguaiaaaa.geocraft.atmosphere.DefaultAtmosphere;
import top.qiguaiaaaa.geocraft.atmosphere.state.DefaultTemperatureState;

import static top.qiguaiaaaa.geocraft.api.atmosphere.Atmosphere.TEMPERATURE_NOISE;

public class GroundAtmosphereLayer extends QiguaiAtmosphereLayer {
    public static final int 温度过渡开始高度 =10,温度过渡区间长度 =4,厚度=12;
    public GroundAtmosphereLayer(DefaultAtmosphere atmosphere) {
        super(atmosphere);
    }

    @Override
    public Vec3d 计算水平风速分量(Atmosphere to, EnumFacing dir){
        Vec3d wind = Vec3d.ZERO;
        Altitude 对方平均海拔 = to.getUnderlying().getAltitude();
        if(getTopY()>对方平均海拔.get()){
            double 对方大气同高度气压 = to.getPressure(new BlockPos(0,getCenterY(),0));
            double 水平风 = Math.sqrt(Math.abs(本层气压-对方大气同高度气压)/平均密度)/2*(本层气压>对方大气同高度气压?1:-1);

            double 海拔差 = 对方平均海拔.get()-getBeginY();

            if(Math.abs(海拔差)<2){
                wind = new Vec3d(dir.getDirectionVec()).scale(水平风);
            }else{
                wind = 计算坡度修饰后风速分量(水平风,海拔差,dir);
            }
        }

        for(AtmosphereProperty property: GeographyPropertyManager.getWindEffectedProperties()){
            wind = wind.add(property.getWind(this,to,dir));
        }
        return wind;
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
        return Vec3d.ZERO;
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
            property.onConvect(this, up,实际垂直风速);
        }
    }

    @Override
    protected double[] 对外长波辐射() {
        double 总量 = 长波发射率 * AtmosphereUtil.FinalFactors.斯特藩_玻尔兹曼常数 *
                Math.pow(temperature.get(), 4) *
                AtmosphereUtil.FinalFactors.大气单元底面积* GeoAtmosphereProperty.getSimulationGap();
        return new double[]{总量*0.5,总量*0.5};
    }

    @Override
    public void initialise(Chunk chunk) {
        if(!temperature.isInitialised()){
            temperature.set(DefaultTemperatureState.calculateBaseTemperature(chunk,atmosphere.getUnderlying()));
            if(lowerLayer != null) lowerLayer.getTemperature().set(temperature);
        }
        super.initialise(chunk);
    }

    @Override
    public float getTemperature(BlockPos pos, boolean notAir) {
        double 地面海拔 = atmosphere.getUnderlying().getAltitude().get();
        double 相对海拔 = pos.getY()-地面海拔;
        if(相对海拔<=0){
            if(isLowerLayerValid) return low.getTemperature(pos,notAir);
            return temperature.get();
        }
        if(相对海拔>厚度+0.01){
            if(!isUpperLayerValid) return getTemperature(new BlockPos(pos.getX(),地面海拔+厚度-0.01,pos.getZ()),notAir);
            return up.getTemperature(pos,notAir);
        }
        double temp;
        if(相对海拔<=温度过渡开始高度 || !isUpperLayerValid){
            double 高度差 = Altitude.to物理高度(相对海拔);
            temp = temperature.get() - AtmosphereUtil.FinalFactors.对流层温度直减率 * 高度差;
        }else {
            double 过渡区开始温度 = temperature.get() - AtmosphereUtil.FinalFactors.对流层温度直减率*Altitude.to物理高度(温度过渡开始高度) ;
            double 过渡区结束温度 = upperLayer.getTemperature().get();
            temp = (过渡区结束温度-过渡区开始温度)/温度过渡区间长度*(相对海拔-温度过渡开始高度)+过渡区开始温度;
        }
        float noise = (float)(TEMPERATURE_NOISE.getValue((float)pos.getX() / 8.0f, (float)pos.getZ() / 8.0f) * 4.0d);
        return (float) Math.max(temp - noise*0.05,3);
    }

    @Override
    public double getBeginY() {
        return atmosphere.getUnderlying().getAltitude().get();
    }

    @Override
    public double getDepth() {
        return 厚度;
    }

    @Override
    public String getTagName() {
        return "la";
    }
}
