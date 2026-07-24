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

import moe.qingu.nickel.I18nKeys;
import moe.qingu.nickel.command.exception.NickelRuntimeException;
import moe.qingu.nickel.nbt.path.NBTPath;
import net.minecraft.command.CommandException;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
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
 * NBTPath 修改类操作（set / insert / remove / init）测试。
 * 约定：修改类操作在类型不符 / 越界时抛 NickelRuntimeException；resolveParents 无命中抛 *_NOT_FOUND；
 * 多命中且 allowMulti=false 抛 *_FOUND_MULTI；allowMulti=true 时单点失败被吞、计数继续。
 * @author Claude
 * @see moe.qingu.nickel.nbt.path.NBTPath
 */
public final class TestNBTPathModify extends 镍测试 {
    private @Nonnull NBTTagCompound root;

    /**
     * Claude Generated
     * 构造测试树（同 TestNBTPathResolve 的结构，另加空列表）
     */
    @BeforeEach
    public void setup(){
        root = new NBTTagCompound();
        root.setString("name","GeoCraft");
        root.setInteger("count",42);

        final NBTTagCompound pos = new NBTTagCompound();
        pos.setInteger("x",1);
        pos.setInteger("y",2);
        root.setTag("pos",pos);

        final NBTTagList list = new NBTTagList();
        list.appendTag(new NBTTagString("a"));
        list.appendTag(new NBTTagString("b"));
        list.appendTag(new NBTTagString("c"));
        root.setTag("list",list);

        final NBTTagList players = new NBTTagList();
        final NBTTagCompound p1 = new NBTTagCompound();
        p1.setInteger("id",1);
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
        root.setTag("empty",new NBTTagList());
    }

    @Nonnull
    private NBTPath path(final @Nonnull String raw) throws CommandException {
        return NBTPathTestSupport.parse(raw);
    }

    private void assertThrowsKey(final @Nonnull String key,final @Nonnull org.junit.jupiter.api.function.Executable executable){
        final NickelRuntimeException e = Assertions.assertThrows(NickelRuntimeException.class,executable);
        NBTPathTestSupport.assertInfoHasKey(e,key);
    }

    // ======================== set ========================

    /**
     * Claude Generated
     * set 新建键与覆盖已有键
     */
    @Test
    public void setNewAndOverwriteTest() throws CommandException {
        Assertions.assertEquals(1,path("newkey").set(root,new NBTTagInt(7),false));
        Assertions.assertEquals(new NBTTagInt(7),root.getTag("newkey"));
        Assertions.assertEquals(1,path("name").set(root,new NBTTagString("changed"),false));
        Assertions.assertEquals("changed",root.getString("name"));
        Assertions.assertEquals(1,path("pos.x").set(root,new NBTTagInt(9),false));
        Assertions.assertEquals(9,root.getCompoundTag("pos").getInteger("x"));
    }

    /**
     * Claude Generated
     * set 写入的是 replacement 的拷贝，事后改原标签不影响树
     */
    @Test
    public void setCopySemanticsTest() throws CommandException {
        final NBTTagCompound replacement = new NBTTagCompound();
        replacement.setInteger("v",1);
        path("holder").set(root,replacement,false);
        replacement.setInteger("v",2);
        Assertions.assertEquals(1,root.getCompoundTag("holder").getInteger("v"));
    }

    /**
     * Claude Generated
     * 多命中：allowMulti=false 抛 SET_FOUND_MULTI，true 时全部写入并计数
     */
    @Test
    public void setMultiTest() throws CommandException {
        assertThrowsKey(I18nKeys.NBTPath.SET_FOUND_MULTI,
                () -> path("players[{id:1}].seen").set(root,new NBTTagByte((byte) 1),false));
        Assertions.assertEquals(2,path("players[{id:1}].seen").set(root,new NBTTagByte((byte) 1),true));
    }

    /**
     * Claude Generated
     * set 的空路径、父路径无命中与末节点不可修改（方法节点）三类失败
     */
    @Test
    public void setFailuresTest() throws CommandException {
        assertThrowsKey(I18nKeys.NBTPath.SET_EMPTY,
                () -> new NBTPath().set(root,new NBTTagInt(1),false));
        assertThrowsKey(I18nKeys.NBTPath.SET_NOT_FOUND,
                () -> path("nosuch.key").set(root,new NBTTagInt(1),false));
        assertThrowsKey(I18nKeys.NBTPath.SET_UNSUPPORTED,
                () -> path("pos.values()").set(root,new NBTTagInt(1),false));
    }

