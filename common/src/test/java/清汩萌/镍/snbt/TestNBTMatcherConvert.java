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
import moe.qingu.nickel.nbt.matcher.NBTByteMatcher;
import moe.qingu.nickel.nbt.matcher.NBTCompoundMatcher;
import moe.qingu.nickel.nbt.matcher.NBTDoubleMatcher;
import moe.qingu.nickel.nbt.matcher.NBTFloatMatcher;
import moe.qingu.nickel.nbt.matcher.NBTIntMatcher;
import moe.qingu.nickel.nbt.matcher.NBTListMatcher;
import moe.qingu.nickel.nbt.matcher.NBTLongMatcher;
import moe.qingu.nickel.nbt.matcher.NBTMatcher;
import moe.qingu.nickel.nbt.matcher.NBTShortMatcher;
import moe.qingu.nickel.nbt.matcher.NBTStringMatcher;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagEnd;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagLongArray;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.nbt.NBTTagString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import javax.annotation.Nonnull;
import java.util.stream.Stream;

import 清汩萌.镍.镍测试;

/**
 * NBTMatcher.toMatcher 转换与 toNBT 往返的语义测试
 * Claude Generated Tests
 * @author Claude
 * @see moe.qingu.nickel.nbt.matcher.NBTMatcher#toMatcher(NBTBase)
 */
public final class TestNBTMatcherConvert {

    /**
     * Claude Generated
     * 数据源：各类型 NBT 样本，转换出的 matcher 应能匹配原 NBT
     */
    @SuppressWarnings("unused")
    public static @Nonnull Stream<NBTBase> pullDataForRoundTrip(){
        final NBTTagCompound compound = new NBTTagCompound();
        compound.setByte("b",(byte)1);
        compound.setString("s","天圆地方");

        final NBTTagList list = new NBTTagList();
        list.appendTag(new NBTTagInt(1));
        list.appendTag(new NBTTagInt(2));

        final NBTTagList dupList = new NBTTagList();
        dupList.appendTag(new NBTTagInt(1));
        dupList.appendTag(new NBTTagInt(1));
        dupList.appendTag(new NBTTagInt(2));

        return Stream.of(
                new NBTTagByte((byte)7),
                new NBTTagShort((short)-3),
                new NBTTagInt(114514),
                new NBTTagLong(Long.MIN_VALUE),
                new NBTTagFloat(1.5f),
                new NBTTagDouble(-2.5d),
                new NBTTagString("hello 世界"),
                new NBTTagString(""),
                compound,
                new NBTTagCompound(),
                list,
                dupList,
                new NBTTagList(),
                new NBTTagByteArray(new byte[]{1,1,2}),
                new NBTTagIntArray(new int[]{-1,0,Integer.MAX_VALUE}),
                new NBTTagLongArray(new long[]{Long.MIN_VALUE,-5L,Long.MAX_VALUE})
        );
    }

    /**
     * Claude Generated
     * 测试往返匹配：任意 NBT 经 toMatcher 转换后必能匹配其原件
     */
    @ParameterizedTest
    @MethodSource("pullDataForRoundTrip")
    public void roundTripMatchTest(final @Nonnull NBTBase nbt){
        final @Nonnull NBTMatcher<?> matcher = NBTMatcher.toMatcher(nbt);
        镍测试._镍日志_.info("roundTrip nbt={} matcher={}",nbt,matcher);
        Assertions.assertTrue(matcher.match(nbt),"matcher "+matcher+" should match its source "+nbt);
    }

    /**
     * Claude Generated
     * 测试 toMatcher 对各标量类型分派到正确的匹配器类
     */
    @Test
    public void dispatchTest(){
        Assertions.assertEquals(NBTByteMatcher.class,NBTMatcher.toMatcher(new NBTTagByte((byte)1)).getClass());
        Assertions.assertEquals(NBTShortMatcher.class,NBTMatcher.toMatcher(new NBTTagShort((short)1)).getClass());
        Assertions.assertEquals(NBTIntMatcher.class,NBTMatcher.toMatcher(new NBTTagInt(1)).getClass());
        Assertions.assertEquals(NBTLongMatcher.class,NBTMatcher.toMatcher(new NBTTagLong(1L)).getClass());
        Assertions.assertEquals(NBTFloatMatcher.class,NBTMatcher.toMatcher(new NBTTagFloat(1f)).getClass());
        Assertions.assertEquals(NBTDoubleMatcher.class,NBTMatcher.toMatcher(new NBTTagDouble(1d)).getClass());
        Assertions.assertEquals(NBTStringMatcher.class,NBTMatcher.toMatcher(new NBTTagString("1")).getClass());
        Assertions.assertEquals(NBTCompoundMatcher.class,NBTMatcher.toMatcher((NBTBase)new NBTTagCompound()).getClass());
        Assertions.assertEquals(NBTListMatcher.class,NBTMatcher.toMatcher(new NBTTagList()).getClass());
        Assertions.assertEquals(NBTArrayMatcher.class,NBTMatcher.toMatcher(new NBTTagByteArray(new byte[]{1})).getClass());
        Assertions.assertEquals(NBTArrayMatcher.class,NBTMatcher.toMatcher(new NBTTagIntArray(new int[]{1})).getClass());
        Assertions.assertEquals(NBTArrayMatcher.class,NBTMatcher.toMatcher(new NBTTagLongArray(new long[]{1L})).getClass());
    }

