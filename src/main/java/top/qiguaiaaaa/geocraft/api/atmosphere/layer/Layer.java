package top.qiguaiaaaa.geocraft.api.atmosphere.layer;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.INBTSerializable;
import org.apache.commons.lang3.tuple.Triple;
import top.qiguaiaaaa.geocraft.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.geocraft.api.property.GeographyProperty;
import top.qiguaiaaaa.geocraft.api.property.TemperatureProperty;
import top.qiguaiaaaa.geocraft.api.atmosphere.raypack.HeatPack;
import top.qiguaiaaaa.geocraft.api.state.FluidState;
import top.qiguaiaaaa.geocraft.api.state.GeographyState;
import top.qiguaiaaaa.geocraft.api.state.TemperatureState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

/**
 * 层级，表示一个竖直方向上的层
 * 按照规范，一个区块的层级应当从上到下分别为：<br/>
 * 若干个大气层级{@link AtmosphereLayer}，可能没有大气<br/>
 * 下垫面层级{@link UnderlyingLayer}，必须要有<br/>
 * 如未说明，所有的能量单位均为Forge Energe（FE)，模组内默认1 FE = 1 J （现实单位）
 */
public interface Layer extends INBTSerializable<NBTTagCompound> {
    /**
     * 初始化时调用
     * @param chunk 层级所在区块
     */
    void onLoad(@Nonnull Chunk chunk);

    /**
     * 区块不在加载状态时加载层级
     */
    void onLoadWithoutChunk();

    /**
     * 该层级是否已经初始化
     * @return 若已经完成初始化，则为true
     */
    boolean isInitialise();

    /**
     * 层级更新
     * @param chunk 层级所属区块,若区块无加载则为null
     * @param neighbors 邻居区块和大气信息,若区块无加载则Triple里面的区块信息为null
     * @param x 区块坐标X
     * @param z 区块坐标Z
     */
    void tick(@Nullable Chunk chunk, @Nonnull Map<EnumFacing, Triple<Atmosphere,Chunk,EnumFacing>> neighbors, int x, int z);

    /**
     * 向该层提供热量
     * @param quanta 热量，单位FE
     * @param pos 热源位置
     */
    void putHeat(double quanta,@Nullable BlockPos pos);

    /**
     * 向该层发送能量
     * @param pack 能量包
     * @param direction 能量发送朝向
     *                  向上为使能量逐步向上层传播
     *                  向下为使能量逐步向下层传播
     *                  null为能量没有特定朝向，即全部被该层大气吸收
     */
    void sendHeat(@Nonnull HeatPack pack, @Nullable EnumFacing direction);

    /**
     * 向该层发送能量
     * @param pack 能量包
     * @param direction 能量发送朝向向量
     */
    void sendHeat(@Nonnull HeatPack pack, @Nullable Vec3i direction);

    /**
     * 向该层发送能量
     * @param pack 能量包
     * @param direction 能量发送朝向向量
     */
    void sendHeat(@Nonnull HeatPack pack, @Nullable Vec3d direction);

    /**
     * 从该层吸收能量
     * @param quanta 吸收的量
     */
    double drawHeat(double quanta,@Nullable BlockPos pos);

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

    default double getTopY(){
        return getBeginY()+getDepth();
    }

    /**
     * 获得该层下面的层
     * @return 下面的层。若已经是最下层（比如下垫面），则返回null
     */
    @Nullable
    Layer getLowerLayer();

    /**
     * 获得该层大气上面的层
     * @return 上面的层。若已经是最上层，则返回null
     */
    @Nullable
    Layer getUpperLayer();

    /**
     * 设置该层下面的层
     * @param layer 下面的层
     */
    void setLowerLayer(@Nullable Layer layer);

    /**
     * 设置该层上面的层
     * @param layer 上面的层
     */
    void setUpperLayer(@Nullable Layer layer);

    /**
     * 获取该层所在位置的大气
     * @return 大气
     */
    Atmosphere getAtmosphere();

    /**
     * 获取该层级的温度状态
     * @return 温度状态
     */
    TemperatureState getTemperature();

    /**
     * 获得某处的温度
     * @param pos 某处
     * @return 温度
     */
    float getTemperature(BlockPos pos);

    /**
     * 获取该层级指定的温度状态
     * @param property 温度状态对应属性
     * @return 温度状态
     */
    @Nullable
    TemperatureState getTemperature(TemperatureProperty property);

    /**
     * 获取该层级的液态水水量状态
     * @return 液态水水量状态,不一定存在
     */
    @Nullable
    FluidState getWater();

    /**
     * 获取该层的总热容
     * @return 总热容，即使整个层级平均温度升高1度所需能量
     */
    double getHeatCapacity();

    /**
     * 获得状态
     * @param property 状态对应的属性
     * @return 状态
     */
    @Nullable
    GeographyState getState(@Nonnull GeographyProperty property);
    /**
     * 添加或覆盖状态
     * @param property 属性
     * @return 如果存在旧状态,则返回.否则返回Null
     */
    @Nullable
    GeographyState addState(@Nonnull GeographyProperty property);

    /**
     * 返回该层序列化的复合标签的标签名称
     * @return 一个标签名称
     */
    String getTagName();

    boolean isSerializable();

    /**
     * 返回该层序列化后的复合标签
     * @return 一个复合标签,表示该层的状态
     */
    @Override
    NBTTagCompound serializeNBT();

    /**
     * 将对应代表层的复合标签解序列化
     * @param nbt 上级向本层提供的复合标签
     */
    @Override
    void deserializeNBT(NBTTagCompound nbt);
}
