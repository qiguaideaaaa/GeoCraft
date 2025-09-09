package top.qiguaiaaaa.geocraft.mixin.groundwater.network;

import net.minecraft.block.Block;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SPacketMultiBlockChange;
import net.minecraft.util.math.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.qiguaiaaaa.geocraft.handler.network.NetworkFakeStateHandler;

@Mixin(value = SPacketMultiBlockChange.class)
public class SPacketMultiBlockChangeMixin {
    @Shadow
    private ChunkPos chunkPos;
    @Shadow
    private SPacketMultiBlockChange.BlockUpdateData[] changedBlocks;

    @Inject(method = "writePacketData",at = @At("HEAD"),cancellable = true)
    public void writePacketData(PacketBuffer buf, CallbackInfo ci) {
        ci.cancel();
        buf.writeInt(this.chunkPos.x);
        buf.writeInt(this.chunkPos.z);
        buf.writeVarInt(this.changedBlocks.length);

        for (SPacketMultiBlockChange.BlockUpdateData spacketmultiblockchange$blockupdatedata : this.changedBlocks) {
            buf.writeShort(spacketmultiblockchange$blockupdatedata.getOffset());
            buf.writeVarInt(Block.BLOCK_STATE_IDS.get(NetworkFakeStateHandler.overwriteState(spacketmultiblockchange$blockupdatedata.getBlockState())));
        }
    }
}
