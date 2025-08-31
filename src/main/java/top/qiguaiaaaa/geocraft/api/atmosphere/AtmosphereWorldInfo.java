package top.qiguaiaaaa.geocraft.api.atmosphere;

import net.minecraft.world.WorldServer;
import top.qiguaiaaaa.geocraft.api.atmosphere.system.IAtmosphereSystem;

public class AtmosphereWorldInfo {
    protected final WorldServer world;
    protected IAtmosphereSystem system;
    protected AtmosphereWorldType type = AtmosphereWorldType.NORMAL;

    public AtmosphereWorldInfo(WorldServer world) {
        this.world = world;
    }

    public WorldServer getWorld() {
        return world;
    }

    public IAtmosphereSystem getSystem() {
        return system;
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

    public void setSystem(IAtmosphereSystem system) {
        this.system = system;
    }
}
