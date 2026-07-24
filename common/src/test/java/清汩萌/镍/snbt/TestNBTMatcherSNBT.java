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

package 清汩萌.镍.snbt;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.Resource;
import io.github.classgraph.ScanResult;
import moe.qingu.nickel.command.context.CommandContext;
import moe.qingu.nickel.nbt.SNBTReader;
import moe.qingu.nickel.nbt.matcher.NBTCompoundMatcher;
import moe.qingu.nickel.nbt.matcher.NBTMatcher;
import moe.qingu.nickel.reader.InputReader;
import net.minecraft.command.CommandException;
import net.minecraft.nbt.NBTTagCompound;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import 清汩萌.镍.镍测试;

/**
 * SNBT 与 NBTMatcher 联动的端到端匹配测试：
 * 字符串 → SNBTReader 解析 → NBTMatcher.toMatcher 转换 → 对 NBTTagCompound 匹配。
 * 用例数据在 test resources 的 data/nickel/snbt/matcher/ 下，YAML 参数化。
 * Claude Generated Tests
 * @author Claude
 * @see moe.qingu.nickel.nbt.matcher.NBTMatcher
 * @see moe.qingu.nickel.nbt.SNBTReader
 */
public final class TestNBTMatcherSNBT extends 镍测试 {

    private static final @Nonnull String DATA_PATH = "data/nickel/snbt/matcher/";
    private static final @Nonnull Yaml YAML = new Yaml();

    /**
     * Claude Generated
     * 为原文构造 InputReader 并挂上无分支的 CommandContext（panic 路径依赖 context）
     */
    @Nonnull
    private static InputReader newInput(final @Nonnull String raw){
        final @Nonnull InputReader reader = new InputReader(raw);
        new CommandContext(reader,null,null,null); //构造器内部会把自身挂到 reader 上
        return reader;
    }

    /**
     * Claude Generated
     * 数据源：扫描 data/nickel/snbt/matcher/ 下所有 yaml 用例文件
     */
    @SuppressWarnings("unchecked")
    public static @Nonnull Stream<SNBT匹配用例> pullDataForSNBTMatch(){
        final List<SNBT匹配用例> cases = new ArrayList<>();
        try (final @Nonnull ScanResult scan = new ClassGraph().acceptPaths(DATA_PATH).scan()){
            for(final Resource res: scan.getResourcesWithExtension("yaml")){
                try(final @Nonnull Reader reader = new InputStreamReader(res.open(),StandardCharsets.UTF_8)){
                    final Map<String,Object> root = YAML.load(reader);
                    final String group = String.valueOf(root.get("group"));
                    final List<Map<String,Object>> rawCases = (List<Map<String,Object>>) root.get("cases");
                    for(final Map<String,Object> raw:rawCases) cases.add(new SNBT匹配用例(group,raw));
                }catch (final IOException e){
                    Assertions.fail("error occurred when processing "+res.getPath(),e);
                }
            }
        }
        Assertions.assertFalse(cases.isEmpty(),"no yaml cases found under "+DATA_PATH);
        return cases.stream();
    }

    /**
     * Claude Generated
     * 端到端：解析 matcher 与 target 两段 SNBT，转换后按期望布尔值断言匹配结果
     */
    @ParameterizedTest
    @MethodSource("pullDataForSNBTMatch")
    public void snbtEndToEndMatchTest(final @Nonnull SNBT匹配用例 c) throws CommandException {
        final @Nonnull NBTTagCompound matcherNBT = SNBTReader.readNBTFromInput(newInput(c.matcherSNBT));
        final @Nonnull NBTTagCompound target = SNBTReader.readNBTFromInput(newInput(c.targetSNBT));
        final @Nonnull NBTCompoundMatcher matcher = NBTMatcher.toMatcher(matcherNBT);
        镍测试.镍日志.info("SNBT匹配用例[{}] matcher={} target={} expected={}",c.name,matcher,c.targetSNBT,c.expected);
        Assertions.assertEquals(c.expected,matcher.match(target),"用例："+c.name);
    }

    /**
     * Claude Generated
     * 端到端补充：同一 matcher 的 toNBT 再转换（matcher→NBT→matcher）保持匹配结果不变
     */
    @ParameterizedTest
    @MethodSource("pullDataForSNBTMatch")
    public void snbtDoubleConvertStableTest(final @Nonnull SNBT匹配用例 c) throws CommandException {
        final @Nonnull NBTTagCompound matcherNBT = SNBTReader.readNBTFromInput(newInput(c.matcherSNBT));
        final @Nonnull NBTTagCompound target = SNBTReader.readNBTFromInput(newInput(c.targetSNBT));
        final @Nonnull NBTCompoundMatcher matcher = NBTMatcher.toMatcher(matcherNBT);
        final @Nonnull NBTCompoundMatcher reconverted = NBTMatcher.toMatcher(matcher.toNBT());
        Assertions.assertEquals(matcher.match(target),reconverted.match(target),"用例："+c.name);
        Assertions.assertEquals(matcher,reconverted,"用例："+c.name+"（往返后 matcher 应相等）");
    }

    /**
     * Claude Generated
     * 数组 SNBT（[B;...]）端到端进入匹配流程（数组匹配语义由 TestNBTMatcherArray 直接覆盖）
     */
    @Test
    public void arraySNBTEndToEndTest() throws CommandException {
        final @Nonnull NBTTagCompound matcherNBT = SNBTReader.readNBTFromInput(newInput("{arr:[B;1,2]}"));
        final @Nonnull NBTCompoundMatcher matcher = NBTMatcher.toMatcher(matcherNBT);
        final @Nonnull NBTTagCompound target = SNBTReader.readNBTFromInput(newInput("{arr:[B;2,1,1]}"));
        Assertions.assertTrue(matcher.match(target));
    }

    /**
     * SNBT 端到端匹配用例：来自 yaml 文件的 (matcher, target, expected) 三元组
     * Claude Generated
     */
    public static final class SNBT匹配用例 {
        public final @Nonnull String name;
        public final @Nonnull String matcherSNBT;
        public final @Nonnull String targetSNBT;
        public final boolean expected;

        public SNBT匹配用例(final @Nonnull String group,final @Nonnull Map<String,Object> raw){
            this.name = group+"-"+raw.get("name");
            this.matcherSNBT = String.valueOf(raw.get("matcher"));
            this.targetSNBT = String.valueOf(raw.get("target"));
            this.expected = (Boolean) raw.get("expected");
        }

        @Override
        public @Nonnull String toString() {
            return name;
        }
    }
}
