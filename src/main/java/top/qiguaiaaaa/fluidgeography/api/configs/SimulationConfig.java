package top.qiguaiaaaa.fluidgeography.api.configs;

import top.qiguaiaaaa.fluidgeography.api.configs.item.CollectionConfigItem;
import top.qiguaiaaaa.fluidgeography.api.configs.item.ConfigItem;
import top.qiguaiaaaa.fluidgeography.api.configs.transfer.BaseTransfer;
import top.qiguaiaaaa.fluidgeography.api.configs.value.collection.ConfigHashSet;
import top.qiguaiaaaa.fluidgeography.api.configs.value.base.ConfigBoolean;
import top.qiguaiaaaa.fluidgeography.api.configs.value.minecraft.ConfigFluid;
import top.qiguaiaaaa.fluidgeography.api.configs.value.number.ConfigDouble;
import top.qiguaiaaaa.fluidgeography.api.configs.value.number.ConfigInteger;
import top.qiguaiaaaa.fluidgeography.api.simulation.SimulationMode;

import static net.minecraftforge.common.config.Configuration.CATEGORY_SPLITTER;

/**
 * 关于流体模拟的配置项目
 */
public final class SimulationConfig {
    public static final String CATEGORY_SIMULATION = "simulation";
    public static final ConfigItem<SimulationMode> SIMULATION_MODE =
            new ConfigItem<>(CATEGORY_SIMULATION,"simulationMode",SimulationMode.VANILLA_LIKE,
                    "设置水物理模拟模式 Set Physical Simulation Mode.\n" +
                            "支持的模式 Support Value: VANILLA | VANILLA_LIKE | MORE_REALITY （原版 | 类原版 | 更真实一些）",true);
    // Vanilla Like Simulation Config
    public static final String CATEGORY_SIMULATION_VANILLA_LIKE = CATEGORY_SIMULATION+CATEGORY_SPLITTER+"vanilla_like";

