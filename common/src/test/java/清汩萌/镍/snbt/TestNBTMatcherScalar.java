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

import moe.qingu.nickel.nbt.matcher.NBTByteMatcher;
import moe.qingu.nickel.nbt.matcher.NBTDoubleMatcher;
import moe.qingu.nickel.nbt.matcher.NBTFloatMatcher;
import moe.qingu.nickel.nbt.matcher.NBTIntMatcher;
import moe.qingu.nickel.nbt.matcher.NBTLongMatcher;
import moe.qingu.nickel.nbt.matcher.NBTMatcher;
import moe.qingu.nickel.nbt.matcher.NBTShortMatcher;
import moe.qingu.nickel.nbt.matcher.NBTStringMatcher;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.annotation.Nonnull;

import 清汩萌.镍.镍测试;

/**
 * NBTMatcher 标量匹配器（Byte/Short/Int/Long/Float/Double/String）的匹配语义测试
 * Claude Generated Tests
 * @author Claude
 * @see moe.qingu.nickel.nbt.matcher.NBTPrimitiveMatcher
 */
public final class TestNBTMatcherScalar {

    /**
     * Claude Generated
     * 测试类型严格性：同值异型的 7x7 全矩阵，仅对角线（同类型）允许匹配
     */
    @Test
    public void typeStrictnessMatrixTest(){
        final @Nonnull NBTMatcher<?>[] matchers = {
                new NBTByteMatcher((byte)1),
                new NBTShortMatcher((short)1),
                new NBTIntMatcher(1),
                new NBTLongMatcher(1L),
                new NBTFloatMatcher(1f),
                new NBTDoubleMatcher(1d),
                new NBTStringMatcher("1")
        };
        final @Nonnull NBTBase[] nbts = {
                new NBTTagByte((byte)1),
                new NBTTagShort((short)1),
                new NBTTagInt(1),
                new NBTTagLong(1L),
                new NBTTagFloat(1f),
                new NBTTagDouble(1d),
                new NBTTagString("1")
        };
        for(int i=0;i<matchers.length;i++)
            for(int j=0;j<nbts.length;j++)
                Assertions.assertEquals(i==j,matchers[i].match(nbts[j]),
                        "matcher "+matchers[i]+" vs nbt "+nbts[j]);
        镍测试.镍日志.info("typeStrictnessMatrixTest passed, {} combinations checked",matchers.length*nbts.length);
    }

    /**
     * Claude Generated
     * 测试字节匹配器的等值与不等值匹配，含负数与边界值
     */
    @Test
    public void byteValueTest(){
        Assertions.assertTrue(new NBTByteMatcher((byte)1).match(new NBTTagByte((byte)1)));
        Assertions.assertFalse(new NBTByteMatcher((byte)1).match(new NBTTagByte((byte)2)));
        Assertions.assertTrue(new NBTByteMatcher((byte)-1).match(new NBTTagByte((byte)-1)));
        Assertions.assertFalse(new NBTByteMatcher((byte)-1).match(new NBTTagByte((byte)1)));
        Assertions.assertTrue(new NBTByteMatcher(Byte.MIN_VALUE).match(new NBTTagByte(Byte.MIN_VALUE)));
        Assertions.assertTrue(new NBTByteMatcher(Byte.MAX_VALUE).match(new NBTTagByte(Byte.MAX_VALUE)));
        Assertions.assertFalse(new NBTByteMatcher(Byte.MIN_VALUE).match(new NBTTagByte(Byte.MAX_VALUE)));
        Assertions.assertTrue(new NBTByteMatcher((byte)0).match(new NBTTagByte((byte)0)));
    }

