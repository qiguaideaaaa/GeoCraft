package top.qiguaiaaaa.geocraft.configs;

import top.qiguaiaaaa.geocraft.api.configs.value.collection.ConfigurableHashSet;
import top.qiguaiaaaa.geocraft.api.simulation.SimulationMode;
import top.qiguaiaaaa.geocraft.api.configs.item.base.ConfigBoolean;
import top.qiguaiaaaa.geocraft.api.configs.item.base.ConfigCustom;
import top.qiguaiaaaa.geocraft.configs.item.collection.ConfigSet;
import top.qiguaiaaaa.geocraft.api.configs.value.minecraft.ConfigurableFluid;
import top.qiguaiaaaa.geocraft.api.configs.item.number.ConfigDouble;
import top.qiguaiaaaa.geocraft.api.configs.item.number.ConfigInteger;

import java.util.HashSet;

import static net.minecraftforge.common.config.Configuration.CATEGORY_SPLITTER;

/**
 * 关于流体模拟的配置项目
 */
public final class SimulationConfig {
    public static final String CATEGORY_SIMULATION = "simulation";
    public static final ConfigCustom<SimulationMode> SIMULATION_MODE =
            new ConfigCustom<>(CATEGORY_SIMULATION,"simulationMode",SimulationMode.MORE_REALITY,
                    "设置水物理模拟模式 Set Physical Simulation Mode.\n" +
                            "支持的模式 Support Value: VANILLA | VANILLA_LIKE | MORE_REALITY （原版 | 类原版 | 更真实一些）",SimulationMode::getInstanceByString,true);
    // Vanilla Like Simulation Config
    public static final String CATEGORY_SIMULATION_VANILLA_LIKE = CATEGORY_SIMULATION+CATEGORY_SPLITTER+"vanilla_like";

