package top.qiguaiaaaa.fluidgeography.mixin.atmosphere;

import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = WorldServer.class)
public class WorldServerMixin {
    @Redirect(method = "updateBlocks",at = @At(value = "INVOKE",target = "Lnet/minecraft/world/WorldProvider;canDoRainSnowIce(Lnet/minecraft/world/chunk/Chunk;)Z"))
    public boolean canDoRainSnowIce(WorldProvider instance, Chunk chunk) {
        return false;
    }
}
