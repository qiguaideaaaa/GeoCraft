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

package moe.qingu.orbtellus.world.scheduler.boxed;

import moe.qingu.orbtellus.world.scheduler.common.方块计划刻调度测试;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import 清汩萌.天圆地方.util.网格工具;
import 清汩萌.天圆地方.世界.扩展模拟区块世界;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.util.stream.Stream;

/**
 * @author QGMoe
 */
public final class 装箱方块计划刻调度器测试 extends 方块计划刻调度测试 {

    @ParameterizedTest
    @MethodSource("为测试方块调度准备数据")
    public void 测试方块调度(final @Nonnull 方块计划刻调度测试.方块调度测试样例 $样例) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        test(new Object[]{网格工具.打包网格数据($样例.$网格),$样例.$计划刻数据,$样例.$测试时段});
    }

    @ParameterizedTest
    @MethodSource("为测试全序方块调度准备数据")
    public void 测试全序方块调度(final @Nonnull 方块调度测试样例 $样例) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        test(new Object[]{网格工具.打包网格数据($样例.$网格),$样例.$计划刻数据,$样例.$测试时段});
    }

    @SuppressWarnings("unused")
    public static void 测试方块调度_Inner(final @Nonnull Object[] $打包网格数据,final @Nonnull String $未解析的计划刻数据,final long[][] $测试时段){
        测试方块调度核心($打包网格数据,$未解析的计划刻数据, $测试时段,
                i -> 扩展模拟区块世界.构建(i,false),
                PartialOrderBoxedBlockTickScheduler::new,
                BoxedBlockTickDatum::new);
    }

    @SuppressWarnings("unused")
    public static void 测试全序方块调度_Inner(final @Nonnull Object[] $打包网格数据,final @Nonnull String $未解析的计划刻数据,final long[][] $测试时段){
        测试方块调度核心($打包网格数据,$未解析的计划刻数据, $测试时段,
                i -> 扩展模拟区块世界.构建(i,false),
                TotalOrderBoxedBlockTickScheduler::new,
                BoxedBlockTickDatum::new);
    }

    @Nonnull
    public static Stream<方块调度测试样例> 为测试方块调度准备数据(){
        return 为测试方块调度准备数据("data/world/schedule/common/偏序调度","data/world/schedule/boxed/调度", "data/world/schedule/boxed/偏序调度");
    }

    @Nonnull
    public static Stream<方块调度测试样例> 为测试全序方块调度准备数据(){
        return 为测试方块调度准备数据("data/world/schedule/common/全序调度", "data/world/schedule/boxed/调度", "data/world/schedule/boxed/全序调度");
    }
}
