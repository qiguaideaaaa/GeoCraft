package top.qiguaiaaaa.fluidgeography.atmosphere.layer;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.chunk.Chunk;
import org.apache.commons.lang3.tuple.Triple;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.AtmosphereWorldInfo;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.layer.UnderlyingLayer;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.raypack.HeatPack;
import top.qiguaiaaaa.fluidgeography.api.configs.AtmosphereConfig;
import top.qiguaiaaaa.fluidgeography.api.util.AtmosphereUtil;
import top.qiguaiaaaa.fluidgeography.api.util.FluidUtil;
import top.qiguaiaaaa.fluidgeography.api.util.math.Altitude;

import javax.annotation.Nullable;

import java.util.Map;

import static top.qiguaiaaaa.fluidgeography.api.util.ChunkUtil.getSameLiquidDepth;

public class Underlying extends UnderlyingLayer {
    protected long heatCapacity;
    public double 平均返照率;
    public double 平均发射率;


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
        double averageReflectivity = 0, averageEmissivity = 0;
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int height = chunk.getHeightValue(x, z);
                IBlockState state = chunk.getBlockState(x, height, z);
                if (state.getBlock() == Blocks.AIR && height > 0) {
                    height--;
                    state = chunk.getBlockState(x, height, z);
                }
                int blockC = AtmosphereConfig.getSpecificHeatCapacity(state);
                if (FluidUtil.isFluid(state) && height > 0) {
                    blockC += blockC * getSameLiquidDepth(chunk, x, height - 1, z, FluidUtil.getFluid(state));
                }
                heatCapacity += blockC * 1000L;
                averageReflectivity += AtmosphereConfig.getReflectivity(state);
                averageEmissivity += AtmosphereConfig.getEmissivity(state);
            }
        }
        平均返照率 = averageReflectivity / 256;
        平均发射率 = averageEmissivity / 256;
        this.heatCapacity = heatCapacity;
        return this;
    }

    @Override
    public void initialise(Chunk chunk) {
        updateAltitude(chunk);
        super.initialise(chunk);
    }

    @Override
    public void tick(Chunk chunk, Map<EnumFacing, Triple<Atmosphere,Chunk,EnumFacing>> neighbors) {
        if(!atmosphere.getAtmosphereWorldInfo().isTemperatureConstant()){
            double 地面辐射损失系数 = AtmosphereConfig.GROUND_RADIATION_LOSS_RATE.getValue().value;
            double 地面长波辐射 = AtmosphereUtil.FinalFactors.每大气刻损失能量常数 * Math.pow(temperature.get(), 4) * 平均发射率*地面辐射损失系数;
            temperature.add热量(-地面长波辐射,heatCapacity);
            if(upperLayer == null) return;
            upperLayer.sendHeat(new HeatPack(HeatPack.HeatType.LONG_WAVE,地面长波辐射), EnumFacing.UP);
        }
        if(!atmosphere.getAtmosphereWorldInfo().isWorldClosed() &&
                atmosphere.tickTime() % AtmosphereConfig.ATMOSPHERE_UNDERLYING_RECALCULATE_GAP.getValue().value == 0 ){
            update(chunk);
        }
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
                    temperature.add热量(pack.drawHeat(pack.getHeat()*平均返照率),heatCapacity);
                    break;
                case LONG_WAVE:
                    temperature.add热量(pack.drawHeat(pack.getHeat()),heatCapacity);
                    break;
            }
            if(upperLayer == null) return;
            upperLayer.sendHeat(pack,new Vec3i(direction.x,-direction.y,direction.z));
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
    public String getTagName() {
        return "g";
    }
}
