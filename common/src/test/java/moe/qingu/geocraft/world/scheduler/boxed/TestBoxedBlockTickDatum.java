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

package moe.qingu.geocraft.world.scheduler.boxed;

import io.github.classgraph.Resource;
import io.github.classgraph.ScanResult;
import moe.qingu.geocraft.util.math.MathUtil;
import moe.qingu.geocraft.world.scheduler.TestBlockTickDatum;
import moe.qingu.geocraft.world.scheduler.计划刻数据;
import moe.qingu.nickel.nbt.SNBTReader;
import moe.qingu.nickel.nbt.matcher.NBTMatcher;
import moe.qingu.nickel.reader.InputReader;
import net.minecraft.command.CommandException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.chunk.EmptyChunk;
import org.apache.commons.io.FilenameUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import 清汩萌.天圆地方.util.ClassGraphUtils;
import 清汩萌.天圆地方.util.IOBiConsumer;
import 清汩萌.天圆地方.world.MockSimpleWorld;
import 清汩萌.天圆地方.world.storage.MockWorldInfo;
import 清汩萌.造.造;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author QGMoe
 */
public final class TestBoxedBlockTickDatum extends TestBlockTickDatum {

    @ParameterizedTest
    @MethodSource("为测试装箱反序列化准备数据")
    public void 测试装箱反序列化(final @Nonnull 装箱反序列化与序列化测试项 data) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        test(new Object[]{data.in,data.ans,data.x,data.z});
    }

    @ParameterizedTest
    @MethodSource("为测试装箱序列化准备数据")
    public void 测试装箱序列化(final @Nonnull 装箱反序列化与序列化测试项 data) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        test(new Object[]{data.in,data.ans,data.x,data.z});
    }

    @SuppressWarnings("unused")
    public static void 测试装箱反序列化_Inner(final @Nonnull String in,final @Nonnull String ans,final int x,final int z) throws CommandException {
        final NBTTagCompound inNBT = SNBTReader.readSingleNBTFromInput(new InputReader(in));
        final 计划刻数据 $答案数据 = 造.YAML.loadAs(ans,计划刻数据.class);
        $答案数据.初始化();
        final World world = MockSimpleWorld.create(MockWorldInfo.create(b -> {}),false);
        final EmptyChunk chunk = new EmptyChunk(world,x,z);
        final BoxedBlockTickDatum datum = new BoxedBlockTickDatum(chunk);
        datum.deserializeNBT(inNBT);
        $答案数据.假设相等(new ArrayList<>(datum.queue));
    }

    @SuppressWarnings("unused")
    public static void 测试装箱序列化_Inner(final @Nonnull String in,final @Nonnull String ans,final int x,final int z) throws CommandException {
        final 计划刻数据 $计划刻数据 = 造.YAML.loadAs(in,计划刻数据.class);
        $计划刻数据.初始化();
        final World world = MockSimpleWorld.create(MockWorldInfo.create(b -> b.withTotalTime($计划刻数据.time)),false);
        final EmptyChunk chunk = new EmptyChunk(world,x,z);
        final BoxedBlockTickDatum datum = new BoxedBlockTickDatum(chunk);
        final int baseX = x << 4;
        final int baseZ = z << 4;
        $计划刻数据.ticks.forEach(t ->{
            Assertions.assertTrue(MathUtil.inRangeClose(t.x,baseX,baseX+15),t.x+" doesn't fit chunk "+baseX +" to "+(baseX+15));
            Assertions.assertTrue(MathUtil.inRangeClose(t.z,baseZ,baseZ+15),t.z+" doesn't fit chunk "+ baseZ +" to "+(baseZ+15));
        });
        $计划刻数据.ticks.forEach(datum::schedule);
        datum.markDirty();
        final NBTTagCompound compound = datum.serializeNBT();
        final NBTTagCompound expected = SNBTReader.readSingleNBTFromInput(new InputReader(ans));
        final NBTMatcher<?> matcher = NBTMatcher.toMatcher(expected);
        matcher.setStrict(true);
        Assertions.assertTrue(matcher.match(compound),()-> "\nexpected:"+expected+"\nactual:"+compound+"\n");
    }

    public static final class 装箱反序列化与序列化测试项 {
        public final String in;
        public final String ans;
        public final int x;
        public final int z;
        public final String name;

        public 装箱反序列化与序列化测试项(final @Nonnull String in, final @Nonnull String ans, final @Nonnull String name) {
            this.in = in;
            this.ans = ans;
            final String z = FilenameUtils.getExtension(name);
            this.z = Integer.parseInt(z);
            this.x = Integer.parseInt(FilenameUtils.getExtension(FilenameUtils.getBaseName(name)));
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    @Nonnull
    public static Stream<装箱反序列化与序列化测试项> 为测试装箱反序列化准备数据(){
        final List<装箱反序列化与序列化测试项> list = new ArrayList<>();
        final IOBiConsumer<ScanResult, Resource> consumer = (scan, r)->{
            final String in = ClassGraphUtils.getLinesWithoutYAMLComments(r).collect(Collectors.joining());
            final String ans = ClassGraphUtils.getAnswerByInput("snbt","yaml",scan,r).getContentAsString();
            list.add(new 装箱反序列化与序列化测试项(in,ans,FilenameUtils.getBaseName(FilenameUtils.getBaseName(r.getPath()))));
        };
        ClassGraphUtils.findInputs("data/world/schedule/common/（反）序列化/","snbt",consumer);
        ClassGraphUtils.findInputs("data/world/schedule/boxed/（反）序列化/","snbt", consumer);
        return list.stream();
    }

    @Nonnull
    public static Stream<装箱反序列化与序列化测试项> 为测试装箱序列化准备数据(){
        final List<装箱反序列化与序列化测试项> list = new ArrayList<>();
        final IOBiConsumer<ScanResult, Resource> consumer = (scan, r)->{
            final String in = r.getContentAsString();
            final String ans = ClassGraphUtils.getLinesWithoutYAMLComments(ClassGraphUtils.getAnswerByInput("yaml","snbt",scan,r)).collect(Collectors.joining());
            list.add(new 装箱反序列化与序列化测试项(in,ans,FilenameUtils.getBaseName(FilenameUtils.getBaseName(r.getPath()))));
        };
        ClassGraphUtils.findInputs("data/world/schedule/common/序列化/","yaml",consumer);
        ClassGraphUtils.findInputs("data/world/schedule/boxed/序列化/","yaml", consumer);
        return list.stream();
    }

}
