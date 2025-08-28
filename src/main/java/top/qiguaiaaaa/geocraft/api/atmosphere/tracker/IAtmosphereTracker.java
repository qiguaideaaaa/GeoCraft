package top.qiguaiaaaa.geocraft.api.atmosphere.tracker;

import top.qiguaiaaaa.geocraft.api.atmosphere.Atmosphere;

/**
 * 大气监听器，用于监听大气状态
 */
public interface IAtmosphereTracker {
    void notify(Atmosphere atmosphere);
}
