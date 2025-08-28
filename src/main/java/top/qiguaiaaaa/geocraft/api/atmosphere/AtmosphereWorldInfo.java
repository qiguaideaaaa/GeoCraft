package top.qiguaiaaaa.geocraft.api.atmosphere;

import net.minecraft.world.WorldServer;

public class AtmosphereWorldInfo {
    protected final WorldServer world;
    protected AtmosphereWorldType type = AtmosphereWorldType.NORMAL;

    public AtmosphereWorldInfo(WorldServer world) {
        this.world = world;
    }

    public WorldServer getWorld() {
        return world;
    }

    public AtmosphereWorldType getType() {
        return type;
    }

    public boolean isTemperatureConstant() {
        return type.isTempConstant;
    }

    public boolean isWorldClosed() {
        return type.isWorldClosed;
    }

    public void setType(AtmosphereWorldType type) {
        this.type = type;
    }
}
