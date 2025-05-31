package top.qiguaiaaaa.fluidgeography.config;

import static top.qiguaiaaaa.fluidgeography.api.configs.AtmosphereConfig.*;
import static top.qiguaiaaaa.fluidgeography.api.configs.GeneralConfig.*;
import static top.qiguaiaaaa.fluidgeography.api.configs.SimulationConfig.*;
import static top.qiguaiaaaa.fluidgeography.config.ConfigurationLoader.registerConfigItem;

public class ConfigInit {
    private static boolean hasLoaded = false;
    public static void initConfigs(){
        if(hasLoaded) return;
        registerConfigItem(leastTemperatureForFluidToCompletelyDestroyBlock);

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

        registerConfigItem(CONSTANT_TEMP_DIMENSIONS);
        registerConfigItem(CLOSED_DIMENSIONS);
        registerConfigItem(SPECIFIC_HEAT_CAPACITIES);
        registerConfigItem(UNDERLYING_REFLECTIVITY);
        registerConfigItem(GROUND_RADIATION_LOSS_RATE);
        registerConfigItem(DEFAULT_UNDERLYING_EMISSIVITY);
        registerConfigItem(UNDERLYING_EMISSIVITY);
        registerConfigItem(ALLOW_CAULDRON_GET_INFINITE_WATER);
        registerConfigItem(ATMOSPHERE_UNDERLYING_RECALCULATE_GAP);;
        hasLoaded = true;
    }
}
