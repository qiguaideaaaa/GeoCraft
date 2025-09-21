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

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.qiguaiaaaa.geocraft.block.IBlockDirt;

import java.util.Random;

import static net.minecraft.block.BlockDirt.SNOWY;
import static net.minecraft.block.BlockDirt.VARIANT;
import static top.qiguaiaaaa.geocraft.api.block.BlockProperties.HUMIDITY;

@Mixin(value = BlockDirt.class)
public class BlockDirtMixin extends Block implements IBlockDirt {
    public BlockDirtMixin(Material materialIn) {
        super(materialIn);
    }

    @Inject(method = "<init>",at = @At(value = "RETURN"))
    private void injectDefaultState(CallbackInfo ci) {
        this.setTickRandomly(true);
        this.setDefaultState((this.blockState.getBaseState().
                withProperty(VARIANT, BlockDirt.DirtType.DIRT)
                .withProperty(SNOWY, Boolean.FALSE)
                .withProperty(HUMIDITY, 0)));
    }
    @Inject(method = "getStateFromMeta",at = @At(value = "HEAD"),cancellable = true)
    private void getStateFromMeta(int meta, CallbackInfoReturnable<IBlockState> cir) {
        cir.cancel();
        cir.setReturnValue(this.getDefaultState()
                .withProperty(VARIANT, BlockDirt.DirtType.byMetadata(meta%3))
                .withProperty(HUMIDITY,Math.min(meta/3,4)));
    }
    @Inject(method = "getMetaFromState",at = @At(value = "HEAD"),cancellable = true)
    public void getMetaFromState(IBlockState state, CallbackInfoReturnable<Integer> cir) {
        cir.cancel();
        cir.setReturnValue(state.getValue(VARIANT).getMetadata()+state.getValue(HUMIDITY)*3);
    }

    @Inject(method = "createBlockState",at = @At(value = "HEAD"),cancellable = true)
    private void createBlockState(CallbackInfoReturnable<BlockStateContainer> cir) {
        cir.cancel();
        cir.setReturnValue(new BlockStateContainer(this, VARIANT, SNOWY, HUMIDITY));
    }

    /**
     * {@link Block#randomTick(World, BlockPos, IBlockState, Random)}
     */
    public void func_180645_a(World worldIn, BlockPos pos, IBlockState state, Random random) {
        this.updateTick(worldIn, pos, state, random);
        this.onRandomTick(worldIn, pos, state, random);
    }

    /**
     * {@link Block#onPlayerDestroy(World, BlockPos, IBlockState)}
     */
    public void func_176206_d(World worldIn, BlockPos pos, IBlockState state) {
        dropWaterWhenBroken(worldIn, pos, state);
    }

    @Override
    public int getMaxStableHumidity(IBlockState state) {
        switch (state.getValue(VARIANT)){
            case PODZOL:
            case COARSE_DIRT:
                return 1;
            case DIRT:
            default:return 2;
        }
    }
}
