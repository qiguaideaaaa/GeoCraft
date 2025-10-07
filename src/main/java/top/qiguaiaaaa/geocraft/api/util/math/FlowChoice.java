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

package top.qiguaiaaaa.geocraft.api.util.math;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;
import top.qiguaiaaaa.geocraft.api.block.IPermeableBlock;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class FlowChoice {
    public final EnumFacing direction;
    public final int heightPerQuanta;
    public final IPermeableBlock block;
    private int quantaOfThisFluid;
    private int quantaOfAll;
    private int baseHeight = 0;
    private int maxQuanta = 8;

    /**
     * 创建一个基本的流动选择,适用十纯单流体的情况
     * @param rawQuanta 该选择最初的流体量
     * @param direction 该选择的方向
     */
    @Deprecated
    public FlowChoice(int rawQuanta,EnumFacing direction){
        this(rawQuanta,direction,1);
    }

    /**
     * 创建一个考虑不同方块类型的流动选择
     * @param direction 该选择的方向
     * @param block 该选择的透水方块
     */
    public FlowChoice(@Nullable EnumFacing direction, @Nonnull IPermeableBlock block, @Nonnull IBlockState state, Fluid fluid){
        this.quantaOfThisFluid = block.getQuanta(state,fluid);
        this.quantaOfAll = block.getQuanta(state,null);
        this.maxQuanta = block.getMaxQuanta(state,fluid);
        this.direction = direction;
        this.heightPerQuanta = block.getHeightPerQuanta(state);
        this.baseHeight = block.getEmptyHeight(state,fluid);
        this.block = block;
    }

    /**
     * 创建一个自定义每量高度的流动选择,一般用于空气
     * @param rawQuanta 该选择最初的流体量
     * @param direction 该选择的方向
     * @param heightPerQuanta 该选择的每量高度
     */
    public FlowChoice(int rawQuanta, @Nullable EnumFacing direction, int heightPerQuanta){
        this.quantaOfThisFluid = rawQuanta;
        this.quantaOfAll = rawQuanta;
        this.direction = direction;
        this.heightPerQuanta = heightPerQuanta;
        this.block = null;
    }

    public boolean isFull(){
        return quantaOfThisFluid >= maxQuanta;
    }

    public int getQuantaOfThisFluid() {
        return quantaOfThisFluid;
    }

    public int getHeight(){
        return baseHeight + quantaOfThisFluid *heightPerQuanta;
    }

    public void addQuanta(int i){
        quantaOfThisFluid +=i;
        quantaOfAll += i;
    }
}
