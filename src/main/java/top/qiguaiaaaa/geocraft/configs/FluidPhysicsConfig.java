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
import top.qiguaiaaaa.geocraft.api.configs.item.collection.ConfigIntegerWeightDistribution;
import top.qiguaiaaaa.geocraft.api.configs.item.collection.ConfigList;
import top.qiguaiaaaa.geocraft.geography.fluid_physics.FluidPressureSearchManager;

/**
 * 关于流体物理的配置项目
 */
@SuppressWarnings("unused")
public final class FluidPhysicsConfig {
    public static final ConfigCategory CATEGORY_FLUID_PHYSICS = new ConfigCategory("fluid_physics")
            .setComment("流体物理配置项");

    public static final ConfigInteger leastTemperatureForFluidToCompletelyDestroyBlock =
            new ConfigInteger(CATEGORY_FLUID_PHYSICS,"leastTemperatureForFluidToCompletelyDestroyBlock",1237,
                    "在流体流动过程中，完全摧毁可摧毁方块（即不会留下掉落物）的最低流体温度，单位为开尔文（K）。",
                    0,Integer.MAX_VALUE,false);
    public static final ConfigCustom<FluidPhysicsMode> FLUID_PHYSICS_MODE =
            new ConfigCustom<>(CATEGORY_FLUID_PHYSICS,"fluidPhysicsMode", FluidPhysicsMode.MORE_REALITY,
                    "设置流体物理模式 Set Fluid Physics Mode.\n" +
                            "支持的模式 Support Values: VANILLA | VANILLA_LIKE | MORE_REALITY （原版 | 类原版 | 更真实一些）", FluidPhysicsMode::getInstanceByString,true);
    //********************************
    // Fluid Updater Config
    //********************************

    public static final ConfigCategory CATEGORY_FLUID_UPDATER = CATEGORY_FLUID_PHYSICS.getChildCategory("fluid_updater")
            .setComment("流体更新任务相关\n");

    public static final ConfigInteger FLUID_UPDATER_MAX_TASKS_PER_TICK = new ConfigInteger(CATEGORY_FLUID_UPDATER,
            "maxTasksPerTick",65536*4,
            "1游戏刻内更新的流体方块数量上限\n" +
                    "The max num of fluid blocks to update within a Game Tick.",1,Integer.MAX_VALUE,true);

    public static final ConfigBoolean FLUID_UPDATER_DROP_EXCESS_TASKS = new ConfigBoolean(CATEGORY_FLUID_UPDATER,
            "dropExcessTasks",true,
            "是否在完成任务更新后，丢弃超额的流体更新任务");

    //********************************
    // Fluid Pressure System Config
    //********************************

    public static final ConfigCategory CATEGORY_FLUID_PRESSURE_SYSTEM = CATEGORY_FLUID_PHYSICS.getChildCategory(FluidPressureSearchManager.CONFIG_CATEGORY_NAME);

    public static final ConfigBoolean RUN_PRESSURE_SYSTEM_AS_ASYNC = new ConfigBoolean(CATEGORY_FLUID_PRESSURE_SYSTEM,
            "async",true,
            "压强系统以多线程模式执行，这可以有效提高性能，但可能导致潜在的多线程并发异常。默认启用，若异步执行的压强系统导致了诸如游戏崩溃的异常，请尝试将此选项改为false以使压强系统同步运行。\n" +
                    "Allow Pressure System running as async, which can greatly improve performance. This option is enabled by default." +
                    "However,if async caused some strange problems such as GAME CRASH, you can try to disable it to make Pressure System run as sync.");

    // ********************
    // Thread Pool

    public static final ConfigCategory CATEGORY_FLUID_PRESSURE_SYSTEM_THREAD_POOL = CATEGORY_FLUID_PRESSURE_SYSTEM.getChildCategory("thread_pool");

    public static final ConfigBoolean PRESSURE_USING_THREAD_POOL = new ConfigBoolean(CATEGORY_FLUID_PRESSURE_SYSTEM_THREAD_POOL,
            "useThreadPool",true,
            "仅在"+RUN_PRESSURE_SYSTEM_AS_ASYNC.getPath()+"为"+true+"的情况下有效\n" +
                    "使用线程池的方式同时运行多个压强任务，可进一步大幅提高性能。\n" +
                    "Valid only when " + RUN_PRESSURE_SYSTEM_AS_ASYNC.getPath() + " is set to " + true + "\n" +
                    "Runs multiple pressure tasks concurrently using a thread pool, which can further significantly improve performance.");

