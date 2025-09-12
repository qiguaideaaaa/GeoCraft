package top.qiguaiaaaa.geocraft.api.atmosphere.gen;

import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldServer;
import net.minecraft.world.gen.ChunkProviderServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.qiguaiaaaa.geocraft.api.atmosphere.storage.AtmosphereData;
import top.qiguaiaaaa.geocraft.api.atmosphere.storage.IAtmosphereDataLoader;
import top.qiguaiaaaa.geocraft.api.util.APIUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * 类似{@link ChunkProviderServer}，为大气数据提供器
 * 该提供器管理大气数据的加载、保存和卸载操作
 */
public class DefaultAtmosphereDataProvider implements IAtmosphereDataProvider {
    private static final Logger LOGGER = LogManager.getLogger();
    protected final Set<Long> droppedAtmospheres = Sets.newHashSet();
    public final WorldServer world;
    public final IAtmosphereDataLoader atmosphereLoader;
    public final Long2ObjectMap<AtmosphereData> loadedAtmosphere = new Long2ObjectOpenHashMap<>(65536);

    /**
     * 创建一个大气数据提供器实例
     * @param world 管理的维度对应世界，要求{@link WorldServer}
     * @param loader 大气数据加载器，用于从文件存储中加载大气数据到内存中
     */
    public DefaultAtmosphereDataProvider(@Nonnull WorldServer world,@Nonnull IAtmosphereDataLoader loader) {
        this.world = world;
        this.atmosphereLoader = loader;
    }

    /**
     * {@inheritDoc}
     * @param x 区块X坐标
     * @param z 区块Z坐标
     * @return {@inheritDoc}
     */
    @Nonnull
    @Override
    public AtmosphereData provideAtmosphereData(int x, int z) {
        AtmosphereData data = this.loadAtmosphereData(x, z);
        if (data == null) {
            data = new AtmosphereData(new NBTTagCompound(),x,z);
            loadedAtmosphere.put(ChunkPos.asLong(x,z),data);
            return data;
        }
        return data;
    }

    /**
     * {@inheritDoc}
     * @param x 区块X坐标
     * @param z 区块Z坐标
     */
    @Override
    public void queueUnloadAtmosphereData(int x, int z) {
        AtmosphereData data = loadedAtmosphere.get(ChunkPos.asLong(x,z));
        if(data == null){
            APIUtil.LOGGER.warn("Someone trying to unload an atmosphere that haven't been loaded.");
            return;
        }
        queueUnloadAtmosphereData(data);
    }

