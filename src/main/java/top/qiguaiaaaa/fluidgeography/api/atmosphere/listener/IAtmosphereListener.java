package top.qiguaiaaaa.fluidgeography.api.atmosphere.listener;

import top.qiguaiaaaa.fluidgeography.api.atmosphere.Atmosphere;

/**
 * 大气监听器，用于监听大气状态
 */
public interface IAtmosphereListener {
    void notifyListener(Atmosphere atmosphere);
}
