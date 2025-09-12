package top.qiguaiaaaa.geocraft.api.configs.value.geo;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 当前游戏使用的水物理模拟模式<br/>
 * 只有天圆地方自己的模拟模式
 */
public enum SimulationMode {
    VANILLA,
    VANILLA_LIKE,
    MORE_REALITY;

    private boolean isStringMatched(@Nullable String s){
        return toString().equalsIgnoreCase(s);
    }

    /**
     * 将对应字符串反序列化为对应模拟模式
     * @param content 字符串
     * @return 模拟模式
     */
    public static @Nonnull SimulationMode getInstanceByString(@Nonnull String content) {
        for(SimulationMode mode:values()){
            if(mode.isStringMatched(content.trim())) return mode;
        }
        return MORE_REALITY;
    }
}