    /**
     * Claude Generated
     * 键值过滤不匹配时 set 抛 SET_TAG_MISVALUE；对非复合父 set 抛 SET_TAG_MISMATCH
     */
    @Test
    public void setTagFilterTest() throws CommandException {
        assertThrowsKey(I18nKeys.NBTPath.SET_TAG_MISVALUE,
                () -> path("pos{x:9}").set(root,new NBTTagCompound(),false));
        Assertions.assertEquals(1,path("pos{x:1}").set(root,new NBTTagCompound(),false)); //过滤匹配则替换
        assertThrowsKey(I18nKeys.NBTPath.SET_TAG_MISMATCH,
                () -> path("name.sub").set(root,new NBTTagInt(1),false)); //父是字符串
    }

    /**
     * Claude Generated
     * 索引 set：列表命中 / 越界 / 类型不符，以及对复合的下标 set 抛 SET_INDEX_MISMATCH
     */
    @Test
    public void setIndexListTest() throws CommandException {
        Assertions.assertEquals(1,path("list[0]").set(root,new NBTTagString("z"),false));
        Assertions.assertEquals("z",root.getTagList("list",8).getStringTagAt(0));
        Assertions.assertEquals(1,path("list[-1]").set(root,new NBTTagString("w"),false));
        Assertions.assertEquals("w",root.getTagList("list",8).getStringTagAt(2));
        assertThrowsKey(I18nKeys.NBTPath.SET_INDEX_LIST_OUT,
                () -> path("list[5]").set(root,new NBTTagString("x"),false));
        assertThrowsKey(I18nKeys.NBTPath.SET_INDEX_NO_TYPE,
                () -> path("list[0]").set(root,new NBTTagInt(1),false));
        assertThrowsKey(I18nKeys.NBTPath.SET_INDEX_MISMATCH,
                () -> path("pos[0]").set(root,new NBTTagInt(1),false));
    }

    /**
     * Claude Generated
     * 数组 set：字节数组原地改、类型限制；整数数组接受不大于 INT 的数值；LongArray 走反射原地改
     */
    @Test
    public void setIndexArrayTest() throws CommandException {
        Assertions.assertEquals(1,path("bytes[0]").set(root,new NBTTagByte((byte) 9),false));
        Assertions.assertEquals(9,((NBTTagByteArray) root.getTag("bytes")).getByteArray()[0]);
        assertThrowsKey(I18nKeys.NBTPath.SET_INDEX_NO_BYTE,
                () -> path("bytes[0]").set(root,new NBTTagInt(1),false));
        assertThrowsKey(I18nKeys.NBTPath.SET_INDEX_ARR_OUT,
                () -> path("bytes[3]").set(root,new NBTTagByte((byte) 1),false));

        Assertions.assertEquals(1,path("ints[1]").set(root,new NBTTagByte((byte) 5),false)); //id<=TAG_INT 的数值可写入
        Assertions.assertEquals(5,((NBTTagIntArray) root.getTag("ints")).getIntArray()[1]);
        assertThrowsKey(I18nKeys.NBTPath.SET_INDEX_NO_INT,
                () -> path("ints[0]").set(root,new NBTTagLong(1L),false));

        Assertions.assertEquals(1,path("longs[1]").set(root,new NBTTagLong(1919810L),false)); //反射拿内部数组原地改
        final List<NBTBase> after = path("longs[1]").resolve(root);
        Assertions.assertEquals(new NBTTagLong(1919810L),after.get(0));
    }

    /**
     * Claude Generated
     * 列表复合 set：替换匹配项并计数、非复合替换值与字符串列表的类型失败
     */
    @Test
    public void setListCompoundTest() throws CommandException {
        final NBTTagCompound replacement = new NBTTagCompound();
        replacement.setInteger("id",99);
        Assertions.assertEquals(2,path("players[{id:1}]").set(root,replacement,true));
        Assertions.assertEquals(99,root.getTagList("players",10).getCompoundTagAt(0).getInteger("id"));
        assertThrowsKey(I18nKeys.NBTPath.SET_LIST_COM_NO_COMPOUND,
                () -> path("players[{id:2}]").set(root,new NBTTagInt(1),false));
        assertThrowsKey(I18nKeys.NBTPath.SET_LIST_COM_NO_LIST_COM,
                () -> path("list[{x:1}]").set(root,new NBTTagCompound(),false));
        assertThrowsKey(I18nKeys.NBTPath.SET_LIST_COM_MISMATCH,
                () -> path("pos[{x:1}]").set(root,new NBTTagCompound(),false));
    }

