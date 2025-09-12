package top.qiguaiaaaa.geocraft.api.atmosphere.tracker;

import top.qiguaiaaaa.geocraft.api.atmosphere.Atmosphere;

/**
 * 大气监听器，用于监听大气状态
 */
public interface IAtmosphereTracker {

    /**
     * 当监听的大气更新的时候，调用该方法
     * @param atmosphere 监听的大气实例
     */
    void notify(Atmosphere atmosphere);
}
