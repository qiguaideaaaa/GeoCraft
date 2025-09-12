package top.qiguaiaaaa.geocraft.api.atmosphere.storage;

import net.minecraft.world.MinecraftException;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;

/**
 * 从系统存储或其他地方加载大气数据的东西
 */
public interface IAtmosphereDataLoader {
    /**
     * 加载指定区块的大气数据
     * @param worldIn 区块所在世界
     * @param x 区块X坐标
     * @param z 区块Z坐标
     * @return 加载的大气数据，可能为null
     * @throws IOException 若出现IO异常，则抛出
     */
    @Nullable
    AtmosphereData loadAtmosphereData(@Nonnull World worldIn, int x, int z) throws IOException;

    /**
     * 保存指定大气数据
     * @param worldIn 大气数据所在世界
     * @param data 需要保存的大气数据
     * @throws MinecraftException 若已有另一个Minecraft实例锁定了存档，则抛出
     * @throws IOException 若出现IO异常，则抛出
     */
    void saveAtmosphereData(@Nonnull World worldIn,@Nonnull AtmosphereData data) throws MinecraftException, IOException;

    /**
     * 检查指定区块的大气是否存在，注意该方法不是检查指定区块的大气是否已加载
     * @param x 区块X坐标
     * @param z 区块Z坐标
     * @return 若存在，则返回true
     */
    boolean doesAtmosphereExistsAt(int x, int z);

    /**
     * 刷新该Loader使得大气数据被立刻（同步）保存或（同步）加载
     */
    void flush();
}
