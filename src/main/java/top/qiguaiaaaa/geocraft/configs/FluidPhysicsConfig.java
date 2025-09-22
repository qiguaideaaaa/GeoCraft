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

import top.qiguaiaaaa.geocraft.api.configs.ConfigCategory;
import top.qiguaiaaaa.geocraft.api.configs.item.base.ConfigBoolean;
import top.qiguaiaaaa.geocraft.api.configs.item.base.ConfigCustom;
import top.qiguaiaaaa.geocraft.api.configs.item.number.ConfigDouble;
import top.qiguaiaaaa.geocraft.api.configs.item.number.ConfigInteger;
import top.qiguaiaaaa.geocraft.api.configs.value.collection.ConfigurableList;
import top.qiguaiaaaa.geocraft.api.configs.value.geo.FluidPhysicsMode;
import top.qiguaiaaaa.geocraft.api.configs.value.minecraft.ConfigurableFluid;
import top.qiguaiaaaa.geocraft.configs.item.collection.ConfigList;

/**
 * 关于流体物理的配置项目
 */
public final class FluidPhysicsConfig {
    public static final ConfigCategory CATEGORY_SIMULATION = new ConfigCategory("fluid_physics")
            .setComment("流体物理配置项");
    public static final ConfigCustom<FluidPhysicsMode> FLUID_PHYSICS_MODE =
            new ConfigCustom<>(CATEGORY_SIMULATION,"fluidPhysicsMode", FluidPhysicsMode.MORE_REALITY,
                    "设置流体物理模式 Set Fluid Physics Mode.\n" +
                            "支持的模式 Support Values: VANILLA | VANILLA_LIKE | MORE_REALITY （原版 | 类原版 | 更真实一些）", FluidPhysicsMode::getInstanceByString,true);
    // *******************************
    // Vanilla Like Simulation Config
    // *******************************
    public static final ConfigCategory CATEGORY_SIMULATION_VANILLA_LIKE = CATEGORY_SIMULATION.getChildCategory("vanilla_like")
            .setComment("设置流体物理模式为"+FluidPhysicsMode.VANILLA_LIKE+"时的参数");

    public static final ConfigBoolean PRESSURE_SYSTEM_FOR_VANILLA_LIKE =
            new ConfigBoolean(CATEGORY_SIMULATION_VANILLA_LIKE,"enablePressureSystem",true,
                    "是否启用压强系统。\n" +
                            "Enable Pressure System");

    public static final ConfigBoolean enableInfiniteWater =
            new ConfigBoolean(CATEGORY_SIMULATION_VANILLA_LIKE,"enableInfiniteWater",false,
                    "是否启用无限水。注意启用之后，由于未经测试，可能会引发一些BUG。\n" +
                            "Set it to true to enable infinite water function of vanilla. PS: Enabling it may cause some problems.");
    public static final ConfigBoolean disableInfiniteFluidForAllModFluid =
            new ConfigBoolean(CATEGORY_SIMULATION_VANILLA_LIKE,"disableInfiniteFluidForAllModFluid",true,
                    "是否禁止所有模组中具有无限液体源功能的液体产生液体源的能力。注意因为未经测试，关闭此选项可能会产生一些BUG。\n" +
                            "Set it to false to enable infinite fluid function of supported fluids in mods. PS: Disabling it may cause some problem.");
    public static final ConfigList<ConfigurableFluid> fluidsNotToSimulateInVanillaLike =
            new ConfigList<>(CATEGORY_SIMULATION_VANILLA_LIKE,"fluidBlackList",
                    new ConfigurableList<>(),
                    "不受此模式影响的流体", ConfigurableFluid::new);
    public static final ConfigCategory CATEGORY_SIMULATION_VANILLA_LIKE_VERTICAL_FLOWING =
            CATEGORY_SIMULATION_VANILLA_LIKE.getChildCategory("vertical_flowing")
                    .setComment("设置流体垂直流动时的参数");
    public static final ConfigInteger findSourceMaxIterationsWhenVerticalFlowing =
            new ConfigInteger(CATEGORY_SIMULATION_VANILLA_LIKE_VERTICAL_FLOWING,"maxIterations",255,
                    "流体垂直流动时，寻找可被移动的流体源的最大迭代次数。\n" +
                            "Maximum iterations to find a fluid source block when vertically flowing.",1,Integer.MAX_VALUE,false);
    public static final ConfigInteger findSourceMaxSameLevelIterationsWhenVerticalFlowing =
            new ConfigInteger(CATEGORY_SIMULATION_VANILLA_LIKE_VERTICAL_FLOWING,"maxSameLevelIterations",0,
                    "流体垂直流动时，在寻找可被移动的流体源时，在同一流体等级上遍历的最大迭代次数。\n" +
                            "Maximum iterations to find a fluid source block via same level fluid block when vertical flowing.",0,Integer.MAX_VALUE,false);
    public static final ConfigCategory CATEGORY_SIMULATION_VANILLA_LIKE_HORIZONTAL_FLOWING =
            CATEGORY_SIMULATION_VANILLA_LIKE.getChildCategory("horizontal_flowing")
                    .setComment("设置流体水平流动时的参数");
    public static final ConfigInteger findSourceMaxIterationsWhenHorizontalFlowing =
            new ConfigInteger(CATEGORY_SIMULATION_VANILLA_LIKE_HORIZONTAL_FLOWING,"maxIterations",17,
                    "流体水平流动时，寻找可被移动的流体源的最大迭代次数。\n" +
                            "Maximum iterations to find a fluid source block when vertically flowing.",1,Integer.MAX_VALUE,false);
    public static final ConfigInteger findSourceMaxSameLevelIterationsWhenHorizontalFlowing =
            new ConfigInteger(CATEGORY_SIMULATION_VANILLA_LIKE_HORIZONTAL_FLOWING,"maxSameLevelIterations",16,
                    "流体水平流动时，在寻找可被移动流体源时，在同一流体等级上遍历的最大迭代次数。\n" +
                            "Maximum iterations to find a fluid source block via same level fluid block when horizontally flowing.",0,Integer.MAX_VALUE,false);
    //More Reality Simulation Config
    public static final ConfigCategory CATEGORY_SIMULATION_MORE_REALITY = CATEGORY_SIMULATION.getChildCategory("more_reality")
            .setComment("设置流体物理模式为"+FluidPhysicsMode.MORE_REALITY+"时的参数");

