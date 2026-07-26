/*
 * Copyright 2026 QGMoe
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
 * 版权所有 2026 QGMoe
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

package 清汩萌.天圆地方.世界.区块;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityDispatcher;
import org.junit.jupiter.api.Assertions;
import 清汩萌.天圆地方.能力.聚合能力提供器;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author QGMoe
 */
public class 模拟区块 extends Chunk {
    protected final 聚合能力提供器 $聚合能力 = new 聚合能力提供器();

    public 模拟区块(final @Nonnull World worldIn,final int x,final int z) {
        super(worldIn, x, z);
        this.markLoaded(true);
        this.setTerrainPopulated(true);
        this.setLightPopulated(true);
    }

    @Nullable
    @Override
    public IBlockState setBlockState(final @Nonnull BlockPos pos,final @Nonnull IBlockState state) {
        final int x = pos.getX() & 0xF;
        final int y = pos.getY();
        final int z = pos.getZ() & 0xF;
        @Nonnull IBlockState oldState = this.getBlockState(pos);

        if (oldState == state) return null;

        final Block block = state.getBlock();
        final Block oldBlock = oldState.getBlock();
        @Nonnull ExtendedBlockStorage extendedblockstorage = this.getBlockStorageArray()[y >> 4];

        if (extendedblockstorage == NULL_BLOCK_STORAGE) {
            if (block == Blocks.AIR) return null;

            extendedblockstorage = new ExtendedBlockStorage(y & 0xFFFF_FFF0, false);
            this.getBlockStorageArray()[y >> 4] = extendedblockstorage;
        }

        extendedblockstorage.set(x, y & 0xF, z, state);
        if(extendedblockstorage.get(x, y & 0xF, z).getBlock() != block) return null;

        if (!getWorld().isRemote && oldBlock != block) block.onBlockAdded(this.getWorld(), pos, state);
        this.markDirty();
        return oldState;
    }

    @Override
    public void onLoad() {}

    @Override
    public void onUnload() {
        Assertions.fail("模拟区块不能卸载");
    }

    @Override
    public void populate(final @Nonnull IChunkProvider chunkProvider,final @Nonnull IChunkGenerator chunkGenrator) {}

    @Override
    protected void populate(final @Nonnull IChunkGenerator generator) {}

    @Override
    public boolean isPopulated() {
        return true;
    }

    @Override
    public boolean wasTicked() {
        return true;
    }

    @Override
    public void read(final @Nonnull PacketBuffer buf,final int availableSections,final boolean groundUpContinuous) {}

    @Nonnull
    public 聚合能力提供器 获取聚合能力(){
        return this.$聚合能力;
    }

    @Nullable
    @Override
    public CapabilityDispatcher getCapabilities() {
        return Assertions.fail("模拟区块不支持 CapabilityDispatcher");
    }

    @Override
    public boolean hasCapability(final @Nonnull Capability<?> capability, @Nullable final EnumFacing facing) {
        return $聚合能力.hasCapability(capability, facing);
    }

    @Override
    @Nullable
    public <T> T getCapability(final @Nonnull Capability<T> capability, @Nullable final EnumFacing facing) {
        return $聚合能力.getCapability(capability, facing);
    }
}
