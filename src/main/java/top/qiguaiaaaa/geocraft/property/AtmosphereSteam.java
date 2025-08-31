package top.qiguaiaaaa.geocraft.property;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fluids.FluidRegistry;
import top.qiguaiaaaa.geocraft.GeoCraft;
import top.qiguaiaaaa.geocraft.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.geocraft.api.atmosphere.layer.AtmosphereLayer;
import top.qiguaiaaaa.geocraft.api.atmosphere.layer.Layer;
import top.qiguaiaaaa.geocraft.api.property.FluidProperty;
import top.qiguaiaaaa.geocraft.api.state.FluidState;
import top.qiguaiaaaa.geocraft.api.util.AtmosphereUtil;
import top.qiguaiaaaa.geocraft.api.util.math.Altitude;
import top.qiguaiaaaa.geocraft.state.SteamState;
import top.qiguaiaaaa.geocraft.util.math.MathUtil;

public class AtmosphereSteam extends FluidProperty {
    public static final AtmosphereSteam STEAM = new AtmosphereSteam();
    protected AtmosphereSteam() {
        super(FluidRegistry.WATER, false, true);
        setRegistryName(GeoCraft.MODID,"steam");
    }

    @Override
    public void onFlow(AtmosphereLayer from, Chunk fromChunk, Atmosphere to, Chunk toChunk, EnumFacing direction, Vec3d windSpeed) {
        double fromTop = from.getBeginY()+from.getDepth();
        if (to.getUnderlying().getAltitude().get() > fromTop) return;
        FluidState steam = from.getSteam();
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
        }else if(speed <-1e-5){
            Layer layer = to.getLayer(centerPos);
            if(!(layer instanceof AtmosphereLayer)) return;
            FluidState toSteam = ((AtmosphereLayer)layer).getSteam();
            if(toSteam == null) return;
            int transferAmount = getSteamTransferAmount(toSteam.getAmount()
                            *Math.min((fromTop-layer.getBeginY())/ from.getDepth(),1)/4.0,
                    -speed);
            if(toSteam.addAmount(-transferAmount)){
                steam.addAmount(transferAmount);
            }
        }
    }

    @Override
    public void onConvect(AtmosphereLayer lower, AtmosphereLayer upper, double speed) {
        FluidState from = lower.getSteam();
        FluidState to = upper.getSteam();
        if(from == null || to == null) return;
        double fromWP = lower.getWaterPressure(),toWP = upper.getWaterPressure();
        speed = speed+计算水气压差动力(fromWP,toWP);
        if(speed >1e-6){
            double dis = Altitude.to物理高度(upper.getBeginY()+upper.getDepth()/2-(lower.getBeginY()+lower.getDepth()/2));
            int waterTransferAmount = getSteamTransferAmountVertically(from.getAmount()/ AtmosphereUtil.FinalFactors.大气单元底面积,speed, dis);
            if(from.addAmount(-waterTransferAmount)){
                to.addAmount(waterTransferAmount);
            }
        }else if(speed <-1e-6){
            double dis = Altitude.to物理高度(upper.getBeginY()+upper.getDepth()/2-(lower.getBeginY()+lower.getDepth()/2));
            int waterTransferAmount = getSteamTransferAmountVertically(to.getAmount()/ AtmosphereUtil.FinalFactors.大气单元底面积,-speed, dis);
            if(to.addAmount(-waterTransferAmount)){
                from.addAmount(waterTransferAmount);
            }
        }
    }

    @Override
    public FluidState getStateInstance() {
        return new SteamState(0);
    }

    public static int getSteamTransferAmount(double totalAmount, double windSpeed){
        return (int) (totalAmount*windSpeed/(windSpeed+17));
    }
    public static int getSteamTransferAmountVertically(double totalAmount, double windSpeed,double distance){
        return (int) (totalAmount*windSpeed/(windSpeed+distance)*216); //时间步长
    }

    public static double 计算水气压差动力(double from,double to){
        return (from-to)/1000;
    }
}
