package top.qiguaiaaaa.fluidgeography.atmosphere.property;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fluids.FluidRegistry;
import top.qiguaiaaaa.fluidgeography.FluidGeography;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.layer.AtmosphereLayer;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.property.GasProperty;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.state.GasState;
import top.qiguaiaaaa.fluidgeography.api.util.AtmosphereUtil;
import top.qiguaiaaaa.fluidgeography.api.util.math.Altitude;
import top.qiguaiaaaa.fluidgeography.atmosphere.state.SteamState;
import top.qiguaiaaaa.fluidgeography.util.MathUtil;

public class AtmosphereSteam extends GasProperty {
    public static final AtmosphereSteam STEAM = new AtmosphereSteam();
    protected AtmosphereSteam() {
        super(FluidRegistry.WATER, false, true);
        setRegistryName(FluidGeography.MODID,"steam");
    }

    @Override
    public void onFlow(AtmosphereLayer from, Chunk fromChunk, Atmosphere to, Chunk toChunk, EnumFacing direction, Vec3d windSpeed) {
        if (to.getUnderlying().getAltitude().get() > from.getBeginY()+from.getDepth()) return;
        GasState steam = from.getSteam();
        if(steam == null) return;
        double speed = MathUtil.获得带水平正负方向的速度(windSpeed,direction);
        BlockPos centerPos = new BlockPos(0,from.getBeginY()+from.getDepth()/2,0);
        double fromWP = from.getWaterPressure(),toWP = to.getWaterPressure(centerPos);
        speed = speed+计算水气压差动力(fromWP,toWP);
        if(speed >1e-5){
            int waterTransferAmount = getSteamTransferAmount(steam.getAmount()/4.0,speed);
            if(steam.addAmount(-waterTransferAmount)){
                to.addSteam(waterTransferAmount,centerPos);
            }
        }
    }

    @Override
    public void onConvect(AtmosphereLayer lower, AtmosphereLayer upper, double speed) {
        GasState from = lower.getSteam();
        GasState to = upper.getSteam();
        if(from == null || to == null) return;
        double fromWP = lower.getWaterPressure(),toWP = upper.getWaterPressure();
        speed = speed+计算水气压差动力(fromWP,toWP);
        if(speed >1e-6){
            double dis = Altitude.to物理高度(upper.getBeginY()-lower.getBeginY());
            int waterTransferAmount = getSteamTransferAmountVertically(from.getAmount()/ AtmosphereUtil.FinalFactors.大气单元底面积,speed, dis);
            if(from.addAmount(-waterTransferAmount)){
                to.addAmount(waterTransferAmount);
            }
        }
    }

    @Override
    public GasState getStateInstance() {
        return new SteamState(0);
    }

    public static int getSteamTransferAmount(double totalAmount, double windSpeed){
        return (int) (totalAmount*windSpeed/(windSpeed+17));
    }
    public static int getSteamTransferAmountVertically(double totalAmount, double windSpeed,double distance){
        return (int) (totalAmount*windSpeed/(windSpeed+distance)*216); //时间步长
    }

    public static double 计算水气压差动力(double from,double to){
        return (from-to)/10000;
    }
}
