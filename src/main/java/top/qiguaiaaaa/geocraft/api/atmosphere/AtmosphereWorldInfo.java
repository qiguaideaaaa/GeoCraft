package top.qiguaiaaaa.geocraft.api.atmosphere;

import net.minecraft.world.WorldServer;
import top.qiguaiaaaa.geocraft.api.atmosphere.system.IAtmosphereSystem;

public class AtmosphereWorldInfo {
    protected final WorldServer world;
    protected IAtmosphereSystem system;

    public AtmosphereWorldInfo(WorldServer world) {
        this.world = world;
    }

    public WorldServer getWorld() {
        return world;
    }

    public IAtmosphereSystem getSystem() {
        return system;
    }

    public void setSystem(IAtmosphereSystem system) {
        this.system = system;
    }
}
