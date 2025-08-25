package top.qiguaiaaaa.fluidgeography.api.atmosphere.raypack;

/**
 *
 */
public class HeatPack implements RayPack{
    protected double heat;
    protected final HeatType type;

    public HeatPack(HeatType type){
        this(type,0);
    }
    public HeatPack(HeatType type, double raw){
        this.type = type;
        this.heat = Math.max(raw,0);
    }
    public boolean isEmpty(){
        return heat < 1;
    }
    public double getHeat() {
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
