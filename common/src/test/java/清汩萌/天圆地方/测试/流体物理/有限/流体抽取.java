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

package 清汩萌.天圆地方.测试.流体物理.有限;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import moe.qingu.geocraft.api.util.QBUtil;
import moe.qingu.geocraft.geography.fluidphysics.finite.flow.FiniteFlowingVanilla;
import moe.qingu.geocraft.util.wrappers.FiniteBlockLiquidWrapper;
import 清汩萌.天圆地方.util.网格工具;
import 清汩萌.天圆地方.世界.沙盒.数组沙盒;
import 清汩萌.天圆地方.世界.沙盒.沙盒测试样例;
import 清汩萌.天圆地方.世界.沙盒.测试参数;
import 清汩萌.天圆地方.天圆地方测试;
import 清汩萌.造.工具.StringUtil;
import 清汩萌.造.格文件;
import 清汩萌.造.空间.空间构造器;
import 清汩萌.造.空间.词块网格;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.stream.Stream;

import static 清汩萌.天圆地方.原料.方块原料.原版流体.getFlowingByMaterial;

/**
 * @author QGMoe
 */
public final class 流体抽取 extends 有限模式测试 {

    @ParameterizedTest
    @MethodSource("为测试抽取原版流体准备数据")
    public void 测试抽取原版流体(final @Nonnull 流体抽取测试样例 c) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        test(new Object[]{网格工具.打包网格数据(c.$网格),
                c.$抽取坐标,
                c.$按QB计应当的抽取量});
    }

    public static final class 流体抽取测试样例 extends 沙盒测试样例 {
        @测试参数(键 = "drain_at", 型 = 测试参数.型.坐标) int[] $抽取坐标;
        @测试参数(键 = "drained", 于 = "expected")     long $应当的抽取量;
        @测试参数(键 = "unit", 于 = "expected")        String $抽取量单位;
        final long $按QB计应当的抽取量;

        流体抽取测试样例(final @Nonnull 格文件 $格文件) {
            super($格文件);
            this.$按QB计应当的抽取量 = Optional.of($抽取量单位)
                    .map(StringUtil::removeWhites)
                    .map(unit -> $应当的抽取量 * ("QB".equalsIgnoreCase(unit) ? 1L :
                            "MB".equalsIgnoreCase(unit) ? QBUtil.MB_VOLUME :
                                    Assertions.<Long>fail("Unknown unit " + unit))).get();
        }
    }

    @Nonnull
    public static Stream<流体抽取测试样例> 为测试抽取原版流体准备数据(){
        return 格文件.获取目录下所有格文件("data/流体物理/有限/原版流体抽取").map(流体抽取测试样例::new);
    }


    @SuppressWarnings("unused")
    public static void 测试抽取原版流体_Inner(final @Nonnull Object[] $打包网格数据,
                                            final @Nonnull int[] $drainPosRaw,
                                            final long expectedDrainedQB){
        final BlockPos drainPos = new BlockPos($drainPosRaw[0],$drainPosRaw[1],$drainPosRaw[2]);
        final 词块网格 $网格 = 网格工具.恢复网格数据($打包网格数据);
        final 空间构造器 $构造器 = 获取或用默认构造器($网格);
        final @Nonnull 数组沙盒 sandbox = initWorldSandbox($网格,drainPos);
        final @Nonnull IBlockState state = world.getBlockState(drainPos);
        天圆地方测试.LOGGER.info("{} block state is {}",drainPos,state);

        final @Nonnull FiniteFlowingVanilla flowing = getFlowingByMaterial(state.getMaterial());
        final @Nonnull FiniteBlockLiquidWrapper wrapper = new FiniteBlockLiquidWrapper(flowing,world,drainPos);
        final @Nullable FluidStack stack = wrapper.drain(Fluid.BUCKET_VOLUME,true);
        final long drained = QBUtil.toQBFromMB(stack == null?0:stack.amount);
        $构造器.打印(sandbox.getStructure());
        Assertions.assertEquals(expectedDrainedQB,drained);
    }
}
