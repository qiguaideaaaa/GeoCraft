package top.qiguaiaaaa.fluidgeography.api.configs;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import top.qiguaiaaaa.fluidgeography.api.configs.item.CollectionConfigItem;
import top.qiguaiaaaa.fluidgeography.api.configs.item.ConfigItem;
import top.qiguaiaaaa.fluidgeography.api.configs.item.MapConfigItem;
import top.qiguaiaaaa.fluidgeography.api.configs.value.base.ConfigBoolean;
import top.qiguaiaaaa.fluidgeography.api.configs.value.base.ConfigString;
import top.qiguaiaaaa.fluidgeography.api.configs.value.collection.ConfigHashSet;
import top.qiguaiaaaa.fluidgeography.api.configs.value.map.ConfigMap;
import top.qiguaiaaaa.fluidgeography.api.configs.value.map.entry.BlockIntegerEntry;
import top.qiguaiaaaa.fluidgeography.api.configs.value.map.entry.StringIntegerEntry;
import top.qiguaiaaaa.fluidgeography.api.configs.value.minecraft.ConfigBlock;
import top.qiguaiaaaa.fluidgeography.api.configs.value.number.ConfigDouble;
import top.qiguaiaaaa.fluidgeography.api.configs.value.number.ConfigInteger;

public final class AtmosphereConfig {
    public static final String CATEGORY_ATMOSPHERE = "atmosphere";
    public static final CollectionConfigItem<ConfigInteger, ConfigHashSet<ConfigInteger>> CONSTANT_TEMP_DIMENSIONS = new CollectionConfigItem<>(CATEGORY_ATMOSPHERE,
            "constantTempDimensions", new ConfigHashSet<>(
                    new ConfigInteger(1),
                    new ConfigInteger(-1)
            ),"温度不受太阳辐射影响的维度");
    public static final CollectionConfigItem<ConfigInteger,ConfigHashSet<ConfigInteger>> CLOSED_DIMENSIONS = new CollectionConfigItem<>(CATEGORY_ATMOSPHERE,
            "closedDimensions",new ConfigHashSet<>(
                    new ConfigInteger(-1)
    ),"地形封闭的维度");
    public static final MapConfigItem<ConfigString, ConfigInteger> SPECIFIC_HEAT_CAPACITIES =
            new MapConfigItem<>(CATEGORY_ATMOSPHERE,"specificHeatCapacities",new ConfigMap<>(
                    new StringIntegerEntry("minecraft:water",4200),
                    new StringIntegerEntry("minecraft:flowing_water",4200),
                    new StringIntegerEntry("minecraft:ice",2060),
                    new StringIntegerEntry("minecraft:gravel",920),
                    new StringIntegerEntry("minecraft:sand",840),
                    new StringIntegerEntry("minecraft:sandstone",920),
                    new StringIntegerEntry("minecraft:red_sandstone",920),
                    new StringIntegerEntry("minecraft:concrete",920),
                    new StringIntegerEntry("minecraft:concrete_powder",900),
                    new StringIntegerEntry("minecraft:melon_block",3800),
                    new StringIntegerEntry("minecraft:grass",2000),
                    new StringIntegerEntry("minecraft:dirt",1500),
                    new StringIntegerEntry("minecraft:snow",2060),
                    new StringIntegerEntry("minecraft:snow_layer",2060),
                    new StringIntegerEntry("minecraft:packed_ice",2060),
                    new StringIntegerEntry("minecraft:wool",1360),
                    new StringIntegerEntry("minecraft:air",1),
                    new StringIntegerEntry("minecraft:iron_block",444),
                    new StringIntegerEntry("minecraft:diamond_block",509),
                    new StringIntegerEntry("minecraft:gold_block",126),
                    new StringIntegerEntry("minecraft:leaves",3000),
                    new StringIntegerEntry("minecraft:tallgrass",1300),
                    new StringIntegerEntry("minecraft:cactus",3800),
                    new StringIntegerEntry("minecraft:redstone_block",600),
                    new StringIntegerEntry("minecraft:quartz_block",670),
                    new StringIntegerEntry("minecraft:emerald_block",840),
                    new StringIntegerEntry("minecraft:log",520),
                    new StringIntegerEntry("minecraft:log2",520)
            ),
                    "方块的比热容，默认为1000，单位为FE/(kg·K)");
    public static final MapConfigItem<ConfigBlock,ConfigInteger> UNDERLYING_REFLECTIVITY =
            new MapConfigItem<>(CATEGORY_ATMOSPHERE,"underlyingReflectivity",new ConfigMap<>(
                    new BlockIntegerEntry("minecraft:snow_layer",70),
                    new BlockIntegerEntry("minecraft:snow",70),
                    new BlockIntegerEntry("minecraft:ice",40),
                    new BlockIntegerEntry("minecraft:packed_ice",55),
                    new BlockIntegerEntry("minecraft:water",5),
                    new BlockIntegerEntry("minecraft:flowing_water",3),
                    new BlockIntegerEntry("minecraft:iron_block",50),
                    new BlockIntegerEntry("minecraft:gold_block",98),
                    new BlockIntegerEntry("minecraft:coal_block",0),
                    new BlockIntegerEntry("minecraft:black_shulker_box",1),
                    new BlockIntegerEntry("minecraft:white_shulker_box",50),
                    new BlockIntegerEntry("minecraft:lapis_block",30),
                    new BlockIntegerEntry("minecraft:diamond_block",40),
                    new BlockIntegerEntry("minecraft:red_sandstone",5),
                    new BlockIntegerEntry("minecraft:concrete",15,0),
                    new BlockIntegerEntry("minecraft:concrete",0,20)
            ),
                    "下垫面方块的反射率，默认为8%，单位为%");
    public static final MapConfigItem<ConfigBlock,ConfigInteger> UNDERLYING_EMISSIVITY =
            new MapConfigItem<>(CATEGORY_ATMOSPHERE,"underlyingEmissivity", new ConfigMap<>(
                    new BlockIntegerEntry("minecraft:water",96),
                    new BlockIntegerEntry("minecraft:flowing_water",98),
                    new BlockIntegerEntry("minecraft:sand",90),
                    new BlockIntegerEntry("minecraft:gravel",88),
                    new BlockIntegerEntry("minecraft:sandstone",90),
                    new BlockIntegerEntry("minecraft:red_sandstone",90),
                    new BlockIntegerEntry("minecraft:snow",95),
                    new BlockIntegerEntry("minecraft:snow_layer",95),
                    new BlockIntegerEntry("minecraft:ice",93),
                    new BlockIntegerEntry("minecraft:packed_ice",90),
                    new BlockIntegerEntry("minecraft:dirt",88),
                    new BlockIntegerEntry("minecraft:grass",94),
                    new BlockIntegerEntry("minecraft:leaves",95),
                    new BlockIntegerEntry("minecraft:air",70),
                    new BlockIntegerEntry("minecraft:concrete",94),
                    new BlockIntegerEntry("minecraft:concrete_powder",92),
                    new BlockIntegerEntry("minecraft:iron_block",40),
                    new BlockIntegerEntry("minecraft:diamond_block",50),
                    new BlockIntegerEntry("minecraft:gold_block",20),
                    new BlockIntegerEntry("minecraft:stone",0,92),
                    new BlockIntegerEntry("minecraft:stone",1,90), // 花岗岩
                    new BlockIntegerEntry("minecraft:stone",2,86),
                    new BlockIntegerEntry("minecraft:stone",3,87), // 闪长岩
                    new BlockIntegerEntry("minecraft:stone",4,83),
                    new BlockIntegerEntry("minecraft:stone",5,91), // 安山岩
                    new BlockIntegerEntry("minecraft:stone",6,87),
                    new BlockIntegerEntry("minecraft:clay",92)
            ),
                    "下垫面在热红外波段的发射率");
    public static final ConfigItem<ConfigInteger> DEFAULT_UNDERLYING_EMISSIVITY = new ConfigItem<>(CATEGORY_ATMOSPHERE,
            "underlyingDefaultEmissivity",new ConfigInteger(86),"下垫面默认发射率");
    public static final ConfigItem<ConfigDouble> GROUND_RADIATION_LOSS_RATE = new ConfigItem<>(CATEGORY_ATMOSPHERE,"radiationLossRate",
            new ConfigDouble(0.101),"地表因热辐射直接射向宇宙空间而损失的热量的比例，默认为0.17");
    public static final ConfigItem<ConfigBoolean> ALLOW_CAULDRON_GET_INFINITE_WATER = new ConfigItem<>(CATEGORY_ATMOSPHERE,"allowCauldronGetInfiniteWater",
            new ConfigBoolean(false),"是否允许炼药锅接无限量的水，即在接水时不会消耗大气水");
    public static final ConfigItem<ConfigInteger> ATMOSPHERE_UNDERLYING_RECALCULATE_GAP = new ConfigItem<>(CATEGORY_ATMOSPHERE,
            "atmosphereUnderlyingRecalculateGap",new ConfigInteger(15),"大气重新计算下垫面性质的间隔时间，单位为大气刻");

