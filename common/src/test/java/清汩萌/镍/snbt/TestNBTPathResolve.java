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

import net.minecraft.command.CommandException;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagLongArray;
import net.minecraft.nbt.NBTTagString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.annotation.Nonnull;
import java.util.List;

import 清汩萌.镍.镍测试;

/**
 * NBTPath 对真实 NBT 树的求值（resolve）测试。
 * 约定：resolve 未命中 / 类型不符一律返回空集合，不抛异常。
 * @author Claude
 * @see moe.qingu.nickel.nbt.path.NBTPath#resolve(NBTTagCompound)
 */
public final class TestNBTPathResolve extends 镍测试 {
    private @Nonnull NBTTagCompound root;

    /**
     * Claude Generated
     * 构造标准测试树：字符串、整数、复合、列表、复合列表与三种数组
     */
    @BeforeEach
    public void setup(){
        root = new NBTTagCompound();
        root.setString("name","GeoCraft");
        root.setInteger("count",42);

        final NBTTagCompound pos = new NBTTagCompound();
        pos.setInteger("x",1);
        pos.setInteger("y",2);
        pos.setInteger("z",3);
        root.setTag("pos",pos);

        final NBTTagList list = new NBTTagList();
        list.appendTag(new NBTTagString("a"));
        list.appendTag(new NBTTagString("b"));
        list.appendTag(new NBTTagString("c"));
        root.setTag("list",list);

        final NBTTagList players = new NBTTagList();
        final NBTTagCompound p1 = new NBTTagCompound();
        p1.setInteger("id",1);
        p1.setString("tag","alpha");
        final NBTTagCompound p2 = new NBTTagCompound();
        p2.setInteger("id",2);
        final NBTTagCompound p3 = new NBTTagCompound();
        p3.setInteger("id",1);
        players.appendTag(p1);
        players.appendTag(p2);
        players.appendTag(p3);
        root.setTag("players",players);

        root.setTag("bytes",new NBTTagByteArray(new byte[]{1,2,3}));
        root.setTag("ints",new NBTTagIntArray(new int[]{10,20,30}));
        root.setTag("longs",new NBTTagLongArray(new long[]{Long.MIN_VALUE,0L,Long.MAX_VALUE}));
    }

    @Nonnull
    private List<NBTBase> resolve(final @Nonnull String path) throws CommandException {
        return NBTPathTestSupport.parse(path).resolve(root);
    }

    /**
     * Claude Generated
     * 简单键命中
     */
    @Test
    public void resolveSimpleKeyTest() throws CommandException {
        final List<NBTBase> res = resolve("name");
        Assertions.assertEquals(1,res.size());
        Assertions.assertEquals(new NBTTagString("GeoCraft"),res.get(0));
    }

    /**
     * Claude Generated
     * 嵌套键命中，且返回原树中同一实例
     */
    @Test
    public void resolveNestedKeyTest() throws CommandException {
        final List<NBTBase> res = resolve("pos.x");
        Assertions.assertEquals(1,res.size());
        Assertions.assertEquals(new NBTTagInt(1),res.get(0));
        Assertions.assertSame(root.getCompoundTag("pos").getTag("x"),res.get(0));
    }

    /**
     * Claude Generated
     * 未命中与类型不符（对字符串取子键）均返回空集，不抛
     */
    @Test
    public void resolveMissTest() throws CommandException {
        Assertions.assertTrue(resolve("nosuch").isEmpty());
        Assertions.assertTrue(resolve("pos.nosuch").isEmpty());
        Assertions.assertTrue(resolve("name.x").isEmpty()); //字符串不是复合，类型不符
        Assertions.assertTrue(resolve("name[0]").isEmpty()); //字符串不可索引
        Assertions.assertTrue(resolve("pos[0]").isEmpty()); //复合不可索引
        Assertions.assertTrue(resolve("name[]").isEmpty()); //字符串上的全部节点
        Assertions.assertTrue(resolve("pos[{x:1}]").isEmpty()); //复合不是列表
    }