    public static final ConfigInteger PRESSURE_THREAD_COUNT = new ConfigInteger(CATEGORY_FLUID_PRESSURE_SYSTEM_THREAD_POOL,
            "numberOfThreadsInTheThreadPool",Runtime.getRuntime().availableProcessors(),
            "线程池中的线程数量，一般取CPU的核心数。\n" +
                    "The number of threads in the thread pool, typically set to the number of CPU cores.",1,Integer.MAX_VALUE,false);

    // Ended
    //**********************

    public static final ConfigInteger PRESSURE_TICK_DURATION = new ConfigInteger(CATEGORY_FLUID_PRESSURE_SYSTEM,
            "pressureTickDuration",40,
            "1压强刻的理想时长，单位为毫秒。仅在多线程模式下有效。\n" +
                    "The expected milliseconds duration for 1 pressure tick.",
            10,Integer.MAX_VALUE,false
    );

    public static final ConfigInteger PRESSURE_MAX_TASKS_PER_TICK = new ConfigInteger(CATEGORY_FLUID_PRESSURE_SYSTEM,
            "maxTasksPerPressureSystemTick",65536*2,
            "压强系统在一压强刻内最大处理的压强任务数量，若将此值设置过低可能导致任务堆积，过高可能导致在大量流体更新时的卡顿问题。\n" +
                    "Max number of tasks to be dealt within a Pressure Tick. Set it much lower may cause tasks to be accumulate or may cause lagging when " +
                    "there are many fluid blocks updating.",1,Integer.MAX_VALUE,true);

    public static final ConfigInteger PRESSURE_MAX_UPDATES_PER_TICK = new ConfigInteger(CATEGORY_FLUID_PRESSURE_SYSTEM,
            "maxUpdatesPerGameTick",65536*4,
            "压强系统在一游戏刻内通知已完成压强任务的方块的最大数量，若总需要通知的方块数量超过该值，则多余的方块会被放弃更新。\n" +
                    "Max number of blocks to be notified within a Game Tick. If the total number of blocks to notify excesses this value, the left part whill be ignored.",
            1,Integer.MAX_VALUE,true);

    public static final ConfigInteger PRESSURE_DROP_EXCESS_TASKS_PERIOD = new ConfigInteger(CATEGORY_FLUID_PRESSURE_SYSTEM,
            "dropExcessTasksPeriod",200,
            "压强系统清理并丢弃过量的压强任务的周期，单位为压强刻。过高的值可能导致内存泄漏。注意，若此值过低，由于压强系统采用的队列的size()方法的时间复杂度为O(n)，频繁的清理也会导致性能下降。\n" +
                    "The period for Pressure System to clean up excess tasks in Pressure Tick." +
                    "Much higher value may cause potentially memory leak. If the value is too low, the performance may also drop.",2,Integer.MAX_VALUE,false);

    public static final ConfigInteger PRESSURE_EMPTY_RESULTS_PERIOD = new ConfigInteger(CATEGORY_FLUID_PRESSURE_SYSTEM,
            "cleanTaskResultsPeriod",60,
            "清理压强计算结果的周期，单位游戏刻。过高的值可能导致内存泄漏，过低可能导致压强计算的结果来不及被获取就被清理。\n" +
                    "The period to clean up calculated results in Game Tick. Much higher value may cause potentially memory leak, and much lower value may cause" +
                    "the result is being cleaned up before being received.",
            2,Integer.MAX_VALUE,false
    );

    public static final ConfigInteger PRESSURE_CLEAN_UP_THRESHOLD = new ConfigInteger(CATEGORY_FLUID_PRESSURE_SYSTEM,
            "dropExcessTasksThreshold",65536,
            "压强系统在清理任务时，清理触发的阈值任务数量。\n" +
                    "The threshold for Pressure System to clean excess tasks.",
            0,Integer.MAX_VALUE,false
    );

    public static final ConfigBoolean PAUSE_PRESSURE_SYSTEM_WHILE_CHUNK_SAVING = new ConfigBoolean(CATEGORY_FLUID_PRESSURE_SYSTEM,
            "pausePressureSystemWhileChunkSaving",true,
            "当压强系统异步加载的时候，在区块保存时停止压强系统运行，以防止可能的多线程竞争导致的崩溃问题。\n" +
                    "Pause Async Pressure System while chunk is saving to prevent potential crash.",true);

