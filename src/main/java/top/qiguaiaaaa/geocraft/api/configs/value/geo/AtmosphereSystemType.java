package top.qiguaiaaaa.geocraft.api.configs.value.geo;

import javax.annotation.Nonnull;

/**
 * 维度使用的大气系统类型，用于判断采用哪个大气系统<br/>
 * 该类只有天圆地方本身的大气系统类型，通过判断玩家是否指定{@link #THIRD_PARTY_ATMOSPHERE_SYSTEM}以判断是否使用第三方模组大气系统。第三方模组应自行实现大气系统类型的配置<br/>
 * 若类型为{@link #NO_ATMOSPHERE_SYSTEM}，则不应添加大气系统
 */
public enum AtmosphereSystemType {
    SURFACE_ATMOSPHERE_SYSTEM("surface"),
    VANILLA_ATMOSPHERE_SYSTEM("vanilla"),
    HALL_ATMOSPHERE_SYSTEM("hall"),
    THIRD_PARTY_ATMOSPHERE_SYSTEM("third_party"),
    NO_ATMOSPHERE_SYSTEM("none");
    public final String configName;
    AtmosphereSystemType(@Nonnull String configName){
        this.configName = configName;
    }

    private boolean isStringMatched(String s){
        return configName.equalsIgnoreCase(s);
    }

    /**
     * 将对应字符串反序列化为对应大气系统类型
     * @param s 字符串
     * @return 反序列化后的大气系统类型
     */
    @Nonnull
    public static AtmosphereSystemType getInstanceByString(@Nonnull String s){
        for(AtmosphereSystemType type:values()){
            if(type.isStringMatched(s.trim())) return type;
        }
        return NO_ATMOSPHERE_SYSTEM;
    }

    @Override
    public String toString() {
        return configName;
    }
}
