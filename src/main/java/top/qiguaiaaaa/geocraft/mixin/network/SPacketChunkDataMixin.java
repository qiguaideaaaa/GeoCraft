package top.qiguaiaaaa.geocraft.mixin.network;

import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.qiguaiaaaa.geocraft.util.mixinapi.network.NetworkOverridable;

@Mixin(value = SPacketChunkData.class)
public class SPacketChunkDataMixin {
    @Shadow
    private boolean fullChunk;

    @Inject(method = "extractChunkData",at =@At("HEAD"),cancellable = true)
    public void extractChunkData(PacketBuffer buf, Chunk chunkIn, boolean writeSkylight, int changedSectionFilter, CallbackInfoReturnable<Integer> cir) {
        cir.cancel();
        int i = 0;
        ExtendedBlockStorage[] aextendedblockstorage = chunkIn.getBlockStorageArray();
        int j = 0;

        for (int k = aextendedblockstorage.length; j < k; ++j) {
            ExtendedBlockStorage extendedblockstorage = aextendedblockstorage[j];

            if (extendedblockstorage != Chunk.NULL_BLOCK_STORAGE && (!this.isFullChunk() || !extendedblockstorage.isEmpty()) && (changedSectionFilter & 1 << j) != 0) {
                i |= 1 << j;
                ((NetworkOverridable)extendedblockstorage.getData()).networkWrite(buf);
                buf.writeBytes(extendedblockstorage.getBlockLight().getData());

                if (writeSkylight) {
                    buf.writeBytes(extendedblockstorage.getSkyLight().getData());
                }
            }
        }

        if (this.isFullChunk())
        {
            buf.writeBytes(chunkIn.getBiomeArray());
        }
        cir.setReturnValue(i);
    }
    @Shadow
    public boolean isFullChunk() {
        return this.fullChunk;
    }
}
