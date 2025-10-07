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

package top.qiguaiaaaa.geocraft.api.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.Fluid;
import top.qiguaiaaaa.geocraft.api.util.FluidUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

/**
 * 可透水(或其他流体)方块
 * 注意这和含水方块有本质区别，含水方块是透水方块的子集。例如，泥土不是含水方块，但应该为透水方块。
 * @author QiguaiAAAA
 */
public interface IPermeableBlock {
    int DEFAULT_MAX_HEIGHT = 720720;
    int EIGHTH_OF_HEIGHT = DEFAULT_MAX_HEIGHT/8;

    /**
     * 返回当前位置透水方块所含流体或期望含有的流体。若不含有任何流体或可以含有多种流体，则返回null。
     * @param state 该位置方块状态
     * @return 流体
     */
    @Nonnull
    Set<Fluid> getFluid(@Nonnull IBlockState state);

    int getQuanta(@Nonnull IBlockState state,@Nullable Fluid fluid);

    default int getMaxQuanta(@Nonnull IBlockState state,@Nullable Fluid fluid){
        return (DEFAULT_MAX_HEIGHT -getEmptyHeight(state,fluid))/getHeightPerQuanta(state);
    }

    /**
     * 返回水位高度
     * @param state 状态
     * @return 水位高度，分成16份，数字越大越高
     */
    default int getHeight(@Nonnull IBlockState state,@Nullable Fluid fluid){
        return getEmptyHeight(state,fluid)+getQuanta(state,fluid)*getHeightPerQuanta(state);
    }

    /**
     * 返回若没有水的情况下的基准高度
     * @param state 状态
     * @return 基准高度,单位和水位高度一样
     */
    int getEmptyHeight(@Nonnull IBlockState state,@Nullable Fluid fluid);

    default int getMaxHeight(@Nonnull IBlockState state,@Nullable Fluid fluid){
        return getEmptyHeight(state,fluid) + getMaxQuanta(state,fluid)*getHeightPerQuanta(state);
    }

    /**
     * 每量的高度，必须是{@link #DEFAULT_MAX_HEIGHT}的因数
     * @return 每量的高度
     */
    int getHeightPerQuanta(@Nonnull IBlockState state);

    /**
     * 添加或移除指定片当前所含的液体
     * @param world 世界
     * @param pos 位置
     * @param state 状态，必须是当前方块的状态
     * @param quanta 片数
     */
    default void addQuanta(@Nonnull World world,@Nonnull BlockPos pos,@Nonnull IBlockState state,@Nonnull Fluid fluid,int quanta){
        int newQuanta = MathHelper.clamp(getQuanta(state,fluid)+quanta,0,getMaxQuanta(state,fluid));
        setQuanta(world, pos, state,fluid, newQuanta);
    }

    /**
     * 添加指定片液体，其中每片的含量为{@link FluidUtil#ONE_IN_EIGHT_OF_BUCKET_VOLUME}
     * @param world 世界
     * @param pos 位置
     * @param state 状态
     * @param fluid 要添加的流体
     * @param quanta 片数
     * @param doAdd 是否真的添加
     * @return 实际添加的片数
     */
    default int addQuanta(@Nonnull World world,@Nonnull BlockPos pos,@Nonnull IBlockState state,@Nonnull Fluid fluid,int quanta,boolean doAdd){
        if(quanta == 0) return 0;
        int curQuanta = getQuanta(state,fluid);
        int quantaInFact = MathHelper.clamp(quanta,-curQuanta,getMaxQuanta(state,fluid)-curQuanta);
        if(quantaInFact == 0) return 0;
        if(doAdd) addQuanta(world, pos, state, fluid,quantaInFact);
        return quantaInFact;
    }

    /**
     * 吸取指定片液体，其中每片的含量为{@link FluidUtil#ONE_IN_EIGHT_OF_BUCKET_VOLUME}
     * @param world 世界
     * @param pos 位置
     * @param state 状态
     * @param fluid 要吸取的流体
     * @param quanta 片数
     * @param doDrain 是否真的吸取
     * @return 实际吸取的片数
     */
    default int drainQuanta(@Nonnull World world,@Nonnull BlockPos pos,@Nonnull IBlockState state,@Nonnull Fluid fluid,int quanta,boolean doDrain){
        if(quanta == 0) return 0;
        return -addQuanta(world, pos, state, fluid, -quanta, doDrain);
    }

    /**
     * 将当前方块的流体量设置到指定量
     * @param world 世界
     * @param pos 当前位置
     * @param state 方框状态
     * @param newQuanta 新的量
     */
    default void setQuanta(@Nonnull World world,@Nonnull BlockPos pos,@Nonnull IBlockState state,@Nonnull Fluid fluid,int newQuanta){
        if(!getFluid(state).isEmpty() && !getFluid(state).contains(fluid)) return;
        newQuanta = MathHelper.clamp(newQuanta,0,getMaxQuanta(state,fluid));
        IBlockState quantaState = getQuantaState(state,fluid,newQuanta);
        if(quantaState == null) return;
        world.setBlockState(pos,quantaState, Constants.BlockFlags.SEND_TO_CLIENTS);
    }

    /**
     * 获取指定quanta时的方块状态
     * @return 若指定状态不存在,返回null
     */
    @Nullable
    IBlockState getQuantaState(@Nonnull IBlockState state,@Nonnull Fluid fluid, int quanta);

    boolean canFill(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Fluid fluid, @Nonnull EnumFacing side, @Nullable IBlockState source);

    default boolean canDrain(@Nonnull World world,@Nonnull BlockPos pos,@Nonnull IBlockState state,@Nonnull Fluid fluid,@Nonnull EnumFacing side,@Nullable IBlockState source){
        return true;
    }

    default boolean isFull(@Nonnull IBlockState state,@Nullable Fluid fluid){
        return getQuanta(state,fluid) == getMaxQuanta(state,fluid);
    }
}