    /**
     * Claude Generated
     * [] 对空列表 set 返回 0：列表为空，没有元素可替换
     */
    @Test
    public void setAllEmptyListTest() throws CommandException {
        Assertions.assertEquals(0,path("empty[]").set(root,new NBTTagInt(1),false));
    }

    /**
     * Claude Generated
     * [] 对非空列表 set：清空后填入 N 份拷贝并返回 N
     */
    @Test
    public void setAllNonEmptyListTest() throws CommandException {
        Assertions.assertEquals(3,path("list[]").set(root,new NBTTagString("k"),false));
        final NBTTagList list = root.getTagList("list",8);
        Assertions.assertEquals(3,list.tagCount());
        for(int i=0;i<3;i++) Assertions.assertEquals("k",list.getStringTagAt(i));
    }

    /**
     * Claude Generated
     * [] 对数组 set：Arrays.fill 原地填充并返回长度
     */
    @Test
    public void setAllArrayTest() throws CommandException {
        Assertions.assertEquals(3,path("bytes[]").set(root,new NBTTagByte((byte) 7),false));
        for(final byte b:((NBTTagByteArray) root.getTag("bytes")).getByteArray()) Assertions.assertEquals(7,b);
        Assertions.assertEquals(3,path("longs[]").set(root,new NBTTagInt(3),false)); //id<=TAG_LONG 的数值可写入
        Assertions.assertEquals(new NBTTagLong(3L),path("longs[0]").resolve(root).get(0));
        assertThrowsKey(I18nKeys.NBTPath.SET_ALL_NO_BYTE,
                () -> path("bytes[]").set(root,new NBTTagInt(1),false));
        assertThrowsKey(I18nKeys.NBTPath.SET_ALL_MISMATCH,
                () -> path("pos[]").set(root,new NBTTagInt(1),false));
    }

    // ======================== insert ========================

    /**
     * Claude Generated
     * insert 头插 / 中插 / 尾插（i==tagCount）后的顺序正确性
     */
    @Test
    public void insertOrderTest() throws CommandException {
        Assertions.assertEquals(1,path("list[0]").insert(root,new NBTTagString("头"),false));
        Assertions.assertEquals(1,path("list[2]").insert(root,new NBTTagString("中"),false));
        Assertions.assertEquals(1,path("list[5]").insert(root,new NBTTagString("尾"),false)); //i==tagCount 尾插
        final NBTTagList list = root.getTagList("list",8);
        final String[] expected = {"头","a","中","b","c","尾"};
        Assertions.assertEquals(expected.length,list.tagCount());
        for(int i=0;i<expected.length;i++)
            Assertions.assertEquals(expected[i],list.getStringTagAt(i),"下标 "+i);
    }

    /**
     * Claude Generated
     * 负索引 insert：i = tagCount + index
     */
    @Test
    public void insertNegativeIndexTest() throws CommandException {
        Assertions.assertEquals(1,path("list[-1]").insert(root,new NBTTagString("z"),false));
        final NBTTagList list = root.getTagList("list",8);
        Assertions.assertEquals("z",list.getStringTagAt(2)); //-1 → 3-1=2，插在 c 之前
        Assertions.assertEquals("c",list.getStringTagAt(3));
    }

    /**
     * Claude Generated
     * 空列表 insert 与各类失败：越界、类型不符、非列表、末节点非索引、空路径、未命中
     */
    @Test
    public void insertFailuresTest() throws CommandException {
        Assertions.assertEquals(1,path("empty[0]").insert(root,new NBTTagInt(1),false)); //空列表下标 0 == tagCount，可插
        assertThrowsKey(I18nKeys.NBTPath.INSERT_INDEX_OUT,
                () -> path("list[4]").insert(root,new NBTTagString("x"),false));
        assertThrowsKey(I18nKeys.NBTPath.INSERT_INDEX_OUT,
                () -> path("list[-5]").insert(root,new NBTTagString("x"),false));
        assertThrowsKey(I18nKeys.NBTPath.INSERT_INDEX_NO_TYPE,
                () -> path("list[0]").insert(root,new NBTTagInt(1),false));
        assertThrowsKey(I18nKeys.NBTPath.INSERT_INDEX_MISMATCH,
                () -> path("bytes[0]").insert(root,new NBTTagByte((byte) 1),false)); //数组不支持 insert
        assertThrowsKey(I18nKeys.NBTPath.INSERT_UNSUPPORTED,
                () -> path("name").insert(root,new NBTTagInt(1),false)); //末节点是标签节点
        assertThrowsKey(I18nKeys.NBTPath.INSERT_EMPTY,
                () -> new NBTPath().insert(root,new NBTTagInt(1),false));
        assertThrowsKey(I18nKeys.NBTPath.INSERT_NOT_FOUND,
                () -> path("nosuch[0]").insert(root,new NBTTagInt(1),false));
    }

