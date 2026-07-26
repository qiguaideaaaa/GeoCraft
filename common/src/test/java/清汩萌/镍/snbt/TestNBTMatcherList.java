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

import moe.qingu.nickel.nbt.matcher.NBTCompoundMatcher;
import moe.qingu.nickel.nbt.matcher.NBTIntMatcher;
import moe.qingu.nickel.nbt.matcher.NBTListMatcher;
import moe.qingu.nickel.nbt.matcher.NBTStringMatcher;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.annotation.Nonnull;

import 清汩萌.镍.镍测试;

/**
 * NBTListMatcher 列表匹配器的匹配语义（存在量词、顺序无关、集合去重）测试
 * Claude Generated Tests
 * @author Claude
 * @see moe.qingu.nickel.nbt.matcher.NBTListMatcher
 */
public final class TestNBTMatcherList {

    @Nonnull
    private static NBTTagList intList(final @Nonnull int... values){
        final NBTTagList list = new NBTTagList();
        for(final int v:values) list.appendTag(new NBTTagInt(v));
        return list;
    }

    /**
     * Claude Generated
     * 预期行为：空列表匹配器仅匹配空列表（与复合匹配器"空=通配"不对称）
     */
    @Test
    public void emptyMatcherOnlyMatchesEmptyListTest(){
        final @Nonnull NBTListMatcher matcher = new NBTListMatcher();
        Assertions.assertTrue(matcher.match(new NBTTagList()));
        Assertions.assertFalse(matcher.match(intList(1)));
        Assertions.assertFalse(matcher.match(intList(1,2,3)));
    }

    /**
     * Claude Generated
     * 测试顺序无关匹配：期望与目标顺序不同仍匹配
     */
    @Test
    public void orderInsensitiveTest(){
        final @Nonnull NBTListMatcher matcher = new NBTListMatcher();
        matcher.expect(new NBTIntMatcher(1));
        matcher.expect(new NBTIntMatcher(2));
        matcher.expect(new NBTIntMatcher(3));
        Assertions.assertTrue(matcher.match(intList(1,2,3)));
        Assertions.assertTrue(matcher.match(intList(3,2,1)));
        Assertions.assertTrue(matcher.match(intList(2,3,1)));
    }

    /**
     * Claude Generated
     * 测试子集语义：每个期望须命中目标某一元素，目标多出的元素不影响
     */
    @Test
    public void subsetSemanticsTest(){
        final @Nonnull NBTListMatcher matcher = new NBTListMatcher();
        matcher.expect(new NBTIntMatcher(1));
        matcher.expect(new NBTIntMatcher(2));
        Assertions.assertTrue(matcher.match(intList(3,2,1)));
        Assertions.assertTrue(matcher.match(intList(1,2)));
        Assertions.assertFalse(matcher.match(intList(1,3))); //缺 2
        Assertions.assertFalse(matcher.match(intList(3,4,5))); //全缺
    }

    /**
     * Claude Generated
     * 测试期望数量超过列表长度时直接失败
     */
    @Test
    public void moreMatchersThanElementsTest(){
        final @Nonnull NBTListMatcher matcher = new NBTListMatcher();
        matcher.expect(new NBTIntMatcher(1));
        matcher.expect(new NBTIntMatcher(2));
        matcher.expect(new NBTIntMatcher(3));
        Assertions.assertFalse(matcher.match(intList(1,2)));
        Assertions.assertFalse(matcher.match(new NBTTagList()));
    }

    /**
     * Claude Generated
     * 预期行为：重复 expect 相同匹配器被 HashSet 去重，{1,1} 等价于 {1}，可匹配单元素列表 [1]
     */
    @Test
    public void duplicateExpectationsDeduplicatedTest(){
        final @Nonnull NBTListMatcher matcher = new NBTListMatcher();
        matcher.expect(new NBTIntMatcher(1));
        matcher.expect(new NBTIntMatcher(1)); //与上一个 equals，被去重
        Assertions.assertTrue(matcher.match(intList(1)));
        Assertions.assertTrue(matcher.match(intList(1,1)));
        Assertions.assertTrue(matcher.match(intList(1,2)));
        Assertions.assertFalse(matcher.match(intList(2)));
    }

