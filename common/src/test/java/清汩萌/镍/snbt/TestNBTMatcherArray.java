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

import moe.qingu.nickel.nbt.matcher.NBTArrayMatcher;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLongArray;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.annotation.Nonnull;

import 清汩萌.镍.镍测试;

/**
 * NBTArrayMatcher 数组匹配器的匹配语义（集合语义：顺序、重复无关）测试
 * Claude Generated Tests
 * @author Claude
 * @see moe.qingu.nickel.nbt.matcher.NBTArrayMatcher
 */
public final class TestNBTMatcherArray {

    @Nonnull
    private static NBTArrayMatcher byteArrayMatcher(final @Nonnull long... expectations){
        final NBTArrayMatcher matcher = new NBTArrayMatcher(NBTTagByteArray.class);
        for(final long l:expectations) matcher.expect(l);
        return matcher;
    }

    @Nonnull
    private static NBTArrayMatcher intArrayMatcher(final @Nonnull long... expectations){
        final NBTArrayMatcher matcher = new NBTArrayMatcher(NBTTagIntArray.class);
        for(final long l:expectations) matcher.expect(l);
        return matcher;
    }

    @Nonnull
    private static NBTArrayMatcher longArrayMatcher(final @Nonnull long... expectations){
        final NBTArrayMatcher matcher = new NBTArrayMatcher(NBTTagLongArray.class);
        for(final long l:expectations) matcher.expect(l);
        return matcher;
    }