    /**
     * Claude Generated
     * 多父分支 insert：allowMulti=false 抛 INSERT_FOUND_MULTI；true 时单点失败被吞、返回成功计数
     */
    @Test
    public void insertMultiTest() throws CommandException {
        assertThrowsKey(I18nKeys.NBTPath.INSERT_FOUND_MULTI,
                () -> path("players[][0]").insert(root,new NBTTagInt(1),false));
        //players[] 扇出 3 个复合，对复合 insert 均失败但被吞，计数 0
        Assertions.assertEquals(0,path("players[][0]").insert(root,new NBTTagInt(1),true));
    }

    // ======================== remove ========================

    /**
     * Claude Generated
     * remove 命中删除、键不存在返回 0 不抛
     */
    @Test
    public void removeTagTest() throws CommandException {
        Assertions.assertEquals(1,path("name").remove(root,false));
        Assertions.assertFalse(root.hasKey("name"));
        Assertions.assertEquals(0,path("pos.nosuch").remove(root,false)); //父命中、键不存在 → 0
        assertThrowsKey(I18nKeys.NBTPath.REMOVE_NOT_FOUND,
                () -> path("nosuch.b").remove(root,false)); //父路径无命中
        assertThrowsKey(I18nKeys.NBTPath.REMOVE_TAG_MISVALUE,
                () -> path("pos{x:9}").remove(root,false));
        assertThrowsKey(I18nKeys.NBTPath.REMOVE_TAG_MISMATCH,
                () -> path("count.sub").remove(root,false)); //父是整数标签，非复合
        assertThrowsKey(I18nKeys.NBTPath.REMOVE_EMPTY,
                () -> new NBTPath().remove(root,false));
        assertThrowsKey(I18nKeys.NBTPath.REMOVE_UNSUPPORTED,
                () -> path("pos.values()").remove(root,false));
    }

    /**
     * Claude Generated
     * 索引 remove：列表命中与越界（越界复用 set 的 SET_INDEX_LIST_OUT 键）、数组不支持
     */
    @Test
    public void removeIndexTest() throws CommandException {
        Assertions.assertEquals(1,path("list[1]").remove(root,false));
        final NBTTagList list = root.getTagList("list",8);
        Assertions.assertEquals(2,list.tagCount());
        Assertions.assertEquals("c",list.getStringTagAt(1));
        assertThrowsKey(I18nKeys.NBTPath.SET_INDEX_LIST_OUT, //remove 越界复用 set 的键，为预期行为
                () -> path("list[5]").remove(root,false));
        assertThrowsKey(I18nKeys.NBTPath.REMOVE_INDEX_MISMATCH,
                () -> path("bytes[0]").remove(root,false));
    }

    /**
     * Claude Generated
     * 列表复合 remove：批量删除计数与相邻匹配项的遍历安全（删除时下标不前进）
     */
    @Test
    public void removeListCompoundTest() throws CommandException {
        Assertions.assertEquals(2,path("players[{id:1}]").remove(root,false));
        Assertions.assertEquals(1,root.getTagList("players",10).tagCount());
        Assertions.assertEquals(2,root.getTagList("players",10).getCompoundTagAt(0).getInteger("id"));

        //相邻匹配项：[{k:1},{k:1},{k:2},{k:1}] 删 {k:1} 应删 3 个
        final NBTTagList adjacent = new NBTTagList();
        for(final int k:new int[]{1,1,2,1}){
            final NBTTagCompound c = new NBTTagCompound();
            c.setInteger("k",k);
            adjacent.appendTag(c);
        }
        root.setTag("adj",adjacent);
        Assertions.assertEquals(3,path("adj[{k:1}]").remove(root,false));
        Assertions.assertEquals(1,root.getTagList("adj",10).tagCount());
        Assertions.assertEquals(2,root.getTagList("adj",10).getCompoundTagAt(0).getInteger("k"));

        Assertions.assertEquals(0,path("list[{x:1}]").remove(root,false)); //字符串列表 → 0 不抛
        assertThrowsKey(I18nKeys.NBTPath.REMOVE_LIST_COM_MISMATCH,
                () -> path("pos[{x:1}]").remove(root,false));
    }

