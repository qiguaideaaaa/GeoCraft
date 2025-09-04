package top.qiguaiaaaa.geocraft.api.atmosphere.system;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import top.qiguaiaaaa.geocraft.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.geocraft.api.atmosphere.AtmosphereWorldInfo;
import top.qiguaiaaaa.geocraft.api.atmosphere.accessor.IAtmosphereAccessor;
import top.qiguaiaaaa.geocraft.api.atmosphere.gen.IAtmosphereDataProvider;

import javax.annotation.Nullable;

public interface IAtmosphereSystem {
    /**
     * 每游戏刻更新，更新大气
     */
    void updateTick();

    /**
     * 当一个新区块被生成的时候调用，以生成区块对应的大气
     * @param chunk 区块
     */
    void onChunkGenerated(Chunk chunk);

    /**
     * 当一个已生成区块被加载时，加载对应的大气
     * @param chunk 区块
     */
    void onChunkLoaded(Chunk chunk);

    /**
     * 当一个加载区块被卸载时，处理对应的大气
     * @param chunk 区块
     */
    void onChunkUnloaded(Chunk chunk);

    /**
     * 保存所有大气
     */
    void saveAllAtmospheres();
    boolean isAtmosphereLoaded(ChunkPos pos);

    /**
     * 设置是否停止大气更新
     * @param status 状态
     */
    void setStop(boolean status);

    /**
     * 大气更新是否已经停止
     * @return 若停止，则返回true
     */
    boolean isStopped();

    /**
     * 获取向该大气系统提供底层大气数据的提供者
     * @return 大气数据提供者
     */
    IAtmosphereDataProvider getDataProvider();

    IAtmosphereAccessor getAccessor();

    /**
     * 获取指定位置的大气
     * @param pos 位置
     * @return 若大气可用，则返回大气。否则返回null
     */
    @Nullable
    Atmosphere getAtmosphere(BlockPos pos);

    /**
     * 获取指定区块的大气
     * @param chunk 区块
     * @return 若大气可用，则返回大气，否则返回null
     */
    @Nullable
    Atmosphere getAtmosphere(Chunk chunk);

    /**
     * 获取指定区块坐标的大气
     * @param x 区块坐标X
     * @param z 区块坐标Z
     * @return 若大气可用，则返回大气，否则返回null
     */
    Atmosphere getAtmosphere(int x,int z);

    AtmosphereWorldInfo getAtmosphereWorldInfo();
}
