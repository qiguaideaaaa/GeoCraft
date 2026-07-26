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

package 清汩萌.天圆地方.测试.流体物理.通用;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.Fluid;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import moe.qingu.geocraft.api.util.FluidUtil;
import moe.qingu.geocraft.util.fluid.FluidSearchUtil;
import 清汩萌.天圆地方.测试.流体物理.流体物理测试;
import 清汩萌.天圆地方.util.MessyUtil;
import 清汩萌.天圆地方.util.NullableArg;
import 清汩萌.天圆地方.util.网格工具;
import 清汩萌.天圆地方.世界.沙盒.沙盒测试样例;
import 清汩萌.天圆地方.世界.沙盒.测试参数;
import 清汩萌.天圆地方.天圆地方测试;
import 清汩萌.造.格文件;
import 清汩萌.造.空间.词块网格;
import 清汩萌.造.词块.词块;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author QGMoe
 */
public final class 流体搜寻 extends 流体物理测试 {

    @ParameterizedTest
    @MethodSource("为测试流体搜寻准备数据")
    public void 测试流体搜寻(final @Nonnull 搜索流体测试样例 $样例) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        test(new Object[]{网格工具.打包网格数据($样例.$网格),
                $样例.$起始搜寻位置,
                NullableArg.of(String.class, MessyUtil.toNullableString($样例.$目标流体)),
                NullableArg.of(Set.class,$样例.ignoredPos),
                $样例.打包参数()});
    }

    public static final class 搜索流体测试样例 extends 沙盒测试样例 {
        @测试参数(键 = "begin_at", 型 = 测试参数.型.坐标) int[] $起始搜寻位置;
        @测试参数(键 = "target") @Nullable             词块 $目标流体;
        @测试参数(键 = "flat",可选 = true)              boolean $仅平面搜索;
        @测试参数(键 = "up_if_possible",可选 = true)    boolean $优先往上搜索;
        @测试参数(键 = "max_iter")                     int $最大迭代次数;
        @测试参数(键 = "expected")                     boolean $期望是否可以搜寻到流体;
        @测试参数(键 = "ignored") @Nullable            Collection<?> $跳过搜寻的位置;
        @Nullable Set<int[]> ignoredPos;

        @SuppressWarnings("OptionalGetWithoutIsPresent")
        搜索流体测试样例(final @Nonnull 格文件 $格文件) {
            super($格文件);
            this.ignoredPos = Optional.ofNullable($跳过搜寻的位置)
                    .map(o -> o.stream()
                            .map(测试参数.型::解析为方块坐标).map(Optional::get)
                            .collect(Collectors.toSet()))
                    .orElse(null);
        }

        long 打包参数(){
            return (((long) $最大迭代次数)<<Integer.SIZE)|($仅平面搜索 ?1:0)|($优先往上搜索 ?0b10:0)|($期望是否可以搜寻到流体 ?0b100:0);
        }
    }

    @Nonnull
    public static Stream<搜索流体测试样例> 为测试流体搜寻准备数据(){
        return 格文件.获取目录下所有格文件("data/流体物理/通用/流体搜寻/").map(搜索流体测试样例::new);
    }

    @SuppressWarnings("unused")
    public static void 测试流体搜寻_Inner(final @Nonnull Object[] $打包网格数据,
                                          final @Nonnull int[] posRaw,
                                          final @Nullable String fluidRaw,
                                          final @Nullable Set<int[]> ignoredPosRaw,
                                          final long paras){
        final BlockPos beginPos = MessyUtil.toPosFromRaw(posRaw);
        final 词块网格 $网格 = 网格工具.恢复网格数据($打包网格数据);
        final @Nullable IBlockState fluidBlockState = fluidRaw==null?null:获取或用默认构造器($网格).进行映射(词块.of(fluidRaw));
        final @Nullable Fluid fluid;
        if(fluidBlockState == null) fluid = null;
        else {
            fluid = FluidUtil.getFluid(fluidBlockState);
            Assertions.assertNotNull(fluid,fluidBlockState+" isn't a valid fluid block ");
        }
        final @Nullable Set<BlockPos> ignoredPos = ignoredPosRaw==null?null:ignoredPosRaw.stream()
                .map(MessyUtil::toPosFromRaw)
                .collect(Collectors.toSet());
        initWorldSandbox($网格,beginPos);
        final Optional<BlockPos> res = FluidSearchUtil.findFluid(world,
                beginPos,
                fluid,
                ignoredPos,
                (paras&1) != 0,
                (paras&0b10)!=0,
                (int)(paras>>>Integer.SIZE));
        天圆地方测试.LOGGER.info("Find:{}",res.isPresent()?res.get():"NULL");
        Assertions.assertEquals((paras&0b100)!=0,res.isPresent());
    }
}