    /**
     * Claude Generated
     * 测试短整型/整型/长整型匹配器的等值语义，含各自类型的边界值
     */
    @Test
    public void shortIntLongValueTest(){
        Assertions.assertTrue(new NBTShortMatcher((short)114).match(new NBTTagShort((short)114)));
        Assertions.assertFalse(new NBTShortMatcher((short)114).match(new NBTTagShort((short)514)));
        Assertions.assertTrue(new NBTShortMatcher(Short.MIN_VALUE).match(new NBTTagShort(Short.MIN_VALUE)));

        Assertions.assertTrue(new NBTIntMatcher(114514).match(new NBTTagInt(114514)));
        Assertions.assertFalse(new NBTIntMatcher(114514).match(new NBTTagInt(1919810)));
        Assertions.assertTrue(new NBTIntMatcher(Integer.MIN_VALUE).match(new NBTTagInt(Integer.MIN_VALUE)));
        Assertions.assertTrue(new NBTIntMatcher(Integer.MAX_VALUE).match(new NBTTagInt(Integer.MAX_VALUE)));

        Assertions.assertTrue(new NBTLongMatcher(1919810L).match(new NBTTagLong(1919810L)));
        Assertions.assertFalse(new NBTLongMatcher(1919810L).match(new NBTTagLong(114514L)));
        Assertions.assertTrue(new NBTLongMatcher(Long.MIN_VALUE).match(new NBTTagLong(Long.MIN_VALUE)));
        Assertions.assertTrue(new NBTLongMatcher(Long.MAX_VALUE).match(new NBTTagLong(Long.MAX_VALUE)));
        Assertions.assertFalse(new NBTLongMatcher(Long.MIN_VALUE).match(new NBTTagLong(Long.MAX_VALUE)));
    }

    /**
     * Claude Generated
     * 测试浮点匹配器的精确比较语义（==，不带容差）
     */
    @Test
    public void floatExactCompareTest(){
        Assertions.assertTrue(new NBTFloatMatcher(1.5f).match(new NBTTagFloat(1.5f)));
        Assertions.assertFalse(new NBTFloatMatcher(1.5f).match(new NBTTagFloat(1.5000001f)));
        Assertions.assertTrue(new NBTFloatMatcher(0.1f).match(new NBTTagFloat(0.1f)));
        Assertions.assertFalse(new NBTFloatMatcher(-1.5f).match(new NBTTagFloat(1.5f)));

        Assertions.assertTrue(new NBTDoubleMatcher(2.5d).match(new NBTTagDouble(2.5d)));
        Assertions.assertFalse(new NBTDoubleMatcher(2.5d).match(new NBTTagDouble(2.5000000001d)));
        Assertions.assertTrue(new NBTDoubleMatcher(0.1d).match(new NBTTagDouble(0.1d)));
        //0.1f 提升为 double 后不等于 0.1d
        Assertions.assertFalse(new NBTDoubleMatcher(0.1d).match(new NBTTagDouble(0.1f)));
    }

    /**
     * Claude Generated
     * 预期行为：NaN 按 == 语义永不匹配（包括 matcher 与目标均为 NaN 的情形）
     */
    @Test
    public void floatNaNNeverMatchTest(){
        Assertions.assertFalse(new NBTFloatMatcher(Float.NaN).match(new NBTTagFloat(Float.NaN)));
        Assertions.assertFalse(new NBTFloatMatcher(Float.NaN).match(new NBTTagFloat(1f)));
        Assertions.assertFalse(new NBTFloatMatcher(1f).match(new NBTTagFloat(Float.NaN)));
        Assertions.assertFalse(new NBTDoubleMatcher(Double.NaN).match(new NBTTagDouble(Double.NaN)));
        Assertions.assertFalse(new NBTDoubleMatcher(1d).match(new NBTTagDouble(Double.NaN)));
    }

    /**
     * Claude Generated
     * 预期行为：+0.0 与 -0.0 按 == 语义互相匹配
     */
    @Test
    public void floatSignedZeroTest(){
        Assertions.assertTrue(new NBTFloatMatcher(0.0f).match(new NBTTagFloat(-0.0f)));
        Assertions.assertTrue(new NBTFloatMatcher(-0.0f).match(new NBTTagFloat(0.0f)));
        Assertions.assertTrue(new NBTDoubleMatcher(0.0d).match(new NBTTagDouble(-0.0d)));
        Assertions.assertTrue(new NBTDoubleMatcher(-0.0d).match(new NBTTagDouble(0.0d)));
    }

