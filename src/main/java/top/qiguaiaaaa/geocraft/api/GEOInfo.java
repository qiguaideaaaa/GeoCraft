package top.qiguaiaaaa.geocraft.api;

import org.apache.logging.log4j.Logger;

public final class GEOInfo {
    private static Logger logger;
    private static String modVersion;

    public static void setLogger(Logger logger) {
        GEOInfo.logger = logger;
    }

    public static void setModId(String modId) {
    }

    public static void setModVersion(String modVersion) {
        GEOInfo.modVersion = modVersion;
    }

    public static Logger getLogger() {
        return logger;
    }

    public static String getModVersion() {
        return modVersion;
    }
}
