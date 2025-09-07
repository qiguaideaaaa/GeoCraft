package top.qiguaiaaaa.geocraft.mixin.chunk;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.IntIdentityHashBiMap;
import net.minecraft.world.chunk.BlockStatePaletteHashMap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.qiguaiaaaa.geocraft.util.mixinapi.network.NetworkOverridable;

import static top.qiguaiaaaa.geocraft.api.block.BlockProperties.HUMIDITY;

@Mixin(value = BlockStatePaletteHashMap.class)
public class BlockStatePaletteHashMapMixin implements NetworkOverridable {
    @Final
    @Shadow
    private IntIdentityHashBiMap<IBlockState> statePaletteMap;

    @Override
    public void networkWrite(PacketBuffer buf) {
        int i = statePaletteMap.size();
        buf.writeVarInt(i);

        for (int j = 0; j < i; ++j) {
            IBlockState thisState = statePaletteMap.get(j);
            IBlockState fakeState = thisState;
            if(thisState.getBlock() == Blocks.DIRT){
                fakeState = thisState.withProperty(HUMIDITY,0);
            }else if(thisState.getBlock() == Blocks.GRASS){
                fakeState = thisState.withProperty(HUMIDITY,0);
            }else if(thisState.getBlock() == Blocks.SAND){
                fakeState = thisState.withProperty(HUMIDITY,0);
            }
            buf.writeVarInt(Block.BLOCK_STATE_IDS.get(fakeState));
        }
    }
}