    /**
     * Claude Generated
     * 测试目标列表含重复元素时的匹配：重复元素可满足同一期望
     */
    @Test
    public void duplicateTargetElementsTest(){
        final @Nonnull NBTListMatcher matcher = new NBTListMatcher();
        matcher.expect(new NBTIntMatcher(1));
        matcher.expect(new NBTIntMatcher(2));
        Assertions.assertTrue(matcher.match(intList(1,1,2,2)));
        Assertions.assertFalse(matcher.match(intList(1,1,1))); //缺 2，长度够也不行
    }

    /**
     * Claude Generated
     * 测试复合标签元素列表的匹配：元素匹配器按子集语义命中任一元素
     */
    @Test
    public void compoundElementsTest(){
        final @Nonnull NBTCompoundMatcher elementMatcher = new NBTCompoundMatcher();
        elementMatcher.expect("id",new NBTStringMatcher("geocraft:water"));
        final @Nonnull NBTListMatcher matcher = new NBTListMatcher();
        matcher.expect(elementMatcher);

        final @Nonnull NBTTagCompound e1 = new NBTTagCompound();
        e1.setString("id","geocraft:lava");
        final @Nonnull NBTTagCompound e2 = new NBTTagCompound();
        e2.setString("id","geocraft:water");
        e2.setInteger("amount",8); //多余键不影响
        final @Nonnull NBTTagList target = new NBTTagList();
        target.appendTag(e1);
        target.appendTag(e2);
        Assertions.assertTrue(matcher.match(target));

        final @Nonnull NBTTagList onlyLava = new NBTTagList();
        onlyLava.appendTag(e1.copy());
        Assertions.assertFalse(matcher.match(onlyLava));
    }

    /**
     * Claude Generated
     * 测试嵌套列表（列表的列表）的匹配
     */
    @Test
    public void nestedListTest(){
        final @Nonnull NBTListMatcher innerMatcher = new NBTListMatcher();
        innerMatcher.expect(new NBTIntMatcher(1));
        final @Nonnull NBTListMatcher matcher = new NBTListMatcher();
        matcher.expect(innerMatcher);

        final @Nonnull NBTTagList target = new NBTTagList();
        target.appendTag(intList(2,3));
        target.appendTag(intList(1));
        Assertions.assertTrue(matcher.match(target));

        final @Nonnull NBTTagList noHit = new NBTTagList();
        noHit.appendTag(intList(2,3));
        Assertions.assertFalse(matcher.match(noHit));
    }

    /**
     * Claude Generated
     * 测试类型严格性：列表匹配器不匹配复合标签与标量
     */
    @Test
    public void typeMismatchTest(){
        final @Nonnull NBTListMatcher matcher = new NBTListMatcher();
        Assertions.assertFalse(matcher.match(new NBTTagCompound()));
        Assertions.assertFalse(matcher.match(new NBTTagInt(1)));
    }

    /**
     * Claude Generated
     * 测试 equals/hashCode 按匹配器集合比较，与插入顺序无关；toNBT 元素数等于去重后集合大小
     */
    @Test
    public void equalsHashCodeAndToNBTTest(){
        final @Nonnull NBTListMatcher a = new NBTListMatcher();
        a.expect(new NBTIntMatcher(1));
        a.expect(new NBTIntMatcher(2));
        final @Nonnull NBTListMatcher b = new NBTListMatcher();
        b.expect(new NBTIntMatcher(2));
        b.expect(new NBTIntMatcher(1));
        Assertions.assertEquals(a,b);
        Assertions.assertEquals(a.hashCode(),b.hashCode());
        b.expect(new NBTIntMatcher(3));
        Assertions.assertNotEquals(a,b);

        final @Nonnull NBTListMatcher dup = new NBTListMatcher();
        dup.expect(new NBTIntMatcher(1));
        dup.expect(new NBTIntMatcher(1));
        Assertions.assertEquals(1,dup.toNBT().tagCount()); //去重后只剩一个元素
        镍测试._镍日志_.info("list matcher a={} dup={}",a,dup);
    }
}
