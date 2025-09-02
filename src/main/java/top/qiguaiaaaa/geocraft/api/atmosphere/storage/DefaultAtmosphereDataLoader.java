package top.qiguaiaaaa.geocraft.api.atmosphere.storage;

import com.google.common.collect.Maps;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.World;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.storage.IThreadedFileIO;
import net.minecraft.world.storage.ThreadedFileIOBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * 仿造{@link AnvilChunkLoader}写的一个大气数据加载器
 * 该加载器会读取位于每个维度下面的atmosphere文件夹中的数据,并转换为{@link AtmosphereData}
 * 写入为异步操作,读取支持异步操作
 */
public class DefaultAtmosphereDataLoader implements IAtmosphereLoader, IThreadedFileIO {
    private static final Logger LOGGER = LogManager.getLogger();
    public final File saveFolder;
    protected final Map<ChunkPos, NBTTagCompound> atmospheresToSave = Maps.newConcurrentMap();
    protected final Set<ChunkPos> atmospheresBeingSaved = Collections.newSetFromMap(Maps.newConcurrentMap());
    protected boolean flushing;

    public DefaultAtmosphereDataLoader(File atmosphereSaveFolder){
        this.saveFolder = atmosphereSaveFolder;
    }
    @Nullable
    @Override
    public AtmosphereData loadAtmosphereData(World worldIn, int x, int z) throws IOException {
        ChunkPos chunkpos = new ChunkPos(x, z);
        NBTTagCompound compound = this.atmospheresToSave.get(chunkpos);

        if (compound == null) {
            DataInputStream input = AtmosphereRegionFileCache.getAtmosphereInputStream(saveFolder, x, z);

            if (input == null) {
                return null;
            }

            compound = CompressedStreamTools.read(input);
            input.close();
        }
        return new AtmosphereData(compound,x,z);
    }

    @Override
    public void saveAtmosphereData(World worldIn, AtmosphereData data) throws MinecraftException {
        worldIn.checkSessionLock();

        try {
            this.addAtmosphereToPending(data.pos, data.data);
        } catch (Exception exception) {
            LOGGER.error("Failed to save atmosphere", exception);
        }
    }

    @Override
    public boolean doesAtmosphereExistsAt(int x, int z) {
        ChunkPos chunkpos = new ChunkPos(x, z);
        NBTTagCompound nbttagcompound = this.atmospheresToSave.get(chunkpos);
        return nbttagcompound != null || AtmosphereRegionFileCache.atmosphereExists(this.saveFolder, x, z);
    }

    @Override
    public void flush() {
        try {
            this.flushing = true;
            this.writeNextIO();
        } finally {
            this.flushing = false;
        }
    }

    protected void addAtmosphereToPending(ChunkPos pos, NBTTagCompound compound) {
        if (!this.atmospheresBeingSaved.contains(pos)) {
            this.atmospheresToSave.put(pos, compound);
        }

        ThreadedFileIOBase.getThreadedIOInstance().queueIO(this);
    }

    @Override
    public boolean writeNextIO() {
        if (this.atmospheresToSave.isEmpty()) {
            if (this.flushing) {
                LOGGER.info("AtmosphereStorage ({}): All atmospheres are saved", this.saveFolder.getName());
            }

            return false;
        }
        ChunkPos pos;
        try{
            pos = this.atmospheresToSave.keySet().iterator().next();
        }catch (NoSuchElementException e){
            return false;
        }catch (Throwable e){
            LOGGER.error("Unknown Error stopped atmosphere data from being saved!",e);
            throw new RuntimeException(e);
        }
        boolean succeed;

        try {
            this.atmospheresBeingSaved.add(pos);
            NBTTagCompound compound = this.atmospheresToSave.remove(pos);

            if (compound != null) {
                try {
                    this.writeAtmosphereData(pos, compound);
                } catch (Exception exception) {
                    LOGGER.error("Failed to save atmosphere", exception);
                }
            }

            succeed = true;
        } finally {
            this.atmospheresBeingSaved.remove(pos);
        }

        return succeed;
    }

    public int getPendingSaveCount()
    {
        return this.atmospheresToSave.size();
    }

    protected void writeAtmosphereData(ChunkPos pos, NBTTagCompound compound) throws IOException {
        DataOutputStream output = AtmosphereRegionFileCache.getAtmosphereOutputStream(this.saveFolder, pos.x, pos.z);
        CompressedStreamTools.write(compound, output);
        output.close();
    }
}
