package top.qiguaiaaaa.geocraft.configs;

import top.qiguaiaaaa.geocraft.api.configs.item.base.ConfigBoolean;
import top.qiguaiaaaa.geocraft.api.configs.item.number.ConfigInteger;

import static net.minecraftforge.common.config.Configuration.CATEGORY_GENERAL;
public final class GeneralConfig {
    public static final ConfigInteger leastTemperatureForFluidToCompletelyDestroyBlock =
            new ConfigInteger(CATEGORY_GENERAL,"leastTemperatureForFluidToCompletelyDestroyBlock",1237,
                    "在流体流动过程中，完全摧毁可摧毁方块（即不会留下掉落物）的最低流体温度，单位为开尔文（K）。");
    public static final ConfigBoolean ALLOW_CLIENT_TO_READ_HUMIDITY_DATA = new ConfigBoolean(CATEGORY_GENERAL,
            "allowClientToReadHumidityData",false,
            "是否允许客户端读取土壤的湿度数据。默认为禁止。在禁止状态下，模组将会对服务器和客户端的网络通信进行修改，以去除土壤的湿度信息。其原理和反矿透原理类似。" +
                    "如果您遇到兼容性问题，想要禁止mod对网络通信进行修改，或想要允许客户端读取土壤的湿度数据，可以更改此选项为true。这样子，mod将不再修改网络通信，您可以使用其他更专业的mod以阻止客户端阅读土壤湿度数据。" +
                    "请注意,允许客户端阅读湿度数据后,若客户端没有安装此模组,对于土壤相关方块(比如灰化土)的显示可能出现异常.您可以通过其他具有修改网络通信功能的模组来避免此问题,或禁止未安装该模组的客户端连接,或放着不管.",true);
}
