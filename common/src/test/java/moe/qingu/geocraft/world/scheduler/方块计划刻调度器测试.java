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

package moe.qingu.geocraft.world.scheduler;

import moe.qingu.geocraft.api.world.tick.scheduler.BlockTickScheduler;
import moe.qingu.geocraft.handler.CapabilityHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.capabilities.CapabilityCreator;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import 清汩萌.天圆地方.util.ClassGraphUtils;
import 清汩萌.天圆地方.util.网格工具;
import 清汩萌.天圆地方.世界.区块.模拟区块;
import 清汩萌.天圆地方.世界.模拟区块世界;
import 清汩萌.天圆地方.世界.沙盒.沙盒测试样例;
import 清汩萌.天圆地方.世界.配置.模拟世界配置;
import 清汩萌.天圆地方.原料.方块原料;
import 清汩萌.天圆地方.天圆地方测试;
import 清汩萌.造.格文件;
import 清汩萌.造.空间.空间工具;
import 清汩萌.造.空间.空间构造器;
import 清汩萌.造.空间.词块网格;
import 清汩萌.造.造;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static 清汩萌.天圆地方.原料.方块原料.方块计划刻测试方块.猹;

/**
 * @author QGMoe
 */
@ExtendWith(方块计划刻调度器测试.测试环境准备.class)
public class 方块计划刻调度器测试 extends 天圆地方测试 {

    public static final class 方块调度测试样例 extends 沙盒测试样例 {
        public final @Nonnull String $计划刻数据;
        public final @Nonnull long[][] $测试时段;

        @SuppressWarnings("DataFlowIssue")
        public 方块调度测试样例(@Nonnull final 格文件 $格文件, @Nonnull final String $计划刻数据) {
            super($格文件);
            this.$计划刻数据 = $计划刻数据;
            final List<?> $原始测试时段 = (List<?>) this.$格文件.获取附加数据().get("测试时段");
            this.$测试时段 = $原始测试时段.stream().map(s -> ((List<?>) s)
                            .stream()
                            .mapToLong(e -> Long.parseUnsignedLong(e.toString()))
                            .limit(2)
                            .toArray())
                    .toArray(long[][]::new);
        }
    }

    @Nonnull
    public static Stream<方块调度测试样例> 为测试方块调度准备数据(final @Nonnull String $自行目录){
        final List<Supplier<方块调度测试样例>> $样例们 = new ArrayList<>();
        for(final @Nonnull String dir : Arrays.asList("data/world/schedule/common/调度",$自行目录))
            ClassGraphUtils.寻找特定类型文件(dir, 格文件._扩展名_,(scan, $原始网格资源)->{
                final String $计划刻数据 = ClassGraphUtils.基于样例文件获取指定类型文件("yaml",scan,$原始网格资源).getContentAsString();
                $样例们.add(() -> new 方块调度测试样例(格文件.解析($原始网格资源.getURI()),$计划刻数据));
            });
        return $样例们.stream().map(Supplier::get);
    }

