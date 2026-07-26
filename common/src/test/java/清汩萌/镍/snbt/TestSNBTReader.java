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

import moe.qingu.nickel.command.exception.NickelRuntimeException;
import moe.qingu.nickel.nbt.SNBTReader;
import moe.qingu.nickel.reader.InputReader;
import net.minecraft.command.CommandException;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLongArray;
import net.minecraft.nbt.NBTTagString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import javax.annotation.Nonnull;
import java.util.stream.Stream;

import 清汩萌.镍.镍测试;

/**
 * SNBT 读取器（SNBTReader）单元测试：合法输入的结构还原、非法输入的异常行为，
 * 以及 typed array（[B;/[I;/[L;）字面量解析。
 * Claude Generated Tests
 * @author Claude
 * @see SNBTReader
 */
public final class TestSNBTReader {

    // ======================== 数据驱动：合法输入 ========================

    /**
     * Claude Generated
     * 解析合法 SNBT 输入，与期望 NBT 结构做 equals 比较
     */
    @ParameterizedTest
    @MethodSource("pullDataForRead")
    public void readTest(final @Nonnull SNBTTestSupport.SNBTCase data) throws CommandException {
        final NBTBase expected = SNBTTestSupport.buildExpected(data.expected);
        final NBTTagCompound actual = parse(data);
        镍测试._镍日志_.info("SNBT read case [{}] input={} expected={} actual={}",data,data.input,expected,actual);
        Assertions.assertEquals(expected,actual,"Failed case: "+data);
    }

    /**
     * Claude Generated
     * 提供合法输入数据集（data/nickel/snbt/Read/）
     */
    public static @Nonnull Stream<SNBTTestSupport.SNBTCase> pullDataForRead(){
        return SNBTTestSupport.loadCases("data/nickel/snbt/Read/");
    }

    // ======================== 数据驱动：非法输入 ========================

    /**
     * Claude Generated
     * 解析非法 SNBT 输入，断言抛出 NickelRuntimeException（测试环境下所有 panic 的形态）
     */
    @ParameterizedTest
    @MethodSource("pullDataForReadError")
    public void readErrorTest(final @Nonnull SNBTTestSupport.SNBTCase data){
        镍测试._镍日志_.info("SNBT read error case [{}] input={}",data,data.input);
        Assertions.assertThrows(
                NickelRuntimeException.class,
                () -> parse(data),
                "Failed case: "+data
        );
    }

    /**
     * Claude Generated
     * 提供非法输入数据集（data/nickel/snbt/ReadError/）
     */
    public static @Nonnull Stream<SNBTTestSupport.SNBTCase> pullDataForReadError(){
        return SNBTTestSupport.loadCases("data/nickel/snbt/ReadError/");
    }

    /**
     * Claude Generated
     * 按用例的 mode 选择解析入口（compound=readNBTFromInput，single=readSingleNBTFromInput）
     */
    private static @Nonnull NBTTagCompound parse(final @Nonnull SNBTTestSupport.SNBTCase data) throws CommandException {
        final InputReader input = SNBTTestSupport.newInput(data.input);
        if("single".equals(data.mode)) return SNBTReader.readSingleNBTFromInput(input);
        return SNBTReader.readNBTFromInput(input);
    }

    // ======================== 程序生成的极端输入 ========================

    /**
     * Claude Generated
     * 测试 64 层嵌套复合标签（栈递归深度）
     */
    @Test
    public void deepNestingTest() throws CommandException {
        final int depth = 64;
        String input = "{v:1}";
        NBTTagCompound expected = new NBTTagCompound();
        expected.setTag("v",new NBTTagInt(1));
        for(int i=1;i<depth;i++){
            input = "{a:"+input+"}";
            final NBTTagCompound outer = new NBTTagCompound();
            outer.setTag("a",expected);
            expected = outer;
        }
        final NBTTagCompound actual = SNBTReader.readNBTFromInput(SNBTTestSupport.newInput(input));
        镍测试._镍日志_.info("deepNestingTest depth={} inputLength={}",depth,input.length());
        Assertions.assertEquals(expected,actual);
    }