    /**
     * Claude Generated
     * 预期行为：toMatcher(NBTTagEnd) 抛 IllegalArgumentException
     */
    @Test
    public void endTagThrowsTest(){
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> NBTMatcher.toMatcher(new NBTTagEnd())
        );
    }

    /**
     * Claude Generated
     * 测试标量与复合标签的 toNBT 往返无损（NBT equals 相等）
     */
    @Test
    public void toNBTLosslessForScalarAndCompoundTest(){
        final @Nonnull NBTTagCompound compound = new NBTTagCompound();
        compound.setByte("b",(byte)1);
        compound.setShort("s",(short)2);
        compound.setInteger("i",3);
        compound.setLong("l",4L);
        compound.setFloat("f",1.5f);
        compound.setDouble("d",2.5d);
        compound.setString("str","天圆地方");
        final @Nonnull NBTTagCompound inner = new NBTTagCompound();
        inner.setInteger("x",9);
        compound.setTag("inner",inner);

        final @Nonnull NBTTagCompound back = NBTMatcher.toMatcher(compound).toNBT();
        Assertions.assertEquals(compound,back);
    }

    /**
     * Claude Generated
     * 预期行为：列表/数组经 toMatcher 集合化，toNBT 往返丢失重复与顺序
     */
    @Test
    public void toNBTLossyForListAndArrayTest(){
        final @Nonnull NBTTagList dupList = new NBTTagList();
        dupList.appendTag(new NBTTagInt(1));
        dupList.appendTag(new NBTTagInt(1));
        dupList.appendTag(new NBTTagInt(2));
        final @Nonnull NBTListMatcher listMatcher = NBTMatcher.toMatcher(dupList,false);
        Assertions.assertEquals(2,listMatcher.toNBT().tagCount()); //[1,1,2] 去重后只剩 2 个元素
        Assertions.assertTrue(listMatcher.match(dupList)); //但仍匹配原件

        final @Nonnull NBTTagByteArray dupArray = new NBTTagByteArray(new byte[]{1,1,2});
        final @Nonnull NBTMatcher<?> arrayMatcher = NBTMatcher.toMatcher(dupArray);
        final @Nonnull NBTBase backArray = arrayMatcher.toNBT();
        Assertions.assertEquals(2,((NBTTagByteArray)backArray).getByteArray().length);
        Assertions.assertTrue(arrayMatcher.match(dupArray));
    }

    /**
     * Claude Generated
     * 测试深层嵌套（复合-列表-复合三层）的递归转换与匹配
     */
    @Test
    public void deepNestedConversionTest(){
        final @Nonnull NBTTagCompound leaf = new NBTTagCompound();
        leaf.setString("name","水");
        leaf.setInteger("level",8);
        final @Nonnull NBTTagList middle = new NBTTagList();
        middle.appendTag(leaf);
        final @Nonnull NBTTagCompound root = new NBTTagCompound();
        root.setTag("fluids",middle);
        root.setByte("version",(byte)1);

        final @Nonnull NBTCompoundMatcher matcher = NBTMatcher.toMatcher(root);
        Assertions.assertTrue(matcher.match(root));

        //目标多出键仍匹配（子集语义贯穿各层）
        final @Nonnull NBTTagCompound richerLeaf = leaf.copy();
        richerLeaf.setBoolean("static",true);
        final @Nonnull NBTTagList richerMiddle = new NBTTagList();
        richerMiddle.appendTag(richerLeaf);
        final @Nonnull NBTTagCompound richerRoot = new NBTTagCompound();
        richerRoot.setTag("fluids",richerMiddle);
        richerRoot.setByte("version",(byte)1);
        richerRoot.setString("extra","多余键");
        Assertions.assertTrue(matcher.match(richerRoot));

        //深层值被改动则失败
        final @Nonnull NBTTagCompound brokenLeaf = leaf.copy();
        brokenLeaf.setInteger("level",7);
        final @Nonnull NBTTagList brokenMiddle = new NBTTagList();
        brokenMiddle.appendTag(brokenLeaf);
        final @Nonnull NBTTagCompound brokenRoot = new NBTTagCompound();
        brokenRoot.setTag("fluids",brokenMiddle);
        brokenRoot.setByte("version",(byte)1);
        Assertions.assertFalse(matcher.match(brokenRoot));
    }

    /**
     * Claude Generated
     * 测试转换保持值语义：转换出的标量匹配器对不同值/不同类型不匹配
     */
    @Test
    public void convertedMatcherNegativeTest(){
        final @Nonnull NBTMatcher<?> matcher = NBTMatcher.toMatcher(new NBTTagInt(1));
        Assertions.assertFalse(matcher.match(new NBTTagInt(2)));
        Assertions.assertFalse(matcher.match(new NBTTagLong(1L)));
        Assertions.assertFalse(matcher.match(new NBTTagString("1")));
    }
}
