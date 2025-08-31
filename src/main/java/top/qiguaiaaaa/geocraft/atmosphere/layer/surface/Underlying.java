package top.qiguaiaaaa.geocraft.atmosphere.layer.surface;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.Chunk;
import org.apache.commons.lang3.tuple.Triple;
import top.qiguaiaaaa.geocraft.api.GeoCraftProperties;
import top.qiguaiaaaa.geocraft.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.geocraft.api.atmosphere.AtmosphereWorldInfo;
import top.qiguaiaaaa.geocraft.api.atmosphere.layer.AtmosphereLayer;
import top.qiguaiaaaa.geocraft.api.atmosphere.layer.UnderlyingLayer;
import top.qiguaiaaaa.geocraft.api.property.TemperatureProperty;
import top.qiguaiaaaa.geocraft.api.atmosphere.raypack.HeatPack;
import top.qiguaiaaaa.geocraft.api.state.TemperatureState;
import top.qiguaiaaaa.geocraft.api.setting.GeoAtmosphereSetting;
import top.qiguaiaaaa.geocraft.api.setting.GeoBlockSetting;
import top.qiguaiaaaa.geocraft.api.util.AtmosphereUtil;
import top.qiguaiaaaa.geocraft.api.util.FluidUtil;
import top.qiguaiaaaa.geocraft.api.util.math.Altitude;
import top.qiguaiaaaa.geocraft.property.AltitudeProperty;
import top.qiguaiaaaa.geocraft.state.AltitudeState;
import top.qiguaiaaaa.geocraft.state.HeatCapacityState;
import top.qiguaiaaaa.geocraft.state.ReflectivityState;

import javax.annotation.Nullable;
import java.util.Map;

import static top.qiguaiaaaa.geocraft.util.ChunkUtil.getSameLiquidDepth;

public class Underlying extends UnderlyingLayer {
    public static final int 底层相对周围最低海拔最低距离 = 10;
    public static final float 地底温度受地表影响系数 = 0.001f;
    public double 平均返照率;
    protected final TemperatureState temperature = GeoCraftProperties.TEMPERATURE.getStateInstance();
    protected TemperatureState deepTemperature = GeoCraftProperties.DEEP_TEMPERATURE.getStateInstance();
    protected AltitudeState altitudeState = new AltitudeState(altitude);
    protected HeatCapacityState heatCapacityState = new HeatCapacityState();
    protected ReflectivityState reflectivityState = new ReflectivityState();
    protected double 周围区块最高平均海拔 = -100000,周围区块最低平均海拔 = -100000;
    boolean afterFirstTick = false;

    public Underlying(Atmosphere atmosphere) {
        super(atmosphere);
        altitude.set(AltitudeProperty.UNAVAILABLE);
        states.put(GeoCraftProperties.TEMPERATURE, temperature);
        states.put(GeoCraftProperties.DEEP_TEMPERATURE,deepTemperature);
        states.put(altitudeState.getProperty(),altitudeState);
        states.put(heatCapacityState.getProperty(),heatCapacityState);
        states.put(reflectivityState.getProperty(),reflectivityState);
    }

    @Override
    public void putHeat(double quanta, @Nullable BlockPos pos) {
        if(pos != null){
            if(upperLayer != null && pos.getY()>周围区块最高平均海拔){
                upperLayer.putHeat(quanta, pos);
                return;
            }
            else if(pos.getY()<=周围区块最低平均海拔-底层相对周围最低海拔最低距离) return;
            else if(pos.getY()<周围区块最低平均海拔){
                super.putHeat(quanta*(pos.getY()-周围区块最低平均海拔+底层相对周围最低海拔最低距离)/底层相对周围最低海拔最低距离,pos);
                return;
            }
        }
        super.putHeat(quanta, pos);
    }

