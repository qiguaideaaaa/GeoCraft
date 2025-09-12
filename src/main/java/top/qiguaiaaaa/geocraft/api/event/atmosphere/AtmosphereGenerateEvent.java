package top.qiguaiaaaa.geocraft.api.event.atmosphere;

import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import top.qiguaiaaaa.geocraft.api.atmosphere.Atmosphere;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 大气生成事件，当大气生成的时候会调用
 */
public class AtmosphereGenerateEvent extends AtmosphereEvent{
    private final WorldServer world;
    private final Chunk chunk;
    public AtmosphereGenerateEvent(@Nonnull WorldServer world,@Nonnull Chunk chunk,@Nullable Atmosphere atmosphere) {
        super(atmosphere);
        this.world = world;
        this.chunk= chunk;
    }
    @Nonnull
    public WorldServer getWorld() {
        return world;
    }
    @Nonnull
    public Chunk getChunk() {
        return chunk;
    }

    @Nullable
    @Override
    public Atmosphere getAtmosphere() {
        return super.getAtmosphere();
    }

    /**
     * 在大气生成之前，这时候{@link #getAtmosphere()}是null
     */
    public static class Pre extends AtmosphereGenerateEvent{

        public Pre(@Nonnull WorldServer world,@Nonnull Chunk chunk) {
            super(world, chunk, null);
        }

    }
}
