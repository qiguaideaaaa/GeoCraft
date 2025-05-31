package top.qiguaiaaaa.fluidgeography.api.atmosphere.property;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.Chunk;
import top.qiguaiaaaa.fluidgeography.api.FGInfo;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.state.TemperatureState;

public class AtmosphereTemperature extends AtmosphereProperty {
    public static final AtmosphereTemperature TEMPERATURE = new AtmosphereTemperature();
    public static final int BOILED_POINT = 373;
    public static final int ICE_POINT = 273;

    protected AtmosphereTemperature(){
        setWindEffect(true);
        setFlowable(true);
        setRegistryName(new ResourceLocation(FGInfo.getModId(),"temperature"));
    }

    @Override
    public Vec3d getWind(Atmosphere a, Atmosphere b, EnumFacing direction) {
        double size = getWindSpeedSize(a.getTemperature(),b.getTemperature());
        return new Vec3d(direction.getDirectionVec()).scale(size);
    }

    @Override
    public void onAtmosphereFlow(Atmosphere from, Chunk fromChunk, Atmosphere to, Chunk toChunk, EnumFacing direction, Vec3d windSpeed) {
        if(from.getTemperature()>to.getTemperature()) return;
        double windSpeedSize = windSpeed.dotProduct(new Vec3d(direction.getDirectionVec()));
        double heatTransferQuantity = getHeatQuantityTransferAmount(from,to,windSpeedSize);
        to.addHeatQuantity(heatTransferQuantity);
        from.addHeatQuantity(-heatTransferQuantity);
    }

    @Override
    public TemperatureState getStateInstance() {
        return new TemperatureState(-100);
    }

    /**
     * 计算A区块和B区块气流在温度方面的速度分量的大小（其实应该是加速度）
     * @param tempA 区块A大气温度
     * @param tempB 区块B大气温度
     * @return 速度大小，正数为A指向B，负数为B指向A
     */
    public static double getWindSpeedSize(float tempA, float tempB){
        return Math.sqrt(1000*Math.abs(tempA-tempB)/2)*0.1*(tempA>tempB?-1:1);
    }

    /**
     * 计算A大气和B大气热量传输的大小
     * @param a A大气
     * @param b B大气
     * @param windSpeed 大气间风速，正方向为A指向B
     * @return 互相传输的热量，正为A向B传输，负为B向A传输
     */
    public static double getHeatQuantityTransferAmount(Atmosphere a, Atmosphere b, double windSpeed){
        return 3072000*Math.min(Math.abs(windSpeed),16.0)*(a.getTemperature()-b.getTemperature());
    }
}
