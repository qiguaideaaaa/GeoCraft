package top.qiguaiaaaa.fluidgeography.api.simulation;

import top.qiguaiaaaa.fluidgeography.api.configs.value.Configurable;
import top.qiguaiaaaa.fluidgeography.api.configs.SimulationConfig;

public enum SimulationMode implements Configurable {
    VANILLA,
    VANILLA_LIKE,
    MORE_REALITY;

    private boolean isStringMatched(String s){
        return toString().equalsIgnoreCase(s);
    }

    @Override
    public SimulationMode getInstanceByString(String content) {
        for(SimulationMode mode:values()){
            if(mode.isStringMatched(content.trim())) return mode;
        }
        return SimulationConfig.SIMULATION_MODE.getDefaultValue();
    }
}
