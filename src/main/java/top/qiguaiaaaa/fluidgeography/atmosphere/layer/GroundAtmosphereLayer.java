package top.qiguaiaaaa.fluidgeography.atmosphere.layer;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.property.AtmosphereProperty;
import top.qiguaiaaaa.fluidgeography.api.util.AtmosphereUtil;
import top.qiguaiaaaa.fluidgeography.api.util.math.Altitude;
import top.qiguaiaaaa.fluidgeography.atmosphere.AtmospherePropertyManager;
import top.qiguaiaaaa.fluidgeography.atmosphere.DefaultAtmosphere;

import static top.qiguaiaaaa.fluidgeography.api.atmosphere.Atmosphere.TEMPERATURE_NOISE;

public class GroundAtmosphereLayer extends QiguaiAtmosphereLayer {
    public static final int 温度过渡开始高度 =8,温度过渡区间长度 =4,厚度=10;
    public GroundAtmosphereLayer(DefaultAtmosphere atmosphere) {
        super(atmosphere);
    }

    @Override
    public Vec3d 计算水平风速分量(Atmosphere to, EnumFacing dir){
        Vec3d wind = Vec3d.ZERO;
        Altitude 对方平均海拔 = to.getUnderlying().getAltitude();
        if(getTopY()>对方平均海拔.get()){
            double 本层气压 = getPressure();
            double 对方大气同高度气压 = to.getPressure(new BlockPos(0,getCenterY(),0));
            double 水平风 = Math.sqrt(Math.abs(本层气压-对方大气同高度气压)/平均密度)*0.1*(本层气压>对方大气同高度气压?1:-1);

            double 海拔差 = 对方平均海拔.get()-getBeginY();

            if(Math.abs(海拔差)<1e-2){
                wind = new Vec3d(dir.getDirectionVec()).scale(水平风);
            }else{
                wind = 计算坡度修饰后风速分量(水平风,海拔差,dir);
            }
        }

        for(AtmosphereProperty property: AtmospherePropertyManager.getWindEffectedProperties()){
            wind.add(property.getWind(this,to,dir));
        }
        return wind;
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
        return Vec3d.ZERO;
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
        for(AtmosphereProperty property:AtmospherePropertyManager.getFlowableProperties()){
            property.onConvect(this,upperLayer,实际垂直风速);
        }
    }

    @Override
    public float getTemperature(BlockPos pos, boolean notAir) {
        double 地面海拔 = atmosphere.getUnderlying().getAltitude().get();
        double 相对海拔 = pos.getY()-地面海拔;
        if(相对海拔<=0){
            return temperature.get();
        }
        if(相对海拔>厚度+0.01){
            if(upperLayer == null) return getTemperature(new BlockPos(pos.getX(),地面海拔+厚度-0.01,pos.getZ()),notAir);
            return upperLayer.getTemperature(pos,notAir);
        }
        double temp;
        if(相对海拔<=温度过渡开始高度 || upperLayer == null){
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
