package top.qiguaiaaaa.fluidgeography.atmosphere.model;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.WorldInfo;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.AtmosphereSystemManager;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.Underlying;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.state.AtmosphereStates;
import top.qiguaiaaaa.fluidgeography.api.configs.AtmosphereConfig;
import top.qiguaiaaaa.fluidgeography.api.util.AtmosphereUtil;
import top.qiguaiaaaa.fluidgeography.api.util.ChunkUtil;
import top.qiguaiaaaa.fluidgeography.api.util.math.ExtendedChunkPos;

import java.util.EnumMap;
import java.util.Map;

import static top.qiguaiaaaa.fluidgeography.api.util.AtmosphereUtil.*;

public class NewAtmosphereModel extends DefaultAtmosphereModel {
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
            Underlying 新下垫面 = ChunkUtil.getUnderlying(chunk,atmosphere.get下垫面().get地面平均海拔());
            states.set下垫面(新下垫面);
        }

        return states;
    }
    public static void calculateTemperature(Atmosphere atmosphere){
        WorldInfo worldInfo = atmosphere.getAtmosphereWorldInfo().getWorld().getWorldInfo();
        Underlying 下垫面 = atmosphere.get下垫面();
        double 太阳辐射透过率 = get大气透过率(atmosphere,worldInfo);
        double 太阳短波辐射 = getSunEnergyPerChunk(worldInfo)*(1-下垫面.平均返照率)*太阳辐射透过率;

        // 地面长波辐射
        double 地面辐射损失系数 = AtmosphereConfig.GROUND_RADIATION_LOSS_RATE.getValue().value;
        double 地面长波辐射 = 每大气刻损失能量常数 * Math.pow(atmosphere.get地表温度(), 4) * 下垫面.平均发射率*地面辐射损失系数;

        // 云层和大气的辐射
        double 吸收系数 = 0.01;
        double 云量 = 1-太阳辐射透过率;
        double 云层回辐射 = 每大气刻损失能量常数 * Math.pow(atmosphere.get低层大气温度(), 4) * 云量;
        double 云层高度 = 1500;
        double 云层回辐射到达地面比例 = 0.5 * Math.exp(
                        -吸收系数 *
                        AtmosphereUtil.get低层大气密度(下垫面.get地面平均海拔().get物理海拔()+云层高度)
                        * 云层高度)
                * (1 - 0.5 * 云量);

        // 大气辐射
        double 大气发射率 = get大气发射率(atmosphere);
        double 大气辐射 = 每大气刻损失能量常数 * Math.pow(atmosphere.get低层大气温度(), 4) * 大气发射率 * (1.0 - 云量);
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
    public static double getAtmosphereTemperatureLoss(Atmosphere atmosphere){
        WorldInfo worldInfo = atmosphere.getAtmosphereWorldInfo().getWorld().getWorldInfo();
        Underlying 下垫面 = atmosphere.get下垫面();
        double 太阳辐射透过率 = get大气透过率(atmosphere,worldInfo);
        double 太阳短波辐射 = getSunEnergyPerChunk(worldInfo)*(1-下垫面.平均返照率)*太阳辐射透过率;

        // 地面长波辐射
        double 地面辐射损失系数 = AtmosphereConfig.GROUND_RADIATION_LOSS_RATE.getValue().value;
        double 地面长波辐射 = 每大气刻损失能量常数 * Math.pow(atmosphere.get地表温度(), 4) * 下垫面.平均发射率*地面辐射损失系数;

        // 云层和大气的辐射
        double 吸收系数 = 0.01;
        double 云量 = 1-太阳辐射透过率;
        double 云层回辐射 = 每大气刻损失能量常数 * Math.pow(atmosphere.get低层大气温度(), 4) * 云量;
        double 云层高度 = 1500;
        double 云层回辐射到达地面比例 = 0.5 * Math.exp(
                -吸收系数 *
                        AtmosphereUtil.get低层大气密度(下垫面.get地面平均海拔().get物理海拔()+云层高度)
                        * 云层高度)
                * (1 - 0.5 * 云量);

        // 大气辐射
        double 大气发射率 = get大气发射率(atmosphere);
        double 大气辐射 = 每大气刻损失能量常数 * Math.pow(atmosphere.get低层大气温度(), 4) * 大气发射率 * (1.0 - 云量);
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
        return (-大气净辐射损失)/atmosphere.get低层大气热容();
    }
    public static double getGroundTemperatureLoss(Atmosphere atmosphere){
        WorldInfo worldInfo = atmosphere.getAtmosphereWorldInfo().getWorld().getWorldInfo();
        Underlying 下垫面 = atmosphere.get下垫面();
        double 太阳辐射透过率 = get大气透过率(atmosphere,worldInfo);
        double 太阳短波辐射 = getSunEnergyPerChunk(worldInfo)*(1-下垫面.平均返照率)*太阳辐射透过率;

        // 地面长波辐射
        double 地面辐射损失系数 = AtmosphereConfig.GROUND_RADIATION_LOSS_RATE.getValue().value;
        double 地面长波辐射 = 每大气刻损失能量常数 * Math.pow(atmosphere.get地表温度(), 4) * 下垫面.平均发射率*地面辐射损失系数;

        // 云层和大气的辐射
        double 吸收系数 = 0.01;
        double 云量 = 1-太阳辐射透过率;
        double 云层回辐射 = 每大气刻损失能量常数 * Math.pow(atmosphere.get低层大气温度(), 4) * 云量;
        double 云层高度 = 1500;
        double 云层回辐射到达地面比例 = 0.5 * Math.exp(
                -吸收系数 *
                        AtmosphereUtil.get低层大气密度(下垫面.get地面平均海拔().get物理海拔()+云层高度)
                        * 云层高度)
                * (1 - 0.5 * 云量);

        // 大气辐射
        double 大气发射率 = get大气发射率(atmosphere);
        double 大气辐射 = 每大气刻损失能量常数 * Math.pow(atmosphere.get低层大气温度(), 4) * 大气发射率 * (1.0 - 云量);
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
        return (太阳短波辐射-地面净辐射损失)/atmosphere.get下垫面().热容;
    }
}
