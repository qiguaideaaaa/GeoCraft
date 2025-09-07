package top.qiguaiaaaa.geocraft.api.configs.value.geo;

public enum AtmosphereSystemType {
    SURFACE_ATMOSPHERE_SYSTEM("surface"),
    VANILLA_ATMOSPHERE_SYSTEM("vanilla"),
    HALL_ATMOSPHERE_SYSTEM("hall"),
    THIRD_PARTY_ATMOSPHERE_SYSTEM("third_party"),
    NO_ATMOSPHERE_SYSTEM("none");
    public final String configName;
    AtmosphereSystemType(String configName){
        this.configName = configName;
    }

    private boolean isStringMatched(String s){
        return configName.equalsIgnoreCase(s);
    }

    public static AtmosphereSystemType getInstanceByString(String s){
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