    @Override
    public double drawHeat(double quanta, @Nullable BlockPos pos) {
        if(pos != null){
            if(upperLayer != null && pos.getY()>周围区块最高平均海拔) return upperLayer.drawHeat(quanta, pos);
            else if(pos.getY()<=周围区块最低平均海拔-底层相对周围最低海拔最低距离+1e-2) return quanta;
            else if(pos.getY()<周围区块最低平均海拔){
                double 修饰比 = (pos.getY()-周围区块最低平均海拔+底层相对周围最低海拔最低距离)/底层相对周围最低海拔最低距离;
                return super.drawHeat(quanta*修饰比,pos)/修饰比;
            }
        }
        return super.drawHeat(quanta, pos);
    }

    public void updateAltitude(Chunk chunk){
        AtmosphereWorldInfo worldInfo = this.atmosphere.getAtmosphereWorldInfo();
        if(!worldInfo.isWorldClosed()) setAltitude(Altitude.getMiddleHeight(chunk));
        else setAltitude(worldInfo.getWorld().getSeaLevel());
    }

    public void updateNeighborAltitudeInfo(Map<EnumFacing, Triple<Atmosphere,Chunk,EnumFacing>>  neighbors){
        周围区块最高平均海拔 = altitude.get();
        周围区块最低平均海拔 = altitude.get();
        for(Triple<Atmosphere,Chunk,EnumFacing> neighbor:neighbors.values()){
            周围区块最高平均海拔 = Math.max(周围区块最高平均海拔,neighbor.getLeft().getUnderlying().getAltitude().get());
            周围区块最低平均海拔 = Math.min(周围区块最低平均海拔,neighbor.getLeft().getUnderlying().getAltitude().get());
        }
    }

    /**
     * 更新下垫面属性
     * @param chunk 下垫面所属区块
     * @return 自身
     */
    @Override
    public Underlying load(Chunk chunk) {
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
                        double thisReflectivity = GeoBlockSetting.getBlockReflectivity(state)*(1-upReflectivity);
                        averageReflectivity += thisReflectivity;
                        upReflectivity += thisReflectivity;
                        canSunReach = false;
                    }
                    int C = GeoBlockSetting.getBlockHeatCapacity(state);
                    blockC += C;
                    if(FluidUtil.isFluid(state) && height > 0){
                        blockC += C * getSameLiquidDepth(chunk, x, height - 1, z, FluidUtil.getFluid(state),5);
                        break;
                    }
                    if(!state.isOpaqueCube()){
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
        reflectivityState.reflectivity = 平均返照率 = averageReflectivity / 256;
        heatCapacityState.heatCapacity = this.heatCapacity = heatCapacity;
        if(upperLayer != null) upperLayer.setLowerLayer(this); //刷新缓存高度
        return this;
    }

    @Override
    public void onLoad(Chunk chunk) {
        updateAltitude(chunk);
        周围区块最低平均海拔 = 周围区块最高平均海拔 = altitude.get();
        super.onLoad(chunk);
    }

    @Override
    public void onLoadWithoutChunk() {
        周围区块最低平均海拔 = 周围区块最高平均海拔 = altitude.get();
        super.onLoadWithoutChunk();
    }

    @Override
    public boolean isInitialise() {
        return this.altitudeState.isInitialised() && super.isInitialise();
    }

    @Override
    public void tick(@Nullable Chunk chunk, Map<EnumFacing, Triple<Atmosphere,Chunk,EnumFacing>> neighbors,int x,int z) {
        if(!afterFirstTick){
            updateNeighborAltitudeInfo(neighbors);
            afterFirstTick = true;
        }
        if(altitude.get() <= 0){
            return; //空的，下垫面都没有
        }
        if(atmosphere.getAtmosphereWorldInfo().isTemperatureConstant()) return;

        if(!atmosphere.getAtmosphereWorldInfo().isWorldClosed() && atmosphere.tickTime()+x+5L*z % GeoAtmosphereSetting.getUnderlyingReloadGap() == 0 ){
            if(chunk != null) load(chunk);
            updateNeighborAltitudeInfo(neighbors);
        }

        double 地面长波辐射 = AtmosphereUtil.FinalFactors.每秒损失能量常数 * Math.pow(temperature.get(), 4)* GeoAtmosphereSetting.getSimulationGap();
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

        //更新地底温度
        deepTemperature.set(deepTemperature.get()*(1-地底温度受地表影响系数)+地底温度受地表影响系数*temperature.get());
    }

