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
import net.minecraft.block.BlockGrass;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.qiguaiaaaa.geocraft.api.block.IBlockFalling;
import top.qiguaiaaaa.geocraft.block.IBlockSoil;
import top.qiguaiaaaa.geocraft.configs.SoilConfig;
import top.qiguaiaaaa.geocraft.geography.soil.BlockSoilType;

import javax.annotation.Nonnull;
import java.util.Random;

import static net.minecraft.block.BlockGrass.SNOWY;
import static top.qiguaiaaaa.geocraft.api.block.BlockProperties.HUMIDITY;

@Mixin(value = BlockGrass.class)
public class BlockGrassMixin extends Block implements IBlockSoil, IBlockFalling {
    @Unique
    private static final int STABLE_HUMIDITY  = SoilConfig.STABLE_HUMIDITY.getValue().get(BlockSoilType.GRASS);
    @Unique
    private final ThreadLocal<Boolean> isRandomTick = ThreadLocal.withInitial(()-> Boolean.FALSE);
    public BlockGrassMixin(Material materialIn) {
        super(materialIn);
    }

    @Inject(method = "<init>",at =@At("RETURN"))
    protected void BlockGrass(CallbackInfo ci) {
        this.setDefaultState(this.blockState.getBaseState()
                .withProperty(SNOWY, Boolean.FALSE)
                .withProperty(HUMIDITY, 0));
    }

    /**
     * {@link Block#getStateFromMeta(int)}
     */
    public IBlockState func_176203_a(int meta) {
        if(meta>4) return this.getDefaultState();
        return this.getDefaultState().withProperty(HUMIDITY,meta);
    }

    @Inject(method = "getMetaFromState",at = @At("HEAD"), cancellable = true)
    protected void getMetaFromState(IBlockState state, CallbackInfoReturnable<Integer> cir) {
        cir.cancel();
        cir.setReturnValue(state.getValue(HUMIDITY));
    }

    @Inject(method = "createBlockState",at = @At("HEAD"), cancellable = true)
    protected void createBlockState(CallbackInfoReturnable<BlockStateContainer> cir) {
        cir.cancel();
        cir.setReturnValue(new BlockStateContainer(this, SNOWY, HUMIDITY));
    }

    @Override
    public void randomTick(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Random random) {
        isRandomTick.set(Boolean.TRUE);
        super.randomTick(worldIn, pos, state, random);
        this.onRandomTick(worldIn, pos, state, random);
        isRandomTick.set(Boolean.FALSE);
    }

    @Override
    public void onPlayerDestroy(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        dropWaterWhenBroken(worldIn, pos, state);
    }

    @Override
    public void neighborChanged(IBlockState state, @Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull Block blockIn, @Nonnull BlockPos fromPos) {
        if(state.getValue(HUMIDITY) <= getMaxStableHumidity(state)) return;
        worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
    }

    @Override
    public int tickRate(@Nonnull World worldIn) {
        return 2;
    }

    @Override
    public void onBlockAdded(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        if(state.getValue(HUMIDITY) <= getMaxStableHumidity(state)) return;
        worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
    }

    @Inject(method = "updateTick",at =@At("HEAD"),cancellable = true)
    public void updateTick_CheckFalling(World world,BlockPos pos,IBlockState state,Random rand,CallbackInfo ci){
        if(isRandomTick.get()) return;
        ci.cancel();
        if(state.getValue(HUMIDITY) <= getMaxStableHumidity(state)) return;
        if(!world.isRemote){
            checkAndFall(world, pos);
        }
    }

    @Redirect(method = "updateTick",at =
    @At(value = "INVOKE",
            target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)Z",
            ordinal = 0))
    public boolean updateTick_TurnToDirt(World instance, BlockPos pos, IBlockState state) {
        IBlockState curState = instance.getBlockState(pos);
        return instance.setBlockState(pos, state.withProperty(HUMIDITY,curState.getValue(HUMIDITY)));
    }

    @Redirect(method = "updateTick",at =
    @At(value = "INVOKE",
            target = "Lnet/minecraft/world/World;setBlockState(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)Z",
            ordinal = 1))
    public boolean updateTick_TurnToGrass(World instance, BlockPos pos, IBlockState state) {
        IBlockState curState = instance.getBlockState(pos);
        return instance.setBlockState(pos, Blocks.GRASS.getDefaultState().withProperty(HUMIDITY,curState.getValue(HUMIDITY)));
    }

    @Nonnull
    @Override
    public BlockSoilType getType(@Nonnull IBlockState state) {
        return BlockSoilType.GRASS;
    }

    @Override
    public int getMaxStableHumidity(@Nonnull IBlockState state) {
        return STABLE_HUMIDITY;
    }

    @Override
    public double getFlowInPossibility(@Nonnull IBlockState state) {
        return 0.2;
    }
}
