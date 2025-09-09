package top.qiguaiaaaa.geocraft.api.event.atmosphere;

import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import top.qiguaiaaaa.geocraft.api.atmosphere.system.IAtmosphereSystem;

public class AtmosphereSystemEvent extends Event {
    private final WorldServer world;

    public AtmosphereSystemEvent(WorldServer world) {
        this.world = world;
    }

    public WorldServer getWorld() {
        return world;
    }

    @Cancelable
    public static class Create extends AtmosphereSystemEvent{
        private IAtmosphereSystem systemToBeUsed;

        public Create(WorldServer world) {
            super(world);
        }

        public IAtmosphereSystem getSystem() {
            return systemToBeUsed;
        }

        public void setSystem(IAtmosphereSystem systemToBeUsed) {
            this.systemToBeUsed = systemToBeUsed;
        }
    }
}
