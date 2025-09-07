package top.qiguaiaaaa.geocraft.configs;

import static top.qiguaiaaaa.geocraft.configs.AtmosphereConfig.*;
import static top.qiguaiaaaa.geocraft.configs.GeneralConfig.*;
import static top.qiguaiaaaa.geocraft.configs.SimulationConfig.*;
import static top.qiguaiaaaa.geocraft.configs.ConfigurationLoader.registerConfigItem;

public class ConfigInit {
    private static boolean hasLoaded = false;
    public static void initConfigs(){
        if(hasLoaded) return;
        registerConfigItem(leastTemperatureForFluidToCompletelyDestroyBlock);
        registerConfigItem(ALLOW_CLIENT_TO_READ_HUMIDITY_DATA);

        registerConfigItem(SIMULATION_MODE);
        // Vanilla Like Simulation Config
        registerConfigItem(enableInfiniteWater);
        registerConfigItem(disableInfiniteFluidForAllModFluid);
        registerConfigItem(fluidsNotToSimulateInVanillaLike);

        registerConfigItem(findSourceMaxIterationsWhenHorizontalFlowing);
        registerConfigItem(findSourceMaxIterationsWhenVerticalFlowing);
        registerConfigItem(findSourceMaxSameLevelIterationsWhenVerticalFlowing);
        registerConfigItem(findSourceMaxSameLevelIterationsWhenHorizontalFlowing);
        //More Reality Simulation Config
        registerConfigItem(slopeFindDistanceForWaterWhenQuantaAbove1);
        registerConfigItem(slopeFindDistanceForLavaWhenQuantaAbove1);
        registerConfigItem(slopeFindDistanceMultiplierForModFluidWhenQuantaAbove1);
        registerConfigItem(bucketFindFluidMaxDistance);
        registerConfigItem(allowBucketToDrainFluidWhenAmountIsSmallerThan1000mB);
        registerConfigItem(bottleFindFluidMaxDistance);
        registerConfigItem(fluidsWhoseBucketsBehavesAsVanillaBuckets);
        registerConfigItem(fluidsNotToSimulate);
        //** IC2 Config
        registerConfigItem(enableSupportForIC2);
        registerConfigItem(IC2PumpFluidSearchMaxIterations);
        //** IE Config
        registerConfigItem(enableSupportForIE);

        registerConfigItem(ENABLE_DETAIL_LOGGING);
        registerConfigItem(ATMOSPHERE_SYSTEM_TYPES);
        registerConfigItem(SPECIFIC_HEAT_CAPACITIES);
        registerConfigItem(UNDERLYING_REFLECTIVITY);
        registerConfigItem(ALLOW_CAULDRON_GET_INFINITE_WATER);
        registerConfigItem(ATMOSPHERE_UNDERLYING_RECALCULATE_GAP);
        hasLoaded = true;
    }
}
