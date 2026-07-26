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

import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import 清汩萌.天圆地方.天圆地方测试;

import javax.annotation.Nonnull;
import java.util.Random;
import java.util.logging.Logger;

import static 清汩萌.天圆地方.原料.方块原料.常用.〇;

/**
 * @author QGMoe
 */
public final class 猹方块 extends Block {
    public static final PropertyInteger _出现时间_ = PropertyInteger.create("time",0,15);

    public 猹方块() {
        super(Material.ROCK, MapColor.STONE);
        this.setDefaultState(this.getDefaultState().withProperty(_出现时间_,0));
        this.setRegistryName(new ResourceLocation(天圆地方测试.MODID,"猹"));
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta(final int meta) {
        return this.getDefaultState().withProperty(_出现时间_,meta);
    }

    @Override
    public int getMetaFromState(final @Nonnull IBlockState state) {
        return state.getValue(_出现时间_);
    }

    @Override
    public void updateTick(final @Nonnull World worldIn,
                           final @Nonnull BlockPos pos,
                           final @Nonnull IBlockState state,
                           final @Nonnull Random rand) {
        天圆地方测试.LOGGER.info("世界时间 {} (猹月 {} )时.位于 {} 的猹 {} 更新了!",
                Long.toUnsignedString(worldIn.getTotalWorldTime()),
                worldIn.getTotalWorldTime() & 0xFL,
                pos,
                state.getValue(_出现时间_));
        if((worldIn.getTotalWorldTime() & 0xFL) == state.getValue(_出现时间_))
            worldIn.setBlockState(pos, 〇, Constants.BlockFlags.NO_OBSERVERS | Constants.BlockFlags.NO_RERENDER);
    }

    @Nonnull
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, _出现时间_);
    }
}
