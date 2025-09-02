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
import top.qiguaiaaaa.geocraft.api.atmosphere.storage.IAtmosphereLoader;
import top.qiguaiaaaa.geocraft.api.util.APIUtil;

import javax.annotation.Nullable;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * 类似{@link ChunkProviderServer}
 */
public class DefaultAtmosphereDataProvider implements IAtmosphereDataProvider {
    private static final Logger LOGGER = LogManager.getLogger();
    protected final Set<Long> droppedAtmospheres = Sets.newHashSet();
    public final WorldServer world;
    public final IAtmosphereLoader atmosphereLoader;
    public final Long2ObjectMap<AtmosphereData> loadedAtmosphere = new Long2ObjectOpenHashMap<>(65536);

    public DefaultAtmosphereDataProvider(WorldServer world, IAtmosphereLoader loader) {
        this.world = world;
        this.atmosphereLoader = loader;
    }

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

    @Override
    public void queueUnloadAtmosphereData(int x, int z) {
        AtmosphereData data = loadedAtmosphere.get(ChunkPos.asLong(x,z));
        if(data == null){
            APIUtil.LOGGER.warn("Someone trying to unload an atmosphere that haven't been loaded.");
            return;
        }
        queueUnloadAtmosphereData(data);
    }

    @Override
    public void saveAtmosphereData(int x, int z) {
        AtmosphereData data = loadedAtmosphere.get(ChunkPos.asLong(x,z));
        if(data == null){
            APIUtil.LOGGER.warn("Someone trying to save an atmosphere {} {} in DIM{} that haven't been loaded.",x,z,world.provider.getDimension());
            return;
        }
        saveAtmosphereData(data);
    }

    @Override
    public void saveAllAtmosphereData() {
        for(AtmosphereData data:loadedAtmosphere.values()){
            saveAtmosphereData(data);
            atmosphereLoader.flush();
        }
        APIUtil.LOGGER.info("All Atmosphere Data for DIM{} has saved!",world.provider.getDimension());
    }

    @Override
    public boolean tick() {
        if (!this.world.disableLevelSaving) {
            if (!this.droppedAtmospheres.isEmpty()) {
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
                        i++;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public String makeString() {
        return "ServerAtmosphereCache: " + this.loadedAtmosphere.size() + " Drop: " + this.droppedAtmospheres.size();
    }

    @Override
    public boolean doesAtmosphereExistsAt(int x, int z) {
        return this.loadedAtmosphere.containsKey(ChunkPos.asLong(x, z)) || this.atmosphereLoader.doesAtmosphereExistsAt(x, z);
    }

    @Nullable
    public AtmosphereData loadAtmosphereData(int x, int z) {
        AtmosphereData data = this.getLoadedAtmosphereData(x, z);
        if (data == null) {
            data = this.loadAtmosphereDataFromFile(x, z);

            if (data != null) {
                this.loadedAtmosphere.put(ChunkPos.asLong(x, z), data);
            }
        }
        return data;
    }

    @Nullable
    @Override
    public AtmosphereData getLoadedAtmosphereData(int x, int z) {
        long i = ChunkPos.asLong(x, z);
        AtmosphereData data = this.loadedAtmosphere.get(i);

        if (data != null) data.setUnloadQueued(false);

        return data;
    }

    @Override
    public Collection<AtmosphereData> getLoadedAtmosphereDataCollection() {
        return loadedAtmosphere.values();
    }

    @Nullable
    protected AtmosphereData loadAtmosphereDataFromFile(int x, int z) {
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

    public void queueUnloadAtmosphereData(AtmosphereData data) {
        if (this.world.provider.canDropChunk(data.pos.x, data.pos.z)) {
            this.droppedAtmospheres.add(ChunkPos.asLong(data.pos.x, data.pos.z));
            data.setUnloadQueued(true);
        }
    }

    public void saveAtmosphereData(AtmosphereData data) {
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