    public static final ConfigInteger PAUSE_TIME_FOR_PRESSURE_PRE_CHUNK_SAVING = new ConfigInteger(CATEGORY_FLUID_PRESSURE_SYSTEM,
            "maxWaitTimeForServerThreadBeforeChunkSaving",20,
            "Minecraft服务器线程在区块保存前等待压强系统停止运行的最大等待时长，单位为毫秒。将此值设置为0则允许线程一直等待下去。\n" +
                    "Max waiting time for Minecraft Server Thread to wait until Pressure System stops before saving chunks. Set it to 0 to allow permanent waiting.",
            0,Integer.MAX_VALUE,false);

    public static final ConfigInteger PAUSE_TIME_FOR_PRESSURE_SYSTEM = new ConfigInteger(CATEGORY_FLUID_PRESSURE_SYSTEM,
            "maxPauseTimeForPressureSystem",200,
            "压强系统暂停运行的最长时间，单位为毫秒。将此值设置为0则允许压强系统一直等待下去，直到被其他线程唤醒。\n" +
                    "Max pause time for the pressure system (ms). A value of 0 means it will wait indefinitely until another thread resumes it.",
            0,Integer.MAX_VALUE,false);


    // *******************************
    // Vanilla Like Fluid Physics Config
    // *******************************
    public static final ConfigCategory CATEGORY_FLUID_PHYSICS_VANILLA_LIKE = CATEGORY_FLUID_PHYSICS.getChildCategory("vanilla_like")
            .setComment("设置流体物理模式为"+FluidPhysicsMode.VANILLA_LIKE+"时的参数\n" +
                    "Parameters when fluid physics mode is set to " + FluidPhysicsMode.VANILLA_LIKE);

    public static final ConfigBoolean PRESSURE_SYSTEM_FOR_VANILLA_LIKE =
            new ConfigBoolean(CATEGORY_FLUID_PHYSICS_VANILLA_LIKE,"enablePressureSystem",true,
                    "是否启用压强系统。\n" +
                            "Enable Pressure System");

    public static final ConfigBoolean enableInfiniteWater =
            new ConfigBoolean(CATEGORY_FLUID_PHYSICS_VANILLA_LIKE,"enableInfiniteWater",false,
                    "是否启用无限水。注意启用之后，由于未经测试，可能会引发一些BUG。\n" +
                            "Set it to true to enable infinite water function of vanilla. PS: Enabling it may cause some problems.");
    public static final ConfigBoolean disableInfiniteFluidForAllModFluid =
            new ConfigBoolean(CATEGORY_FLUID_PHYSICS_VANILLA_LIKE,"disableInfiniteFluidForAllModFluid",true,
                    "是否禁止所有模组中具有无限液体源功能的液体产生液体源的能力。注意因为未经测试，关闭此选项可能会产生一些BUG。\n" +
                            "Set it to false to enable infinite fluid function of supported fluids in mods. PS: Disabling it may cause some problem.");
    public static final ConfigList<ConfigurableFluid> fluidsNotToSimulateInVanillaLike =
            new ConfigList<>(CATEGORY_FLUID_PHYSICS_VANILLA_LIKE,"fluidBlackList",
                    new ConfigurableList<>(),
                    "不受此模式影响的流体", ConfigurableFluid::new);
    public static final ConfigCategory CATEGORY_SIMULATION_VANILLA_LIKE_VERTICAL_FLOWING =
            CATEGORY_FLUID_PHYSICS_VANILLA_LIKE.getChildCategory("vertical_flowing")
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
            CATEGORY_FLUID_PHYSICS_VANILLA_LIKE.getChildCategory("horizontal_flowing")
                    .setComment("设置流体水平流动时的参数");
    public static final ConfigInteger findSourceMaxIterationsWhenHorizontalFlowing =
            new ConfigInteger(CATEGORY_SIMULATION_VANILLA_LIKE_HORIZONTAL_FLOWING,"maxIterations",17,
                    "流体水平流动时，寻找可被移动的流体源的最大迭代次数。\n" +
                            "Maximum iterations to find a fluid source block when vertically flowing.",1,Integer.MAX_VALUE,false);
    public static final ConfigInteger findSourceMaxSameLevelIterationsWhenHorizontalFlowing =
            new ConfigInteger(CATEGORY_SIMULATION_VANILLA_LIKE_HORIZONTAL_FLOWING,"maxSameLevelIterations",16,
                    "流体水平流动时，在寻找可被移动流体源时，在同一流体等级上遍历的最大迭代次数。\n" +
                            "Maximum iterations to find a fluid source block via same level fluid block when horizontally flowing.",0,Integer.MAX_VALUE,false);
    //******************************
    //More Reality Fluid Physics Config
    //******************************
    public static final ConfigCategory CATEGORY_SIMULATION_MORE_REALITY = CATEGORY_FLUID_PHYSICS.getChildCategory("more_reality")
            .setComment("设置流体物理模式为"+FluidPhysicsMode.MORE_REALITY+"时的参数");