    /**
     * Claude Generated
     * 列表索引：正索引、负索引（从尾数）与双向越界
     */
    @Test
    public void resolveIndexTest() throws CommandException {
        Assertions.assertEquals(new NBTTagString("a"),resolve("list[0]").get(0));
        Assertions.assertEquals(new NBTTagString("c"),resolve("list[2]").get(0));
        Assertions.assertEquals(new NBTTagString("c"),resolve("list[-1]").get(0));
        Assertions.assertEquals(new NBTTagString("a"),resolve("list[-3]").get(0));
        Assertions.assertTrue(resolve("list[3]").isEmpty());
        Assertions.assertTrue(resolve("list[-4]").isEmpty());
    }

    /**
     * Claude Generated
     * 三种数组的索引求值（元素装箱为新标签），含 LongArray 的极值
     */
    @Test
    public void resolveArrayIndexTest() throws CommandException {
        Assertions.assertEquals(2,((net.minecraft.nbt.NBTTagByte) resolve("bytes[1]").get(0)).getByte());
        Assertions.assertEquals(new NBTTagInt(30),resolve("ints[-1]").get(0));
        Assertions.assertEquals(new NBTTagLong(Long.MIN_VALUE),resolve("longs[0]").get(0));
        Assertions.assertEquals(new NBTTagLong(Long.MAX_VALUE),resolve("longs[2]").get(0));
        Assertions.assertTrue(resolve("bytes[3]").isEmpty());
        Assertions.assertTrue(resolve("longs[-4]").isEmpty());
    }

    /**
     * Claude Generated
     * [] 全部节点的多分支扇出：列表返回原实例，数组返回装箱副本
     */
    @Test
    public void resolveAllTest() throws CommandException {
        final List<NBTBase> listAll = resolve("list[]");
        Assertions.assertEquals(3,listAll.size());
        Assertions.assertSame(root.getTagList("list",8).get(0),listAll.get(0)); //列表元素是原实例
        Assertions.assertEquals(3,resolve("bytes[]").size());
        Assertions.assertEquals(3,resolve("ints[]").size());
        Assertions.assertEquals(3,resolve("longs[]").size());
        Assertions.assertEquals(new NBTTagInt(10),resolve("ints[]").get(0)); //数组元素是装箱副本，改它不回写
    }

    /**
     * Claude Generated
     * 多分支扇出后续接键：players[].id 得到全部 id
     */
    @Test
    public void resolveFanOutTest() throws CommandException {
        final List<NBTBase> ids = resolve("players[].id");
        Assertions.assertEquals(3,ids.size());
        Assertions.assertEquals(new NBTTagInt(1),ids.get(0));
        Assertions.assertEquals(new NBTTagInt(2),ids.get(1));
        Assertions.assertEquals(new NBTTagInt(1),ids.get(2));
    }

    /**
     * Claude Generated
     * [{...}] 列表复合过滤：按值筛选复合元素
     */
    @Test
    public void resolveListCompoundTest() throws CommandException {
        Assertions.assertEquals(2,resolve("players[{id:1}]").size());
        Assertions.assertEquals(1,resolve("players[{id:2}]").size());
        Assertions.assertTrue(resolve("players[{id:3}]").isEmpty());
        Assertions.assertEquals(1,resolve("players[{id:1,tag:\"alpha\"}]").size());
    }

    /**
     * Claude Generated
     * 键值过滤：a{...} 命中与不命中
     */
    @Test
    public void resolveTagValueFilterTest() throws CommandException {
        Assertions.assertEquals(1,resolve("pos{x:1}").size());
        Assertions.assertTrue(resolve("pos{x:9}").isEmpty());
        Assertions.assertEquals(1,resolve("pos{x:1,y:2}.z").size());
    }

