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
import moe.qingu.nickel.nbt.matcher.NBTCompoundMatcher;
import moe.qingu.nickel.nbt.matcher.NBTStringMatcher;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.annotation.Nonnull;

import 清汩萌.镍.镍测试;

/**
 * NBTCompoundMatcher 复合标签匹配器的匹配语义（子集语义）测试
 * Claude Generated Tests
 * @author Claude
 * @see moe.qingu.nickel.nbt.matcher.NBTCompoundMatcher
 */
public final class TestNBTMatcherCompound {

    /**
     * Claude Generated
     * 测试嵌套复合标签的递归匹配：内层子集通过、内层值错失败
     */
    @Test
    public void nestedCompoundTest(){
        final @Nonnull NBTCompoundMatcher inner = new NBTCompoundMatcher();
        inner.expectByte("flag",(byte)1);
        final @Nonnull NBTCompoundMatcher matcher = new NBTCompoundMatcher();
        matcher.expect("data",inner);

        final @Nonnull NBTTagCompound innerTarget = new NBTTagCompound();
        innerTarget.setByte("flag",(byte)1);
        innerTarget.setString("extra","内层多余键"); //内层同样是子集语义
        final @Nonnull NBTTagCompound target = new NBTTagCompound();
        target.setTag("data",innerTarget);
        Assertions.assertTrue(matcher.match(target));

        innerTarget.setByte("flag",(byte)0); //内层值错
        Assertions.assertFalse(matcher.match(target));

        final @Nonnull NBTTagCompound notCompound = new NBTTagCompound();
        notCompound.setInteger("data",1); //内层类型错
        Assertions.assertFalse(matcher.match(notCompound));
    }

    /**
     * Claude Generated
     * 测试类型严格性：复合匹配器不匹配列表、标量等其他类型
     */
    @Test
    public void typeMismatchTest(){
        final @Nonnull NBTCompoundMatcher matcher = new NBTCompoundMatcher();
        Assertions.assertFalse(matcher.match(new NBTTagList()));
        Assertions.assertFalse(matcher.match(new NBTTagInt(1)));
        Assertions.assertFalse(matcher.match(new NBTTagString("{}")));
    }

    /**
     * Claude Generated
     * 测试中文键与中文值的匹配
     */
    @Test
    public void chineseKeyValueTest(){
        final @Nonnull NBTCompoundMatcher matcher = new NBTCompoundMatcher();
        matcher.expect("天圆",new NBTStringMatcher("地方"));
        final @Nonnull NBTTagCompound target = new NBTTagCompound();
        target.setString("天圆","地方");
        Assertions.assertTrue(matcher.match(target));
        target.setString("天圆","方地");
        Assertions.assertFalse(matcher.match(target));
        final @Nonnull NBTTagCompound wrongKey = new NBTTagCompound();
        wrongKey.setString("天方","地方");
        Assertions.assertFalse(matcher.match(wrongKey));
    }

    /**
     * Claude Generated
     * 测试 equals/hashCode/getKeysToMatch/getSize 与 toString 的键转义
     */
    @Test
    public void equalsHashCodeToStringTest(){
        final @Nonnull NBTCompoundMatcher a = new NBTCompoundMatcher();
        a.expectByte("k",(byte)1);
        final @Nonnull NBTCompoundMatcher b = new NBTCompoundMatcher();
        b.expect("k",new NBTByteMatcher((byte)1));
        Assertions.assertEquals(a,b);
        Assertions.assertEquals(a.hashCode(),b.hashCode());
        b.expectByte("k2",(byte)2);
        Assertions.assertNotEquals(a,b);

        Assertions.assertEquals(1,a.getSize());
        Assertions.assertTrue(a.getKeysToMatch().contains("k"));
        Assertions.assertEquals("{k:1b}",a.toString());
        Assertions.assertEquals("{}",new NBTCompoundMatcher().toString());

        //含空格的键在 toString 中被引号转义
        final @Nonnull NBTCompoundMatcher quoted = new NBTCompoundMatcher();
        quoted.expectByte("a b",(byte)1);
        Assertions.assertEquals("{\"a b\":1b}",quoted.toString());
        镍测试._镍日志_.info("compound matcher toString: {} / {}",a,quoted);
    }
}