    /**
     * Claude Generated
     * 测试字符串匹配器：equals 语义、空串、中文、大小写敏感
     */
    @Test
    public void stringValueTest(){
        Assertions.assertTrue(new NBTStringMatcher("hello").match(new NBTTagString("hello")));
        Assertions.assertFalse(new NBTStringMatcher("hello").match(new NBTTagString("world")));
        Assertions.assertFalse(new NBTStringMatcher("hello").match(new NBTTagString("Hello"))); //大小写敏感
        Assertions.assertTrue(new NBTStringMatcher("").match(new NBTTagString("")));
        Assertions.assertFalse(new NBTStringMatcher("").match(new NBTTagString(" ")));
        Assertions.assertTrue(new NBTStringMatcher("天圆地方").match(new NBTTagString("天圆地方")));
        Assertions.assertFalse(new NBTStringMatcher("天圆地方").match(new NBTTagString("天圆")));
        Assertions.assertFalse(new NBTStringMatcher("hello").match(new NBTTagString("hello "))); //尾随空格不等
    }

    /**
     * Claude Generated
     * 测试标量匹配器的 equals/hashCode：同内容不同实例相等，跨子类（后缀不同）不相等
     */
    @Test
    public void equalsAndHashCodeTest(){
        Assertions.assertEquals(new NBTByteMatcher((byte)1),new NBTByteMatcher((byte)1));
        Assertions.assertEquals(new NBTByteMatcher((byte)1).hashCode(),new NBTByteMatcher((byte)1).hashCode());
        Assertions.assertNotEquals(new NBTByteMatcher((byte)1),new NBTByteMatcher((byte)2));
        //跨子类：后缀不同（b vs s vs 无 vs L），即使数值相同也不相等
        Assertions.assertNotEquals(new NBTByteMatcher((byte)1),new NBTShortMatcher((short)1));
        Assertions.assertNotEquals(new NBTIntMatcher(1),new NBTLongMatcher(1L));
        Assertions.assertNotEquals(new NBTShortMatcher((short)1),new NBTIntMatcher(1));

        Assertions.assertEquals(new NBTFloatMatcher(1.5f),new NBTFloatMatcher(1.5f));
        Assertions.assertNotEquals(new NBTFloatMatcher(1.5f),new NBTDoubleMatcher(1.5d));
        Assertions.assertEquals(new NBTDoubleMatcher(2.5d),new NBTDoubleMatcher(2.5d));
        Assertions.assertEquals(new NBTStringMatcher("a"),new NBTStringMatcher("a"));
        Assertions.assertNotEquals(new NBTStringMatcher("a"),new NBTStringMatcher("b"));
        Assertions.assertEquals(new NBTStringMatcher("a").hashCode(),new NBTStringMatcher("a").hashCode());
    }

    /**
     * Claude Generated
     * 测试标量匹配器的 toString：整型带后缀（int 无后缀），浮点恒带 f/d
     */
    @Test
    public void scalarToStringTest(){
        Assertions.assertEquals("1b",new NBTByteMatcher((byte)1).toString());
        Assertions.assertEquals("2s",new NBTShortMatcher((short)2).toString());
        Assertions.assertEquals("3",new NBTIntMatcher(3).toString()); //int 后缀为 0，无后缀
        Assertions.assertEquals("4L",new NBTLongMatcher(4L).toString());
        Assertions.assertEquals("-1b",new NBTByteMatcher((byte)-1).toString());
        Assertions.assertEquals(1.5f+"f",new NBTFloatMatcher(1.5f).toString());
        Assertions.assertEquals(2.5d+"d",new NBTDoubleMatcher(2.5d).toString());
        //无需引号的字符串保持原样，含特殊字符的字符串被引号包裹
        Assertions.assertEquals("abc",new NBTStringMatcher("abc").toString());
        Assertions.assertEquals("\"a b\"",new NBTStringMatcher("a b").toString());
        镍测试.镍日志.info("scalarToStringTest passed");
    }

    /**
     * Claude Generated
     * 测试 getExpectedLong/getExpectedFloat/getExpectedDouble/getExpected 取值一致
     */
    @Test
    public void expectedValueAccessorTest(){
        Assertions.assertEquals(5L,new NBTByteMatcher((byte)5).getExpectedLong());
        Assertions.assertEquals(-7L,new NBTLongMatcher(-7L).getExpectedLong());
        Assertions.assertEquals(1.5f,new NBTFloatMatcher(1.5f).getExpectedFloat());
        Assertions.assertEquals(2.5d,new NBTDoubleMatcher(2.5d).getExpectedDouble());
        Assertions.assertEquals("圆方",new NBTStringMatcher("圆方").getExpected());
    }
}
