package top.qiguaiaaaa.geocraft.api.atmosphere.storage;

import com.google.common.collect.Maps;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import static top.qiguaiaaaa.geocraft.api.atmosphere.storage.AtmosphereRegionFile.STORAGE_ATMOSPHERES_COUNT_LOG;

public class AtmosphereRegionFileCache {
    public static final String WORKING_DIR = "atmosphere",FILE_SUFFIX = ".atmdat";
    public static final int ATMOSPHERE_SAVE_LOC_BYTE = (1<< STORAGE_ATMOSPHERES_COUNT_LOG)-1;
    private static final Map<File, AtmosphereRegionFile> ATMOSPHERE_REGIONS_BY_FILE = Maps.newHashMap();

    public static synchronized AtmosphereRegionFile createOrLoadAtmosphereRegionFile(File worldDir, int chunkX, int chunkZ) {
        File dir = new File(worldDir, WORKING_DIR);
        File file = new File(dir, "r." + (chunkX >> STORAGE_ATMOSPHERES_COUNT_LOG) + "." + (chunkZ >> STORAGE_ATMOSPHERES_COUNT_LOG) + FILE_SUFFIX);
        AtmosphereRegionFile regionFile = ATMOSPHERE_REGIONS_BY_FILE.get(file);

        if (regionFile == null) {
            if (!dir.exists()) {
                dir.mkdirs();
            }

            if (ATMOSPHERE_REGIONS_BY_FILE.size() >= 256) {
                clearRegionFileReferences();
            }

            regionFile = new AtmosphereRegionFile(file);
            ATMOSPHERE_REGIONS_BY_FILE.put(file, regionFile);
        }
        return regionFile;
    }

    public static synchronized AtmosphereRegionFile getAtmosphereRegionFileIfExists(File worldDir, int chunkX, int chunkZ) {
        File dir = new File(worldDir, WORKING_DIR);
        File file = new File(dir, "r." + (chunkX >> STORAGE_ATMOSPHERES_COUNT_LOG) + "." + (chunkZ >> STORAGE_ATMOSPHERES_COUNT_LOG) + FILE_SUFFIX);
        AtmosphereRegionFile regionFile = ATMOSPHERE_REGIONS_BY_FILE.get(file);

        if (regionFile != null) {
            return regionFile;
        } else if (dir.exists() && file.exists()) {
            if (ATMOSPHERE_REGIONS_BY_FILE.size() >= 256) {
                clearRegionFileReferences();
            }

            regionFile = new AtmosphereRegionFile(file);
            ATMOSPHERE_REGIONS_BY_FILE.put(file, regionFile);
            return regionFile;
        } else {
            return null;
        }
    }

    public static synchronized void clearRegionFileReferences() {
        for (AtmosphereRegionFile file : ATMOSPHERE_REGIONS_BY_FILE.values()) {
            try {
                if (file != null) {
                    file.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ATMOSPHERE_REGIONS_BY_FILE.clear();
    }

    public static DataInputStream getAtmosphereInputStream(File worldDir, int chunkX, int chunkZ) {
        AtmosphereRegionFile file = createOrLoadAtmosphereRegionFile(worldDir, chunkX, chunkZ);
        return file.getAtmosphereDataInputStream(chunkX & ATMOSPHERE_SAVE_LOC_BYTE, chunkZ & ATMOSPHERE_SAVE_LOC_BYTE);
    }

    public static DataOutputStream getAtmosphereOutputStream(File worldDir, int chunkX, int chunkZ) {
        AtmosphereRegionFile file = createOrLoadAtmosphereRegionFile(worldDir, chunkX, chunkZ);
        return file.getAtmosphereDataOutputStream(chunkX & ATMOSPHERE_SAVE_LOC_BYTE, chunkZ & ATMOSPHERE_SAVE_LOC_BYTE);
    }

    public static boolean atmosphereExists(File worldDir, int chunkX, int chunkZ) {
        AtmosphereRegionFile regionFile = getAtmosphereRegionFileIfExists(worldDir, chunkX, chunkZ);
        return regionFile != null && regionFile.isAtmosphereSaved(chunkX & ATMOSPHERE_SAVE_LOC_BYTE, chunkZ & ATMOSPHERE_SAVE_LOC_BYTE);
    }
}