    // ********************
    // Pressure System

    public static final ConfigCategory CATEGORY_SIMULATION_MORE_REALITY_PRESSURE = CATEGORY_SIMULATION_MORE_REALITY.getChildCategory("pressure_system")
            .setComment("压强系统参数");

    public static final ConfigBoolean PRESSURE_SYSTEM_FOR_REALITY = new ConfigBoolean(CATEGORY_SIMULATION_MORE_REALITY_PRESSURE,
            "enablePressureSystem",true,"是否启用压强系统\n" +
            "Enable Pressure System.");

    public static final ConfigDouble POSSIBILITY_FOR_STATIC_VANILLA_LIQUID_TO_CREATE_PRESSURE_TASK = new ConfigDouble(CATEGORY_SIMULATION_MORE_REALITY_PRESSURE,
            "possibilityForVanillaStaticLiquidToCreatePressureTask",0.4,
            "原版流体处于静止状态时，创建压强任务的可能性。过高的值可能导致压强任务的频繁创建，从而导致卡顿。\n" +
                    "Possibility for Vanilla static liquids to create a pressure task. Higher value may cause the pressure tasks to be created frequently and then cause lagging.",
            0,0.9999,false
    );

    public static final ConfigDouble POSSIBILITY_FOR_CLASSIC_FLUIDS_TO_CREATE_PRESSURE_TASK = new ConfigDouble(CATEGORY_SIMULATION_MORE_REALITY_PRESSURE,
            "possibilityForModClassicFluidsToCreatePressureTask",0.4,
            "继承自BlockFluidClassic的模组流体处于静止状态时，创建压强任务的可能性。过高的值可能导致压强任务的频繁创建，从而导致卡顿。\n" +
                    "Possibility for Vanilla static liquids to create a pressure task. Higher value may cause the pressure tasks to be created frequently and then cause lagging.",
            0,0.9999,false
    );

    public static final ConfigIntegerWeightDistribution WEIGHT_DISTRIBUTION_FOR_PRESSURE_SEARCH_RANGE = new ConfigIntegerWeightDistribution(CATEGORY_SIMULATION_MORE_REALITY_PRESSURE,
            "pressureSearchRangeWeights",new ConfigurableList<>(0,0,0,80,10,5,4,1),
            "压强搜寻的范围等级概率分布。第一个表示范围等级为-1的权重，第二个表示为范围等级为0的权重，以此类推。\n" +
                    "例如，[0,0,0,80,10,5,4,1]表示下面的概率分布\n" +
                    "范围等级 -> 概率\n" +
                    "-1 -> 0%\n" +
                    "0 -> 0%\n" +
                    "1 -> 0%\n" +
                    "2 -> 80%\n" +
                    "3 -> 10%\n" +
                    "4 -> 5%\n" +
                    "5 -> 4%\n" +
                    "6 -> 1%\n" +
                    "压强搜寻具体范围，即广度优先搜索的迭代最大次数，等于2^(范围等级+5)。例如，范围等级为2表示迭代最大次数为128。本列表支持的最小范围等级为-1，表示迭代最大次数为16。")
            .setMinValue(0)
            .setBegin(-1)
            .setMaxListSize(17);

    public static final ConfigInteger REALITY_MAX_SEARCH_TIMES_PER_SEARCH_FOR_SMALL_RANGE_TASK = new ConfigInteger(CATEGORY_SIMULATION_MORE_REALITY_PRESSURE,
            "maxSearchTimesPerSearchForSmallRangeTask",128,
            "小范围流体压强任务在单次更新中，最大的迭代次数。若任务的搜索范围小于该值，则该任务会被转换为单次搜寻任务，从而大幅度减少内存开销。但值越大也意味着对CPU性能要求更高。\n" +
                    "Max iterated times in a single search for Small Range Pressure Search Task.If the search range of task is smaller than or equal to this, " +
                    "the Task will be transformed to single search task to reduce memory usage. However, higher value means more cpu load needed.",
            1,511,true);