    /**
     * {@inheritDoc}
     * @param x 区块X坐标
     * @param z 区块Z坐标
     */
    @Override
    public void saveAtmosphereData(int x, int z) {
        AtmosphereData data = loadedAtmosphere.get(ChunkPos.asLong(x,z));
        if(data == null){
            APIUtil.LOGGER.warn("Someone trying to save an atmosphere {} {} in DIM{} that haven't been loaded.",x,z,world.provider.getDimension());
            return;
        }
        saveAtmosphereData(data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveAllAtmosphereData() {
        for(AtmosphereData data:loadedAtmosphere.values()){
            saveAtmosphereData(data);
            atmosphereLoader.flush();
        }
        APIUtil.LOGGER.info("All Atmosphere Data for DIM{} has saved!",world.provider.getDimension());
    }

    /**
     * {@inheritDoc}
     * 每游戏刻会处理将要被卸载的大气数据，将处于常加载区块的大气数据排除，然后卸载已被标记为将要卸载的大气数据
     * @return {@inheritDoc}
     */
    @Override
    public boolean tick() {
        if (this.world.disableLevelSaving) return false;
        if (this.droppedAtmospheres.isEmpty()) return false;

        for (ChunkPos forced : this.world.getPersistentChunks().keySet()) {
            this.droppedAtmospheres.remove(ChunkPos.asLong(forced.x, forced.z));
        }
        Iterator<Long> iterator = this.droppedAtmospheres.iterator();
        for (int i = 0; i < 1000 && iterator.hasNext(); iterator.remove()) {
            Long id = iterator.next();
            AtmosphereData data = this.loadedAtmosphere.get(id);

            if (data != null && data.isUnloadQueued()) {
                if(data.getAtmosphere() != null) {
                    data.getAtmosphere().onUnload();
                }
                this.saveAtmosphereData(data);
                this.loadedAtmosphere.remove(id);
                data.markUnloaded();
                i++;
            }
        }

        return false;
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Nonnull
    @Override
    public String makeString() {
        return "ServerAtmosphereCache: " + this.loadedAtmosphere.size() + " Drop: " + this.droppedAtmospheres.size();
    }

    /**
     * {@inheritDoc}
     * @param x 区块X坐标
     * @param z 区块Z坐标
     * @return {@inheritDoc}
     */
    @Override
    public boolean isAtmosphereGeneratedAt(int x, int z) {
        return this.loadedAtmosphere.containsKey(ChunkPos.asLong(x, z)) || this.atmosphereLoader.doesAtmosphereExistsAt(x, z);
    }

    /**
     * 加载指定区块的大气数据，若对应区块的大气数据没有加载，则会尝试从文件中加载
     * @param x 区块X坐标
     * @param z 区块Z坐标
     * @return 加载的大气数据。若为null则意味对应区块的大气尚未生成
     */
    @Nullable
    public AtmosphereData loadAtmosphereData(int x, int z) {
        AtmosphereData data = this.getLoadedAtmosphereData(x, z);
        if (data == null) {
            data = this.loadAtmosphereDataFromLoader(x, z);

            if (data != null) {
                this.loadedAtmosphere.put(ChunkPos.asLong(x, z), data);
            }
        }
        return data;
    }

    /**
     * {@inheritDoc}
     * @param x 区块X坐标
     * @param z 区块Z坐标
     * @return {@inheritDoc}
     */
    @Nullable
    @Override
    public AtmosphereData getLoadedAtmosphereData(int x, int z) {
        long i = ChunkPos.asLong(x, z);
        AtmosphereData data = this.loadedAtmosphere.get(i);

        if (data != null) data.setUnloadQueued(false);

        return data;
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Nonnull
    @Override
    public Collection<AtmosphereData> getLoadedAtmosphereDataCollection() {
        return loadedAtmosphere.values();
    }

    /**
     * 从{@link IAtmosphereDataLoader}中加载大气数据
     * @param x 区块X坐标
     * @param z 区块Z坐标
     * @return 加载的大气数据，可能为null
     */
    @Nullable
    protected AtmosphereData loadAtmosphereDataFromLoader(int x, int z) {
        try {
            AtmosphereData data = this.atmosphereLoader.loadAtmosphereData(this.world, x, z);

            if (data != null) {
                data.setLastSaveTime(this.world.getTotalWorldTime());
            }

            return data;
        } catch (Exception exception) {
            LOGGER.error("Couldn't load atmosphere data:", exception);
            return null;
        }
    }

    /**
     * 将指定大气数据标记为待卸载
     * @param data 要被卸载的大气数据
     */
    public void queueUnloadAtmosphereData(@Nonnull AtmosphereData data) {
        if (this.world.provider.canDropChunk(data.pos.x, data.pos.z)) {
            this.droppedAtmospheres.add(ChunkPos.asLong(data.pos.x, data.pos.z));
            data.setUnloadQueued(true);
        }
    }

    /**
     * 保存指定的大气数据
     * @param data 要保存的大气数据
     */
    public void saveAtmosphereData(@Nonnull AtmosphereData data) {
        try {
            data.setLastSaveTime(this.world.getTotalWorldTime());
            this.atmosphereLoader.saveAtmosphereData(this.world, data);
        } catch (IOException e) {
            LOGGER.error("Couldn't save atmosphere", e);
        } catch (MinecraftException e) {
            LOGGER.error("Couldn't save atmosphere; already in use by another instance of Minecraft?", e);
        }
    }
}
