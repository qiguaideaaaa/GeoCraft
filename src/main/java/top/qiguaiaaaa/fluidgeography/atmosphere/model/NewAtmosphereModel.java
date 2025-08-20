package top.qiguaiaaaa.fluidgeography.atmosphere.model;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.WorldInfo;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.AtmosphereSystemManager;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.Underlying;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.AtmosphereStates;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.model.IAtmosphereModel;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.property.AtmosphereProperty;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.property.TemperatureProperty;
import top.qiguaiaaaa.fluidgeography.api.configs.AtmosphereConfig;
import top.qiguaiaaaa.fluidgeography.api.util.AtmosphereUtil;
import top.qiguaiaaaa.fluidgeography.api.util.ChunkUtil;
import top.qiguaiaaaa.fluidgeography.api.util.math.Altitude;
import top.qiguaiaaaa.fluidgeography.api.util.math.Degree;
import top.qiguaiaaaa.fluidgeography.api.util.math.ExtendedChunkPos;
import top.qiguaiaaaa.fluidgeography.atmosphere.AtmospherePropertyManager;
import top.qiguaiaaaa.fluidgeography.atmosphere.state.LowerAtmosphereTemperatureState;

import java.util.EnumMap;
import java.util.Map;

import static top.qiguaiaaaa.fluidgeography.api.util.AtmosphereUtil.*;

public class NewAtmosphereModel implements IAtmosphereModel {
    @Override
    public AtmosphereStates run(Atmosphere atmosphere, AtmosphereStates states, Chunk chunk) {
        ExtendedChunkPos chunkPos = new ExtendedChunkPos(chunk.x,chunk.z);
        final Map<EnumFacing, Triple<Atmosphere,Chunk,EnumFacing>> neighbors = new EnumMap<>(EnumFacing.class);
        final WorldServer world = atmosphere.getAtmosphereWorldInfo().getWorld();
        final Map<EnumFacing, Vec3d> winds = states.getWinds();

        //大气热量变化
        if(!atmosphere.getAtmosphereWorldInfo().isTemperatureConstant()){
            calculateTemperature(atmosphere);
        }
        //预处理邻居大气
        for(EnumFacing facing: ChunkUtil.HORIZONTALS){
            ExtendedChunkPos facingPos = chunkPos.offset(facing);
            if(!world.isAreaLoaded(facingPos.getBlock(8,64,8),1)) continue;
            Chunk neighborChunk = world.getChunk(facingPos.x,facingPos.z);
            Atmosphere neighborAtmosphere = AtmosphereSystemManager.getAtmosphere(neighborChunk);
            if(neighborAtmosphere == null) continue;
            Triple<Atmosphere,Chunk,EnumFacing> triple = new ImmutableTriple<>(neighborAtmosphere,neighborChunk,facing);
            neighbors.put(facing,triple);
        }
        // **** 计算风速 ****
        for(Triple<Atmosphere,Chunk,EnumFacing> neighbor:neighbors.values()){
            EnumFacing direction = neighbor.getRight();
            Vec3d newWindSpeed = getSingleDirectionWindSpeed(atmosphere,neighbor.getLeft(),direction);
            winds.put(direction,newWindSpeed);
        }
        // **** 更新属性 ****
        if(!neighbors.isEmpty()){ //主动扩散
            for(Triple<Atmosphere,Chunk,EnumFacing> neighbor:neighbors.values()){
                flowProperties(atmosphere,states,chunk,neighbor);
            }
        }
        //计算下垫面性质
        if(!atmosphere.getAtmosphereWorldInfo().isWorldClosed() &&
                atmosphere.tickTime() % AtmosphereConfig.ATMOSPHERE_UNDERLYING_RECALCULATE_GAP.getValue().value == 0 ){
            Underlying 新下垫面 = Underlying.getUnderlying(chunk,atmosphere.get下垫面().get地面平均海拔());
            states.set下垫面(新下垫面);
        }

        return states;
    }
    @Override
    public double getPressure(Atmosphere atmosphere) {
        return FinalFactors.海平面气压 *
                Math.exp(
                        -FinalFactors.干空气摩尔质量 *
                                FinalFactors.重力加速度 *
                                atmosphere.get下垫面().get地面平均海拔().get物理海拔() /
                                (FinalFactors.气体常数 * atmosphere.get低层大气温度())
                );
    }

    @Override
    public double getPressure(Atmosphere atmosphere, Altitude altitude) {
        return FinalFactors.海平面气压 *
                Math.exp(
                        -FinalFactors.干空气摩尔质量 *
                                FinalFactors.重力加速度 *
                                altitude.get物理海拔() /
                                (FinalFactors.气体常数 * getTemperature(atmosphere,altitude))
                );
    }

