package top.qiguaiaaaa.fluidgeography.api.atmosphere.property;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.Chunk;
import top.qiguaiaaaa.fluidgeography.api.FGInfo;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.state.WaterState;

public class AtmosphereWater extends AtmosphereProperty{
    public static final AtmosphereWater WATER = new AtmosphereWater();
    protected AtmosphereWater() {
        setFlowable(true);
        setRegistryName(new ResourceLocation(FGInfo.getModId(),"water"));
    }

    @Override
    public void onAtmosphereFlow(Atmosphere from, Chunk fromChunk, Atmosphere to, Chunk toChunk, EnumFacing direction, Vec3d windSpeed) {
        double windSpeedSize = windSpeed.dotProduct(new Vec3d(direction.getDirectionVec()));
        if(windSpeedSize >0){ //主动扩散
            int waterTransferAmount = getWaterTransferAmount(from,windSpeedSize);
            to.addWaterAmount(waterTransferAmount);
            if(!from.addWaterAmount(-waterTransferAmount)){
                from.setWaterAmount(0);
            }
        }else if(windSpeedSize<0){ //被动扩散
            if(to.getWaterAmount()>= from.getWaterAmount()) return;
            int waterTransferAmount = getWaterSpreadAmount(from,to);
            if(waterTransferAmount<=0) return;
            to.addWaterAmount(waterTransferAmount);
            if(!from.addWaterAmount(-waterTransferAmount)){
                from.setWaterAmount(0);
            }
        }
    }

    @Override
    public WaterState getStateInstance() {
        return new WaterState(0);
    }

    public static int getWaterTransferAmount(Atmosphere a, double windSpeed){
        int waterAmount = a.getWaterAmount();
        double expectedTransferAmount = waterAmount*0.2;
        double transferAmount = Math.min(Math.log((windSpeed+17)/17)/Math.log(17)*expectedTransferAmount,expectedTransferAmount);
        return (int) transferAmount;
    }

    public static int getWaterSpreadAmount(Atmosphere a, Atmosphere b){
        int waterAmountDiff = a.getWaterAmount()-b.getWaterAmount();
        if(waterAmountDiff == 0) return 0;
        return (int) (waterAmountDiff*0.1);
    }
}
