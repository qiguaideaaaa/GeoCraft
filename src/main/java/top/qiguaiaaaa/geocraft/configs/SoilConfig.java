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
import top.qiguaiaaaa.geocraft.api.configs.GeoConfig;
import top.qiguaiaaaa.geocraft.api.configs.item.base.ConfigBoolean;
import top.qiguaiaaaa.geocraft.api.configs.item.collection.set.ConfigIntegerSet;
import top.qiguaiaaaa.geocraft.api.configs.item.collection.set.ConfigSet;
import top.qiguaiaaaa.geocraft.api.configs.item.map.ConfigMap;
import top.qiguaiaaaa.geocraft.api.configs.value.collection.ConfigurableSet;
import top.qiguaiaaaa.geocraft.api.configs.value.map.entry.ConfigEntry;
import top.qiguaiaaaa.geocraft.api.configs.value.minecraft.ConfigurableBiome;
import top.qiguaiaaaa.geocraft.api.configs.value.minecraft.ConfigurableBlockState;
import top.qiguaiaaaa.geocraft.geography.soil.BlockSoilType;

/**
 * @author QiguaiAAAA
 */
@SuppressWarnings("unused")
public final class SoilConfig {
    public static final ConfigCategory CATEGORY_SOIL = new ConfigCategory("soil");

    @Config.RequiresMcRestart
    public static final ConfigBoolean ALLOW_CLIENT_TO_READ_HUMIDITY_DATA = new ConfigBoolean(CATEGORY_SOIL,
            "allowClientToReadHumidityData",true,
            "是否允许客户端读取土壤的湿度数据。默认为允许。在禁止状态下，模组将会对服务器和客户端的网络通信进行修改，以去除土壤的湿度信息。其原理和反矿透原理类似。\n" +
                    "如果您想要允许客户端读取土壤的湿度数据，可以更改此选项为true。这样子，mod将不再修改网络通信，您可以使用其他更专业的mod以阻止客户端阅读土壤湿度数据。\n" +
                    "请注意,部分信息在关闭此项后仍然无法被客户端读取，除非你关闭"+GeneralConfig.COMPATIBLE_FOR_VANILLA_CLIENT.getPath()+"。若关闭前面这个选项，则模组自身无法阻止客户端阅读土壤的湿度数据，即使本配置项为禁用状态。\n" +
                    "该配置项在客户端无效。\n" +
                    "Whether to allow the client to read soil humidity data. Default is enabled. When disabled, the mod will modify network communication between the server and client to remove soil moisture information. The principle is similar to anti-X-ray mechanisms.\n" +
                    "If you wish to allow clients to read soil moisture data, you can change this option to true. In this case, the mod will no longer modify network communication, and you can use other more specialized mods to prevent clients from reading soil moisture data.\n" +
                    "Please note that certain information remains unreadable by the client even after disabling this option, unless you disable " + GeneralConfig.COMPATIBLE_FOR_VANILLA_CLIENT.getPath() + ". If the aforementioned option is disabled, the mod itself cannot prevent clients from reading soil moisture data, even if this configuration is set to disabled.\n" +
                    "This configuration option has no effect on the client side.",true);

    //*********************
    // Generation
    //*********************

    @Config.Comment("土壤相关的世界生成机制控制\n" +
            "Control for soil-related world generation features")
    public static final ConfigCategory CATEGORY_SOIL_GENERATION = CATEGORY_SOIL.getChildCategory("generation");

    public static final ConfigBoolean ENABLE_GENERATION = new ConfigBoolean(CATEGORY_SOIL_GENERATION,
            "enable",true,"开启土壤相关的世界生成机制。\n" +
            "若禁用，自然生成的土壤和其他透水方块将默认不会有湿度，该配置块内的其他功能同样不会生效。\n" +
            "Enable soil-related world generation features.\n" +
            "If disabled, naturally generated soil and other permeable blocks will have no moisture by default, and other functions within this configuration block will also be disabled.");

