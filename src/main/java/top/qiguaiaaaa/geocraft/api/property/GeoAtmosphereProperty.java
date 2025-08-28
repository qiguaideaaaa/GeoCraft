package top.qiguaiaaaa.geocraft.api.property;

public final class GeoAtmosphereProperty {
    /**
     * 下垫面重载的时间间隔,单位大气刻
     */
    private static int UNDERLYING_RELOAD_GAP = 60;
    /**
     * 大气刻每过{@link GeoAtmosphereProperty#ATMOSPHERE_TICK} 游戏刻更新一次
     */
    private static int ATMOSPHERE_TICK = 60;
    /**
     * 大气刻在Minecraft中对应的虚拟模拟秒数
     */
    private static int ATMOSPHERE_TICK_SECONDS_IN_SIMULATION = (int)(ATMOSPHERE_TICK*86400L/24000);
    /**
     * 大气是否需要在遇到更新错误时,打印出详细的日志信息以供分析。
     * 需要注意，详细的信息很多。若错误批量出现，可能日志会爆炸。
     */
    private static boolean ENABLE_DETAILED_LOGGING = false;

    public static void setUnderlyingReloadGap(int gap){
        if(gap <1) return;
        UNDERLYING_RELOAD_GAP = gap;
    }

    public static void setEnableDetailedLogging(boolean enableDetailedLogging) {
        ENABLE_DETAILED_LOGGING = enableDetailedLogging;
    }

    /**
     * 设置大气刻每过多少游戏刻执行
     * 实验性功能
     * @param atmosphereTick 大气刻对应的游戏刻长度
     */
    public static void setAtmosphereTick(int atmosphereTick) {
        ATMOSPHERE_TICK = atmosphereTick;
        ATMOSPHERE_TICK_SECONDS_IN_SIMULATION = (int)(ATMOSPHERE_TICK*86400L/24000);
    }

    public static int getUnderlyingReloadGap() {
        return UNDERLYING_RELOAD_GAP;
    }

    public static int getAtmosphereTick() {
        return ATMOSPHERE_TICK;
    }

    public static int getSimulationGap() {
        return ATMOSPHERE_TICK_SECONDS_IN_SIMULATION;
    }

    public static boolean isEnableDetailedLogging() {
        return ENABLE_DETAILED_LOGGING;
    }
}
