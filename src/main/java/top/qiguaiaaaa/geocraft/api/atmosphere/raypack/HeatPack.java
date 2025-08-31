package top.qiguaiaaaa.geocraft.api.atmosphere.raypack;

import top.qiguaiaaaa.geocraft.api.util.APIUtil;

public class HeatPack implements StuffPack {
    public static final int MIN_HEAT = 20;
    protected double heat;
    protected final HeatType type;

    public HeatPack(HeatType type){
        this(type,0);
    }
    public HeatPack(HeatType type, double raw){
        this.type = type;
        this.heat = Math.max(raw,0);
        if(Double.isInfinite(raw) || Double.isNaN(raw)){
            APIUtil.LOGGER.warn("{} creates a NaN or Infinite Heat Pack! Something must be wrong!",APIUtil.callerInfo(1));
        }
    }
    public boolean isEmpty(){
        return heat < MIN_HEAT;
    }
    public double getAmount() {
        return heat;
    }
    public double drawHeat(double amount){
        amount = Math.min(amount,heat);
        heat -= amount;
        return amount;
    }

    public HeatType getType() {
        return type;
    }

    public enum HeatType {
        SHORT_WAVE,LONG_WAVE
    }
}
