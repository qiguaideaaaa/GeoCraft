package top.qiguaiaaaa.geocraft.api.atmosphere.storage;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import top.qiguaiaaaa.geocraft.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.geocraft.api.atmosphere.gen.IAtmosphereDataProvider;
import top.qiguaiaaaa.geocraft.api.atmosphere.system.IAtmosphereSystem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 大气数据。由{@link IAtmosphereDataLoader}从磁盘中加载，{@link IAtmosphereDataProvider}提供和管理，{@link IAtmosphereSystem}处理，并将{@link #data}交给大气的{@link Atmosphere#serializeNBT()}和{@link Atmosphere#deserializeNBT(NBTBase)}处理
 */
public class AtmosphereData {
    public static final String TAG_POS_X = "posX",TAG_POS_Z = "posZ",TAG_DATA = "Level";

    public final ChunkPos pos;
    public NBTTagCompound data;
    protected Atmosphere atmosphere;
    protected Chunk chunk;
    protected boolean unloadQueued = false,unloaded=false;
    protected long lastSaveTime;

    /**
     * 创建一个大气数据
     * @param data 具体NBT数据
     * @param x 区块X坐标
     * @param z 区块Z坐标
     */
    public AtmosphereData(@Nonnull NBTTagCompound data, int x, int z) {
        this.data = data;
        data.setInteger(TAG_POS_X,x);
        data.setInteger(TAG_POS_Z,z);
        pos = new ChunkPos(x,z);
    }

    /**
     * 若该实例持有一个大气，则将大气实例序列化为NBT保存在大气数据的NBT中
     */
    public void saveAtmosphere(){
        if(atmosphere != null){
            this.data.setTag(TAG_DATA,atmosphere.serializeNBT());
        }
    }

    /**
     * 标记该大气数据是否将要被卸载
     * @param isQueuedToBeUnloaded 是否将要被卸载
     */
    public void setUnloadQueued(boolean isQueuedToBeUnloaded){
        unloadQueued = isQueuedToBeUnloaded;
    }

    /**
     * 标记该大气数据是否被卸载<br/>
     * 若一个大气数据实例已经被标记为被卸载，则不应该再次使用该大气数据实例
     */
    public void markUnloaded() {
        this.unloaded = true;
    }

    public void setLastSaveTime(long time){
        lastSaveTime = time;
    }

    public void setAtmosphere(@Nullable Atmosphere atmosphere) {
        this.atmosphere = atmosphere;
    }

    /**
     * 设置该大气数据对应的区块实例
     * @param chunk 区块实例
     */
    public void setChunk(@Nullable Chunk chunk) {
        this.chunk = chunk;
    }

    public boolean isUnloadQueued() {
        return unloadQueued;
    }

    /**
     * 该大气数据是否已被卸载<br/>
     * 应尽可能避免持有和使用已被卸载的大气数据
     * @return 若已被卸载，则返回true
     */
    public boolean isUnloaded() {
        return unloaded;
    }

    /**
     * 该大气数据是否没有持有任何数据
     * @return 若没有，则返回true
     */
    public boolean isEmpty(){
        return getSaveCompound().isEmpty();
    }

    /**
     * 获取该大气数据保存具体数据的NBT复合标签
     * @return 一个NBT复合标签，内为大气序列化后的NBT数据
     */
    @Nonnull
    public NBTTagCompound getSaveCompound() {
        return this.data.getCompoundTag(TAG_DATA);
    }

    public long getLastSaveTime() {
        return lastSaveTime;
    }
    @Nullable
    public Atmosphere getAtmosphere() {
        return atmosphere;
    }

    /**
     * 获取该大气数据对应的区块，若对应区块未加载则返回null
     * @return 一个区块
     */
    @Nullable
    public Chunk getChunk() {
        return chunk;
    }
}