    public static final ConfigCategory CATEGORY_SIMULATION_MORE_REALITY_PRESSURE = CATEGORY_SIMULATION_MORE_REALITY.getChildCategory("pressure_system")
            .setComment("压强系统参数");

    public static final ConfigBoolean PRESSURE_SYSTEM_FOR_REALITY = new ConfigBoolean(CATEGORY_SIMULATION_MORE_REALITY_PRESSURE,
            "enablePressureSystem",true,"是否启用压强系统\n" +
            "Enable Pressure System.");


    public static final ConfigBoolean slopeModeForVanillaWhenOnLiquids =
            new ConfigBoolean(CATEGORY_SIMULATION_MORE_REALITY,"enableSlopeModeForVanillaLiquidsWhenOnLiquid",false,
                    "当液体下方也为该液体的时候，为原版液体使用坡度流动算法。");
    public static final ConfigInteger slopeFindDistanceForWaterWhenQuantaAbove1 =
            new ConfigInteger(CATEGORY_SIMULATION_MORE_REALITY,"slopeFindDistanceForWaterWhenQuantaAbove1",6,
                    "在原版水液体量大于1，且周围无液体量低于其超过1的方块时，寻找可流动方位的最大曼哈顿距离。该值越大，液体越稀，对性能的要求越高。",
                    1,Integer.MAX_VALUE,false);
    public static final ConfigInteger slopeFindDistanceForLavaWhenQuantaAbove1 =
            new ConfigInteger(CATEGORY_SIMULATION_MORE_REALITY,"slopeFindDistanceForLavaWhenQuantaAbove1",4,
                    "在原版岩浆液体量大于1，且周围无液体量低于其超过1的方块时，寻找可流动方位的最大曼哈顿距离。该值越大，液体越稀，对性能的要求越高。",
                    1,Integer.MAX_VALUE,false);

