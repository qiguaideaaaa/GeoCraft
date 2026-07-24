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

import moe.qingu.nickel.nbt.path.NBTPath;
import moe.qingu.nickel.nbt.path.NBTPathReader;
import moe.qingu.nickel.nbt.path.node.NBTPathAll;
import moe.qingu.nickel.nbt.path.node.NBTPathIndex;
import moe.qingu.nickel.nbt.path.node.NBTPathTag;
import moe.qingu.nickel.reader.InputReader;
import net.minecraft.command.CommandException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.stream.Stream;

import 清汩萌.镍.镍测试;

/**
 * NBTPath 路径语法解析测试（NBTPathReader）。
 * 参数化数据在 data/nickel/snbt/nbtpath/解析用例.yaml。
 * @author Claude
 * @see moe.qingu.nickel.nbt.path.NBTPathReader
 */
public final class TestNBTPathParse extends 镍测试 {
    public static final @Nonnull String DATA_DIR = "data/nickel/snbt/nbtpath/";

    /**
     * Claude Generated
     * 数据驱动的路径语法解析用例
     */
    @ParameterizedTest
    @MethodSource("pullDataForParse")
    public void parseTest(final @Nonnull ParseCase c){
        if(c.error){
            final CommandException e = Assertions.assertThrows(
                    CommandException.class,
                    () -> NBTPathTestSupport.parse(c.input)
            );
            镍测试.镍日志.info("解析用例[{}] 输入=<{}> 报错类型={}",c.name,c.input,e.getClass().getSimpleName());
            if(c.errorKey != null) NBTPathTestSupport.assertInfoHasKey(e,c.errorKey);
            return;
        }
        try{
            final InputReader input = NBTPathTestSupport.readerOf(c.input);
            final NBTPath path = NBTPathReader.readPathFromInput(input);
            镍测试.镍日志.info("解析用例[{}] 输入=<{}> 结果=<{}>",c.name,c.input,path);
            if(c.expected != null) Assertions.assertEquals(c.expected,path.toString(),"用例："+c.name);
            if(c.length != null) Assertions.assertEquals(c.length.intValue(),path.length(),"用例："+c.name);
            if(c.remaining != null) Assertions.assertEquals(c.remaining,input.getSubInput(input.getCursor()),"用例："+c.name);
        }catch (final CommandException e){
            Assertions.fail("用例 "+c.name+" 不应报错",e);
        }
    }

    public static @Nonnull Stream<ParseCase> pullDataForParse(){
        return NBTPathTestSupport.loadYamlCases(DATA_DIR,"解析用例.yaml").stream().map(ParseCase::new);
    }

    /**
     * Claude Generated
     * 超长路径：100 节点解析后长度与 toString 均正确
     */
    @Test
    public void longPathTest() throws CommandException {
        final StringBuilder builder = new StringBuilder("n0");
        for(int i=1;i<100;i++) builder.append(".n").append(i);
        final NBTPath path = NBTPathTestSupport.parse(builder.toString());
        Assertions.assertEquals(100,path.length());
        Assertions.assertEquals(builder.toString(),path.toString());
    }

    /**
     * Claude Generated
     * toString 不与解析语法往返：引号键 'a.b' 的 toString 是裸 a.b，重新解析会裂成两个节点
     */
    @Test
    public void toStringNotRoundTripTest() throws CommandException {
        final NBTPath quoted = NBTPathTestSupport.parse("'a.b'");
        Assertions.assertEquals(1,quoted.length());
        Assertions.assertEquals("a.b",quoted.toString());
        final NBTPath reparsed = NBTPathTestSupport.parse(quoted.toString());
        Assertions.assertEquals(2,reparsed.length()); //预期行为：toString 不保证往返
    }

    /**
     * Claude Generated
     * 空白前置的不对称宽容：'a. b' 两节点，'a .b' 在空白处停止只得一节点
     */
    @Test
    public void whitespaceAsymmetryTest() throws CommandException {
        Assertions.assertEquals(2,NBTPathTestSupport.parse("a. b").length());
        final InputReader input = NBTPathTestSupport.readerOf("a .b");
        final NBTPath path = NBTPathReader.readPathFromInput(input);
        Assertions.assertEquals(1,path.length());
        Assertions.assertEquals(" .b",input.getSubInput(input.getCursor()));
    }

    /**
     * Claude Generated
     * subPath 取前缀，超长参数抛 IllegalArgumentException
     */
    @Test
    public void subPathTest() throws CommandException {
        final NBTPath path = NBTPathTestSupport.parse("a.b.c");
        Assertions.assertEquals("a.b",path.subPath(2).toString());
        Assertions.assertEquals(0,path.subPath(0).length());
        Assertions.assertEquals(3,path.subPath(3).length());
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> path.subPath(4)
        );
    }

    /**
     * Claude Generated
     * 节点 equals/hashCode 按内容：标签节点按键与过滤器，索引节点按下标
     */
    @Test
    public void nodeEqualityTest(){
        Assertions.assertEquals(new NBTPathTag("a",null),new NBTPathTag("a",null));
        Assertions.assertEquals(new NBTPathTag("a",null).hashCode(),new NBTPathTag("a",null).hashCode());
        Assertions.assertNotEquals(new NBTPathTag("a",null),new NBTPathTag("b",null));
        Assertions.assertEquals(new NBTPathIndex(3),new NBTPathIndex(3));
        Assertions.assertNotEquals(new NBTPathIndex(3),new NBTPathIndex(-3));
        Assertions.assertSame(NBTPathAll.ALL,NBTPathAll.ALL);
        Assertions.assertEquals("[]",NBTPathAll.ALL.toString());
    }

    /**
     * Claude Generated
     * append 追加节点并返回自身，length 随之增长
     */
    @Test
    public void appendTest(){
        final NBTPath path = new NBTPath();
        Assertions.assertEquals(0,path.length());
        Assertions.assertSame(path,path.append(new NBTPathTag("k",null)));
        Assertions.assertEquals(1,path.length());
        Assertions.assertEquals("k",path.toString());
    }

    /**
     * 解析用例数据类。
     * @author Claude
     */
    public static final class ParseCase {
        final @Nonnull String name;
        final @Nonnull String input;
        final @Nullable String expected;
        final @Nullable Integer length;
        final boolean error;
        final @Nullable String errorKey;
        final @Nullable String remaining;

        ParseCase(final @Nonnull Map<String,Object> raw){
            final String n = NBTPathTestSupport.str(raw,"名");
            this.name = n == null?"未命名":n;
            final String in = NBTPathTestSupport.str(raw,"输入");
            this.input = in == null?"":in;
            this.expected = NBTPathTestSupport.str(raw,"期望");
            final Object len = raw.get("长度");
            this.length = len == null?null:Integer.valueOf(((Number) len).intValue());
            this.error = Boolean.TRUE.equals(raw.get("异常"));
            this.errorKey = NBTPathTestSupport.str(raw,"键");
            this.remaining = NBTPathTestSupport.str(raw,"剩余");
        }

        @Override
        public @Nonnull String toString() {
            return name;
        }
    }
}
