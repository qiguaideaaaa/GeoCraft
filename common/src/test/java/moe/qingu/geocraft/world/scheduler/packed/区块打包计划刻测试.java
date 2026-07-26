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

package moe.qingu.geocraft.world.scheduler.packed;

import io.github.classgraph.Resource;
import io.github.classgraph.ScanResult;
import moe.qingu.geocraft.api.world.tick.IScheduledTick;
import moe.qingu.geocraft.world.scheduler.方块计划刻调度器测试;
import moe.qingu.geocraft.world.scheduler.计划刻数据;
import moe.qingu.nickel.nbt.SNBTReader;
import moe.qingu.nickel.nbt.matcher.NBTMatcher;
import moe.qingu.nickel.reader.InputReader;
import net.minecraft.block.Block;
import net.minecraft.command.CommandException;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import 清汩萌.天圆地方.util.ClassGraphUtils;
import 清汩萌.天圆地方.util.IOBiConsumer;
import 清汩萌.造.造;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author QGMoe
 */
public final class 区块打包计划刻测试 extends 方块计划刻调度器测试 {

    @ParameterizedTest
    @MethodSource("为测试打包反序列化准备数据")
    public void 测试打包反序列化(final @Nonnull 打包反序列化与序列化测试项 $项) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        test(new Object[]{$项.in,$项.ans});
    }

    @ParameterizedTest
    @MethodSource("为测试打包序列化准备数据")
    public void 测试打包序列化(final @Nonnull 打包反序列化与序列化测试项 $项) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        test(new Object[]{$项.in,$项.ans});
    }

    @SuppressWarnings("unused")
    public static void 测试打包反序列化_Inner(final @Nonnull String in,final @Nonnull String ans) throws CommandException {
        final NBTTagCompound inNBT = SNBTReader.readSingleNBTFromInput(new InputReader(in));
        final 计划刻数据 $答案数据 = 造.YAML.loadAs(ans,计划刻数据.class);
        $答案数据.初始化();
        final PackedBlockTickDatum datum = new PackedBlockTickDatum();
        datum.deserializeNBT(inNBT);
        final List<IScheduledTick> inTicks = Optional.ofNullable(datum.queue)
                .map(q -> q.stream().collect(Collectors.toList()))
                .orElse(Collections.emptyList());
        $答案数据.假设相等(inTicks);
    }

    @SuppressWarnings("unused")
    public static void 测试打包序列化_Inner(final @Nonnull String in,final @Nonnull String ans) throws CommandException {
        final 计划刻数据 $计划刻数据 = 造.YAML.loadAs(in,计划刻数据.class);
        $计划刻数据.初始化();
        final PackedBlockTickDatum datum = new PackedBlockTickDatum();
        $计划刻数据.ticks.forEach(t -> datum.schedule($计划刻数据.time, t.$于[0],t.$于[1],t.$于[2], Block.REGISTRY.getIDForObject(t.block()),t.$时 -$计划刻数据.time,t.priority()));
        datum.markDirty();
        final NBTTagCompound compound = datum.serializeNBT();
        final NBTTagCompound expected = SNBTReader.readSingleNBTFromInput(new InputReader(ans));
        final NBTMatcher<?> matcher = NBTMatcher.toMatcher(expected,true);
        Assertions.assertTrue(matcher.match(compound),()-> "\nexpected:"+expected+"\nactual:"+compound+"\n");
    }

    public static final class 打包反序列化与序列化测试项 {
        public final String in;
        public final String ans;
        public final String name;

        public 打包反序列化与序列化测试项(final @Nonnull String in, final @Nonnull String ans, final @Nonnull String name) {
            this.in = in;
            this.ans = ans;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    @Nonnull
    public static Stream<打包反序列化与序列化测试项> 为测试打包反序列化准备数据(){
        final List<打包反序列化与序列化测试项> list = new ArrayList<>();
        final IOBiConsumer<ScanResult, Resource> consumer = (scan,r)->{
            final String in = ClassGraphUtils.获取去除YAML风格注释的文本(r).collect(Collectors.joining());
            final String ans = ClassGraphUtils.基于样例输入获取答案("yaml",scan,r).getContentAsString();
            list.add(new 打包反序列化与序列化测试项(in,ans, FilenameUtils.getBaseName(FilenameUtils.getBaseName(r.getPath()))));
        };
        ClassGraphUtils.寻找样例输入("data/world/schedule/common/（反）序列化/","snbt",consumer);
        ClassGraphUtils.寻找样例输入("data/world/schedule/packed/（反）序列化/","snbt",consumer);
        return list.stream();
    }

    @Nonnull
    public static Stream<打包反序列化与序列化测试项> 为测试打包序列化准备数据(){
        final List<打包反序列化与序列化测试项> list = new ArrayList<>();
        final IOBiConsumer<ScanResult, Resource> consumer = (scan,r)->{
            final String in = r.getContentAsString();
            final String ans = ClassGraphUtils.获取去除YAML风格注释的文本(ClassGraphUtils.基于样例输入获取答案("snbt",scan,r)).collect(Collectors.joining());
            list.add(new 打包反序列化与序列化测试项(in,ans, FilenameUtils.getBaseName(FilenameUtils.getBaseName(r.getPath()))));
        };
        ClassGraphUtils.寻找样例输入("data/world/schedule/common/序列化/","yaml",consumer);
        ClassGraphUtils.寻找样例输入("data/world/schedule/packed/序列化/","yaml",consumer);
        return list.stream();
    }


}
