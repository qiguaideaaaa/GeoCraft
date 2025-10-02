/*
 * Copyright 2025 QiguaiAAAA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * 版权所有 2025 QiguaiAAAA
 * 根据Apache许可证第2.0版（“本许可证”）许可；
 * 除非符合本许可证的规定，否则你不得使用此文件。
 * 你可以在此获取本许可证的副本：
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * 除非所适用法律要求或经书面同意，在本许可证下分发的软件是“按原样”分发的，
 * 没有任何形式的担保或条件，不论明示或默示。
 * 请查阅本许可证了解有关本许可证下许可和限制的具体要求。
 * 中文译文来自开放原子开源基金会，非官方译文，如有疑议请以英文原文为准
 */

package top.qiguaiaaaa.geocraft.configs;

import net.minecraftforge.common.config.Config;
import top.qiguaiaaaa.geocraft.api.configs.ConfigCategory;
import top.qiguaiaaaa.geocraft.api.configs.item.base.ConfigBoolean;
import top.qiguaiaaaa.geocraft.api.configs.item.number.ConfigInteger;

import static top.qiguaiaaaa.geocraft.api.configs.ConfigCategory.GENERAL;

@SuppressWarnings("unused")
public final class GeneralConfig {
    public static final ConfigBoolean ALLOW_CLIENT_TO_READ_HUMIDITY_DATA = new ConfigBoolean(GENERAL,
            "allowClientToReadHumidityData",false,
            "是否允许客户端读取土壤的湿度数据。默认为禁止。在禁止状态下，模组将会对服务器和客户端的网络通信进行修改，以去除土壤的湿度信息。其原理和反矿透原理类似。\n" +
                    "如果您遇到兼容性问题，想要禁止mod对网络通信进行修改，或想要允许客户端读取土壤的湿度数据，可以更改此选项为true。这样子，mod将不再修改网络通信，您可以使用其他更专业的mod以阻止客户端阅读土壤湿度数据。\n" +
                    "请注意,允许客户端阅读湿度数据后,若客户端没有安装此模组,对于土壤相关方块(比如灰化土)的显示可能出现异常.您可以通过其他具有修改网络通信功能的模组来避免此问题,或禁止未安装该模组的客户端连接,或放着不管.\n" +
                    "Whether to allow the client to read soil humidity data. Default is disabled. When disabled, the mod will modify network communication between the server and client to remove soil humidity information. The principle is similar to anti-X-ray mechanisms.\n" +
                    "If you encounter compatibility issues and wish to disable the mod's network modifications or allow clients to read soil humidity data, you can change this option to true. In this case, the mod will no longer modify network communication, and you can use other more specialized mods to prevent clients from reading soil humidity data.\n" +
                    "Please note that after enabling client access to humidity data, if the client does not have this mod installed, the display of soil-related blocks (such as podzol) may appear abnormal. You can address this by using other mods with network modification capabilities, prohibiting connections from clients without this mod, or leaving it as is.",true);

    //*********************
    // Block Updater
    //*********************

    public static final ConfigCategory CATEGORY_BLOCK_UPDATER = GENERAL.getChildCategory("block_updater");

    @Config.RangeInt(min = 1)
    public static final ConfigInteger BLOCK_UPDATER_MAX_UPDATES_BLOCK = new ConfigInteger(CATEGORY_BLOCK_UPDATER,
            "maxUpdateBlocksPerTick",65536*4,
            "天圆地方内置的附加方块更新器在一游戏刻内最多更新的方块数量，多余的更新任务会被忽略。\n" +
                    "The max number of blocks to update by Block Updater inside GeoCraft. The excess part will be ignored.",true);

    //*********************
    // Performance
    //*********************

    @Config.Comment({
            "模组性能调整",
            "Mod Performance Tuning"})
    public static final ConfigCategory CATEGORY_PERFORMANCE = GENERAL.getChildCategory("performance");

    public static final ConfigBoolean ENABLE_SINGLE_TICK_DELAY_DETECT = new ConfigBoolean(CATEGORY_PERFORMANCE,
            "enableSingleTickDelayDetect",false,
            "开启基于单游戏刻的延迟检测。默认关闭，开启意味着更加激进的优化。\n" +
                    "Enable single-tick based lag detection. Default is disabled; enabling this means more aggressive optimization.");
    @Config.RangeInt(min = 1)
    public static final ConfigInteger SINGLE_TICK_DELAY_WARNING_THRESHOLD = new ConfigInteger(CATEGORY_PERFORMANCE,
            "singleTickDelayWarningThreshold",100,
            "当单游戏刻时长超过该阈值时，将会触发服务端延迟警报，模组将会尝试减少一些次要运算以优化性能。\n" +
                    "When the duration of a single game tick exceeds this threshold, a server delay alert will be triggered, and the mod will attempt to reduce non-essential operations to optimize performance.");

    @Config.RangeInt(min = 1)
    public static final ConfigInteger SINGLE_TICK_DELAY_THRESHOLD = new ConfigInteger(CATEGORY_PERFORMANCE,
            "singleTickDelayThreshold",200,
            "当单游戏刻时长超过该阈值时，将会被认为是服务器延迟，模组将会尝试进一步减少运算以优化性能。\n" +
                    "When the duration of a single game tick exceeds this threshold, it will be considered server lag, and the mod will attempt to further reduce computations to optimize performance.");

    @Config.RangeInt(min = 1)
    public static final ConfigInteger AVERAGE_TICK_DELAY_WARNING_THRESHOLD = new ConfigInteger(CATEGORY_PERFORMANCE,
            "averageTickDelayWarningThreshold",60,
            "当平均游戏刻时长超过该阈值时，将会触发服务端延迟警报，模组将会尝试减少一些次要运算以优化性能。\n" +
                    "When the average game tick duration exceeds this threshold, a server delay alert will be triggered, and the mod will attempt to reduce non-essential operations to optimize performance.");

    @Config.RangeInt(min = 1)
    public static final ConfigInteger AVERAGE_TICK_DELAY_THRESHOLD = new ConfigInteger(CATEGORY_PERFORMANCE,
            "averageTickDelayThreshold",100,
            "当平均游戏刻时长超过该阈值时，将会被认为是服务器延迟，模组将会尝试进一步减少运算以优化性能。\n" +
                    "When the average game tick duration exceeds this threshold, it will be identified as server lag, and the mod will attempt to further reduce computational load to optimize performance.");
}
