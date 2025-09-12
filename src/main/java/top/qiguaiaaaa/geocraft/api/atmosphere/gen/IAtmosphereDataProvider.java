package top.qiguaiaaaa.geocraft.api.atmosphere.gen;

import net.minecraft.world.chunk.IChunkProvider;
import top.qiguaiaaaa.geocraft.api.atmosphere.storage.AtmosphereData;
import top.qiguaiaaaa.geocraft.api.atmosphere.storage.IAtmosphereDataLoader;
import top.qiguaiaaaa.geocraft.api.atmosphere.system.IAtmosphereSystem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

/**
 * 大气数据提供者，类似{@link IChunkProvider}
 * 用于向{@link IAtmosphereSystem}提供对应区块的{@link AtmosphereData}
 */
public interface IAtmosphereDataProvider {
    /**
     * 获取指定区块已加载的大气数据，该方法不应创建新的大气数据或从磁盘读取未加载的数据
     * @param x 区块X坐标
     * @param z 区块Z坐标
     * @return 一个大气数据，若为null则没有大气已加载
     */
    @Nullable
    AtmosphereData getLoadedAtmosphereData(int x, int z);

    /**
     * 获取当前已经加载的大气集合
     * @return 一个大气数据集合，全部都是已加载的大气数据
     */
    @Nonnull
    Collection<AtmosphereData> getLoadedAtmosphereDataCollection();

    /**
     * 提供指定区块的大气数据，若没有则应当从磁盘读取，还是没有的话应新建数据。
     * @param x 区块X坐标
     * @param z 区块Z坐标
     * @return 一个大气数据，不应为null
     */
    @Nonnull AtmosphereData provideAtmosphereData(int x, int z);

    /**
     * 指定区块的大气是否已经生成，注意可能对应的区块或大气还尚未加载
     * @param x 区块X坐标
     * @param z 区块Z坐标
     * @return 若已经生成，则返回true，否则返回false
     */
    boolean isAtmosphereGeneratedAt(int x, int z);

    /**
     * 将指定区块的大气数据标记为待卸载
     * @param x 区块X坐标
     * @param z 区块Z坐标
     */
    void queueUnloadAtmosphereData(int x,int z);

    /**
     * 直接保存指定区块的大气数据
     * 注意考虑到{@link IAtmosphereDataLoader}的实现，该方法不一定是同步的
     * @param x 区块X坐标
     * @param z 区块Z坐标
     */
    void saveAtmosphereData(int x,int z);

    /**
     * 直接保存所有大气数据
     * 注意考虑到{@link IAtmosphereDataLoader}的实现，该方法不一定是同步的
     */
    void saveAllAtmosphereData();

    /**
     * 每游戏刻调用一次
     * @return 暂时不知道有什么用
     */
    boolean tick();

    /**
     * 将当前{@link IAtmosphereDataProvider}的大气加载信息输出为字符串
     * @return 序列化为字符串的大气加载信息
     */
    @Nonnull
    String makeString();
}
