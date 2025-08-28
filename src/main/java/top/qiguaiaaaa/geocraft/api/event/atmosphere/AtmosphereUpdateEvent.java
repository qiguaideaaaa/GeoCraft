package top.qiguaiaaaa.geocraft.api.event.atmosphere;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import top.qiguaiaaaa.geocraft.api.atmosphere.Atmosphere;

public class AtmosphereUpdateEvent extends AtmosphereEvent {
    private final Chunk chunk;
    public AtmosphereUpdateEvent(Chunk chunk, Atmosphere atmosphere) {
        super(atmosphere);
        this.chunk =chunk;
    }

    public Chunk getChunk() {
        return chunk;
    }
    public World getWorld(){
        return chunk.getWorld();
    }
    @HasResult
    public static class RainAndSnow extends AtmosphereUpdateEvent{
        private final BlockPos randPos;
        private final double rainPossibility;
        private IBlockState newState;
        public RainAndSnow(Chunk chunk, Atmosphere atmosphere, BlockPos randPos, double possibility) {
            super(chunk, atmosphere);
            this.randPos = randPos;
            this.rainPossibility = possibility;
        }

        public BlockPos getRandPos() {
            return randPos;
        }

        public double getRainPossibility() {
            return rainPossibility;
        }

        public void setState(IBlockState newState) {
            this.newState = newState;
        }

        public IBlockState getState() {
            return newState;
        }
    }
    public static class Finish extends AtmosphereUpdateEvent{
        public Finish(Chunk chunk, Atmosphere atmosphere) {
            super(chunk, atmosphere);
        }
    }
}
