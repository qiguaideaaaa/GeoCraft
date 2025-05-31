package top.qiguaiaaaa.fluidgeography.api.configs;

import top.qiguaiaaaa.fluidgeography.api.configs.item.ConfigItem;
import top.qiguaiaaaa.fluidgeography.api.configs.value.number.ConfigInteger;

import static net.minecraftforge.common.config.Configuration.CATEGORY_GENERAL;
public final class GeneralConfig {
    public static final ConfigItem<ConfigInteger> leastTemperatureForFluidToCompletelyDestroyBlock =
            new ConfigItem<>(CATEGORY_GENERAL,"leastTemperatureForFluidToCompletelyDestroyBlock",new ConfigInteger(1237),
                    "在流体流动过程中，完全摧毁可摧毁方块（即不会留下掉落物）的最低流体温度，单位为开尔文（K）。");
}