    public static final ConfigBoolean slopeModeForModsWhenOnFluids =
            new ConfigBoolean(CATEGORY_SIMULATION_MORE_REALITY,"enableSlopeModeForModFluidsWhenOnFluid",false,
                    "当流体下方也为该流体的时候，为模组流体使用坡度流动算法。");
    public static final ConfigDouble slopeFindDistanceMultiplierForModFluidWhenQuantaAbove1 =
            new ConfigDouble(CATEGORY_SIMULATION_MORE_REALITY,"slopeFindDistanceMultiplierForModFluidWhenQuantaAbove1",1.5d,
                    "当流体量大于1，且周围无流体量低于其超过1的方块时，其他模组所添加的流体寻找可流动方位的最大曼哈顿距离乘数。该值越大，液体越稀，对性能的要求越高。\n" +
                            "实际流体寻找可流动方位的最大曼哈顿距离 = ( (满液体方块液体量 * 该乘数) / 2 ) 向下取整",
                    0.1d,Double.POSITIVE_INFINITY,false);
    public static final ConfigInteger bucketFindFluidMaxDistance =
            new ConfigInteger(CATEGORY_SIMULATION_MORE_REALITY,"bucketFindFluidMaxDistance",5,
                    "空桶装流体时的寻找流体的最大范围（即从起点到范围边界的曼哈顿距离）。",
                    1,Integer.MAX_VALUE,false);
    public static final ConfigBoolean allowBucketToDrainFluidWhenAmountIsSmallerThan1000mB =
            new ConfigBoolean(CATEGORY_SIMULATION_MORE_REALITY,"allowBucketToDrainFluidWhenAmountIsSmallerThan1000mB",false,
                    "允许空桶在地上流体少于1000mB时装入流体，注意这时候你不会获得一个装满流体的桶，地上的流体会直接消失。");
    public static final ConfigInteger bottleFindFluidMaxDistance =
            new ConfigInteger(CATEGORY_SIMULATION_MORE_REALITY,"bottleFindFluidMaxDistance",3,
                    "空瓶装流体（一般是水）时寻找流体的最大范围（即从起点到范围边界的曼哈顿距离）",
                    1,Integer.MAX_VALUE,false);
    public static final ConfigList<ConfigurableFluid> fluidsWhoseBucketsBehavesAsVanillaBuckets =
            new ConfigList<>(CATEGORY_SIMULATION_MORE_REALITY,"fluidsWhoseBucketsBehavesAsVanillaBuckets",
                    new ConfigurableList<>(),
                    "流体对应的桶其行为表现不受本模组影响的流体。", ConfigurableFluid::new);
    public static final ConfigList<ConfigurableFluid> fluidsNotToSimulate =
            new ConfigList<>(CATEGORY_SIMULATION_MORE_REALITY,"fluidBlackList",
                    new ConfigurableList<>(),
                    "不受此模式影响的流体。在下方填入的流体也相当于在"+fluidsWhoseBucketsBehavesAsVanillaBuckets.getPath()+"内填入对应流体，即流体对应的桶行为同样也会变为原版的情况。",
                    ConfigurableFluid::new);
    public static final ConfigCategory CATEGORY_SIMULATION_MORE_REALITY_MOD_SUPPORT = CATEGORY_SIMULATION_MORE_REALITY.getChildCategory("mod_support")
            .setComment("设置第三方模组联动参数");
    // ** IC2 Config
    public static final ConfigCategory CATEGORY_SIMULATION_MORE_REALITY_MOD_SUPPORT_IC2 = CATEGORY_SIMULATION_MORE_REALITY_MOD_SUPPORT.getChildCategory("ic2")
            .setComment("设置关于[IC2]工业时代II的参数");
    public static final ConfigBoolean enableSupportForIC2 =
            new ConfigBoolean(CATEGORY_SIMULATION_MORE_REALITY_MOD_SUPPORT_IC2,"IC2Support",true,
                    "如果你已经安装了工业时代2，那么这将控制模组是否启用IC2的相关支持，例如泵的专门优化。\n" +
                            "If you have installed Industrial Craft II, this option will control whether the mod enable supports for IC2 or not.",true);
    public static final ConfigInteger IC2PumpFluidSearchMaxIterations =
            new ConfigInteger(CATEGORY_SIMULATION_MORE_REALITY_MOD_SUPPORT_IC2,"pumpSearchFluidMaxIterations",8,
                    "控制泵搜寻流体的迭代次数。",1,Integer.MAX_VALUE,false);
    // ** IE Config
    public static final ConfigCategory CATEGORY_SIMULATION_MORE_REALITY_MOD_SUPPORT_IE =
            CATEGORY_SIMULATION_MORE_REALITY_MOD_SUPPORT.getChildCategory("immersiveengineering")
                    .setComment("设置关于[IE]沉浸工程的参数");
    public static final ConfigBoolean enableSupportForIE =
            new ConfigBoolean(CATEGORY_SIMULATION_MORE_REALITY_MOD_SUPPORT_IE,"ImmersiveEngineeringSupport",true,
                    "如果你已经安装了沉浸工程，那么这将控制模组是否启用沉浸工程的相关支持，例如具有物理性质的混凝土液体。\n" +
                            "If you have installed Immersive Engineering, this option will control whether the mod enable supports for Immersive Engineering or not.",true);

}