    private static int getBlockValue(IBlockState state,MapConfigItem<ConfigBlock,ConfigInteger> item,int defaultValue){
        Block block = state.getBlock();
        ResourceLocation registryName = block.getRegistryName();
        if(registryName == null) return defaultValue;
        ConfigInteger integer = item.get(new ConfigBlock(registryName.toString(),block.getMetaFromState(state)));
        if(integer == null) integer = item.get(new ConfigBlock(registryName.toString(),-1));
        if(integer == null) return defaultValue;
        return integer.value;
    }
    public static int getSpecificHeatCapacity(IBlockState state){
        ResourceLocation registryName = state.getBlock().getRegistryName();
        ConfigInteger integer = registryName != null? SPECIFIC_HEAT_CAPACITIES.get(new ConfigString(registryName.toString())):null;
        if(integer == null) return 1000;
        return integer.value;
    }
    public static double getReflectivity(IBlockState state){
        final int defaultReflectivity = 8;
        return getBlockValue(state,UNDERLYING_REFLECTIVITY,defaultReflectivity)/100.0;
    }
    public static double getEmissivity(IBlockState state){
        int defaultEmissivity = DEFAULT_UNDERLYING_EMISSIVITY.getValue().value;
        return getBlockValue(state,UNDERLYING_EMISSIVITY,defaultEmissivity)/100.0;
    }
}
