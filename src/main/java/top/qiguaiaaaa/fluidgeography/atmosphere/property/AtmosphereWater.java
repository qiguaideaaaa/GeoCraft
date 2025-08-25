package top.qiguaiaaaa.fluidgeography.atmosphere.property;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fluids.FluidRegistry;
import top.qiguaiaaaa.fluidgeography.api.FGInfo;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.layer.AtmosphereLayer;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.property.GasProperty;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.state.GasState;
import top.qiguaiaaaa.fluidgeography.atmosphere.state.WaterState;
import top.qiguaiaaaa.fluidgeography.util.MathUtil;

public class AtmosphereWater extends GasProperty {
    public static final AtmosphereWater WATER = new AtmosphereWater();
    protected AtmosphereWater() {
        super(FluidRegistry.WATER,false,true);
        setRegistryName(new ResourceLocation(FGInfo.getModId(),"water"));
    }

    @Override
    public void onFlow(AtmosphereLayer from, Chunk fromChunk, Atmosphere to, Chunk toChunk, EnumFacing direction, Vec3d windSpeed) {
        if (to.getUnderlying().getAltitude().get() > from.getBeginY()+from.getDepth()) return;
        GasState water = from.getWater();
        if(water == null) return;
        double speed = MathUtil.获得带水平正负方向的速度(windSpeed,direction);
        if(speed<=0) return;
        int transferAmount = getWaterTransferAmount(water.getAmount()/4.0,speed);
        if(water.addAmount(-transferAmount)){
            to.addWater(transferAmount,new BlockPos(0,from.getBeginY()+from.getDepth()/2,0));
        }
    }

    public static int getWaterTransferAmount(double totalAmount,double windSpeed){
        return (int) (Math.max(windSpeed/16,1)*totalAmount);
    }

    @Override
    public WaterState getStateInstance() {
        return new WaterState(0);
    }
}
