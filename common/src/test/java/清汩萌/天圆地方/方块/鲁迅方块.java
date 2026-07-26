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
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import 清汩萌.天圆地方.天圆地方测试;

import javax.annotation.Nonnull;
import java.util.Random;

import static 清汩萌.天圆地方.原料.方块原料._猹_;

/**
 * @author QGMoe
 */
public final class 鲁迅方块 extends Block {
    public static final PropertyInteger _方位模式_ = PropertyInteger.create("f_mode",0,7);
    public static final PropertyBool _增减模式_ = PropertyBool.create("d_mode");

    public 鲁迅方块() {
        super(Material.ROCK, MapColor.STONE);
        this.setDefaultState(this.getDefaultState().withProperty(_方位模式_,0)
                .withProperty(_增减模式_,false));
        this.setRegistryName(new ResourceLocation(天圆地方测试.MODID,"鲁迅"));
    }

    @Override
    public void updateTick(final @Nonnull World worldIn,
                           final @Nonnull BlockPos pos,
                           final @Nonnull IBlockState state,
                           final @Nonnull Random rand) {
        final int delta = state.getValue(_增减模式_)?1:-1;
        final int facing = state.getValue(_方位模式_);
        if(facing<6){
            final EnumFacing side = EnumFacing.VALUES[facing];
            检查猹(worldIn,pos.offset(side),delta);
        }else if(facing == 6) for(final EnumFacing side:EnumFacing.HORIZONTALS) 检查猹(worldIn,pos.offset(side),delta);
        else for(final EnumFacing side:EnumFacing.VALUES) 检查猹(worldIn,pos.offset(side),delta);
    }

    private static void 检查猹(final @Nonnull World world,final @Nonnull BlockPos pos,final int delta){
        final IBlockState sideState = world.getBlockState(pos);
        if(sideState.getBlock() != _猹_) return;
        world.setBlockState(pos,对猹方块进行增减(sideState,delta), Constants.BlockFlags.NO_OBSERVERS | Constants.BlockFlags.NO_RERENDER);
    }

    @Nonnull
    private static IBlockState 对猹方块进行增减(final @Nonnull IBlockState $猹,final int diff){
        final int time = $猹.getValue(猹方块._出现时间_);
        final int newTime = (time+diff)&0b1111;
        return $猹.withProperty(猹方块._出现时间_,newTime);
    }

    @Nonnull
    @Override
    @SuppressWarnings({"deprecation", "OctalInteger"})
    public IBlockState getStateFromMeta(final int meta) {
        return this.getDefaultState().withProperty(_方位模式_,meta&0_7).withProperty(_增减模式_,(meta&0b1000) != 0);
    }

    @Override
    public int getMetaFromState(final @Nonnull IBlockState state) {
        return state.getValue(_方位模式_) | (state.getValue(_增减模式_)?0b1000:0);
    }


    @Nonnull
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, _方位模式_,_增减模式_);
    }
}
