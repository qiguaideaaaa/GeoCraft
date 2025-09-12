package top.qiguaiaaaa.geocraft.api.setting;

import net.minecraft.block.state.IBlockState;
import top.qiguaiaaaa.geocraft.api.configs.value.minecraft.ConfigurableBlockState;

/**
 * 查询天圆地方关于方块的配置
 * @author QiguaiAAAA
 */
public final class GeoBlockSetting {
    private static final BlockSettingQuery<Byte> BLOCK_REFLECTIVITY = new BlockSettingQuery<>((byte)12);
    private static final BlockSettingQuery<Integer> BLOCK_HEAT_CAPACITY = new BlockSettingQuery<>(2000);

    /**
     * 设置某个方块指定状态的反射率
     * @param state 方块状态
     * @param reflectivity 反射率，0~100，表示百分比
     */
    public static void setBlockReflectivity(ConfigurableBlockState state, int reflectivity){
        if(reflectivity<0 || reflectivity>100) return;
        BLOCK_REFLECTIVITY.addConfiguration(state,(byte)reflectivity);
    }

    /**
     * 设置方块默认的反射率
     * @param defaultReflectivity 默认反射率
     */
    public static void setDefaultReflectivity(int defaultReflectivity) {
        if(defaultReflectivity<0 || defaultReflectivity>100) return;
        BLOCK_REFLECTIVITY.setDefaultValue((byte)defaultReflectivity);
    }

    public static void setBlockHeatCapacity(ConfigurableBlockState state, int heatCapacity){
        if(heatCapacity<1) return;
        BLOCK_HEAT_CAPACITY.addConfiguration(state,heatCapacity);
    }

    public static void setBlockDefaultHeatCapacity(int heatCapacity){
        if(heatCapacity <1) return;
        BLOCK_HEAT_CAPACITY.setDefaultValue(heatCapacity);
    }

    /**
     * 获得某个方块状态的反射率
     * @param state 方块状态
     * @return 反射率，介于0~100，单位百分比
     */
    public static float getBlockReflectivity(IBlockState state){
        return BLOCK_REFLECTIVITY.querySettingValue(state)/100.0f;
    }

    public static float getDefaultReflectivity(){
        return BLOCK_REFLECTIVITY.getDefaultValue()/100.0f;
    }

    public static int getBlockHeatCapacity(IBlockState state){
        return BLOCK_HEAT_CAPACITY.querySettingValue(state);
    }

    public static int getDefaultHeatCapacity() {
        return BLOCK_HEAT_CAPACITY.getDefaultValue();
    }
}
