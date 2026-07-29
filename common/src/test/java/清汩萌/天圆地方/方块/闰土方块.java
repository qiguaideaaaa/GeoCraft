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

package 清汩萌.天圆地方.方块;

import moe.qingu.geocraft.api.world.tick.scheduler.BlockTickScheduler;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import org.junit.jupiter.api.Assertions;
import 清汩萌.天圆地方.天圆地方测试;

import javax.annotation.Nonnull;
import java.util.Random;

import static 清汩萌.天圆地方.原料.方块原料.常用.〇;
import static 清汩萌.天圆地方.原料.方块原料.方块计划刻测试方块.猹;

/**
 * @author QGMoe
 */
public final class 闰土方块 extends Block {
    public static final PropertyInteger _计划时间_ = PropertyInteger.create("time",0,15);

    public 闰土方块() {
        super(Material.ROCK);
        this.setDefaultState(this.getDefaultState().withProperty(_计划时间_,9));
        this.setRegistryName(new ResourceLocation(天圆地方测试.MODID,"闰土"));
    }


    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta(final int meta) {
        return this.getDefaultState().withProperty(_计划时间_,meta);
    }

    @Override
    public int getMetaFromState(final @Nonnull IBlockState state) {
        return state.getValue(_计划时间_);
    }

    @Override
    public void updateTick(final @Nonnull World worldIn,
                           final @Nonnull BlockPos pos,
                           final @Nonnull IBlockState state,
                           final @Nonnull Random rand) {
        final IBlockState up;
        if((up = worldIn.getBlockState(pos.up())) == 〇){
            天圆地方测试.LOGGER.info("世界时间 {} 时.位于 {} 的闰土 {} 计划了任务，并发现了猹!",
                    Long.toUnsignedString(worldIn.getTotalWorldTime()),
                    pos,
                    state.getValue(_计划时间_)-8);
            BlockTickScheduler.schedule(worldIn,pos,state.getBlock(),state.getValue(_计划时间_)-8);
            worldIn.setBlockState(pos.up(),猹.withProperty(猹方块._出现时间_,(int) (worldIn.getTotalWorldTime()+state.getValue(_计划时间_)-8L &0xFL)),
                    Constants.BlockFlags.NO_OBSERVERS | Constants.BlockFlags.NO_RERENDER);
        }else {
            final IBlockState down = worldIn.getBlockState(pos.down());
            Assertions.assertEquals(up,down,()->
                    String.format("世界时间 %s 时，闰土 %s 在 %s 发现了不一致！",Long.toUnsignedString(worldIn.getTotalWorldTime()), state.getValue(_计划时间_)-8, pos));
        }
    }

    @Nonnull
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, _计划时间_);
    }
}
