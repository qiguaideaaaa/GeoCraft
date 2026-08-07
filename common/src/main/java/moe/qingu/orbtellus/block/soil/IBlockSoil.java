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

package moe.qingu.orbtellus.block.soil;

import moe.qingu.orbtellus.api.fluidphysics.FluidPhysicsMode;
import moe.qingu.orbtellus.api.laminarifer.AHUnit;
import moe.qingu.orbtellus.api.laminarifer.IBlockStateLaminarifer;
import moe.qingu.orbtellus.api.laminarifer.Laminarifers;
import moe.qingu.orbtellus.api.laminarifer.drainer.IFlowDrainer;
import moe.qingu.orbtellus.api.laminarifer.qb.QBUnit;
import moe.qingu.orbtellus.api.laminarifer.source.FlowSources;
import moe.qingu.orbtellus.api.laminarifer.source.IFlowSource;
import moe.qingu.orbtellus.api.util.annotation.MultiThread;
import moe.qingu.orbtellus.api.util.annotation.ThreadType;
import moe.qingu.orbtellus.geography.soil.BlockSoilType;
import moe.qingu.orbtellus.util.BaseUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.BlockFlags;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static moe.qingu.orbtellus.api.block.BlockProperties.HUMIDITY;
import static moe.qingu.orbtellus.api.fluidphysics.FluidPhysicsMode.getCurrentMode;

public interface IBlockSoil extends IBlockStateLaminarifer, IFlowSource {
    /**
     * 土壤在破坏时掉水的能力
     */
    default void dropWaterWhenBroken(World world, BlockPos pos, IBlockState state){
        int humidity = getLayers(world,pos,state,FluidRegistry.WATER);
        if(humidity == 0) return;
        if(getCurrentMode() != FluidPhysicsMode.FINITE){
            world.spawnParticle(EnumParticleTypes.BLOCK_CRACK,
                    pos.getX()+0.5, pos.getY()+0.5, pos.getZ()+0.5,
                    0, 0, 0, Block.getStateId(Blocks.WATER.getDefaultState()));
            return;
        }
        world.setBlockState(pos, Blocks.FLOWING_WATER.getDefaultState().withProperty(BlockLiquid.LEVEL,8-humidity),BlockFlags.DEFAULT);
    }

    /**
     * 当玩家右键土壤添加水分的操作
     * @see BlockCauldron#onBlockActivated(World, BlockPos, IBlockState, EntityPlayer, EnumHand, EnumFacing, float, float, float)
     */
    @MultiThread({ThreadType.MINECRAFT_CLIENT,ThreadType.MINECRAFT_SERVER})
    default boolean onPlayerUseBottle(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
        final ItemStack stack = playerIn.getHeldItem(hand);
        if(stack.isEmpty()) return false;
        int moisture = getLayers(worldIn,pos,state,FluidRegistry.WATER);
        final Item item = stack.getItem();
        if(moisture >2) return false;
        if (item == Items.POTIONITEM && PotionUtils.getPotionFromItem(stack) == PotionTypes.WATER) {
            if (!playerIn.capabilities.isCreativeMode) {
                ItemStack bottleStack = new ItemStack(Items.GLASS_BOTTLE);
                playerIn.setHeldItem(hand, bottleStack);

                if (playerIn instanceof EntityPlayerMP) {
                    ((EntityPlayerMP)playerIn).sendContainerToPlayer(playerIn.inventoryContainer);
                }
            }

            worldIn.playSound(null, pos, SoundEvents.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
            this.addLayer(worldIn,pos,state,FluidRegistry.WATER,2);
            return true;
        }
        return false;
    }

    @Nonnull
    BlockSoilType getType(@Nonnull IBlockState state);

    default int getMaxStableHumidity(@Nonnull IBlockState state){
        return getType(state).getMaxStableHumidity();
    }

    default double getFlowInPossibility(@Nonnull IBlockState state){
        return getType(state).getFlowInPossibility();
    }

    default double getRainInPossibility(@Nonnull IBlockState state){
        return getType(state).getRainInPossibility();
    }

    //******************
    // ILayeredFluidHost
    //******************


    @Override
    default boolean isAcceptedFluid(@Nonnull final IBlockState state, @Nonnull final Fluid fluid, @Nullable final NBTTagCompound nbt){
        return fluid == FluidRegistry.WATER;
    }

    @Override
    default boolean canFill(@Nonnull final World world,
                            @Nonnull final IBlockState state,
                            @Nullable final EnumFacing side,
                            @Nonnull final Fluid fluid,
                            @Nullable final NBTTagCompound nbt,
                            @Nullable final IFlowSource source) {
        if(fluid != FluidRegistry.WATER) return false;
        if(Laminarifers.isFull(this,state,fluid,nbt)) return false;
        if(FlowSources.isAtmosphere(source)){
            return BaseUtil.getRandomResult(world.rand,getRainInPossibility(state));
        }else if (FlowSources.isRunoff(source)){
            return BaseUtil.getRandomResult(world.rand, getFlowInPossibility(state));
        }
        return true;
    }

    @Override
    default boolean canDrain(@Nonnull final World world,
                             @Nonnull final IBlockState state,
                             @Nullable final EnumFacing side,
                             @Nonnull final Fluid fluid,
                             @Nullable final NBTTagCompound nbt,
                             @Nullable final IFlowDrainer drainer) {
        if(fluid != FluidRegistry.WATER) return false;
        return getLayers(state,fluid,nbt)>getMaxStableHumidity(state);
    }

    @Override
    default long getMaxLayers(@Nonnull final IBlockState state, @Nonnull final Fluid fluid, @Nullable final NBTTagCompound nbt){
        if(fluid != FluidRegistry.WATER) return 0L;
        return 4L;
    }

    @Override
    default long getLayers(@Nonnull final IBlockState state, @Nonnull final Fluid fluid, @Nullable final NBTTagCompound nbt){
        if(fluid != FluidRegistry.WATER) return 0L;
        return state.getValue(HUMIDITY);
    }

    @Override
    default long getEmptyHeight(@Nonnull final IBlockState state, @Nonnull final Fluid fluid, @Nullable final NBTTagCompound nbt){
        return 0L;
    }

    @Override
    default long getHeightPerLayer(@Nonnull final IBlockState state, @Nonnull final Fluid fluid, @Nullable final NBTTagCompound nbt){
        return AHUnit.FIFTH_FLUID;
    }

    @Override
    default long getMaxHeight(@Nonnull final IBlockState state, @Nonnull final Fluid fluid, @Nullable final NBTTagCompound nbt) {
        return fluid == FluidRegistry.WATER? AHUnit.FIFTH_FLUID<<2:0L;
    }

    @Override
    default long getHeight(@Nonnull final IBlockState state, @Nonnull final Fluid fluid, @Nullable final NBTTagCompound nbt) {
        if(fluid != FluidRegistry.WATER) return 0L;
        return state.getValue(HUMIDITY) * getHeightPerLayer(state,fluid,nbt);
    }

    @Override
    default long getAmountInQBPerLayer(@Nonnull final IBlockState state, @Nonnull final Fluid fluid, @Nullable final NBTTagCompound nbt){
        return QBUnit.QUANTA_VOLUME;
    }

    @Override
    default IBlockState getLayerState(@Nonnull final IBlockState state, @Nonnull final Fluid fluid, @Nullable final NBTTagCompound nbt, final long layer){
        if(fluid != FluidRegistry.WATER) return null;
        if(layer < 0L || layer> 4L) return null;
        return state.withProperty(HUMIDITY, (int) layer);
    }
}
