package top.qiguaiaaaa.geocraft.mixin.groundwater.chunk;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.chunk.BlockStatePaletteLinear;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.qiguaiaaaa.geocraft.handler.network.NetworkFakeStateHandler;
import top.qiguaiaaaa.geocraft.util.mixinapi.network.NetworkOverridable;

@Mixin(BlockStatePaletteLinear.class)
public class BlockStatePaletteLinearMixin implements NetworkOverridable {
    @Final
    @Shadow
    private IBlockState[] states;
    @Shadow
    private int arraySize;

    @Override
    public void networkWrite(PacketBuffer buf) {
        buf.writeVarInt(this.arraySize);

        for (int i = 0; i < this.arraySize; ++i) {
            IBlockState thisState = this.states[i];
            IBlockState fakeState = NetworkFakeStateHandler.overwriteState(thisState);
            buf.writeVarInt(Block.BLOCK_STATE_IDS.get(fakeState));
        }
    }
}
