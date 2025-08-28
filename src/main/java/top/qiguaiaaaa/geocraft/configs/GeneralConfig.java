package top.qiguaiaaaa.geocraft.configs;

import top.qiguaiaaaa.geocraft.api.configs.item.number.ConfigInteger;

import static net.minecraftforge.common.config.Configuration.CATEGORY_GENERAL;
public final class GeneralConfig {
    public static final ConfigInteger leastTemperatureForFluidToCompletelyDestroyBlock =
            new ConfigInteger(CATEGORY_GENERAL,"leastTemperatureForFluidToCompletelyDestroyBlock",1237,
                    "在流体流动过程中，完全摧毁可摧毁方块（即不会留下掉落物）的最低流体温度，单位为开尔文（K）。");
}