    @Override
    public void sendHeat(HeatPack pack, @Nullable Vec3d direction) {
        if(direction == null || pack.getType() == null || direction.y == 0){
            this.putHeat(pack.getAmount(),null);
            return;
        }
        if(direction.y<0){
            switch (pack.getType()){
                case SHORT_WAVE:
                    temperature.add热量(pack.drawHeat(pack.getAmount()*(1-平均返照率)),heatCapacity);
                    break;
                case LONG_WAVE:
                    temperature.add热量(pack.drawHeat(pack.getAmount()),heatCapacity);
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
    public TemperatureState getTemperature() {
        return temperature;
    }

    @Override
    public float getTemperature(BlockPos pos) {
        if(pos.getY()<=周围区块最低平均海拔-底层相对周围最低海拔最低距离+1e-2){
            double 深度 = Altitude.to物理高度((周围区块最低平均海拔-底层相对周围最低海拔最低距离)-pos.getY());
            return (float) (deepTemperature.get()+深度* AtmosphereUtil.FinalFactors.地下温度直增率);
        }
        if(pos.getY() <= 周围区块最低平均海拔){
            double upTemp = (altitude.get()-底层相对周围最低海拔最低距离<周围区块最低平均海拔)?temperature.get():
            temperature.get()+Altitude.to物理高度(altitude.get()-底层相对周围最低海拔最低距离-周围区块最低平均海拔)*AtmosphereUtil.FinalFactors.对流层温度直减率;
            return (float) ((upTemp-deepTemperature.get())*(pos.getY()-周围区块最低平均海拔+底层相对周围最低海拔最低距离)/底层相对周围最低海拔最低距离+deepTemperature.get());
        }
        if(pos.getY() >= 周围区块最低平均海拔 && (pos.getY() < altitude.get()-底层相对周围最低海拔最低距离)){
            return (float) (temperature.get()+Altitude.to物理高度(altitude.get()-底层相对周围最低海拔最低距离-pos.getY())* AtmosphereUtil.FinalFactors.对流层温度直减率);
        }
        if(pos.getY() > 周围区块最高平均海拔){
            return (float)
                    Math.max(temperature.get()-(周围区块最高平均海拔-altitude.get())* AtmosphereUtil.FinalFactors.对流层温度直减率*32
                            - (pos.getY()-周围区块最高平均海拔)* AtmosphereUtil.FinalFactors.对流层温度直减率*64
                            , TemperatureProperty.MIN);
        }
        if(pos.getY()<=altitude.get()) return temperature.get();
        double 高差 = pos.getY()-altitude.get();
        return (float) Math.max(temperature.get()-高差* AtmosphereUtil.FinalFactors.对流层温度直减率*32, TemperatureProperty.MIN);
    }

    @Override
    public String getTagName() {
        return "g";
    }

    @Override
    public boolean isSerializable() {
        return true;
    }

    protected double 获取上面平均风速(){
        if(!(upperLayer instanceof AtmosphereLayer)) return 0;
        AtmosphereLayer layer = (AtmosphereLayer) upperLayer;
        double wind = layer.getWind(new BlockPos(4,altitude.get(),4)).length()+layer.getWind(new BlockPos(4,altitude.get(),8)).length()
                +layer.getWind(new BlockPos(8,altitude.get(),4)).length()+layer.getWind(new BlockPos(8,altitude.get(),8)).length();
        return wind/4;
    }
}
