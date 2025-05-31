package top.qiguaiaaaa.fluidgeography.api.event.atmosphere;

import net.minecraftforge.fml.common.eventhandler.Event;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.Atmosphere;

public class AtmosphereEvent extends Event {
    private final Atmosphere atmosphere;

    public AtmosphereEvent(Atmosphere atmosphere) {
        this.atmosphere = atmosphere;
    }

    public Atmosphere getAtmosphere() {
        return atmosphere;
    }
}
