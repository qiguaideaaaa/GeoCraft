package top.qiguaiaaaa.fluidgeography.api;

import org.apache.logging.log4j.Logger;

public final class FGInfo {
    private static Logger logger;
    private static String modId = "fluidgeography";
    private static String modVersion;

    public static void setLogger(Logger logger) {
        FGInfo.logger = logger;
    }

    public static void setModId(String modId) {
        FGInfo.modId = modId;
    }

    public static void setModVersion(String modVersion) {
        FGInfo.modVersion = modVersion;
    }

    public static Logger getLogger() {
        return logger;
    }

    public static String getModId() {
        return modId;
    }

    public static String getModVersion() {
        return modVersion;
    }
}
