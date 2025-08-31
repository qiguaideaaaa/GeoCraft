package top.qiguaiaaaa.geocraft.mixin.network;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.qiguaiaaaa.geocraft.handler.network.NetworkFakeStateHandler;

@Mixin(value = SPacketBlockChange.class)
public class SPacketBlockChangeMixin {
    @Shadow
    private BlockPos blockPosition;
    @Shadow
    public IBlockState blockState;

    @Inject(method = "writePacketData",at = @At("HEAD"),cancellable = true)
    public void writePacketData(PacketBuffer buf, CallbackInfo ci) {
        ci.cancel();
        buf.writeBlockPos(this.blockPosition);
        buf.writeVarInt(Block.BLOCK_STATE_IDS.get(NetworkFakeStateHandler.overwriteState(blockState)));
    }
}
