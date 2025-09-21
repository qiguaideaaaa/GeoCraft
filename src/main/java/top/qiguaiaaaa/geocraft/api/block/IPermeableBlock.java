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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import top.qiguaiaaaa.geocraft.api.util.FluidUtil;

import javax.annotation.Nonnull;

/**
 * 可透水(或其他流体)方块
 * 注意这和含水方块有本质区别，含水方块是透水方块的子集。例如，泥土不是含水方块，但应该为透水方块。
 * @author QiguaiAAAA
 */
public interface IPermeableBlock {
    int MAX_HEIGHT = 16;
    @Nonnull
    Fluid getFluid(@Nonnull World world,@Nonnull BlockPos pos,@Nonnull IBlockState state);
    int getQuanta(@Nonnull World world,@Nonnull BlockPos pos,@Nonnull IBlockState state);

    /**
     * 返回水位高度
     * @param world 世界
     * @param pos 位置
     * @param state 状态
     * @return 水位高度，分成16份，数字越大越高
     */
    int getHeight(@Nonnull World world,@Nonnull BlockPos pos,@Nonnull IBlockState state);

    int getHeightPerQuanta();

    /**
     * 添加指定片水
     * @param world 世界
     * @param pos 位置
     * @param state 状态
     * @param quanta 片数，一片是{@link FluidUtil#ONE_IN_EIGHT_OF_BUCKET_VOLUME} mB
     */
    void addQuanta(@Nonnull World world,@Nonnull BlockPos pos,@Nonnull IBlockState state,int quanta);

    void setQuanta(@Nonnull World world,@Nonnull BlockPos pos,@Nonnull IBlockState state,int newQuanta);

    /**
     * 为区块生成时提供的设置量的方法
     */
    @Nonnull
    IBlockState getQuantaState(@Nonnull IBlockState state, int newQuanta);
    default boolean isFull(@Nonnull World world,@Nonnull BlockPos pos,@Nonnull IBlockState state){
        return getHeight(world,pos,state) == MAX_HEIGHT;
    }
}
