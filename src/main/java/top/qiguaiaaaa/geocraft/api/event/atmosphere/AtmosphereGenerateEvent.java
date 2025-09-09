package top.qiguaiaaaa.geocraft.api.event.atmosphere;

import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import top.qiguaiaaaa.geocraft.api.atmosphere.Atmosphere;

public class AtmosphereGenerateEvent extends AtmosphereEvent{
    private final WorldServer world;
    private final Chunk chunk;
    public AtmosphereGenerateEvent(WorldServer world,Chunk chunk,Atmosphere atmosphere) {
        super(atmosphere);
        this.world = world;
        this.chunk= chunk;
    }

    public WorldServer getWorld() {
        return world;
    }

    public Chunk getChunk() {
        return chunk;
    }

    public static class Pre extends AtmosphereGenerateEvent{

        public Pre(WorldServer world, Chunk chunk) {
            super(world, chunk, null);
        }

    }
}
