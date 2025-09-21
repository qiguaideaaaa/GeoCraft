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

package top.qiguaiaaaa.geocraft.util.fluid;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;

import static net.minecraft.block.BlockLiquid.LEVEL;

/**
 * @author QiguaiAAAA
 */
public final class BlockLiquidUtil {

    /**
     * 对应方块状态是否阻挡{@link BlockLiquid}的流动
     * @param state 需要检测的方块状态
     * @return 若阻挡，则返回true
     */
    public static boolean isBlocked(IBlockState state) {
        Block block = state.getBlock();
        Material material = state.getMaterial();

        if (!(block instanceof BlockDoor) && block != Blocks.STANDING_SIGN && block != Blocks.LADDER && block != Blocks.REEDS) {
            return material == Material.PORTAL || material == Material.STRUCTURE_VOID || material.blocksMovement();
        }
        return true;
    }

    public static int getDepth(IBlockState state,BlockLiquid liquid){
        return state.getMaterial() == liquid.getDefaultState().getMaterial() ? state.getValue(LEVEL) : -1;
    }

    public static void placeStaticBlock(World world, BlockPos pos,IBlockState curState,BlockLiquid liquid){
        world.setBlockState(pos,
                BlockLiquid.getStaticBlock(liquid.getDefaultState().getMaterial())
                        .getDefaultState().withProperty(LEVEL, curState.getValue(LEVEL)), Constants.BlockFlags.SEND_TO_CLIENTS);
    }

    /**
     * 指定液体是否可以流进指定方块位置
     * @param state 该方块位置的方块状态
     * @param liquid 液体
     * @return 若能,则返回true,否则返回false
     */
    public static boolean canFlowInto(IBlockState state, BlockLiquid liquid){
        Material material = state.getMaterial();
        return material != liquid.getDefaultState().getMaterial() && material != Material.LAVA && !isBlocked(state);
    }

    /**
     * 尝试以指定的液体等级(meta)流进指定位置
     * @param world 所在世界
     * @param pos 流进的指定位置
     * @param state 流进位置的方块状态
     * @param liquid 液体
     * @param level 期望流进的液体等级
     */
    public static void tryFlowInto(World world,BlockPos pos,IBlockState state,BlockLiquid liquid,int level){
        if (!canFlowInto(state,liquid)) return;
        if (state.getMaterial() != Material.AIR) {
            if (liquid.getDefaultState().getMaterial() == Material.LAVA) {
                FluidOperationUtil.triggerFluidMixEffects(world, pos);
            } else {
                if (state.getBlock() != Blocks.SNOW_LAYER)
                    state.getBlock().dropBlockAsItem(world, pos, state, 0);
            }
        }

        world.setBlockState(pos, liquid.getDefaultState().withProperty(LEVEL, level), Constants.BlockFlags.DEFAULT);
    }

}
