package top.qiguaiaaaa.geocraft.atmosphere.layer;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.Chunk;
import org.apache.commons.lang3.tuple.Triple;
import top.qiguaiaaaa.geocraft.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.geocraft.api.atmosphere.AtmosphereWorldInfo;
import top.qiguaiaaaa.geocraft.api.atmosphere.layer.AtmosphereLayer;
import top.qiguaiaaaa.geocraft.api.atmosphere.layer.UnderlyingLayer;
import top.qiguaiaaaa.geocraft.api.atmosphere.property.TemperatureProperty;
import top.qiguaiaaaa.geocraft.api.atmosphere.raypack.HeatPack;
import top.qiguaiaaaa.geocraft.api.property.GeoAtmosphereProperty;
import top.qiguaiaaaa.geocraft.api.property.GeoBlockProperty;
import top.qiguaiaaaa.geocraft.api.util.AtmosphereUtil;
import top.qiguaiaaaa.geocraft.api.util.FluidUtil;
import top.qiguaiaaaa.geocraft.api.util.math.Altitude;

import javax.annotation.Nullable;
import java.util.Map;

import static top.qiguaiaaaa.geocraft.util.ChunkUtil.getSameLiquidDepth;

public class Underlying extends UnderlyingLayer {
    public double 平均返照率;
    protected double 周围区块最高平均海拔 = -100000;
    boolean afterFirstTick = false;


    public Underlying(Atmosphere atmosphere) {
        super(atmosphere);
    }

    public Underlying updateAltitude(Chunk chunk){
        AtmosphereWorldInfo worldInfo = this.atmosphere.getAtmosphereWorldInfo();
        if(!worldInfo.isWorldClosed()) setAltitude(Altitude.getAverageHeight(chunk));
        else setAltitude(worldInfo.getWorld().getSeaLevel());
        return this;
    }