    public static final ConfigBoolean enableInfiniteWater =
            new ConfigBoolean(CATEGORY_SIMULATION_VANILLA_LIKE,"enableInfiniteWater",false,
                    "是否启用无限水。注意启用之后，由于未经测试，可能会引发一些BUG。\n" +
                            "Set it to true to enable infinite water function of vanilla. PS: Enabling it may cause some problems.");
    public static final ConfigBoolean disableInfiniteFluidForAllModFluid =
            new ConfigBoolean(CATEGORY_SIMULATION_VANILLA_LIKE,"disableInfiniteFluidForAllModFluid",true,
                    "是否禁止所有模组中具有无限液体源功能的液体产生液体源的能力。注意因为未经测试，关闭此选项可能会产生一些BUG。\n" +
                            "Set it to false to enable infinite fluid function of supported fluids in mods. PS: Disabling it may cause some problem.");
    public static final ConfigSet<ConfigurableFluid> fluidsNotToSimulateInVanillaLike =
            new ConfigSet<>(CATEGORY_SIMULATION_VANILLA_LIKE,"fluidsNotToSimulate",
                    new ConfigurableHashSet<>(),
                    "不受此模组影响的流体", ConfigurableFluid::new);
    public static final String CATEGORY_SIMULATION_VANILLA_LIKE_VERTICAL_FLOWING = CATEGORY_SIMULATION_VANILLA_LIKE+CATEGORY_SPLITTER+"vertical_flowing";
    public static final ConfigInteger findSourceMaxIterationsWhenVerticalFlowing =
            new ConfigInteger(CATEGORY_SIMULATION_VANILLA_LIKE_VERTICAL_FLOWING,"maxIterations",255,
                    "流体垂直流动时，寻找可被移动的流体源的最大迭代次数。\n" +
                            "Maximum iterations to find a fluid source block when vertically flowing.");
    public static final ConfigInteger findSourceMaxSameLevelIterationsWhenVerticalFlowing =
            new ConfigInteger(CATEGORY_SIMULATION_VANILLA_LIKE_VERTICAL_FLOWING,"maxSameLevelIterations",0,
                    "流体垂直流动时，在寻找可被移动的流体源时，在同一流体等级上遍历的最大迭代次数。\n" +
                            "Maximum iterations to find a fluid source block via same level fluid block when vertical flowing.");
    public static final String CATEGORY_SIMULATION_VANILLA_LIKE_HORIZONTAL_FLOWING = CATEGORY_SIMULATION_VANILLA_LIKE+CATEGORY_SPLITTER+"horizontal_flowing";
    public static final ConfigInteger findSourceMaxIterationsWhenHorizontalFlowing =
            new ConfigInteger(CATEGORY_SIMULATION_VANILLA_LIKE_HORIZONTAL_FLOWING,"maxIterations",17,
                    "流体水平流动时，寻找可被移动的流体源的最大迭代次数。\n" +
                            "Maximum iterations to find a fluid source block when vertically flowing.");
    public static final ConfigInteger findSourceMaxSameLevelIterationsWhenHorizontalFlowing =
            new ConfigInteger(CATEGORY_SIMULATION_VANILLA_LIKE_HORIZONTAL_FLOWING,"maxSameLevelIterations",16,
                    "流体水平流动时，在寻找可被移动流体源时，在同一流体等级上遍历的最大迭代次数。\n" +
                            "Maximum iterations to find a fluid source block via same level fluid block when horizontally flowing.");
    //More Reality Simulation Config
    public static final String CATEGORY_SIMULATION_MORE_REALITY = CATEGORY_SIMULATION+CATEGORY_SPLITTER+"more_reality";
    public static final ConfigInteger slopeFindDistanceForWaterWhenQuantaAbove1 =
            new ConfigInteger(CATEGORY_SIMULATION_MORE_REALITY,"slopeFindDistanceForWaterWhenQuantaAbove1",6,
                    "在原版水液体量大于1，且周围无液体量低于其超过1的方块时，寻找可流动方位的最大曼哈顿距离。该值越大，液体越稀，对性能的要求越高。");
    public static final ConfigInteger slopeFindDistanceForLavaWhenQuantaAbove1 =
            new ConfigInteger(CATEGORY_SIMULATION_MORE_REALITY,"slopeFindDistanceForLavaWhenQuantaAbove1",4,
                    "在原版岩浆液体量大于1，且周围无液体量低于其超过1的方块时，寻找可流动方位的最大曼哈顿距离。该值越大，液体越稀，对性能的要求越高。");
    public static final ConfigDouble slopeFindDistanceMultiplierForModFluidWhenQuantaAbove1 =
            new ConfigDouble(CATEGORY_SIMULATION_MORE_REALITY,"slopeFindDistanceMultiplierForModFluidWhenQuantaAbove1",1.5d,
                    "当流体量大于1，且周围无流体量低于其超过1的方块时，其他模组所添加的流体寻找可流动方位的最大曼哈顿距离乘数。该值越大，液体越稀，对性能的要求越高。\n" +
                            "实际流体寻找可流动方位的最大曼哈顿距离 = ( (满液体方块液体量 * 该乘数) / 2 ) 向下取整");
    public static final ConfigInteger bucketFindFluidMaxDistance =
            new ConfigInteger(CATEGORY_SIMULATION_MORE_REALITY,"bucketFindFluidMaxDistance",5,
                    "空桶装流体时的寻找流体的最大范围（即从起点到范围边界的曼哈顿距离）。");
    public static final ConfigBoolean allowBucketToDrainFluidWhenAmountIsSmallerThan1000mB =
            new ConfigBoolean(CATEGORY_SIMULATION_MORE_REALITY,"allowBucketToDrainFluidWhenAmountIsSmallerThan1000mB",false,
                    "允许空桶在地上流体少于1000mB时装入流体，注意这时候你不会获得一个装满流体的桶，地上的流体会直接消失。");
    public static final ConfigInteger bottleFindFluidMaxDistance =
            new ConfigInteger(CATEGORY_SIMULATION_MORE_REALITY,"bottleFindFluidMaxDistance",3,
                    "空瓶装流体（一般是水）时寻找流体的最大范围（即从起点到范围边界的曼哈顿距离）");
    public static final ConfigSet<ConfigurableFluid> fluidsWhoseBucketsBehavesAsVanillaBuckets =
            new ConfigSet<>(CATEGORY_SIMULATION_MORE_REALITY,"fluidsWhoseBucketsBehavesAsVanillaBuckets",
                    new ConfigurableHashSet<>(),
                    "流体对应的桶其行为表现不受本模组影响的流体。", ConfigurableFluid::new);
    public static final ConfigSet<ConfigurableFluid> fluidsNotToSimulate =
            new ConfigSet<>(CATEGORY_SIMULATION_MORE_REALITY,"fluidsNotToSimulate",
                    new ConfigurableHashSet<>(),
                    "不受此模组影响的流体。在下方填入的流体也相当于在"+fluidsWhoseBucketsBehavesAsVanillaBuckets.getPath()+"内填入对应流体，即流体对应的桶行为同样也会变为原版的情况。",
                    ConfigurableFluid::new);
    public static final String CATEGORY_SIMULATION_MORE_REALITY_MOD_SUPPORT = CATEGORY_SIMULATION_MORE_REALITY+CATEGORY_SPLITTER+"mod_support";
    // ** IC2 Config
    public static final String CATEGORY_SIMULATION_MORE_REALITY_MOD_SUPPORT_IC2 = CATEGORY_SIMULATION_MORE_REALITY_MOD_SUPPORT+CATEGORY_SPLITTER+"ic2";
    public static final ConfigBoolean enableSupportForIC2 =
            new ConfigBoolean(CATEGORY_SIMULATION_MORE_REALITY_MOD_SUPPORT_IC2,"IC2Support",true,
                    "如果你已经安装了工业时代2，那么这将控制模组是否启用IC2的相关支持，例如泵的专门优化。\n" +
                            "If you have installed Industrial Craft II, this option will control whether the mod enable supports for IC2 or not.",true);
    public static final ConfigInteger IC2PumpFluidSearchMaxIterations =
            new ConfigInteger(CATEGORY_SIMULATION_MORE_REALITY_MOD_SUPPORT_IC2,"pumpSearchFluidMaxIterations",8,
                    "控制泵搜寻流体的迭代次数。");
    // ** IE Config
    public static final String CATEGORY_SIMULATION_MORE_REALITY_MOD_SUPPORT_IE = CATEGORY_SIMULATION_MORE_REALITY_MOD_SUPPORT+CATEGORY_SPLITTER+"immersiveengineering";
    public static final ConfigBoolean enableSupportForIE =
            new ConfigBoolean(CATEGORY_SIMULATION_MORE_REALITY_MOD_SUPPORT_IE,"ImmersiveEngineeringSupport",true,
                    "如果你已经安装了沉浸工程，那么这将控制模组是否启用沉浸工程的相关支持，例如具有物理性质的混凝土液体。\n" +
                            "If you have installed Immersive Engineering, this option will control whether the mod enable supports for Immersive Engineering or not.",true);

}
