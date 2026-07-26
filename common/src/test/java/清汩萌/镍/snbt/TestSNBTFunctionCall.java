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
import moe.qingu.nickel.command.exception.NickelRuntimeException;
import moe.qingu.nickel.nbt.SNBTReader;
import moe.qingu.nickel.nbt.operation.SNBTOperations;
import moe.qingu.nickel.reader.InputReader;
import net.minecraft.command.CommandException;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import 清汩萌.造.造;
import 清汩萌.镍.镍测试;

/**
 * SNBT 函数在表达式中的端到端调用路径测试：字符串 → SNBTReader 解析 →
 * SNBTOperations.resolve 重载决议 → 函数求值。用例数据与格式见 data/nickel/snbt/函数调用/。
 * <p>注册表为全局状态，内置函数由基类 {@link 镍测试} 幂等加载，绝不清空注册表。
 * @author QGMoe, Claude
 * @see SNBTReader
 * @see SNBTOperations
 */
public final class TestSNBTFunctionCall extends 镍测试 {
    private static final @Nonnull String DATA_PATH = "data/nickel/snbt/函数调用/";

    /**
     * Claude Generated
     * 解析 SNBT 文本；挂一个无命令分支的弱上下文，使解析失败时 panic 抛 NickelRuntimeException 而非 NPE
     */
    @Nonnull
    private static NBTTagCompound parseSNBT(final @Nonnull String snbt) throws CommandException {
        final InputReader input = new InputReader(snbt);
        new CommandContext(input,null,null,null); //构造器会把自身挂到 input 上
        return SNBTReader.readNBTFromInput(input);
    }

    /**
     * Claude Generated
     * 端到端用例：正常用例比较解析结果与期望结构，错误用例断言抛 NickelRuntimeException
     */
    @ParameterizedTest
    @MethodSource("pullDataForTestFunctionCall")
    public void testFunctionCall(final @Nonnull FunctionCallCase c) throws CommandException {
        if(c.error){
            final NickelRuntimeException e = Assertions.assertThrows(
                    NickelRuntimeException.class,
                    () -> parseSNBT(c.input)
            );
            镍测试._镍日志_.info("用例 [{}] 输入={} 按预期抛出：{}",c.name,c.input,e.getInformation());
        }else {
            final NBTTagCompound expected = parseSNBT(c.expected);
            final NBTTagCompound actual = parseSNBT(c.input);
            镍测试._镍日志_.info("用例 [{}] 输入={} 期望={} 实际={}",c.name,c.input,expected,actual);
            Assertions.assertEquals(expected,actual,"用例："+c.name);
        }
    }

    /**
     * Claude Generated
     * uuid 单参的端到端调用（含与其他键共存），期望值以程序构造直接断言
     */
    @Test
    public void uuidSingleArgEndToEndTest() throws CommandException {
        final NBTTagCompound basic = parseSNBT("{id:uuid(\"00000000-0000-0001-0000-000000000002\")}");
        Assertions.assertEquals(new NBTTagIntArray(new int[]{0,1,0,2}),basic.getTag("id"),"uuid-单参基本");

        final NBTTagCompound repeated = parseSNBT("{id:uuid(\"12345678-1234-5678-1234-567812345678\")}");
        Assertions.assertEquals(new NBTTagIntArray(new int[]{305419896,305419896,305419896,305419896}),
                repeated.getTag("id"),"uuid-单参重复段");

        final NBTTagCompound allF = parseSNBT("{id:uuid(\"FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF\")}");
        Assertions.assertEquals(new NBTTagIntArray(new int[]{-1,-1,-1,-1}),allF.getTag("id"),"uuid-单参全F大写");

        final NBTTagCompound coexist = parseSNBT("{a:1,id:uuid(\"00000000-0000-0001-0000-000000000002\"),b:2}");
        Assertions.assertEquals(new NBTTagInt(1),coexist.getTag("a"),"uuid-与其他键共存：a");
        Assertions.assertEquals(new NBTTagIntArray(new int[]{0,1,0,2}),coexist.getTag("id"),"uuid-与其他键共存：id");
        Assertions.assertEquals(new NBTTagInt(2),coexist.getTag("b"),"uuid-与其他键共存：b");
    }

    /**
     * Claude Generated
     * typed array 字面量作函数实参（concat([B;1b,2b],[B;3b])）正常求值
     */
    @Test
    public void typedArrayArgumentTest() throws CommandException {
        final NBTTagCompound actual = parseSNBT("{r:concat([B;1b,2b],[B;3b])}");
        Assertions.assertEquals(new NBTTagByteArray(new byte[]{1,2,3}),actual.getTag("r"));
    }

    /**
     * Claude Generated
     * 从 data/nickel/snbt/函数调用/ 目录下的全部 YAML 文件收集用例
     */
    @Nonnull
    public static Stream<FunctionCallCase> pullDataForTestFunctionCall(){
        final List<FunctionCallCase> cases = new ArrayList<>();
        try (final @Nonnull ScanResult scan = new ClassGraph().acceptPaths(DATA_PATH).scan()){
            for(final Resource res:scan.getResourcesWithExtension("yaml")){
                try(final @Nonnull Reader reader = Files.newBufferedReader(Paths.get(res.getURI()),StandardCharsets.UTF_8)){
                    final Map<String,Object> root = 造.YAML.load(reader);
                    @SuppressWarnings("unchecked")
                    final List<Map<String,Object>> rawCases = (List<Map<String,Object>>) root.get("cases");
                    Assertions.assertNotNull(rawCases,"数据文件缺少 cases 节点："+res.getPath());
                    for(final Map<String,Object> raw:rawCases) cases.add(new FunctionCallCase(res.getPath(),raw));
                }
            }
        }catch (final Exception e){
            Assertions.fail("error occurred when loading cases from "+DATA_PATH,e);
        }
        if(cases.isEmpty()) Assertions.fail("未在 "+DATA_PATH+" 找到任何用例");
        return cases.stream();
    }

    /**
     * 端到端用例数据（Claude Generated）
     */
    public static final class FunctionCallCase {
        final @Nonnull String name;
        final @Nonnull String input;
        final @Nullable String expected;
        final boolean error;

        FunctionCallCase(final @Nonnull String source,final @Nonnull Map<String,Object> raw){
            final Object rawName = raw.get("name");
            final Object rawInput = raw.get("input");
            final Object rawExpected = raw.get("expected");
            final Object rawError = raw.get("error");
            Assertions.assertNotNull(rawName,"用例缺少 name 字段（"+source+"）");
            Assertions.assertNotNull(rawInput,"用例缺少 input 字段："+rawName);
            this.name = (String) rawName;
            this.input = (String) rawInput;
            this.expected = (String) rawExpected;
            this.error = rawError != null && (Boolean) rawError;
            if(!this.error) Assertions.assertNotNull(this.expected,"非错误用例必须提供 expected："+this.name);
        }

        @Override
        public @Nonnull String toString() {
            return name;
        }
    }
}
