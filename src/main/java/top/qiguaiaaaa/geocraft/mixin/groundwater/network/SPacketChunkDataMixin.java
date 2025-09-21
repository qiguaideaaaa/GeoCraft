/*
 * Copyright 2025 QiguaiAAAA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * 版权所有 2025 QiguaiAAAA
 * 根据Apache许可证第2.0版（“本许可证”）许可；
 * 除非符合本许可证的规定，否则你不得使用此文件。
 * 你可以在此获取本许可证的副本：
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * 除非所适用法律要求或经书面同意，在本许可证下分发的软件是“按原样”分发的，
 * 没有任何形式的担保或条件，不论明示或默示。
 * 请查阅本许可证了解有关本许可证下许可和限制的具体要求。
 * 中文译文来自开放原子开源基金会，非官方译文，如有疑议请以英文原文为准
 */

package top.qiguaiaaaa.geocraft.mixin.groundwater.network;

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