    @SuppressWarnings("unused")
    public static <T extends ChunkyBlockTickDatum & ICapabilityProvider> void 测试方块调度核心(final @Nonnull Object[] $打包网格数据,
                                                                                               final @Nonnull String $未解析的计划刻数据,
                                                                                               final long[][] $测试时段,
                                                                                               final @Nonnull Function<WorldInfo,? extends 模拟区块世界> $世界供应,
                                                                                               final @Nonnull Function<World,ChunkyBlockTickScheduler<T>> $调度器供应,
                                                                                               final @Nonnull Function<模拟区块,T> $区块化数据供应){
        final @Nonnull 词块网格 $网格 = 网格工具.恢复网格数据($打包网格数据);
        final @Nonnull 空间构造器 $构造器 = 获取或用默认构造器($网格);
        if($网格.获取默认填充方块() == null) $网格.默认用("〇");
        final @Nonnull 计划刻数据 $计划刻数据 = 造.YAML.loadAs($未解析的计划刻数据,计划刻数据.class);
        $计划刻数据.初始化();
        final @Nonnull 模拟区块世界 $世界 = $世界供应.apply(模拟世界配置.create(b -> b.withTotalTime($计划刻数据.time)));
        final ChunkyBlockTickScheduler<?> scheduler = $调度器供应.apply($世界);
        BlockTickScheduler.getSchedulers().put($世界.provider.getDimension(),scheduler);
        $世界.getChunkProvider().监听区块创建(c -> c.获取聚合能力().注册($区块化数据供应.apply(c)));
        空间工具.导入世界($世界,$计划刻数据.获取基点(),$网格.构造($构造器));
        final ArrayList<可测试的计划刻> $计划表 = new ArrayList<>($计划刻数据.ticks);
        for (final @Nonnull long[] $时段 : $测试时段) {
            final long begin = $计划刻数据.time + $时段[0];
            final long end = $计划刻数据.time + $时段[1];
            $世界.setTotalWorldTime(begin);
            while ($世界.getTotalWorldTime() != end) {
                final Iterator<可测试的计划刻> iterator = $计划表.iterator();
                while (iterator.hasNext()) {
                    final 可测试的计划刻 $计划刻 = iterator.next();
                    if ($计划刻.$创时 == $世界.getTotalWorldTime()) {
                        iterator.remove();
                        scheduler.schedule($计划刻);
                    }
                }
                BlockTickScheduler.onWorldTick($世界);
                $世界.setTotalWorldTime($世界.getTotalWorldTime() + 1L);
            }
        }
        final IBlockState[][][] $结果 = 空间工具.导出世界($世界,$计划刻数据.获取基点(),$网格.获取层数(),$网格.获取行数(),$网格.获取列数());
        $构造器.打印($结果,LOGGER);
        final List<BlockPos> $不合法位置 = new ArrayList<>();
        for(int $层=1;$层<=$结果.length;$层++) for(int $行=1;$行<=$结果[$层-1].length;$行++) for(int $列=1;$列<=$结果[$层-1][$行-1].length;$列++)
            if($结果[$层-1][$行-1][$列-1].getBlock() == 猹.getBlock()) $不合法位置.add(new BlockPos($层,$行,$列));
        if($不合法位置.isEmpty()) return;
        Assertions.fail($不合法位置.stream()
                .map(p -> "在第 "+p.getX()+" 层第 "+p.getY()+" 行第 "+p.getZ()+" 列的方块仍然是 " + $构造器.进行映射($结果[p.getX()-1][p.getY()-1][p.getZ()-1]))
                .collect(Collectors.joining("\n")));
    }

    @AfterEach
    public void 结束测试方块调度() throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        test();
    }

    @SuppressWarnings("unused")
    public static void 结束测试方块调度_Inner() {
        BlockTickScheduler.onServerStop();
    }

    @Nonnull
    @SuppressWarnings("DataFlowIssue")
    public static 空间构造器 获取或用默认构造器(final @Nonnull 词块网格 $网格){
        return $网格.获取当前构造器() == null? 方块原料.方块计划刻测试方块._方块计划刻测试构造器_ :$网格.获取当前构造器();
    }

    static final class 测试环境准备 implements BeforeAllCallback {

        @Override
        public void beforeAll(final @Nonnull ExtensionContext context) throws Exception {
            run(测试环境准备.class.getName(),"setup");
        }

        @SuppressWarnings("unused")
        public static void setup(){
            LOGGER.info("加载可测试计划刻的SNBT辅助函数中");
            可测试的计划刻.加载SNBT辅助函数();
            LOGGER.info("准备Capability");
            CapabilityHandler.CHUNKY_BLOCK_TICK_DATUM = CapabilityCreator.create("chunkyBlockTickDatum",null,null);
        }
    }
}
