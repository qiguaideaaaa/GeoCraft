package top.qiguaiaaaa.geocraft.property;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
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
import top.qiguaiaaaa.geocraft.state.WaterState;
import top.qiguaiaaaa.geocraft.util.math.MathUtil;

public class AtmosphereWater extends FluidProperty {
    public static final AtmosphereWater WATER = new AtmosphereWater();
    protected AtmosphereWater() {
        super(FluidRegistry.WATER,false,true);
        setRegistryName(new ResourceLocation(GeoCraft.MODID,"water"));
    }

    @Override
    public void onFlow(AtmosphereLayer from, Chunk fromChunk, Atmosphere to, Chunk toChunk, EnumFacing direction, Vec3d windSpeed) {
        double fromTop = from.getBeginY()+from.getDepth();
        if (to.getUnderlying().getAltitude().get() > fromTop) return;
        BlockPos centerPos = new BlockPos(0,from.getBeginY()+from.getDepth()/2,0);
        Layer layer = to.getLayer(centerPos);
        if(!(layer instanceof AtmosphereLayer)) return;
        FluidState water = from.getWater();
        if(water == null) return;
        FluidState toWater = layer.getWater();
        if(toWater == null) return;

        double speed = MathUtil.获得带水平正负方向的速度(windSpeed,direction)+(water.getAmount()-toWater.getAmount())/2000d;
        if(speed>1e-5){
            int transferAmount = getWaterTransferAmount(water.getAmount()/4.0,speed);
            if(water.addAmount(-transferAmount)){
                to.addWater(transferAmount,centerPos);
            }
        }else if(speed<-1e-5){
            int transferAmount = getWaterTransferAmount(toWater.getAmount()
                    *Math.min((fromTop-layer.getBeginY())/from.getDepth(),1)/4.0,
                    -speed);
            if(toWater.addAmount(-transferAmount)){
                water.addAmount(transferAmount);
            }
        }

    }

    @Override
    public void onConvect(AtmosphereLayer lower, AtmosphereLayer upper, double speed) {
        if(speed<=0.01) return;
        FluidState from = lower.getWater(),to = upper.getWater();
        if(from == null || to == null) return;
        double dis = Altitude.to物理高度(upper.getBeginY()+upper.getDepth()/2-(lower.getBeginY()+lower.getDepth()/2));
        int waterTransferAmount = getWaterTransferAmountVertically(from.getAmount()/ AtmosphereUtil.FinalFactors.大气单元底面积,speed, dis*2);
        if(from.addAmount(-waterTransferAmount)){
            to.addAmount(waterTransferAmount);
        }
    }

    public static int getWaterTransferAmount(double totalAmount, double windSpeed){
        return (int) (Math.max(windSpeed/16,1)*totalAmount);
    }
    public static int getWaterTransferAmountVertically(double totalAmount, double windSpeed,double distance){
        return (int) (totalAmount*windSpeed/(windSpeed+distance)*216); //时间步长
    }

    @Override
    public WaterState getStateInstance() {
        return new WaterState(0);
    }
}