    @Override
    public float getTemperature(Atmosphere atmosphere, Altitude altitude) {
        double 物理海拔 = altitude.get物理海拔();
        double 地面海拔 = atmosphere.get下垫面().get地面平均海拔().get物理海拔();
        float 低层温度 = atmosphere.get低层大气温度();
        double 高度差 = 物理海拔 - 地面海拔;
        if (高度差 <= FinalFactors.低层大气厚度) {
            double 温度 = 低层温度 - FinalFactors.对流层温度直减率 * 高度差;
            return (float) 温度;
        }
        double 低层顶温度 = 低层温度 - FinalFactors.对流层温度直减率 * FinalFactors.低层大气厚度;

        // 计算高层气温递减
        double 高层高度差 = 高度差 - FinalFactors.低层大气厚度;
        double 高层直减率 = FinalFactors.对流层温度直减率 * 0.5; // 高层递减率较低
        double 高层温度 = 低层顶温度 - 高层直减率 * 高层高度差;

        // 辐射冷却影响
        double 当前密度 = get低层大气密度(物理海拔);
        double 透过率 = get大气透过率(atmosphere);

        double 辐射冷却 = (1.0 - 透过率) * (FinalFactors.海平面密度 / (当前密度 + 0.01)) * 0.5;
        高层温度 -= 辐射冷却;
        return (float) 高层温度;
    }

    @Override
    public float getInitTemperature(Atmosphere atmosphere, Chunk chunk) {
        return LowerAtmosphereTemperatureState.calculateBaseTemperature(chunk, atmosphere.get下垫面());
    }

    @Override
    public Vec3d getWind(Atmosphere atmosphere, BlockPos pos) {
        int x = pos.getX() & 15;
        int z = pos.getZ() & 15;
        double weightS = z/16.0,
                weightN = 1-weightS,
                weightE = x/16.0,
                weightW = 1-weightE;
        return atmosphere.getWindSpeed(EnumFacing.SOUTH).scale(weightS)
                .add(atmosphere.getWindSpeed(EnumFacing.NORTH).scale(weightN))
                .add(atmosphere.getWindSpeed(EnumFacing.EAST).scale(weightE))
                .add(atmosphere.getWindSpeed(EnumFacing.WEST).scale(weightW));
    }

    @Override
    public double getWaterEvaporatePossibility(Atmosphere atmosphere, BlockPos pos) {
        double temp = atmosphere.get温度(pos,true);
        if(temp>= TemperatureProperty.BOILED_POINT) return 1;
        double possibility = 1.0d;
        possibility *= Math.pow(1.07,temp - TemperatureProperty.ICE_POINT)/Math.pow(1.07,100);
        possibility *= Math.sqrt(1-Math.pow(atmosphere.get水量()/1024.0,2)/1000.0);
        possibility *= 0.5;
        return possibility;
    }

    @Override
    public double getRainPossibility(Atmosphere atmosphere, BlockPos pos) {
        float temp = atmosphere.get温度(pos,true);
        if(temp>= TemperatureProperty.BOILED_POINT) return 0;
        if(temp<= TemperatureProperty.ICE_POINT-100) return 1;
        double strong = atmosphere.getRainStrong();
        return strong/(strong+16384);
    }

    @Override
    public double getFreezePossibility(Atmosphere atmosphere, BlockPos pos) {
        float temp = atmosphere.get温度(pos,false);
        if(temp>= TemperatureProperty.ICE_POINT) return 0;
        if(temp< TemperatureProperty.ICE_POINT-100) return 1;
        float diff = TemperatureProperty.ICE_POINT-temp;
        return (diff/100)*0.94f+0.06f;
    }

    @Override
    public double get低层大气发射率(Atmosphere atmosphere) {
        double 水汽压 = get大气水汽压(atmosphere)*0.01; // hPa
        double 云量 = 1- get大气透过率(atmosphere);
        double 海拔 = atmosphere.get下垫面().get地面平均海拔().get物理海拔();

        // 基础发射率
        double 发射率 = 0.74 + 0.0049 * 水汽压;

        // 云修正
        发射率 *= (1 + 0.22 * 云量);

        // 海拔修正
        double 尺度高度 = 8000.0;
        double 海拔因子 = Math.exp(-海拔 / 尺度高度);
        发射率 *= 海拔因子;

        // 限制范围
        return MathHelper.clamp(发射率, 0.01, 1.0);
    }

