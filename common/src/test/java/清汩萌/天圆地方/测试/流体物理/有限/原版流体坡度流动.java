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

import com.google.common.collect.Maps;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import moe.qingu.orbtellus.geography.fluidphysics.finite.flow.FiniteFlowingVanilla;
import 清汩萌.天圆地方.util.ClassGraphUtils;
import 清汩萌.天圆地方.util.网格工具;
import 清汩萌.天圆地方.世界.沙盒.数组沙盒;
import 清汩萌.天圆地方.世界.沙盒.沙盒测试样例;
import 清汩萌.天圆地方.世界.沙盒.测试参数;
import 清汩萌.造.工具.StringUtil;
import 清汩萌.造.格文件;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static 清汩萌.天圆地方.原料.方块原料.原版流体.*;

/**
 * @author QiguaiAAAA
 */
public final class 原版流体坡度流动 extends 有限模式测试 {
    public static Map<String,EnumFacing> _朝向别名_ = Maps.newHashMap();

    @SuppressWarnings("unused")
    public static void initFacingAliases(){
        final @Nonnull Function<EnumFacing,Consumer<String>> putAllTo = facing -> alias -> _朝向别名_.put(alias,facing);
        Arrays.asList("前","北","HEAD","AHEAD").forEach(putAllTo.apply(EnumFacing.NORTH));
        Arrays.asList("后","南","BACK","BACKWARD").forEach(putAllTo.apply(EnumFacing.SOUTH));
        Arrays.asList("左","西","LEFT").forEach(putAllTo.apply(EnumFacing.WEST));
        Arrays.asList("右","东","RIGHT").forEach(putAllTo.apply(EnumFacing.EAST));
        Arrays.asList("上","UPSTAIRS").forEach(putAllTo.apply(EnumFacing.UP));
        Arrays.asList("下","DOWNSTAIRS").forEach(putAllTo.apply(EnumFacing.DOWN));
    }

    @BeforeAll
    public static void initBeforeSlopeTest() throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        run(原版流体坡度流动.class.getName(),"initFacingAliases");
    }

    @ParameterizedTest
    @MethodSource("为测试单层坡度流动算法准备数据")
    public void 测试单层坡度流动算法(final @Nonnull 单层坡度流动算法测试样例 $样例) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        test(new Object[]{网格工具.打包网格数据($样例.$网格),$样例.$算法开始流体位置,$样例.$应当的结果});
    }

    public static class 单层坡度流动算法测试样例 extends 沙盒测试样例 {
        @测试参数(键 = "begin_at", 型 = 测试参数.型.坐标) int[] $算法开始流体位置;
        public final String[] $应当的结果;

        public 单层坡度流动算法测试样例(final @Nonnull 格文件 $格文件, final @Nonnull String[] $应当的结果) {
            super($格文件);
            this.$应当的结果 = $应当的结果;
        }
    }

    @Nonnull
    public static Stream<单层坡度流动算法测试样例> 为测试单层坡度流动算法准备数据(){
        final ArrayList<单层坡度流动算法测试样例> $样例们 = new ArrayList<>();
        沙盒测试样例.寻找格样例输入("data/流体物理/有限/原版单层坡度流动/",(scan, in) -> {
            final @Nonnull 格文件 $输入 = 格文件.解析(in.getURI());
            $样例们.add(new 单层坡度流动算法测试样例($输入, ClassGraphUtils.获取去除YAML风格注释的文本(ClassGraphUtils.基于样例输入获取一般答案(scan, in))
                    .map(StringUtil::strip)
                    .toArray(String[]::new)));
        });
        return $样例们.stream();
    }

    @Nonnull
    public static Set<EnumFacing> 解析应当的朝向结果(final @Nonnull String[] expectedDirections){
        final @Nonnull Set<EnumFacing> directions = EnumSet.noneOf(EnumFacing.class);
        final @Nonnull BiConsumer<String,EnumFacing> add = (name,facing) ->{
            Assertions.assertFalse(directions.contains(facing),"Facing "+ facing+" ("+name+") "+" is duplicated!");
            directions.add(facing);
        };
        for(final @Nonnull String d : expectedDirections){
            if(d.isEmpty()) continue;
            final @Nonnull String D = d.toUpperCase(Locale.ROOT);
            try {
                add.accept(D,EnumFacing.valueOf(D));
            }catch (final @Nonnull Exception ignore){
                if(_朝向别名_.containsKey(D)){
                    add.accept(D, _朝向别名_.get(D));
                }else if(_朝向别名_.containsKey(new StringBuilder().appendCodePoint(D.codePointAt(0)).toString())){ //单汉字串
                    D.codePoints()
                            .filter(code -> !Character.isWhitespace(code))
                            .mapToObj(code -> new StringBuilder().appendCodePoint(code).toString())
                            .peek(c -> Assertions.assertTrue(_朝向别名_.containsKey(c),c +" isn't a valid direction!"))
                            .map(c -> Pair.of(c, _朝向别名_.get(c)))
                            .forEach(pair -> add.accept(pair.getLeft(),pair.getRight()));
                }else{
                    Assertions.fail("Unknown Direction "+D);
                }
            }
        }
        return directions;
    }

    @SuppressWarnings("unused")
    public static void 测试单层坡度流动算法_Inner(final @Nonnull Object[] raw, final int[] beginPosRaw, final @Nonnull String[] expectedDirections){
        final BlockPos beginPos = new BlockPos(beginPosRaw[0],beginPosRaw[1],beginPosRaw[2]);
        final @Nonnull 数组沙盒 sandbox = initWorldSandbox(raw,beginPos);
        final @Nonnull IBlockState beginState = world.getBlockState(beginPos);
        Assertions.assertEquals(7,beginState.getValue(BlockLiquid.LEVEL));
        final @Nonnull FiniteFlowingVanilla flowing = getFlowingByMaterial(beginState.getMaterial());
        final Set<EnumFacing> directions = EnumSet.noneOf(EnumFacing.class);
        final Set<EnumFacing> expected = 解析应当的朝向结果(expectedDirections);

        flowing.singleSlopeAlgorithm(world,beginPos,directions);

        LOGGER.info("expected : {}",expected.stream().map(EnumFacing::toString).collect(Collectors.joining(",")));
        LOGGER.info("in fact: {}",directions.stream().map(EnumFacing::toString).collect(Collectors.joining(",")));
        Assertions.assertIterableEquals(expected,directions);
    }
}
