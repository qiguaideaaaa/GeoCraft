package top.qiguaiaaaa.fluidgeography.api.event.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fluids.Fluid;

public class StaticLiquidUpdateEvent extends BlockEvent {
    private final Fluid liquid;
    public StaticLiquidUpdateEvent(Fluid liquid,World world, BlockPos pos, IBlockState state) {
        super(world, pos, state);
        this.liquid = liquid;
    }
    public Fluid getLiquid() {
        return liquid;
    }
    @HasResult
    public static class After extends StaticLiquidUpdateEvent{
        private IBlockState newState;
        public After(Fluid liquid, World world, BlockPos pos, IBlockState state) {
            super(liquid, world, pos, state);
            newState = state;
        }

        public void setNewState(IBlockState newState) {
            this.newState = newState;
        }

        public IBlockState getNewState() {
            return newState;
        }
    }
}