    /**
     * Claude Generated
     * 测试超长输入：一万元素整数列表
     */
    @Test
    public void longListInputTest() throws CommandException {
        final int size = 10000;
        final StringBuilder builder = new StringBuilder("{l:[");
        for(int i=0;i<size;i++){
            if(i>0) builder.append(',');
            builder.append(i);
        }
        builder.append("]}");
        final NBTTagCompound actual = SNBTReader.readNBTFromInput(SNBTTestSupport.newInput(builder.toString()));
        final NBTTagList list = (NBTTagList) actual.getTag("l");
        镍测试._镍日志_.info("longListInputTest inputLength={} tagCount={}",builder.length(),list.tagCount());
        Assertions.assertEquals(size,list.tagCount());
        Assertions.assertEquals(0,list.getIntAt(0));
        Assertions.assertEquals(1145,list.getIntAt(1145));
        Assertions.assertEquals(size-1,list.getIntAt(size-1));
    }

    /**
     * Claude Generated
     * 测试超长输入：五万字符的引号字符串值
     */
    @Test
    public void longStringInputTest() throws CommandException {
        final int size = 50000;
        final StringBuilder value = new StringBuilder(size);
        for(int i=0;i<size;i++) value.append((char) ('a'+i%26));
        final NBTTagCompound actual = SNBTReader.readNBTFromInput(SNBTTestSupport.newInput("{a:\""+value+"\"}"));
        镍测试._镍日志_.info("longStringInputTest valueLength={}",size);
        Assertions.assertEquals(new NBTTagString(value.toString()),actual.getTag("a"));
    }

    // ======================== typed array ========================

    /**
     * Claude Generated
     * 字节数组 [B;1b,2b] 读出 NBTTagByteArray
     */
    @Test
    public void typedByteArrayTest() throws CommandException {
        final NBTTagCompound actual = SNBTReader.readNBTFromInput(SNBTTestSupport.newInput("{a:[B;1b,2b]}"));
        final NBTTagCompound expected = new NBTTagCompound();
        expected.setTag("a",new NBTTagByteArray(new byte[]{1,2}));
        Assertions.assertEquals(expected,actual);
    }

    /**
     * Claude Generated
     * 整数数组 [I;1,2,3] 读出 NBTTagIntArray
     */
    @Test
    public void typedIntArrayTest() throws CommandException {
        final NBTTagCompound actual = SNBTReader.readNBTFromInput(SNBTTestSupport.newInput("{a:[I;1,2,3]}"));
        final NBTTagCompound expected = new NBTTagCompound();
        expected.setTag("a",new NBTTagIntArray(new int[]{1,2,3}));
        Assertions.assertEquals(expected,actual);
    }

    /**
     * Claude Generated
     * 空长整数数组 [L;] 读出空 NBTTagLongArray
     */
    @Test
    public void typedLongArrayTest() throws CommandException {
        final NBTTagCompound actual = SNBTReader.readNBTFromInput(SNBTTestSupport.newInput("{a:[L;]}"));
        final NBTTagCompound expected = new NBTTagCompound();
        expected.setTag("a",new NBTTagLongArray(new long[0]));
        Assertions.assertEquals(expected,actual);
    }

    /**
     * Claude Generated
     * 非空 [L;...] 字面量（元素带或不带 l 后缀）解析为长整型数组
     */
    @Test
    public void typedLongArrayNonEmptyTest() throws CommandException {
        final NBTTagCompound expected = new NBTTagCompound();
        expected.setTag("a",new NBTTagLongArray(new long[]{1L,2L}));
        Assertions.assertEquals(expected,
                SNBTReader.readNBTFromInput(SNBTTestSupport.newInput("{a:[L;1,2]}")));
        Assertions.assertEquals(expected,
                SNBTReader.readNBTFromInput(SNBTTestSupport.newInput("{a:[L;1l,2l]}")));
    }

    /**
     * Claude Generated
     * 小范围装大范围（[I;1b] 的 byte 元素装入 int 数组）合法
     */
    @Test
    public void typedArraySmallIntoLargeTest() throws CommandException {
        final NBTTagCompound actual = SNBTReader.readNBTFromInput(SNBTTestSupport.newInput("{a:[I;1b]}"));
        final NBTTagCompound expected = new NBTTagCompound();
        expected.setTag("a",new NBTTagIntArray(new int[]{1}));
        Assertions.assertEquals(expected,actual);
    }

    /**
     * Claude Generated
     * 空字节数组 [B;] 读出空 NBTTagByteArray
     */
    @Test
    public void emptyTypedArrayTest() throws CommandException {
        final NBTTagCompound actual = SNBTReader.readNBTFromInput(SNBTTestSupport.newInput("{a:[B;]}"));
        final NBTTagCompound expected = new NBTTagCompound();
        expected.setTag("a",new NBTTagByteArray(new byte[0]));
        Assertions.assertEquals(expected,actual);
    }
}