    public static final ConfigIntegerSet GENERATION_DIMENSION_BLACK_LIST = new ConfigIntegerSet(CATEGORY_SOIL_GENERATION,
            "dimensionBlackList", new ConfigurableSet<>(1,-1),
            "需要禁用土壤相关世界生成机制的维度。\n" +
                    "Dimensions for which soil-related world generation features should be disabled.");

    public static final ConfigSet<ConfigurableBiome> GENERATION_BIOME_BLACK_LIST = new ConfigSet<>(CATEGORY_SOIL_GENERATION,
            "biomeBlockList",new ConfigurableSet<>(
                    new ConfigurableBiome("minecraft:hell"),
            new ConfigurableBiome("minecraft:void"),
            new ConfigurableBiome("minecraft:sky")
    ),"需要禁用土壤相关世界生成机制的生物群系。\n" +
            "Biomes for which soil-related world generation features should be disabled.",ConfigurableBiome::new);

    public static final ConfigBoolean ENABLE_PRE_PROTECTION_OF_WATER_FALLING = new ConfigBoolean(CATEGORY_SOIL_GENERATION,
            "enablePreProtectionFromWaterFalling",true,
            "开启水下落的预保护机制，通过在区块生成的时候检测竖直方向上可能被水流入的地方，并自动生成方块阻止水流动，以一定程度上避免诸如海洋生物群系在一生成就漏海的情况。\n" +
                    "Enable the pre-protection mechanism for water flow by detecting areas vertically susceptible to water inflow during chunk generation and automatically generating blocks to prevent water movement. " +
                    "This helps mitigate issues such as water leakage in ocean biomes immediately after generation.");

    @Config.RequiresMcRestart
    public static final ConfigMap<ConfigurableBiome, ConfigurableBlockState> WATER_PROTECTION_BLOCK = new ConfigMap<>(CATEGORY_SOIL_GENERATION,
            "preProtectionOfWaterFallingBlocks", "每个生物群系用于阻止水下落而生成的方块。默认为石头。",ConfigurableBiome::new,ConfigurableBlockState::getFixedInstanceByString,
            new ConfigEntry<>(new ConfigurableBiome("minecraft","sky"), new ConfigurableBlockState("minecraft:end_stone",0)),
                    new ConfigEntry<>(new ConfigurableBiome("minecraft","hell"),new ConfigurableBlockState("minecraft:netherrack",0)),
            new ConfigEntry<>(new ConfigurableBiome("minecraft:void"),new ConfigurableBlockState("minecraft:air",0))
    ).setKeyClass(ConfigurableBiome.class)
            .setValueClass(ConfigurableBlockState.class);

    //*********************
    // Water
    //*********************

    public static final ConfigCategory CATEGORY_SOIL_WATER = CATEGORY_SOIL.getChildCategory("water");

    @Config.RequiresMcRestart
    public static final ConfigMap<BlockSoilType,Integer> STABLE_HUMIDITY = new ConfigMap<>(CATEGORY_SOIL_WATER,
            "stableHumidityValues","各种土壤的稳定态湿度值",BlockSoilType::getInstanceByString,Integer::parseInt,
            new ConfigEntry<>(BlockSoilType.DIRT,2),
            new ConfigEntry<>(BlockSoilType.COARSE_DIRT,1),
            new ConfigEntry<>(BlockSoilType.PODZOL,1),
            new ConfigEntry<>(BlockSoilType.GRASS,3),
            new ConfigEntry<>(BlockSoilType.GRASS_PATH,3),
            new ConfigEntry<>(BlockSoilType.SAND,1),
            new ConfigEntry<>(BlockSoilType.GRAVEL,0),
            new ConfigEntry<>(BlockSoilType.FARMLAND,1))
            .setKeyFixed(true)
            .setKeyClass(BlockSoilType.class)
            .setValueClass(Integer.class)
            .setValueComment("土壤的最大稳定湿度值。超过该湿度值时，土壤中的水分将有流动的趋势。这还会使得土壤受重力影响而下落。");
}
