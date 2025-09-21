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

package top.qiguaiaaaa.geocraft.util.wrappers;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.wrappers.BlockLiquidWrapper;
import top.qiguaiaaaa.geocraft.api.util.FluidUtil;
import top.qiguaiaaaa.geocraft.api.util.math.PlaceChoice;
import top.qiguaiaaaa.geocraft.util.fluid.FluidSearchUtil;

import java.util.*;

public class PhysicsBlockLiquidWrapper extends BlockLiquidWrapper {
    protected final Fluid fluid;
    protected boolean ignoreCurrentPos = false;
    protected int expectedQuanta = 8;
    public PhysicsBlockLiquidWrapper(BlockLiquid blockLiquid, World world, BlockPos blockPos) {
        super(blockLiquid, world, blockPos);
        fluid = FluidUtil.getFluid(blockLiquid);
    }
    @Override
    public int fill(FluidStack resource, boolean doFill) {
        final int expectedAmount = expectedQuanta *FluidUtil.ONE_IN_EIGHT_OF_BUCKET_VOLUME;
        if (resource.amount < expectedAmount) {
            return 0;
        }

        final Set<PlaceChoice> choices = FluidSearchUtil.findPlaceableLocations(world,blockPos,fluid,8,ignoreCurrentPos,null);
        if(choices.isEmpty()) return 0;
        int quantaLeft = expectedQuanta;
        for(PlaceChoice choice:choices){
             quantaLeft = placeLiquid(choice.pos,quantaLeft,doFill);
             if(quantaLeft<=0) break;
        }
        if(quantaLeft <=0) return expectedAmount;
        return 0;
    }

    public void setIgnoreCurrentPos(boolean ignoreCurrentPos) {
        this.ignoreCurrentPos = ignoreCurrentPos;
    }

    public void setExpectedQuanta(int expectedQuanta) {
        this.expectedQuanta = expectedQuanta;
    }

    /**
     * 在某处放置指定量的液体
     * @param placePos 位置
     * @param placeQuanta 放置量
     * @return 剩余未放置的量
     */
    protected int placeLiquid(BlockPos placePos,int placeQuanta,boolean doFill){
        if(placeQuanta <=0) return 0;
        IBlockState state = world.getBlockState(placePos);
        int quanta = FluidUtil.getFluidQuanta(world,placePos,state);
        int newQuanta = quanta+placeQuanta;
        if(newQuanta<=8){
            if(doFill) directlyPlaceLiquid(placePos,8-newQuanta);
            return 0;
        }
        if(doFill) directlyPlaceLiquid(placePos,0);
        return newQuanta-8;
    }
    protected void directlyPlaceLiquid(BlockPos placePos,int level){
        Material material = blockLiquid.getDefaultState().getMaterial();
        BlockLiquid block = BlockLiquid.getFlowingBlock(material);
        world.setBlockState(placePos, block.getDefaultState().withProperty(BlockLiquid.LEVEL, level), 11);
    }
}
