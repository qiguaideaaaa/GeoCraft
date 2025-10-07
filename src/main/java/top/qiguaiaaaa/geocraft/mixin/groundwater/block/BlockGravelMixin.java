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

package top.qiguaiaaaa.geocraft.mixin.groundwater.block;

import net.minecraft.block.BlockFalling;
import net.minecraft.block.BlockGravel;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.qiguaiaaaa.geocraft.block.IBlockSoil;
import top.qiguaiaaaa.geocraft.configs.SoilConfig;
import top.qiguaiaaaa.geocraft.geography.soil.BlockSoilType;

import javax.annotation.Nonnull;
import java.util.Random;

import static top.qiguaiaaaa.geocraft.api.block.BlockProperties.HUMIDITY;

/**
 * @author QiguaiAAAA
 */
@Mixin(value = BlockGravel.class)
public class BlockGravelMixin extends BlockFalling implements IBlockSoil {
    @Unique
    private static final int STABLE_HUMIDITY = SoilConfig.STABLE_HUMIDITY.getValue().get(BlockSoilType.GRAVEL);

    @Inject(method = "<init>",at = @At(value = "RETURN"))
    private void injectDefaultState(CallbackInfo ci) {
        this.setTickRandomly(true);
        this.setDefaultState(this.blockState.getBaseState().withProperty(HUMIDITY,0));
    }

    @Override
    @Unique
    public IBlockState getStateFromMeta(int meta) {
        if(meta>=5) return this.getDefaultState();
        return this.getDefaultState().withProperty(HUMIDITY,meta);
    }

    @Override
    @Unique
    public int getMetaFromState(IBlockState state) {
        return state.getValue(HUMIDITY);
    }

    @Nonnull
    @Override
    @Unique
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this,HUMIDITY);
    }

    @Override
    @Unique
    public void randomTick(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Random random) {
        this.onRandomTick(worldIn, pos, state, random);
    }

    @Override
    @Unique
    public void onPlayerDestroy(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        dropWaterWhenBroken(worldIn, pos, state);
    }

    @Nonnull
    @Override
    public BlockSoilType getType(@Nonnull IBlockState state) {
        return BlockSoilType.GRAVEL;
    }

    @Override
    @Unique
    public int getMaxStableHumidity(@Nonnull IBlockState state) {
        return STABLE_HUMIDITY;
    }

    @Override
    public double getFlowInPossibility(@Nonnull IBlockState state) {
        return 0.9;
    }
}