    public static final ConfigInteger REALITY_MAX_SEARCH_TIMES_PER_SEARCH_FOR_LARGE_RANGE_TASK = new ConfigInteger(CATEGORY_SIMULATION_MORE_REALITY_PRESSURE,
            "maxSearchTimesPerSearchForLargeRangeTask",256,
            "大范围流体压强任务在单次更新中，最大的迭代次数。值越大意味着对CPU性能要求更高。\n" +
                    "Max iterated times in a single search for Large Range Pressure Search Task. Higher value means more cpu load needed.",
            1, 1048575,true);

    // Ended
    //**********************

    //**********************
    // Slope Algorithm

    public static final ConfigCategory CATEGORY_MORE_REALITY_SLOPE = CATEGORY_SIMULATION_MORE_REALITY.getChildCategory("slope_algorithm");

    public static final ConfigBoolean slopeModeForVanillaWhenOnLiquidsAndQuantaIs1 =
            new ConfigBoolean(CATEGORY_MORE_REALITY_SLOPE,"enableSlopeModeForVanillaLiquidsWhenOnLiquidAndQuantaIs1",false,
                    "当单层液体下方也为该液体的时候，为原版液体使用坡度流动算法。");

    public static final ConfigBoolean slopeModeForVanillaWhenOnLiquidsAndQuantaAbove1 =
            new ConfigBoolean(CATEGORY_MORE_REALITY_SLOPE,"enableSlopeModeForVanillaLiquidsWhenOnLiquidAndQuantaAbove1",false,
                    "当多层液体下方也为该液体的时候，为原版液体使用坡度流动算法。");
    public static final ConfigInteger slopeFindDistanceForWaterWhenQuantaAbove1 =
            new ConfigInteger(CATEGORY_MORE_REALITY_SLOPE,"slopeFindDistanceForWaterWhenQuantaAbove1",6,
                    "在原版水液体量大于1，且周围无液体量低于其超过1的方块时，寻找可流动方位的最大曼哈顿距离。该值越大，液体越稀，对性能的要求越高。",
                    1,Integer.MAX_VALUE,false);
    public static final ConfigInteger slopeFindDistanceForLavaWhenQuantaAbove1 =
            new ConfigInteger(CATEGORY_MORE_REALITY_SLOPE,"slopeFindDistanceForLavaWhenQuantaAbove1",4,
                    "在原版岩浆液体量大于1，且周围无液体量低于其超过1的方块时，寻找可流动方位的最大曼哈顿距离。该值越大，液体越稀，对性能的要求越高。",
                    1,Integer.MAX_VALUE,false);

    public static final ConfigBoolean slopeModeForModsWhenOnFluidsAndQuantaIs1 =
            new ConfigBoolean(CATEGORY_MORE_REALITY_SLOPE,"enableSlopeModeForModFluidsWhenOnFluidAndQuantaIs1",false,
                    "当单层流体下方也为该流体的时候，为模组流体使用坡度流动算法。");

    public static final ConfigBoolean slopeModeForModsWhenOnFluidsAndQuantaAbove1 =
            new ConfigBoolean(CATEGORY_MORE_REALITY_SLOPE,"enableSlopeModeForModFluidsWhenOnFluidAndQuantaAbove1",false,
                    "当多层流体下方也为该流体的时候，为模组流体使用坡度流动算法。");
    public static final ConfigDouble slopeFindDistanceMultiplierForModFluidWhenQuantaAbove1 =
            new ConfigDouble(CATEGORY_MORE_REALITY_SLOPE,"slopeFindDistanceMultiplierForModFluidWhenQuantaAbove1",1.5d,
                    "当流体量大于1，且周围无流体量低于其超过1的方块时，其他模组所添加的流体寻找可流动方位的最大曼哈顿距离乘数。该值越大，液体越稀，对性能的要求越高。\n" +
                            "实际流体寻找可流动方位的最大曼哈顿距离 = ( (满液体方块液体量 * 该乘数) / 2 ) 向下取整",
                    0.1d,Double.POSITIVE_INFINITY,false);

    // Ended
    //**********************

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
