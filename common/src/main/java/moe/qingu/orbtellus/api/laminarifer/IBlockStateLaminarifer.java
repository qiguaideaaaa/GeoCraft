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

package moe.qingu.orbtellus.api.laminarifer;

import moe.qingu.orbtellus.api.laminarifer.drainer.IFlowDrainer;
import moe.qingu.orbtellus.api.laminarifer.source.IFlowSource;
import moe.qingu.orbtellus.api.util.modifier.BlockFlagModifier;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.Fluid;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author QGMoe
 */
public interface IBlockStateLaminarifer extends ILaminarifer {

    /**
     * 获取指定流体下指定层数时的方块状态
     * @param state 查询的方块状态
     * @param fluid 指定流体
     * @param nbt 附带的 NBT 数据
     * @param layer 指定层数
     * @return 若指定状态不存在,返回null
     */
    IBlockState getLayerState(@Nonnull final IBlockState state,
                              @Nonnull final Fluid fluid,
                              @Nullable final NBTTagCompound nbt,
                              final long layer);

    boolean isAcceptedFluid(@Nonnull final IBlockState state,
                            @Nonnull final Fluid fluid,
                            @Nullable final NBTTagCompound nbt);

    default boolean canFill(@Nonnull final World world,
                            @Nonnull final IBlockState state,
                            @Nullable final EnumFacing side,
                            @Nonnull final Fluid fluid,
                            @Nullable final NBTTagCompound nbt,
                            @Nullable final IFlowSource source) {
        return isAcceptedFluid(state, fluid, nbt) && getLayers(state, fluid, nbt) < getMaxLayers(state, fluid, nbt);
    }

    default boolean canDrain(@Nonnull final World world,
                             @Nonnull final IBlockState state,
                             @Nullable final EnumFacing side,
                             @Nonnull final Fluid fluid,
                             @Nullable final NBTTagCompound nbt,
                             @Nullable final IFlowDrainer drainer) {
        return getLayers(state, fluid, nbt) != 0;
    }

    long getMaxLayers(@Nonnull final IBlockState state,
                      @Nonnull final Fluid fluid,
                      @Nullable final NBTTagCompound nbt);

    long getLayers(@Nonnull final IBlockState state,
                   @Nonnull final Fluid fluid,
                   @Nullable final NBTTagCompound nbt);

    long getEmptyHeight(@Nonnull final IBlockState state,
                        @Nonnull final Fluid fluid,
                        @Nullable final NBTTagCompound nbt);

    long getHeightPerLayer(@Nonnull final IBlockState state,
                           @Nonnull final Fluid fluid,
                           @Nullable final NBTTagCompound nbt);

    default long getMaxHeight(@Nonnull final IBlockState state,
                              @Nonnull final Fluid fluid,
                              @Nullable final NBTTagCompound nbt) {
        return getEmptyHeight(state, fluid, nbt) + getMaxLayers(state, fluid, nbt) * getHeightPerLayer(state, fluid, nbt);
    }

    default long getHeight(@Nonnull final IBlockState state,
                           @Nonnull final Fluid fluid,
                           @Nullable final NBTTagCompound nbt) {
        return getEmptyHeight(state, fluid, nbt) + getLayers(state, fluid, nbt) * getHeightPerLayer(state, fluid, nbt);
    }

    long getAmountInQBPerLayer(@Nonnull final IBlockState state,
                               @Nonnull final Fluid fluid,
                               @Nullable final NBTTagCompound nbt);

    default long getMaxAmountInQB(@Nonnull final IBlockState state,
                                  @Nonnull final Fluid fluid,
                                  @Nullable final NBTTagCompound nbt) {
        return getMaxLayers(state, fluid, nbt) * getAmountInQBPerLayer(state, fluid, nbt);
    }

    /* =========================================
                         重写方法
       ========================================= */

    @Override
    default boolean isAcceptedFluid(@Nonnull final World world,
                                    @Nonnull final BlockPos pos,
                                    @Nonnull final IBlockState state,
                                    @Nonnull final Fluid fluid,
                                    @Nullable final NBTTagCompound nbt){
        return isAcceptedFluid(state, fluid, nbt);
    }