    /**
     * Claude Generated
     * 根复合过滤 {..}：匹配返回根自身，否则空集
     */
    @Test
    public void resolveRootCompoundTest() throws CommandException {
        final List<NBTBase> hit = resolve("{count:42}");
        Assertions.assertEquals(1,hit.size());
        Assertions.assertSame(root,hit.get(0));
        Assertions.assertTrue(resolve("{count:0}").isEmpty());
        Assertions.assertEquals(new NBTTagString("GeoCraft"),resolve("{count:42}.name").get(0));
    }

    /**
     * Claude Generated
     * 空路径求值返回根自身
     */
    @Test
    public void resolveEmptyPathTest(){
        final List<NBTBase> res = new moe.qingu.nickel.nbt.path.NBTPath().resolve(root);
        Assertions.assertEquals(1,res.size());
        Assertions.assertSame(root,res.get(0));
    }

    /**
     * Claude Generated
     * 深嵌套：12 层复合逐层下钻命中
     */
    @Test
    public void resolveDeepNestingTest() throws CommandException {
        final NBTTagCompound deepRoot = new NBTTagCompound();
        NBTTagCompound cur = deepRoot;
        final StringBuilder pathStr = new StringBuilder();
        for(int i=1;i<=12;i++){
            final NBTTagCompound next = new NBTTagCompound();
            cur.setTag("l"+i,next);
            cur = next;
            if(i>1) pathStr.append('.');
            pathStr.append('l').append(i);
        }
        cur.setInteger("value",114514);
        pathStr.append(".value");
        final List<NBTBase> res = NBTPathTestSupport.parse(pathStr.toString()).resolve(deepRoot);
        镍测试._镍日志_.info("深嵌套路径=<{}> 命中数={}",pathStr,res.size());
        Assertions.assertEquals(1,res.size());
        Assertions.assertEquals(new NBTTagInt(114514),res.get(0));
        Assertions.assertTrue(NBTPathTestSupport.parse(pathStr+".more").resolve(deepRoot).isEmpty());
    }

    /**
     * Claude Generated
     * 长列表：1000 元素的索引、负索引、越界与全量扇出
     */
    @Test
    public void resolveLongListTest() throws CommandException {
        final NBTTagCompound bigRoot = new NBTTagCompound();
        final NBTTagList list = new NBTTagList();
        for(int i=0;i<1000;i++) list.appendTag(new NBTTagInt(i));
        bigRoot.setTag("big",list);
        Assertions.assertEquals(new NBTTagInt(999),NBTPathTestSupport.parse("big[999]").resolve(bigRoot).get(0));
        Assertions.assertEquals(new NBTTagInt(0),NBTPathTestSupport.parse("big[-1000]").resolve(bigRoot).get(0));
        Assertions.assertEquals(new NBTTagInt(500),NBTPathTestSupport.parse("big[500]").resolve(bigRoot).get(0));
        Assertions.assertTrue(NBTPathTestSupport.parse("big[1000]").resolve(bigRoot).isEmpty());
        Assertions.assertEquals(1000,NBTPathTestSupport.parse("big[]").resolve(bigRoot).size());
    }

    /**
     * Claude Generated
     * resolveParents：返回末节点之前各分支的父标签
     */
    @Test
    public void resolveParentsTest() throws CommandException {
        final List<NBTBase> parents = NBTPathTestSupport.parse("pos.x").resolveParents(root);
        Assertions.assertEquals(1,parents.size());
        Assertions.assertSame(root.getTag("pos"),parents.get(0));
        Assertions.assertEquals(3,NBTPathTestSupport.parse("players[].id").resolveParents(root).size()); //players[] 扇出 3 个父分支
        final List<NBTBase> single = NBTPathTestSupport.parse("name").resolveParents(root);
        Assertions.assertEquals(1,single.size());
        Assertions.assertSame(root,single.get(0)); //单节点路径的父就是根
    }
}