    /**
     * Claude Generated
     * [] 对空列表 remove 返回 0；对非列表抛 REMOVE_ALL_MISMATCH
     */
    @Test
    public void removeAllEmptyListTest() throws CommandException {
        Assertions.assertEquals(0,path("empty[]").remove(root,false));
        assertThrowsKey(I18nKeys.NBTPath.REMOVE_ALL_MISMATCH,
                () -> path("bytes[]").remove(root,false));
    }

    /**
     * Claude Generated
     * [] 对非空列表 remove：清空、重置列表类型并返回原 size，之后可追加异型标签
     */
    @Test
    public void removeAllNonEmptyListTest() throws CommandException {
        Assertions.assertEquals(3,path("list[]").remove(root,false));
        final NBTTagList list = root.getTagList("list",8);
        Assertions.assertEquals(0,list.tagCount());
        list.appendTag(new NBTTagInt(1)); //类型重置后可接受异型标签
        Assertions.assertEquals(1,list.tagCount());
    }

    // ======================== init ========================

    /**
     * Claude Generated
     * init 补建父级结构：末节点键不建，返回空集；补建后 set 可直接成功
     */
    @Test
    public void initNestedCompoundTest() throws CommandException {
        final NBTTagCompound blank = new NBTTagCompound();
        final List<NBTBase> res = path("a.b.c").init(blank);
        镍测试.镍日志.info("init a.b.c 结果数={} 树={}",res.size(),blank);
        Assertions.assertTrue(res.isEmpty()); //init 只补建父级，末键 c 不建
        Assertions.assertTrue(blank.getCompoundTag("a").hasKey("b")); //a.b 已补建
        Assertions.assertEquals(1,path("a.b.c").set(blank,new NBTTagInt(1),false)); //init 后 set 直接成功
        Assertions.assertEquals(new NBTTagInt(1),path("a.b.c").resolve(blank).get(0));
    }

    /**
     * Claude Generated
     * init a[]：补建空列表；init a[].b：列表内补建一个含 b 的复合
     */
    @Test
    public void initListTest() throws CommandException {
        final NBTTagCompound blank = new NBTTagCompound();
        Assertions.assertTrue(path("a[]").init(blank).isEmpty());
        Assertions.assertEquals(9,blank.getTag("a").getId()); //补建了空列表（TAG_LIST=9）

        final NBTTagCompound blank2 = new NBTTagCompound();
        path("a[].b").init(blank2);
        final NBTTagList list = blank2.getTagList("a",10);
        Assertions.assertEquals(1,list.tagCount()); //[] 的 init 在空列表里填了一个 provide() 复合
        Assertions.assertEquals(1,path("a[].b").set(blank2,new NBTTagInt(5),false));
        Assertions.assertEquals(new NBTTagInt(5),path("a[].b").resolve(blank2).get(0));
    }

    /**
     * Claude Generated
     * init a[{x:1}].y：a 缺失时由 provide 生成含 {x:1} 的列表，init 后 set 成功
     */
    @Test
    public void initListCompoundProvideTest() throws CommandException {
        final NBTTagCompound blank = new NBTTagCompound();
        path("a[{x:1}].y").init(blank);
        final NBTTagList list = blank.getTagList("a",10);
        Assertions.assertEquals(1,list.tagCount());
        Assertions.assertEquals(1,list.getCompoundTagAt(0).getInteger("x")); //provide() 填入 filter.toNBT()
        Assertions.assertEquals(1,path("a[{x:1}].y").set(blank,new NBTTagInt(2),false));
        Assertions.assertEquals(new NBTTagInt(2),path("a[{x:1}].y").resolve(blank).get(0));
    }

    /**
     * Claude Generated
     * init 对已存在的空列表补建 [{x:1}]：重置类型并填入 filter.toNBT，之后 resolve 命中
     */
    @Test
    public void initListCompoundOnExistingEmptyListTest() throws CommandException {
        final List<NBTBase> res = path("empty[{x:1}]").init(root);
        Assertions.assertEquals(1,res.size());
        Assertions.assertEquals(1,root.getTagList("empty",10).getCompoundTagAt(0).getInteger("x"));
    }
}
