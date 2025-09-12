package top.qiguaiaaaa.geocraft.geography.atmosphere.debug;

import top.qiguaiaaaa.geocraft.GeoCraft;
import top.qiguaiaaaa.geocraft.api.atmosphere.raypack.HeatPack;

public class DebugHeatPack extends HeatPack {
    private static long ID = 0;
    private final long id = ID++;
    public DebugHeatPack(HeatType type) {
        super(type);
    }

    public DebugHeatPack(HeatType type, double raw) {
        super(type, raw);
        GeoCraft.getLogger().info("Heat Pack {} created, Q={}, Type = {}",id,raw,type.name());
    }

    @Override
    public double drawHeat(double amount) {
        GeoCraft.getLogger().info("Heat Pack {} is drawn by {} FE, old = {} FE, new = {} FE",id,amount,heat,Math.max(heat-amount,0));
        return super.drawHeat(amount);
    }
}