    /**
     * 更新下垫面属性
     * @param chunk 下垫面所属区块
     * @return 自身
     */
    @Override
    public Underlying update(Chunk chunk) {
        long heatCapacity = 0;
        double averageReflectivity = 0;
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int height = chunk.getHeightValue(x, z);
                IBlockState state;
                int maxDeep = 10;
                boolean canSunReach = true;
                double upReflectivity = 0;
                int blockC = 0;
                while(maxDeep>0){
                    maxDeep--;
                    state = chunk.getBlockState(x, height, z);
                    if (state.getBlock() == Blocks.AIR && height > 0) {
                        height--;
                        continue;
                    }
                    if(canSunReach){
                        double thisReflectivity = GeoBlockProperty.getBlockReflectivity(state)*(1-upReflectivity);
                        averageReflectivity += thisReflectivity;
                        upReflectivity += thisReflectivity;
                        canSunReach = false;
                    }
                    int C = GeoBlockProperty.getBlockHeatCapacity(state);
                    blockC += C;
                    if(FluidUtil.isFluid(state) && height > 0){
                        blockC += C * getSameLiquidDepth(chunk, x, height - 1, z, FluidUtil.getFluid(state),5);
                        break;
                    }
                    if(state.isTranslucent()){
                        canSunReach = true;
                        height--;
                        continue;
                    }
                    if(state.isFullBlock()) break;
                    height--;
                }
                heatCapacity += blockC * 1000L;
            }
        }
        平均返照率 = averageReflectivity / 256;
        this.heatCapacity = heatCapacity;
        return this;
    }

    @Override
    public void initialise(Chunk chunk) {
        updateAltitude(chunk);
        周围区块最高平均海拔 = altitude.get();
        super.initialise(chunk);
    }

    @Override
    public void tick(Chunk chunk, Map<EnumFacing, Triple<Atmosphere,Chunk,EnumFacing>> neighbors) {
        if(!afterFirstTick){
            周围区块最高平均海拔 = altitude.get();
            for(Triple<Atmosphere,Chunk,EnumFacing> neighbor:neighbors.values()){
                周围区块最高平均海拔 = Math.max(周围区块最高平均海拔,neighbor.getLeft().getUnderlying().getAltitude().get());
            }
            afterFirstTick = true;
        }
        if(atmosphere.getAtmosphereWorldInfo().isTemperatureConstant()) return;

        if(!atmosphere.getAtmosphereWorldInfo().isWorldClosed() &&
                atmosphere.tickTime()+chunk.x+5L*chunk.z % GeoAtmosphereProperty.getUnderlyingReloadGap() == 0 ){
            update(chunk);
            周围区块最高平均海拔 = altitude.get();
            for(Triple<Atmosphere,Chunk,EnumFacing> neighbor:neighbors.values()){
                周围区块最高平均海拔 = Math.max(周围区块最高平均海拔,neighbor.getLeft().getUnderlying().getAltitude().get());
            }
        }

        double 地面长波辐射 = AtmosphereUtil.FinalFactors.每秒损失能量常数 * Math.pow(temperature.get(), 4)* GeoAtmosphereProperty.getSimulationGap();
        temperature.add热量(-地面长波辐射,heatCapacity);
        if(upperLayer == null) return;
        upperLayer.sendHeat(new HeatPack(HeatPack.HeatType.LONG_WAVE,地面长波辐射), EnumFacing.UP);

        //接触式热量传递
        if(!(upperLayer instanceof AtmosphereLayer)) return;
        double tempMin = Math.min(temperature.get(),upperLayer.getTemperature().get());
        double tempDiff = temperature.get()-upperLayer.getTemperature().get();
        double 传递热量 = Math.min(heatCapacity,upperLayer.getHeatCapacity())*
                MathHelper.clamp(
                MathHelper.clamp(tempDiff,-tempMin/8,tempMin/8) *Math.min(获取上面平均风速()+1,20) /32
                        ,-Math.abs(tempDiff)/3,Math.abs(tempDiff)/3);
        temperature.add热量(-传递热量,heatCapacity);
        upperLayer.putHeat(传递热量,null);
    }

    @Override
    public void sendHeat(HeatPack pack, @Nullable Vec3d direction) {
        if(direction == null || pack.getType() == null || direction.y == 0){
            this.putHeat(pack.getHeat(),null);
            return;
        }
        if(direction.y<0){
            switch (pack.getType()){
                case SHORT_WAVE:
                    temperature.add热量(pack.drawHeat(pack.getHeat()*(1-平均返照率)),heatCapacity);
                    break;
                case LONG_WAVE:
                    temperature.add热量(pack.drawHeat(pack.getHeat()),heatCapacity);
                    break;
            }
            if(upperLayer == null) return;
            upperLayer.sendHeat(pack,new Vec3d(direction.x,-direction.y,direction.z));
        }else if(direction.y >0){
            if(upperLayer == null) return;
            upperLayer.sendHeat(pack,direction);
        }
    }


    @Override
    public double getDepth() {
        return altitude.get()-getBeginY();
    }

    @Override
    public float getTemperature(BlockPos pos) {
        if(pos.getY()<=周围区块最高平均海拔) return temperature.get();
        double 高差 = pos.getY()-周围区块最高平均海拔;
        return (float) Math.max(temperature.get()-高差* AtmosphereUtil.FinalFactors.对流层温度直减率/2, TemperatureProperty.MIN);
    }

    @Override
    public String getTagName() {
        return "g";
    }

    protected double 获取上面平均风速(){
        if(!(upperLayer instanceof AtmosphereLayer)) return 0;
        AtmosphereLayer layer = (AtmosphereLayer) upperLayer;
        double wind = layer.getWind(new BlockPos(4,altitude.get(),4)).length()+layer.getWind(new BlockPos(4,altitude.get(),8)).length()
                +layer.getWind(new BlockPos(8,altitude.get(),4)).length()+layer.getWind(new BlockPos(8,altitude.get(),8)).length();
        return wind/4;
    }
}