    public static final ConfigItem<ConfigBoolean> enableInfiniteWater =
            new ConfigItem<>(CATEGORY_SIMULATION_VANILLA_LIKE,"enableInfiniteWater",new ConfigBoolean(false),
                    "是否启用无限水。注意启用之后，由于未经测试，可能会引发一些BUG。\n" +
                            "Set it to true to enable infinite water function of vanilla. PS: Enabling it may cause some problems.");
    public static final ConfigItem<ConfigBoolean> disableInfiniteFluidForAllModFluid =
            new ConfigItem<>(CATEGORY_SIMULATION_VANILLA_LIKE,"disableInfiniteFluidForAllModFluid",new ConfigBoolean(true),
                    "是否禁止所有模组中具有无限液体源功能的液体产生液体源的能力。注意因为未经测试，关闭此选项可能会产生一些BUG。\n" +
                            "Set it to false to enable infinite fluid function of supported fluids in mods. PS: Disabling it may cause some problem.");
    public static final CollectionConfigItem<ConfigFluid, ConfigHashSet<ConfigFluid>> fluidsNotToSimulateInVanillaLike =
            new CollectionConfigItem<>(CATEGORY_SIMULATION_VANILLA_LIKE,"fluidsNotToSimulate",
                    new ConfigHashSet<>(new BaseTransfer<>(new ConfigFluid("TRANSFER"))),
                    "不受此模组影响的流体");
    public static final String CATEGORY_SIMULATION_VANILLA_LIKE_VERTICAL_FLOWING = CATEGORY_SIMULATION_VANILLA_LIKE+CATEGORY_SPLITTER+"vertical_flowing";
    public static final ConfigItem<ConfigInteger> findSourceMaxIterationsWhenVerticalFlowing =
            new ConfigItem<>(CATEGORY_SIMULATION_VANILLA_LIKE_VERTICAL_FLOWING,"maxIterations",new ConfigInteger(255),
                    "流体垂直流动时，寻找可被移动的流体源的最大迭代次数。\n" +
                            "Maximum iterations to find a fluid source block when vertically flowing.");
    public static final ConfigItem<ConfigInteger> findSourceMaxSameLevelIterationsWhenVerticalFlowing =
            new ConfigItem<>(CATEGORY_SIMULATION_VANILLA_LIKE_VERTICAL_FLOWING,"maxSameLevelIterations",new ConfigInteger(0),
                    "流体垂直流动时，在寻找可被移动的流体源时，在同一流体等级上遍历的最大迭代次数。\n" +
                            "Maximum iterations to find a fluid source block via same level fluid block when vertical flowing.");
    public static final String CATEGORY_SIMULATION_VANILLA_LIKE_HORIZONTAL_FLOWING = CATEGORY_SIMULATION_VANILLA_LIKE+CATEGORY_SPLITTER+"horizontal_flowing";
    public static final ConfigItem<ConfigInteger> findSourceMaxIterationsWhenHorizontalFlowing =
            new ConfigItem<>(CATEGORY_SIMULATION_VANILLA_LIKE_HORIZONTAL_FLOWING,"maxIterations",new ConfigInteger(17),
                    "流体水平流动时，寻找可被移动的流体源的最大迭代次数。\n" +
                            "Maximum iterations to find a fluid source block when vertically flowing.");
    public static final ConfigItem<ConfigInteger> findSourceMaxSameLevelIterationsWhenHorizontalFlowing =
            new ConfigItem<>(CATEGORY_SIMULATION_VANILLA_LIKE_HORIZONTAL_FLOWING,"maxSameLevelIterations",new ConfigInteger(16),
                    "流体水平流动时，在寻找可被移动流体源时，在同一流体等级上遍历的最大迭代次数。\n" +
                            "Maximum iterations to find a fluid source block via same level fluid block when horizontally flowing.");
    //More Reality Simulation Config
    public static final String CATEGORY_SIMULATION_MORE_REALITY = CATEGORY_SIMULATION+CATEGORY_SPLITTER+"more_reality";
    public static final ConfigItem<ConfigInteger> slopeFindDistanceForWaterWhenQuantaAbove1 =
            new ConfigItem<>(CATEGORY_SIMULATION_MORE_REALITY,"slopeFindDistanceForWaterWhenQuantaAbove1",new ConfigInteger(6),
                    "在原版水液体量大于1，且周围无液体量低于其超过1的方块时，寻找可流动方位的最大曼哈顿距离。该值越大，液体越稀，对性能的要求越高。");
    public static final ConfigItem<ConfigInteger> slopeFindDistanceForLavaWhenQuantaAbove1 =
            new ConfigItem<>(CATEGORY_SIMULATION_MORE_REALITY,"slopeFindDistanceForLavaWhenQuantaAbove1",new ConfigInteger(4),
                    "在原版岩浆液体量大于1，且周围无液体量低于其超过1的方块时，寻找可流动方位的最大曼哈顿距离。该值越大，液体越稀，对性能的要求越高。");
    public static final ConfigItem<ConfigDouble> slopeFindDistanceMultiplierForModFluidWhenQuantaAbove1 =
            new ConfigItem<>(CATEGORY_SIMULATION_MORE_REALITY,"slopeFindDistanceMultiplierForModFluidWhenQuantaAbove1",new ConfigDouble(1.5d),
                    "当流体量大于1，且周围无流体量低于其超过1的方块时，其他模组所添加的流体寻找可流动方位的最大曼哈顿距离乘数。该值越大，液体越稀，对性能的要求越高。\n" +
                            "实际流体寻找可流动方位的最大曼哈顿距离 = ( (满液体方块液体量 * 该乘数) / 2 ) 向下取整");
    public static final ConfigItem<ConfigInteger> bucketFindFluidMaxDistance =
            new ConfigItem<>(CATEGORY_SIMULATION_MORE_REALITY,"bucketFindFluidMaxDistance",new ConfigInteger(5),
                    "空桶装流体时的寻找流体的最大范围（即从起点到范围边界的曼哈顿距离）。");
    public static final ConfigItem<ConfigBoolean> allowBucketToDrainFluidWhenAmountIsSmallerThan1000mB =
            new ConfigItem<>(CATEGORY_SIMULATION_MORE_REALITY,"allowBucketToDrainFluidWhenAmountIsSmallerThan1000mB",new ConfigBoolean(false),
                    "允许空桶在地上流体少于1000mB时装入流体，注意这时候你不会获得一个装满流体的桶，地上的流体会直接消失。");
    public static final ConfigItem<ConfigInteger> bottleFindFluidMaxDistance =
            new ConfigItem<>(CATEGORY_SIMULATION_MORE_REALITY,"bottleFindFluidMaxDistance",new ConfigInteger(3),
                    "空瓶装流体（一般是水）时寻找流体的最大范围（即从起点到范围边界的曼哈顿距离）");
    public static final CollectionConfigItem<ConfigFluid, ConfigHashSet<ConfigFluid>> fluidsWhoseBucketsBehavesAsVanillaBuckets =
            new CollectionConfigItem<>(CATEGORY_SIMULATION_MORE_REALITY,"fluidsWhoseBucketsBehavesAsVanillaBuckets",
                    new ConfigHashSet<>(new BaseTransfer<>(new ConfigFluid("TRANSFER"))),
                    "流体对应的桶其行为表现不受本模组影响的流体。");
    public static final CollectionConfigItem<ConfigFluid, ConfigHashSet<ConfigFluid>> fluidsNotToSimulate =
            new CollectionConfigItem<>(CATEGORY_SIMULATION_MORE_REALITY,"fluidsNotToSimulate",
                    new ConfigHashSet<>(new BaseTransfer<>(new ConfigFluid("TRANSFER"))),
                    "不受此模组影响的流体。在下方填入的流体也相当于在"+fluidsWhoseBucketsBehavesAsVanillaBuckets.getPath()+"内填入对应流体，即流体对应的桶行为同样也会变为原版的情况。");
    public static final String CATEGORY_SIMULATION_MORE_REALITY_MOD_SUPPORT = CATEGORY_SIMULATION_MORE_REALITY+CATEGORY_SPLITTER+"mod_support";
    // ** IC2 Config
    public static final String CATEGORY_SIMULATION_MORE_REALITY_MOD_SUPPORT_IC2 = CATEGORY_SIMULATION_MORE_REALITY_MOD_SUPPORT+CATEGORY_SPLITTER+"ic2";
    public static final ConfigItem<ConfigBoolean> enableSupportForIC2 =
            new ConfigItem<>(CATEGORY_SIMULATION_MORE_REALITY_MOD_SUPPORT_IC2,"IC2Support",new ConfigBoolean(true),
                    "如果你已经安装了工业时代2，那么这将控制模组是否启用IC2的相关支持，例如泵的专门优化。\n" +
                            "If you have installed Industrial Craft II, this option will control whether the mod enable supports for IC2 or not.",true);
    public static final ConfigItem<ConfigInteger> IC2PumpFluidSearchMaxIterations =
            new ConfigItem<>(CATEGORY_SIMULATION_MORE_REALITY_MOD_SUPPORT_IC2,"pumpSearchFluidMaxIterations",new ConfigInteger(8),
                    "控制泵搜寻流体的迭代次数。");
    // ** IE Config
    public static final String CATEGORY_SIMULATION_MORE_REALITY_MOD_SUPPORT_IE = CATEGORY_SIMULATION_MORE_REALITY_MOD_SUPPORT+CATEGORY_SPLITTER+"immersiveengineering";
    public static final ConfigItem<ConfigBoolean> enableSupportForIE =
            new ConfigItem<>(CATEGORY_SIMULATION_MORE_REALITY_MOD_SUPPORT_IE,"ImmersiveEngineeringSupport",new ConfigBoolean(true),
                    "如果你已经安装了沉浸工程，那么这将控制模组是否启用沉浸工程的相关支持，例如具有物理性质的混凝土液体。\n" +
                            "If you have installed Immersive Engineering, this option will control whether the mod enable supports for Immersive Engineering or not.",true);

}
