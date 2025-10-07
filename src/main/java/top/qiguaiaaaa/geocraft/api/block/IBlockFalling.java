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

import net.minecraft.block.BlockFalling;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import top.qiguaiaaaa.geocraft.util.math.vec.BlockPosI;

import javax.annotation.Nonnull;

import java.util.Random;

import static top.qiguaiaaaa.geocraft.util.math.MathUtil.centerPos;

/**
 * 参考自{@link BlockFalling}的实现
 * @author QiguaiAAAA
 */
public interface IBlockFalling {

    default boolean isFallInstantly(){
        return BlockFalling.fallInstantly;
    }

    default void updateTick(@Nonnull World world,@Nonnull BlockPos pos,@Nonnull IBlockState state,@Nonnull Random rand) {
        if (!world.isRemote) {
            this.checkAndFall(world, pos);
        }
    }

    default void checkAndFall(@Nonnull World world,@Nonnull BlockPos pos) {
        if (!canFall(world, pos.down())) return;
        final int checkRange = 32;

        if (!isFallInstantly() && world.isAreaLoaded(pos.add(-checkRange, -checkRange, -checkRange), pos.add(checkRange, checkRange, checkRange))) {
            if (!world.isRemote) {
                final EntityFallingBlock fallingBlock =
                        new EntityFallingBlock(world, centerPos(pos.getX()), pos.getY(), centerPos(pos.getZ()), world.getBlockState(pos));
                this.onStartFalling(fallingBlock);
                world.spawnEntity(fallingBlock);
            }
            return;
        }
        // 下面是在区块生成的时候调用的代码
        IBlockState state = world.getBlockState(pos);
        world.setBlockToAir(pos); //没有生成实体，需要手动设置为空气
        BlockPosI.Mutable curPos = new BlockPosI.Mutable(pos);

        do {
            curPos.downM();
        }while (canFall(world, curPos) && curPos.getY() > 0);

        if (curPos.getY() > 0) {
            world.setBlockState(curPos.upM(), state); //当前位置不能下落到，所以要往上取一格
        }
    }

    /**
     * 检查下方方块，以判断是否可以下落
     * @param world 世界
     * @param pos 下方位置
     * @return 若可以，则返回true
     */
    default boolean canFall(@Nonnull World world,@Nonnull BlockPos pos){
        return (world.isAirBlock(pos) || canFallThrough(world.getBlockState(pos))) && pos.getY() >= -1;
    }

    default void onStartFalling(@Nonnull EntityFallingBlock fallingEntity) {}

    default void onEndFalling(@Nonnull World world,@Nonnull BlockPos pos,@Nonnull IBlockState fallingState,@Nonnull IBlockState hitState) {}

    default void onBroken(@Nonnull World world,@Nonnull BlockPos pos) {}

    default int tickRate(@Nonnull World world) {
        return 2;
    }

    static boolean canFallThrough(@Nonnull IBlockState state) {
        return BlockFalling.canFallThrough(state);
    }

    @SideOnly(Side.CLIENT)
    default int getDustColor(IBlockState state){
        return -16777216;
    }

}
