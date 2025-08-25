package top.qiguaiaaaa.fluidgeography.api.atmosphere.layer;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.INBTSerializable;
import org.apache.commons.lang3.tuple.Triple;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.property.AtmosphereProperty;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.property.GasProperty;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.property.TemperatureProperty;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.raypack.HeatPack;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.state.GasState;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.state.IAtmosphereState;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.state.TemperatureState;

import javax.annotation.Nullable;
import java.util.Map;

public interface AtmosphereLayer extends INBTSerializable<NBTTagCompound> {
    /**
     * 大气初始化时调用,使得大气层初始化
     * @param chunk 大气所在区块
     */
    void initialise(Chunk chunk);
    /**
     * 该大气层级是否已经初始化
     * @return 若已经完成初始化，则为true
     */
    boolean isInitialise();

    /**
     * 大气层级更新
     * @param chunk 大气层级所属区块
     * @param neighbors 邻居大气
     */
    void tick(Chunk chunk, Map<EnumFacing, Triple<Atmosphere,Chunk,EnumFacing>> neighbors);

    /**
     * 向该层大气提供热量
     * @param quanta 热量，单位FE
     * @param pos 热源位置
     */
    void putHeat(double quanta,@Nullable BlockPos pos);

    /**
     * 向该层大气发送能量
     * @param pack 能量包
     * @param direction 能量发送朝向
     *                  向上为使能量逐步向上层传播
     *                  向下为使能量逐步向下层传播
     *                  null为能量没有特定朝向，即全部被该层大气吸收
     */
    void sendHeat(HeatPack pack, @Nullable EnumFacing direction);

    /**
     * 向该层大气发送能量
     * @param pack 能量包
     * @param direction 能量发送朝向向量
     */
    void sendHeat(HeatPack pack, @Nullable Vec3i direction);

    /**
     * 向该层大气发送能量
     * @param pack 能量包
     * @param direction 能量发送朝向向量
     */
    void sendHeat(HeatPack pack, @Nullable Vec3d direction);

    /**
     * 从该层大气吸收能量
     * @param quanta 吸收的量
     */
    double drawHeat(double quanta);

    /**
     * 向该层大气添加气态形式的水
     * @param pos 添加位置
     * @param amount 水量，
     * @return 是否成功
     */
    boolean addSteam(@Nullable BlockPos pos,int amount);

    /**
     * 向该层大气添加液态形式的水
     * @param pos 添加位置
     * @param amount 水量，单位mB
     * @return 是否成功
     */
    boolean addWater(@Nullable BlockPos pos,int amount);

    /**
     * 获得某地的气压
     * @param pos 某地
     * @return 大气压，单位帕 Pa
     */
    double getPressure(BlockPos pos);

    /**
     * 获得某位置的大气水汽压
     * @return 大气水汽压，单位帕 Pa
     */
    double getWaterPressure(BlockPos pos);

    /**
     * 获取整体水汽压
     * @return 大气水汽压,单位帕 Pa
     */
    double getWaterPressure();

    /**
     * 获得某处的温度
     * @param pos 位置
     * @param notAir 如果需要获取非气体温度，则为true。否则返回气体温度。
     * @return 温度
     */
    float getTemperature(BlockPos pos,boolean notAir);

    /**
     * 获得某地的风,注意地面层是不会有风的
     * @param pos 位置
     * @return 代表风速的三维向量。若没有风或无风则返回零向量。
     */
    Vec3d getWind(BlockPos pos);

    /**
     * 获得该层开始的Y坐标
     * @return 一个Y坐标，表示该层的最低高度
     */
    double getBeginY();

    /**
     * 获得厚度,单位为方块
     * @return 大气层的厚度,单位为方块
     */
    double getDepth();

    /**
     * 获得该层大气下面的层
     * @return 下面的层。若已经是最下层（比如下垫面），则返回null
     */
    @Nullable
    AtmosphereLayer getLowerLayer();

    /**
     * 获得该层大气上面的层
     * @return 上面的层。若已经是最上层，则返回null
     */
    @Nullable
    AtmosphereLayer getUpperLayer();

    /**
     * 设置该层大气下面的层
     * @param layer 下面的层
     */
    void setLowerLayer(AtmosphereLayer layer);

    /**
     * 设置该层大气上面的层
     * @param layer 上面的层
     */
    void setUpperLayer(AtmosphereLayer layer);

    /**
     * 获取该层大气所在大气
     * @return 大气
     */
    Atmosphere getAtmosphere();

    /**
     * 获取大气成分状态
     * @param property 大气成分
     * @return 对应的大气状态
     */
    @Nullable
    GasState getGas(GasProperty property);

    /**
     * 获取该大气层的温度状态
     * @return 温度状态
     */
    TemperatureState getTemperature();

    /**
     * 获取该大气层指定的温度状态
     * @param property 温度状态对应属性
     * @return 温度状态
     */
    @Nullable
    TemperatureState getTemperature(TemperatureProperty property);

    /**
     * 获取该大气层的总热容
     * @return 总热容，即使整个大气平均温度升高1度所需能量
     */
    double getHeatCapacity();

    /**
     * 获取该大气层的液态水水量状态
     * @return 液态水水量状态,不一定存在
     */
    @Nullable
    GasState getWater();

    /**
     * 获取该大气层的气态水水量状态
     * @return 气态水水量状态,不一定存在
     */
    @Nullable
    GasState getSteam();

    /**
     * 获得大气状态
     * @param property 大气状态对应的属性
     * @return 大气状态
     */
    @Nullable
    IAtmosphereState getState(AtmosphereProperty property);
    /**
     * 添加或覆盖大气状态
     * @param property 大气属性
     * @return 如果存在旧状态,则返回.否则返回Null
     */
    @Nullable
    IAtmosphereState addState(AtmosphereProperty property);

    /**
     * 返回该大气层序列化的复合标签的标签名称
     * @return 一个标签名称
     */
    String getTagName();

    /**
     * 返回该大气层序列化后的复合标签
     * @return 一个复合标签,表示该大气层的状态
     */
    @Override
    NBTTagCompound serializeNBT();

    /**
     * 将对应代表大气层的复合标签解序列化
     * @param nbt 大气区块向本层大气提供的复合标签
     */
    @Override
    void deserializeNBT(NBTTagCompound nbt);
}
