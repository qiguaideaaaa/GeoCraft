package top.qiguaiaaaa.fluidgeography.atmosphere.property;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.Chunk;
import top.qiguaiaaaa.fluidgeography.api.FGInfo;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.property.TemperatureProperty;
import top.qiguaiaaaa.fluidgeography.atmosphere.state.LowerAtmosphereTemperatureState;
import top.qiguaiaaaa.fluidgeography.api.util.AtmosphereUtil;

public class LowerAtmosphereTemperature extends TemperatureProperty {
    public static final LowerAtmosphereTemperature TEMPERATURE = new LowerAtmosphereTemperature();

    protected LowerAtmosphereTemperature(){
        super(true,true);
        setRegistryName(new ResourceLocation(FGInfo.getModId(),"temperature"));
    }

    @Override
    public Vec3d getWind(Atmosphere a, Atmosphere b, EnumFacing direction) {
        double size = getWindSpeedSize(
                a.getAtmosphereWorldInfo().getModel().getPressure(a),
                b.getAtmosphereWorldInfo().getModel().getPressure(b),
                a.get下垫面().get地面平均海拔().get物理海拔(),
                b.get下垫面().get地面平均海拔().get物理海拔());
        return new Vec3d(direction.getDirectionVec()).scale(size);
    }

    @Override
    public void onAtmosphereFlow(Atmosphere from, Chunk fromChunk, Atmosphere to, Chunk toChunk, EnumFacing direction, Vec3d windSpeed) {
        double windSpeedSize = windSpeed.dotProduct(new Vec3d(direction.getDirectionVec()));
        if(windSpeedSize<0) return;

        double heatTransferQuantity = getHeatQuantityTransferAmount(from,to,windSpeedSize);
        to.add低层大气热量(heatTransferQuantity);
        from.add低层大气热量(-heatTransferQuantity);
    }

    @Override
    public LowerAtmosphereTemperatureState getStateInstance() {
        return new LowerAtmosphereTemperatureState(-100);
    }

    /**
     * 计算A区块和B区块气流在温度方面的速度分量的大小（其实应该是加速度）
     * @param pressureA 区块A大气气压
     * @param pressureB 区块B大气气压
     * @param altA 区块A物理海拔
     * @param altB 区块B物理海拔
     * @return 速度大小，正数为A指向B，负数为B指向A
     */
    public static double getWindSpeedSize(double pressureA, double pressureB,double altA,double altB){
        double 平均密度 = (AtmosphereUtil.get低层大气密度(altA)+AtmosphereUtil.get低层大气密度(altB))/2;
        double 气压差风速 = Math.sqrt(Math.abs(pressureA-pressureB)/平均密度)*0.1*(pressureA>pressureB?-1:1);
        double 重力影响 = Math.sqrt(2 * AtmosphereUtil.FinalFactors.重力加速度 * Math.abs(altB-altA)) * 0.01;
        double 重力差风速 = (altB > altA ? -重力影响 : 重力影响);
        return 气压差风速+重力差风速;
    }

    /**
     * 计算A大气和B大气热量传输的大小
     * @param a A大气
     * @param b B大气
     * @param windSpeed 大气间风速，正方向为A指向B
     * @return 互相传输的热量，正为A向B传输，负为B向A传输
     */
    public static double getHeatQuantityTransferAmount(Atmosphere a, Atmosphere b, double windSpeed){
        double ρAvg  = (
                AtmosphereUtil.get低层大气密度(a.get下垫面().get地面平均海拔().get物理海拔()) +
                        AtmosphereUtil.get低层大气密度(b.get下垫面().get地面平均海拔().get物理海拔())) /2;
        double expK = 0.005;
        // 热量传输效率
        double flowEfficiency = Math.exp(-expK*Math.abs(a.get下垫面().get地面平均海拔().get物理海拔()-b.get下垫面().get地面平均海拔().get物理海拔()));

        double referenceWind = 10.0; // 参考风速 ，用于归一化
        return ρAvg * AtmosphereUtil.FinalFactors.低层大气单元体积 * AtmosphereUtil.FinalFactors.干空气比热容 * (a.get低层大气温度()-b.get低层大气温度()) * Math.min(windSpeed / referenceWind, 1.6) * flowEfficiency;
        //return 3072000*Math.min(Math.abs(windSpeed),16.0)*(a.get低层大气温度()-b.get低层大气温度());
    }
}
