package top.qiguaiaaaa.geocraft.api.simulation;

import top.qiguaiaaaa.geocraft.configs.SimulationConfig;

public enum SimulationMode {
    VANILLA,
    VANILLA_LIKE,
    MORE_REALITY;

    private boolean isStringMatched(String s){
        return toString().equalsIgnoreCase(s);
    }

    public static SimulationMode getInstanceByString(String content) {
        for(SimulationMode mode:values()){
            if(mode.isStringMatched(content.trim())) return mode;
        }
        return SimulationConfig.SIMULATION_MODE.getDefaultValue();
    }
}
