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

package 清汩萌.造.空间;

import moe.qingu.geocraft.api.util.math.vec.MBlockPos;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.Arrays;

/**
 * @author QGMoe
 */
public final class 空间工具 {
    private 空间工具(){}

    public static int[] 转换为游戏坐标(final @Nonnull int[] $网格坐标){
        if($网格坐标.length != 3) throw new IllegalArgumentException();
        return new int[]{$网格坐标[2]-1,$网格坐标[0]-1,$网格坐标[1]-1};
    }

    public static void 打印元数据(final @Nonnull Logger $日志, final @Nonnull 词块网格 $网格){
        $日志.info("[词块网格信息]");
        $日志.info("网格参数：{}", Arrays.toString($网格.获取参数()));
        $日志.info("网格默认构造器名：{}",$网格.获取默认构造器名称() == null?"NULL":$网格.获取默认构造器名称());
        $日志.info("网格使用/附加映射：{}",$网格.获取默认映射器名称集合()==null?"{}":$网格.获取默认映射器名称集合());
        $日志.info("默认方块：{}",$网格.获取默认填充方块()==null?"NULL":$网格.获取默认填充方块());
    }

    public static void 导入世界(final @Nonnull World world, final @Nonnull BlockPos $基点, final @Nonnull IBlockState[][][] $网格){
        final MBlockPos pos = new MBlockPos($基点);
        for(int dy=0;dy<$网格.length;dy++){
            final IBlockState[][] $层 = $网格[dy];
            if($层 == null) continue;
            for(int dz=0;dz<$层.length;dz++){
                final IBlockState[] $行 = $层[dz];
                if($行 == null) continue;
                for(int dx=0;dx<$行.length;dx++){
                    final IBlockState $块 = $行[dx];
                    if($块 == null) continue;
                    pos.setPos($基点.getX()+dx,$基点.getY()+dy,$基点.getZ()+dz);
                    world.setBlockState(pos,$块);
                }
            }
        }
    }

    @Nonnull
    public static IBlockState[][][] 导出世界(final @Nonnull World world, final @Nonnull BlockPos $基点, final int $层数,final int $行数,final int $列数){
        final MBlockPos pos = new MBlockPos($基点);
        final IBlockState[][][] $网格 = new IBlockState[$层数][][];
        for(int dy=0;dy<$网格.length;dy++){
            final IBlockState[][] $层 = $网格[dy] = new IBlockState[$行数][];
            for(int dz=0;dz<$层.length;dz++){
                final IBlockState[] $行 = $层[dz] = new IBlockState[$列数];
                for(int dx=0;dx<$行.length;dx++){
                    pos.setPos($基点.getX()+dx,$基点.getY()+dy,$基点.getZ()+dz);
                    $行[dx] = world.getBlockState(pos);
                }
            }
        }
        return $网格;
    }
}
