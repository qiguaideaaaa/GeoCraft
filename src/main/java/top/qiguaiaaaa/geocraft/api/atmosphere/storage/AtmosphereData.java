package top.qiguaiaaaa.geocraft.api.atmosphere.storage;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import top.qiguaiaaaa.geocraft.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.geocraft.api.atmosphere.system.IAtmosphereSystem;

/**
 * 大气数据类。由{@link IAtmosphereSystem}处理，并将{@link #data}交给大气的{@link Atmosphere#serializeNBT()}和{@link Atmosphere#deserializeNBT(NBTBase)}处理
 */
public class AtmosphereData {
    public static final String TAG_POS_X = "posX",TAG_POS_Z = "posZ",TAG_DATA = "Level";

    public final ChunkPos pos;
    public NBTTagCompound data;
    protected Atmosphere atmosphere;
    protected Chunk chunk;
    protected boolean unloadQueued = false,unloaded=false;
    protected long lastSaveTime;

    public AtmosphereData(NBTTagCompound data,int x,int z) {
        this.data = data;
        data.setInteger(TAG_POS_X,x);
        data.setInteger(TAG_POS_Z,z);
        pos = new ChunkPos(x,z);
    }

    public void saveAtmosphere(){
        if(atmosphere != null){
            this.data.setTag(TAG_DATA,atmosphere.serializeNBT());
        }
    }
    public void setUnloadQueued(boolean isQueuedToBeUnloaded){
        unloadQueued = isQueuedToBeUnloaded;
    }

    public void setUnloaded(boolean unloaded) {
        if(this.unloaded) return;
        this.unloaded = unloaded;
    }

    public void setLastSaveTime(long time){
        lastSaveTime = time;
    }

    public void setAtmosphere(Atmosphere atmosphere) {
        this.atmosphere = atmosphere;
    }

    public void setChunk(Chunk chunk) {
        this.chunk = chunk;
    }

    public boolean isUnloadQueued() {
        return unloadQueued;
    }

    public boolean isUnloaded() {
        return unloaded;
    }

    public boolean isEmpty(){
        return getSaveCompound().isEmpty();
    }
    public NBTTagCompound getSaveCompound() {
        return this.data.getCompoundTag(TAG_DATA);
    }

    public long getLastSaveTime() {
        return lastSaveTime;
    }

    public Atmosphere getAtmosphere() {
        return atmosphere;
    }

    public Chunk getChunk() {
        return chunk;
    }
}
