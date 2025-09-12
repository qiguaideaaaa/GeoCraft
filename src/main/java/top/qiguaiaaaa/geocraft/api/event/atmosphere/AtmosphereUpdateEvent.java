package top.qiguaiaaaa.geocraft.api.event.atmosphere;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import top.qiguaiaaaa.geocraft.api.atmosphere.Atmosphere;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 当大气更新的时候会调用
 * @author QiguaiAAAA
 */
public class AtmosphereUpdateEvent extends AtmosphereEvent {
    private final Chunk chunk;
    public AtmosphereUpdateEvent(@Nullable Chunk chunk,@Nonnull Atmosphere atmosphere) {
        super(atmosphere);
        this.chunk =chunk;
    }
    @Nullable
    public Chunk getChunk() {
        return chunk;
    }
    @Nullable
    public World getWorld(){
        if(chunk == null) return null;
        return chunk.getWorld();
    }

    /**
     * 当大气可能在指定地方下雨或雪的时候调用
     * @author QiguaiAAAA
     */
    @HasResult
    public static class RainAndSnow extends AtmosphereUpdateEvent{
        private final BlockPos randPos;
        private final double rainPossibility;
        private IBlockState newState;

        /**
         * 创建一个雨雪事件
         * @param chunk 发生的区块
         * @param atmosphere 发生的大气
         * @param randPos 预计会下的地方
         * @param possibility 用于参考的下雨、下雪概率，介于0和1之间
         */
        public RainAndSnow(@Nonnull Chunk chunk,@Nonnull Atmosphere atmosphere,@Nonnull BlockPos randPos, double possibility) {
            super(chunk, atmosphere);
            this.randPos = randPos;
            this.rainPossibility = possibility;
        }

        /**
         * 获取将要下的位置
         * @return 一个位置，表示可以放置水或雪的位置
         */
        public BlockPos getRandPos() {
            return randPos;
        }

        /**
         * 获取大气提供的参考概率
         * @return 下雨、雪的概率
         */
        public double getRainPossibility() {
            return rainPossibility;
        }

        /**
         * 设置{@link #randPos}即下雨、雪的位置的方块状态
         * @param newState 新的方块状态
         */
        public void setState(@Nullable IBlockState newState) {
            this.newState = newState;
        }

        /**
         * 获取当前新的方块状态
         * @return 新的方块状态，若没有设置则为null
         */
        @Nullable
        public IBlockState getState() {
            return newState;
        }

        @Nonnull
        @Override
        public Chunk getChunk() {
            assert super.getChunk() != null;
            return super.getChunk();
        }

        @Nonnull
        @Override
        public World getWorld() {
            assert super.getWorld() != null;
            return super.getWorld();
        }
    }

    /**
     * 当大气更新完成之后发布
     * @author QiguaiAAAA
     */
    public static class Post extends AtmosphereUpdateEvent{
        public final int x,z;

        /**
         * 创建一个大气更新之后发布的事件
         * @param chunk 大气所在区块,若未加载则为null
         * @param atmosphere 大气
         * @param x 大气所在的区块X坐标
         * @param z 大气所在区块的Z坐标
         */
        public Post(@Nullable Chunk chunk,@Nonnull Atmosphere atmosphere, int x, int z) {
            super(chunk, atmosphere);
            this.x = x;
            this.z = z;
        }
    }
}