    /**
     * Claude Generated
     * 测试构造器仅接受三种数组 NBT 类，其余抛 IllegalArgumentException
     */
    @Test
    public void constructorRejectsNonArrayTypeTest(){
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new NBTArrayMatcher(NBTTagCompound.class)
        );
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new NBTArrayMatcher(NBTTagList.class)
        );
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> new NBTArrayMatcher(NBTTagInt.class)
        );
        //三种合法类型不抛
        Assertions.assertDoesNotThrow(() -> new NBTArrayMatcher(NBTTagByteArray.class));
        Assertions.assertDoesNotThrow(() -> new NBTArrayMatcher(NBTTagIntArray.class));
        Assertions.assertDoesNotThrow(() -> new NBTArrayMatcher(NBTTagLongArray.class));
    }

    /**
     * Claude Generated
     * 测试字节数组匹配：顺序与重复次数均无关，期望{1,2} 匹配 [2,1,1,2]
     */
    @Test
    public void byteArrayOrderAndDuplicateInsensitiveTest(){
        final @Nonnull NBTArrayMatcher matcher = byteArrayMatcher(1,2);
        Assertions.assertTrue(matcher.match(new NBTTagByteArray(new byte[]{1,2})));
        Assertions.assertTrue(matcher.match(new NBTTagByteArray(new byte[]{2,1})));
        Assertions.assertTrue(matcher.match(new NBTTagByteArray(new byte[]{2,1,1,2})));
        Assertions.assertTrue(matcher.match(new NBTTagByteArray(new byte[]{1,2,3}))); //子集语义
        Assertions.assertFalse(matcher.match(new NBTTagByteArray(new byte[]{1}))); //缺 2
        Assertions.assertFalse(matcher.match(new NBTTagByteArray(new byte[]{1,3})));
        Assertions.assertFalse(matcher.match(new NBTTagByteArray(new byte[0])));
    }

    /**
     * Claude Generated
     * 预期行为：先比目标"去重后"的元素个数，期望{1,2,3} 对 [1,1,2,2]（去重后 2 个）直接失败
     */
    @Test
    public void targetDeduplicatedSizeCheckTest(){
        final @Nonnull NBTArrayMatcher matcher = byteArrayMatcher(1,2,3);
        Assertions.assertFalse(matcher.match(new NBTTagByteArray(new byte[]{1,1,2,2})));
        Assertions.assertTrue(matcher.match(new NBTTagByteArray(new byte[]{1,2,3})));
        Assertions.assertTrue(matcher.match(new NBTTagByteArray(new byte[]{3,3,2,2,1,1})));
    }

    /**
     * Claude Generated
     * 测试三种数组类型互不匹配：值相同但数组类不同一律失败
     */
    @Test
    public void arrayTypesNotInterchangeableTest(){
        final @Nonnull NBTArrayMatcher byteMatcher = byteArrayMatcher(1,2);
        Assertions.assertFalse(byteMatcher.match(new NBTTagIntArray(new int[]{1,2})));
        Assertions.assertFalse(byteMatcher.match(new NBTTagLongArray(new long[]{1,2})));

        final @Nonnull NBTArrayMatcher intMatcher = intArrayMatcher(1,2);
        Assertions.assertFalse(intMatcher.match(new NBTTagByteArray(new byte[]{1,2})));
        Assertions.assertFalse(intMatcher.match(new NBTTagLongArray(new long[]{1,2})));

        final @Nonnull NBTArrayMatcher longMatcher = longArrayMatcher(1,2);
        Assertions.assertFalse(longMatcher.match(new NBTTagByteArray(new byte[]{1,2})));
        Assertions.assertFalse(longMatcher.match(new NBTTagIntArray(new int[]{1,2})));
        //也不匹配非数组类型
        Assertions.assertFalse(byteMatcher.match(new NBTTagList()));
        Assertions.assertFalse(byteMatcher.match(new NBTTagCompound()));
    }

    /**
     * Claude Generated
     * 测试空期望集匹配任意同类数组（包括空数组），但不匹配异类数组
     */
    @Test
    public void emptyExpectationsMatchAnySameTypeArrayTest(){
        final @Nonnull NBTArrayMatcher byteMatcher = byteArrayMatcher();
        Assertions.assertTrue(byteMatcher.match(new NBTTagByteArray(new byte[0])));
        Assertions.assertTrue(byteMatcher.match(new NBTTagByteArray(new byte[]{1,2,3})));
        Assertions.assertFalse(byteMatcher.match(new NBTTagIntArray(new int[0])));

        final @Nonnull NBTArrayMatcher intMatcher = intArrayMatcher();
        Assertions.assertTrue(intMatcher.match(new NBTTagIntArray(new int[0])));
        Assertions.assertTrue(intMatcher.match(new NBTTagIntArray(new int[]{114514})));
    }

    /**
     * Claude Generated
     * 测试整型数组匹配，含负数与 Integer 边界值
     */
    @Test
    public void intArrayValuesTest(){
        final @Nonnull NBTArrayMatcher matcher = intArrayMatcher(-1,Integer.MIN_VALUE,Integer.MAX_VALUE);
        Assertions.assertTrue(matcher.match(new NBTTagIntArray(new int[]{Integer.MAX_VALUE,-1,Integer.MIN_VALUE})));
        Assertions.assertFalse(matcher.match(new NBTTagIntArray(new int[]{Integer.MAX_VALUE,-1})));
        Assertions.assertFalse(matcher.match(new NBTTagIntArray(new int[]{Integer.MAX_VALUE,1,Integer.MIN_VALUE}))); //-1 换成 1
    }

    /**
     * Claude Generated
     * 测试长整型数组匹配（走 NBTUtils.streamOf 的 toString 解析路径），含负数与 Long 边界值
     */
    @Test
    public void longArrayValuesTest(){
        final @Nonnull NBTArrayMatcher matcher = longArrayMatcher(-5L,Long.MIN_VALUE,Long.MAX_VALUE);
        Assertions.assertTrue(matcher.match(new NBTTagLongArray(new long[]{Long.MIN_VALUE,Long.MAX_VALUE,-5L})));
        Assertions.assertTrue(matcher.match(new NBTTagLongArray(new long[]{-5L,-5L,Long.MIN_VALUE,Long.MAX_VALUE,1919810L})));
        Assertions.assertFalse(matcher.match(new NBTTagLongArray(new long[]{Long.MIN_VALUE,Long.MAX_VALUE})));
        Assertions.assertFalse(matcher.match(new NBTTagLongArray(new long[]{5L,Long.MIN_VALUE,Long.MAX_VALUE}))); //-5 换成 5

        final @Nonnull NBTArrayMatcher single = longArrayMatcher(114514L);
        Assertions.assertTrue(single.match(new NBTTagLongArray(new long[]{114514L})));
        Assertions.assertFalse(single.match(new NBTTagLongArray(new long[]{114515L})));
    }

    /**
     * Claude Generated
     * 空期望的长整型数组匹配器匹配空 NBTTagLongArray
     */
    @Test
    public void emptyLongArrayMatchTest(){
        final @Nonnull NBTArrayMatcher matcher = longArrayMatcher();
        Assertions.assertTrue(matcher.match(new NBTTagLongArray(new long[0])));
    }

    /**
     * Claude Generated
     * 测试 equals/hashCode 同时比较数组类与期望集；toString 带 B;/I;/L; 前缀
     */
    @Test
    public void equalsHashCodeToStringTest(){
        Assertions.assertEquals(byteArrayMatcher(1,2),byteArrayMatcher(2,1)); //集合语义，与 expect 顺序无关
        Assertions.assertEquals(byteArrayMatcher(1,2).hashCode(),byteArrayMatcher(2,1).hashCode());
        Assertions.assertNotEquals(byteArrayMatcher(1,2),byteArrayMatcher(1,3));
        Assertions.assertNotEquals(byteArrayMatcher(1,2),intArrayMatcher(1,2)); //数组类不同
        Assertions.assertEquals(byteArrayMatcher(1,1,2),byteArrayMatcher(1,2)); //期望去重

        Assertions.assertEquals("[B;1]",byteArrayMatcher(1).toString());
        Assertions.assertEquals("[I;2]",intArrayMatcher(2).toString());
        Assertions.assertEquals("[L;3]",longArrayMatcher(3).toString());
        Assertions.assertEquals("[B;]",byteArrayMatcher().toString());
        镍测试.镍日志.info("array matcher toString: {} {} {}",
                byteArrayMatcher(1),intArrayMatcher(2),longArrayMatcher(3));
    }

    /**
     * Claude Generated
     * 测试 getMatchType 返回构造时指定的数组类
     */
    @Test
    public void matchTypeTest(){
        Assertions.assertEquals(NBTTagByteArray.class,byteArrayMatcher().getMatchType());
        Assertions.assertEquals(NBTTagIntArray.class,intArrayMatcher().getMatchType());
        Assertions.assertEquals(NBTTagLongArray.class,longArrayMatcher().getMatchType());
    }
}
