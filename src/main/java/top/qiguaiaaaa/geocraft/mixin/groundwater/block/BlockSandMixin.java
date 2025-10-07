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
import net.minecraft.block.BlockSand;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.qiguaiaaaa.geocraft.block.IBlockSoil;
import top.qiguaiaaaa.geocraft.configs.SoilConfig;
import top.qiguaiaaaa.geocraft.geography.soil.BlockSoilType;

import javax.annotation.Nonnull;
import java.util.Random;

import static top.qiguaiaaaa.geocraft.api.block.BlockProperties.HUMIDITY;

@Mixin(value = BlockSand.class)
public class BlockSandMixin extends BlockFalling implements IBlockSoil {
    @Shadow @Final public static PropertyEnum<BlockSand.EnumType> VARIANT;

    @Unique
    private static final int STABLE_HUMIDITY = SoilConfig.STABLE_HUMIDITY.getValue().get(BlockSoilType.SAND);

    @Inject(method = "<init>",at = @At(value = "RETURN"))
    private void injectDefaultState(CallbackInfo ci) {
        this.setTickRandomly(true);
        this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, BlockSand.EnumType.SAND).withProperty(HUMIDITY,0));
    }
    @Inject(method = "getStateFromMeta",at = @At(value = "HEAD"),cancellable = true)
    public void getStateFromMeta(int meta, CallbackInfoReturnable<IBlockState> cir) {
        cir.cancel();
        if(meta>=10) cir.setReturnValue(this.getDefaultState());
        cir.setReturnValue(this.getDefaultState().withProperty(VARIANT,BlockSand.EnumType.byMetadata(meta%2)).withProperty(HUMIDITY,meta/2));
    }
    @Inject(method = "getMetaFromState",at = @At(value = "HEAD"),cancellable = true)
    public void getMetaFromState(IBlockState state, CallbackInfoReturnable<Integer> cir) {
        cir.cancel();
        cir.setReturnValue(state.getValue(VARIANT).getMetadata()+state.getValue(HUMIDITY)*2);
    }

    @Inject(method = "createBlockState",at = @At(value = "HEAD"),cancellable = true)
    protected void createBlockState(CallbackInfoReturnable<BlockStateContainer> cir) {
        cir.cancel();
        cir.setReturnValue(new BlockStateContainer(this, VARIANT,HUMIDITY));
    }

    @Override
    public void randomTick(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Random random) {
        this.onRandomTick(worldIn, pos, state, random);
    }

    @Override
    public void onPlayerDestroy(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        dropWaterWhenBroken(worldIn, pos, state);
    }

    @Nonnull
    @Override
    public BlockSoilType getType(@Nonnull IBlockState state) {
        return BlockSoilType.SAND;
    }

    @Override
    public int getMaxStableHumidity(@Nonnull IBlockState state) {
        return STABLE_HUMIDITY;
    }

    @Override
    public double getFlowInPossibility(@Nonnull IBlockState state) {
        return 0.7;
    }

    @Override
    public void updateTick(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Random rand) {
        if(state.getValue(HUMIDITY) == getMaxStableHumidity(state)) return;
        super.updateTick(worldIn, pos, state, rand);
    }
}
