package top.qiguaiaaaa.geocraft.mixin.chunk;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.BitArray;
import net.minecraft.world.chunk.BlockStateContainer;
import net.minecraft.world.chunk.BlockStatePaletteRegistry;
import net.minecraft.world.chunk.IBlockStatePalette;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import top.qiguaiaaaa.geocraft.data.SpecialBlockData;
import top.qiguaiaaaa.geocraft.util.math.MixinUsageBitArray;
import top.qiguaiaaaa.geocraft.util.mixinapi.network.NetworkOverridable;

@Mixin(BlockStateContainer.class)
public class BlockStateContainerMixin implements NetworkOverridable {
    @Shadow
    protected BitArray storage;
    @Shadow
    protected IBlockStatePalette palette;
    @Shadow
    private int bits;

    @Override
    public void networkWrite(PacketBuffer buf) {
        buf.writeByte(this.bits);
        if(palette instanceof BlockStatePaletteRegistry){
            palette.write(buf);
            long[] arr = this.storage.getBackingLongArray();
            MixinUsageBitArray modifiedArray = new MixinUsageBitArray(bits,4096,arr.clone());
            for(int i=0;i<4096;i++){
                int j = modifiedArray.getAt(i);
                int id = j>>4;
                if(id == SpecialBlockData.BLOCK_DIRT_ID){
                    int meta = j & 0b1111;
                    j = (id<<4) | (meta % 3);
                    modifiedArray.setAt(i,j);
                }else if(id == SpecialBlockData.BLOCK_GRASS_ID){
                    modifiedArray.setAt(i,id<<4);
                }else if(id == SpecialBlockData.BLOCK_SAND_ID){
                    int meta = j & 0b1111;
                    j = (id<<4) | (meta % 2);
                    modifiedArray.setAt(i,j);
                }
            }
            buf.writeLongArray(modifiedArray.getBackingLongArray());
        }else{
            ((NetworkOverridable)palette).networkWrite(buf);
            buf.writeLongArray(this.storage.getBackingLongArray());
        }

    }
}
