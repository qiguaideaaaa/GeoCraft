package top.qiguaiaaaa.fluidgeography.atmosphere.model;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.Underlying;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.model.IAtmosphereModel;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.property.AtmosphereProperty;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.state.AtmosphereStates;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.state.IAtmosphereState;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.state.TemperatureState;
import top.qiguaiaaaa.fluidgeography.api.configs.AtmosphereConfig;
import top.qiguaiaaaa.fluidgeography.api.util.ChunkUtil;
import top.qiguaiaaaa.fluidgeography.api.util.math.Altitude;
import top.qiguaiaaaa.fluidgeography.api.util.math.ExtendedChunkPos;
import top.qiguaiaaaa.fluidgeography.api.util.AtmosphereUtil.FinalFactors;
import top.qiguaiaaaa.fluidgeography.atmosphere.AtmospherePropertyManager;
import top.qiguaiaaaa.fluidgeography.atmosphere.DefaultAtmosphere;

import java.util.EnumMap;
import java.util.Map;

import static top.qiguaiaaaa.fluidgeography.api.util.AtmosphereUtil.get低层大气密度;
import static top.qiguaiaaaa.fluidgeography.api.util.AtmosphereUtil.get大气透过率;

public class DefaultAtmosphereModel implements IAtmosphereModel {
    @Override
    public AtmosphereStates run(Atmosphere atmosphere, AtmosphereStates states, Chunk chunk) {
        ExtendedChunkPos chunkPos = new ExtendedChunkPos(chunk.x,chunk.z);
        final Map<EnumFacing,Triple<Atmosphere,Chunk,EnumFacing>> neighbors = new EnumMap<>(EnumFacing.class);
        final WorldServer world = atmosphere.getAtmosphereWorldInfo().getWorld();

        final Map<EnumFacing,Vec3d> winds = states.getWinds();
        final long tickTimes = atmosphere.tickTime();
        Underlying underlying = states.get下垫面();

        for(EnumFacing facing: ChunkUtil.HORIZONTALS){
            ExtendedChunkPos facingPos = chunkPos.offset(facing);
            if(!world.isAreaLoaded(facingPos.getBlock(8,64,8),1)) continue;
            Chunk neighborChunk = world.getChunk(facingPos.x,facingPos.z);
            if(!neighborChunk.isLoaded()) continue;
            Atmosphere neighborAtmosphere = neighborChunk.getCapability(DefaultAtmosphere.LOWER_ATMOSPHERE,null);
            if(neighborAtmosphere == null) continue;
            if(!neighborAtmosphere.isInitialised()) return states; // 保证拟真，还没完全初始化的话就不要模拟了
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
                tickTimes % AtmosphereConfig.ATMOSPHERE_UNDERLYING_RECALCULATE_GAP.getValue().value == 0 ){
            underlying = ChunkUtil.getUnderlying(chunk,underlying.get地面平均海拔());
            states.set下垫面(underlying);
            states.update低层大气热容();
        }

        //更新状态
        for(IAtmosphereState state:states.getStates()){
            state.onUpdate(atmosphere,chunk);
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
        double 透过率 = get大气透过率(atmosphere, atmosphere.getAtmosphereWorldInfo().getWorld().getWorldInfo());

        double 辐射冷却 = (1.0 - 透过率) * (FinalFactors.海平面密度 / (当前密度 + 0.01)) * 0.5;
        高层温度 -= 辐射冷却;
        return (float) 高层温度;
    }

    @Override
    public float getInitTemperature(Atmosphere atmosphere, Chunk chunk) {
        return TemperatureState.calculateBaseTemperature(chunk, atmosphere.get下垫面());
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
}
