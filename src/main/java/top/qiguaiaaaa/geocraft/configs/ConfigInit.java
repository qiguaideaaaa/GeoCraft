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

import static top.qiguaiaaaa.geocraft.configs.AtmosphereConfig.*;
import static top.qiguaiaaaa.geocraft.configs.ConfigurationLoader.registerConfigCategory;
import static top.qiguaiaaaa.geocraft.configs.GeneralConfig.*;
import static top.qiguaiaaaa.geocraft.configs.FluidPhysicsConfig.*;
import static top.qiguaiaaaa.geocraft.configs.ConfigurationLoader.registerConfigItem;

public class ConfigInit {
    private static boolean hasLoaded = false;
    public static void initConfigs(){
        if(hasLoaded) return;
        //********
        // GENERAL
        //********
        registerConfigCategory(ConfigCategory.GENERAL);
        registerConfigItem(ALLOW_CLIENT_TO_READ_HUMIDITY_DATA);
        //BlockUpdater
        registerConfigCategory(CATEGORY_BLOCK_UPDATER);
        registerConfigItem(BLOCK_UPDATER_MAX_UPDATES_BLOCK);

        //**************
        //Fluid Physics
        //**************
        registerConfigCategory(CATEGORY_FLUID_PHYSICS);
        registerConfigItem(leastTemperatureForFluidToCompletelyDestroyBlock);
        registerConfigItem(FLUID_PHYSICS_MODE);
        // Fluid Updater Config
        registerConfigCategory(CATEGORY_FLUID_UPDATER);
        registerConfigItem(FLUID_UPDATER_MAX_TASKS_PER_TICK);
        registerConfigItem(FLUID_UPDATER_DROP_EXCESS_TASKS);
        // Pressure System Config
        registerConfigCategory(CATEGORY_FLUID_PRESSURE_SYSTEM);
        registerConfigItem(RUN_PRESSURE_SYSTEM_AS_ASYNC);
        registerConfigItem(PRESSURE_TICK_DURATION);
        registerConfigItem(PRESSURE_MAX_TASKS_PER_TICK);
        registerConfigItem(PRESSURE_MAX_UPDATES_PER_TICK);
        registerConfigItem(PRESSURE_DROP_EXCESS_TASKS_PERIOD);
        registerConfigItem(PRESSURE_EMPTY_RESULTS_PERIOD);
        registerConfigItem(PRESSURE_CLEAN_UP_THRESHOLD);
        registerConfigItem(PAUSE_PRESSURE_SYSTEM_WHILE_CHUNK_SAVING);;
        // Vanilla Like Simulation Config
        registerConfigCategory(CATEGORY_FLUID_PHYSICS_VANILLA_LIKE);
        registerConfigItem(enableInfiniteWater);
        registerConfigItem(disableInfiniteFluidForAllModFluid);
        registerConfigItem(fluidsNotToSimulateInVanillaLike);
        registerConfigItem(PRESSURE_SYSTEM_FOR_VANILLA_LIKE);

        registerConfigCategory(CATEGORY_SIMULATION_VANILLA_LIKE_VERTICAL_FLOWING);
        registerConfigItem(findSourceMaxIterationsWhenHorizontalFlowing);
        registerConfigItem(findSourceMaxIterationsWhenVerticalFlowing);
        registerConfigCategory(CATEGORY_SIMULATION_VANILLA_LIKE_HORIZONTAL_FLOWING);
        registerConfigItem(findSourceMaxSameLevelIterationsWhenVerticalFlowing);
        registerConfigItem(findSourceMaxSameLevelIterationsWhenHorizontalFlowing);
        //More Reality Simulation Config
        registerConfigCategory(CATEGORY_SIMULATION_MORE_REALITY);

        registerConfigCategory(CATEGORY_SIMULATION_MORE_REALITY_PRESSURE);
        registerConfigItem(PRESSURE_SYSTEM_FOR_REALITY);
        registerConfigItem(POSSIBILITY_FOR_STATIC_VANILLA_LIQUID_TO_CREATE_PRESSURE_TASK);
        registerConfigItem(POSSIBILITY_FOR_CLASSIC_FLUIDS_TO_CREATE_PRESSURE_TASK);
        registerConfigItem(WEIGHT_DISTRIBUTION_FOR_PRESSURE_SEARCH_RANGE);
        registerConfigItem(REALITY_MAX_SEARCH_TIMES_PER_SEARCH_FOR_SMALL_RANGE_TASK);
        registerConfigItem(REALITY_MAX_SEARCH_TIMES_PER_SEARCH_FOR_LARGE_RANGE_TASK);

        registerConfigCategory(CATEGORY_MORE_REALITY_SLOPE);
        registerConfigItem(slopeModeForVanillaWhenOnLiquidsAndQuantaIs1);
        registerConfigItem(slopeModeForVanillaWhenOnLiquidsAndQuantaAbove1);
        registerConfigItem(slopeModeForModsWhenOnFluidsAndQuantaIs1);
        registerConfigItem(slopeModeForModsWhenOnFluidsAndQuantaAbove1);
        registerConfigItem(slopeFindDistanceForWaterWhenQuantaAbove1);
        registerConfigItem(slopeFindDistanceForLavaWhenQuantaAbove1);
        registerConfigItem(slopeFindDistanceMultiplierForModFluidWhenQuantaAbove1);

        registerConfigItem(bucketFindFluidMaxDistance);
        registerConfigItem(allowBucketToDrainFluidWhenAmountIsSmallerThan1000mB);
        registerConfigItem(bottleFindFluidMaxDistance);
        registerConfigItem(fluidsWhoseBucketsBehavesAsVanillaBuckets);
        registerConfigItem(fluidsNotToSimulate);

        registerConfigCategory(CATEGORY_SIMULATION_MORE_REALITY_MOD_SUPPORT);
        //** IC2 Config
        registerConfigCategory(CATEGORY_SIMULATION_MORE_REALITY_MOD_SUPPORT_IC2);
        registerConfigItem(enableSupportForIC2);
        registerConfigItem(IC2PumpFluidSearchMaxIterations);
        //** IE Config
        registerConfigCategory(CATEGORY_SIMULATION_MORE_REALITY_MOD_SUPPORT_IE);
        registerConfigItem(enableSupportForIE);

        //Atmosphere
        registerConfigCategory(CATEGORY_ATMOSPHERE);
        registerConfigItem(ENABLE_DETAIL_LOGGING);
        registerConfigItem(ATMOSPHERE_SYSTEM_TYPES);
        registerConfigItem(SPECIFIC_HEAT_CAPACITIES);
        registerConfigItem(UNDERLYING_REFLECTIVITY);
        registerConfigItem(ALLOW_CAULDRON_GET_INFINITE_WATER);
        registerConfigItem(ATMOSPHERE_UNDERLYING_RECALCULATE_GAP);
        hasLoaded = true;
    }
}
