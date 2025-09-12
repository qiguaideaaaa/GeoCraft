package top.qiguaiaaaa.geocraft.api.event.block;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fluids.Fluid;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 当静止流体更新的时候发布,目前只有原版流体,即{@link BlockLiquid}<br/>
 * 该事件通过Mixin实现
 * @author QiguaiAAAA
 */
public class StaticLiquidUpdateEvent extends BlockEvent {
    private final Fluid liquid;
    public StaticLiquidUpdateEvent(@Nonnull Fluid liquid,@Nonnull World world,@Nonnull BlockPos pos,@Nonnull IBlockState state) {
        super(world, pos, state);
        this.liquid = liquid;
    }
    @Nonnull
    public Fluid getLiquid() {
        return liquid;
    }

    /**
     * 在流体更新之后发布<br/>
     * 若Result的结果为Allow，且设置了新的方块状态，则流体会调用{@link World#setBlockState(BlockPos, IBlockState)}来将自己变成指定的方块
     */
    @HasResult
    public static class After extends StaticLiquidUpdateEvent{
        private IBlockState newState;
        public After(@Nonnull Fluid liquid,@Nonnull World world,@Nonnull BlockPos pos,@Nonnull IBlockState state) {
            super(liquid, world, pos, state);
        }

        /**
         * 设置流体将会变成的方块状态，设置为null表示不更新
         * @param newState 新方块状态
         */
        public void setNewState(@Nullable IBlockState newState) {
            this.newState = newState;
        }

        /**
         * 获取新的方块状态
         * @return 新的方块状态
         */
        @Nullable
        public IBlockState getNewState() {
            return newState;
        }
    }
}
