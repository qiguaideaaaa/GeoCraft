package top.qiguaiaaaa.geocraft.mixin.chunk;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.chunk.BlockStatePaletteLinear;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.qiguaiaaaa.geocraft.util.mixinapi.network.NetworkOverridable;

import static top.qiguaiaaaa.geocraft.api.block.BlockProperties.HUMIDITY;

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
            IBlockState fakeState = thisState;
            if(thisState.getBlock() == Blocks.DIRT){
                fakeState = thisState.withProperty(HUMIDITY,0);
            }else if(thisState.getBlock() == Blocks.GRASS){
                fakeState = thisState.withProperty(HUMIDITY,0);
            }
            buf.writeVarInt(Block.BLOCK_STATE_IDS.get(fakeState));
        }
    }
}