    @Override
    default boolean canFill(@Nonnull final World world,
                            @Nonnull final BlockPos pos,
                            @Nonnull final IBlockState state,
                            @Nullable final EnumFacing side,
                            @Nonnull final Fluid fluid,
                            @Nullable final NBTTagCompound nbt,
                            @Nullable final IFlowSource source) {
        return canFill(state, side, fluid, nbt, source);
    }

    @Override
    default boolean canDrain(@Nonnull final World world,
                             @Nonnull final BlockPos pos,
                             @Nonnull final IBlockState state,
                             @Nullable final EnumFacing side,
                             @Nonnull final Fluid fluid,
                             @Nullable final NBTTagCompound nbt,
                             @Nullable final IFlowDrainer drainer) {
        return canDrain(state, side, fluid, nbt, drainer);
    }

    @Override
    default long getMaxLayers(@Nonnull final World world,
                              @Nonnull final BlockPos pos,
                              @Nonnull final IBlockState state,
                              @Nonnull final Fluid fluid,
                              @Nullable final NBTTagCompound nbt){
        return getMaxLayers(state, fluid, nbt);
    }

    @Override
    default long getLayers(@Nonnull final World world,
                           @Nonnull final BlockPos pos,
                           @Nonnull final IBlockState state,
                           @Nonnull final Fluid fluid,
                           @Nullable final NBTTagCompound nbt){
        return getLayers(state, fluid, nbt);
    }

    @Override
    default long getEmptyHeight(@Nonnull final World world,
                                @Nonnull final BlockPos pos,
                                @Nonnull final IBlockState state,
                                @Nonnull final Fluid fluid,
                                @Nullable final NBTTagCompound nbt){
        return getEmptyHeight(state, fluid, nbt);
    }

    @Override
    default long getHeightPerLayer(@Nonnull final World world,
                                   @Nonnull final BlockPos pos,
                                   @Nonnull final IBlockState state,
                                   @Nonnull final Fluid fluid,
                                   @Nullable final NBTTagCompound nbt){
        return getHeightPerLayer(state, fluid, nbt);
    }

    @Override
    default long getMaxHeight(@Nonnull final World world,
                              @Nonnull final BlockPos pos,
                              @Nonnull final IBlockState state,
                              @Nonnull final Fluid fluid,
                              @Nullable final NBTTagCompound nbt) {
        return getMaxHeight(state, fluid, nbt);
    }

    @Override
    default long getHeight(@Nonnull final World world,
                           @Nonnull final BlockPos pos,
                           @Nonnull final IBlockState state,
                           @Nonnull final Fluid fluid,
                           @Nullable final NBTTagCompound nbt) {
        return getHeight(state, fluid, nbt);
    }

    @Override
    default boolean setLayer(@Nonnull final World world,
                             @Nonnull final BlockPos pos,
                             @Nonnull final IBlockState state,
                             @Nonnull final Fluid fluid,
                             @Nullable final NBTTagCompound nbt,
                             final long newLayer,
                             final long blockFlagsModifier){
        final @Nullable IBlockState newState = getLayerState(state, fluid, nbt, newLayer);
        if(newState == null) return false;
        return world.setBlockState(pos,newState, BlockFlagModifier.modify(Constants.BlockFlags.DEFAULT,blockFlagsModifier));
    }

    @Override
    default long getAmountInQBPerLayer(@Nonnull final World world,
                                       @Nonnull final BlockPos pos,
                                       @Nonnull final IBlockState state,
                                       @Nonnull final Fluid fluid,
                                       @Nullable final NBTTagCompound nbt){
        return getAmountInQBPerLayer(state, fluid, nbt);
    }

    @Override
    default long getMaxAmountInQB(@Nonnull final World world,
                                  @Nonnull final BlockPos pos,
                                  @Nonnull final IBlockState state,
                                  @Nonnull final Fluid fluid,
                                  @Nullable final NBTTagCompound nbt) {
        return getMaxAmountInQB(state, fluid, nbt);
    }
}