    @Override
    public double get大气透过率(Atmosphere atmosphere) {
        WorldInfo worldInfo = atmosphere.getAtmosphereWorldInfo().getWorld().getWorldInfo();
        double strong = atmosphere.getRainStrong(); // 0 = 无云, ~100 = 极强降雨
        Degree sunHeight = getSunHeight(worldInfo);

        double sunSin = Math.sin(sunHeight.getRadian());
        sunSin = Math.max(sunSin, 0.0001); // 避免日出日落趋近于0

        // 基础光学厚度（云量贡献）
        // 在无雨时, strong=0 → τ接近0, 在大雨时 τ很大
        double 云量贡献;
        if (worldInfo.isRaining()) {
            云量贡献 = 0.3 + (strong / (strong + 5.0)) * 3.0; // 厚云 + 雨滴
        } else {
            云量贡献 = (strong / (strong + 30.0)) * 2.0; // 稀薄云层
        }

        // 路径长度修正: 太阳越低，光路径越长 → 削弱更多
        double pathFactor = 1.0 / sunSin;

        // Beer–Lambert 定律: 透过率 = exp(-τ * 路径系数)
        double transmittance = Math.exp(-云量贡献 * pathFactor);

        // 限制范围在 [0,1]
        return Math.max(0.0, Math.min(1.0, transmittance));
    }

    protected Vec3d getSingleDirectionWindSpeed(Atmosphere from, Atmosphere to, EnumFacing dir){
        Vec3d wind = Vec3d.ZERO;
        for(AtmosphereProperty property: AtmospherePropertyManager.getWindEffectedProperties()){
            wind = wind.add(property.getWind(from,to,dir));
        }
        return wind;
    }
    protected void flowProperties(Atmosphere from,AtmosphereStates states,Chunk chunk, Triple<Atmosphere,Chunk,EnumFacing> neighbor){
        for(AtmosphereProperty property: AtmospherePropertyManager.getFlowableProperties()){
            property.onAtmosphereFlow(from,chunk,neighbor.getLeft(),neighbor.getMiddle(),neighbor.getRight(),states.getWinds().get(neighbor.getRight()));
        }
    }
    public void calculateTemperature(Atmosphere atmosphere){
        WorldInfo worldInfo = atmosphere.getAtmosphereWorldInfo().getWorld().getWorldInfo();
        Underlying 下垫面 = atmosphere.get下垫面();
        double 太阳辐射透过率 = get大气透过率(atmosphere);
        double 太阳短波辐射 = getSunEnergyPerChunk(worldInfo)*(1-下垫面.平均返照率)*太阳辐射透过率;

        // 地面长波辐射
        double 地面辐射损失系数 = AtmosphereConfig.GROUND_RADIATION_LOSS_RATE.getValue().value;
        double 地面长波辐射 = FinalFactors.每大气刻损失能量常数 * Math.pow(atmosphere.get地表温度(), 4) * 下垫面.平均发射率*地面辐射损失系数;

        // 云层和大气的辐射
        double 吸收系数 = 0.01;
        double 云量 = 1-太阳辐射透过率;
        double 云层回辐射 = FinalFactors.每大气刻损失能量常数 * Math.pow(atmosphere.get低层大气温度(), 4) * 云量;
        double 云层高度 = 1500;
        double 云层回辐射到达地面比例 = 0.5 * Math.exp(
                        -吸收系数 *
                        AtmosphereUtil.get低层大气密度(下垫面.get地面平均海拔().get物理海拔()+云层高度)
                        * 云层高度)
                * (1 - 0.5 * 云量);

        // 大气辐射
        double 大气发射率 = get低层大气发射率(atmosphere);
        double 大气辐射 = FinalFactors.每大气刻损失能量常数 * Math.pow(atmosphere.get低层大气温度(), 4) * 大气发射率 * (1.0 - 云量);
        double 大气回辐射到达地面比例 = 0.5 * Math.exp(
                -吸收系数 *
                        AtmosphereUtil.get低层大气密度(下垫面.get地面平均海拔().get物理海拔()+ FinalFactors.低层大气厚度)
                        * 云层高度)
                * FinalFactors.低层大气厚度
                * (1 - 0.5 * 云量);

        double 地面净辐射损失 = (地面长波辐射 - (云层回辐射*云层回辐射到达地面比例 + 大气辐射*大气回辐射到达地面比例));
        double 大气净辐射损失 = 云层回辐射+大气辐射-
                地面长波辐射*AtmosphereUtil.大气吸收系数(
                        FinalFactors.低层大气厚度,
                        get低层大气平均密度(下垫面.get地面平均海拔().get物理海拔()),
                        1);

        atmosphere.add地表热量(太阳短波辐射-地面净辐射损失);
        atmosphere.add低层大气热量(-大气净辐射损失);
    }

}
