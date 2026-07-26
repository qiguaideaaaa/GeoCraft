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
import moe.qingu.nickel.nbt.operation.SNBTOperation;
import moe.qingu.nickel.nbt.operation.SNBTOperations;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.nbt.NBTTagLongArray;
import net.minecraft.nbt.NBTTagString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import javax.annotation.Nonnull;
import java.util.UUID;

import 清汩萌.镍.镍测试;

/**
 * 内置 SNBT 函数（bool/uuid/concat）直接调用语义的单元测试。
 * <p>注册表为全局状态，内置函数由基类 {@link 镍测试} 幂等加载，绝不清空注册表。
 * @author QGMoe, Claude
 * @see SNBTOperations
 */
public final class TestSNBTBuiltinOperations extends 镍测试 {

    @Nonnull
    private static NBTBase call(final @Nonnull String name,final @Nonnull NBTBase... args) throws NickelRuntimeException {
        final SNBTOperation op = SNBTOperations.resolve(name,args);
        Assertions.assertNotNull(op,"未能决议函数 "+name);
        return op.invoke(args);
    }

    /**
     * Claude Generated
     * 测试 bool 对整型参数的判零语义（0→0b，非零→1b）
     */
    @Test
    public void boolIntegerTest() throws NickelRuntimeException {
        Assertions.assertEquals(new NBTTagByte((byte)0),call("bool",new NBTTagInt(0)));
        Assertions.assertEquals(new NBTTagByte((byte)1),call("bool",new NBTTagInt(7)));
        Assertions.assertEquals(new NBTTagByte((byte)1),call("bool",new NBTTagInt(-2)));
        Assertions.assertEquals(new NBTTagByte((byte)0),call("bool",new NBTTagLong(0L)));
        Assertions.assertEquals(new NBTTagByte((byte)1),call("bool",new NBTTagLong(1919810L)));
        Assertions.assertEquals(new NBTTagByte((byte)1),call("bool",new NBTTagByte((byte)1)));
    }

    /**
     * Claude Generated
     * bool 对浮点参数的取整语义（预期行为）：NBTTagDouble.getLong 为 Math.floor（-0.9d→-1→1b），
     * NBTTagFloat.getLong 为强转截断（-0.9f→0→0b）
     */
    @Test
    public void boolFloatingPointTest() throws NickelRuntimeException {
        Assertions.assertEquals(new NBTTagByte((byte)0),call("bool",new NBTTagDouble(0.5D)));
        Assertions.assertEquals(new NBTTagByte((byte)1),call("bool",new NBTTagDouble(1.5D)));
        Assertions.assertEquals(new NBTTagByte((byte)1),call("bool",new NBTTagDouble(-0.9D)),"double 向下取整到 -1，非零");
        Assertions.assertEquals(new NBTTagByte((byte)0),call("bool",new NBTTagFloat(0.5F)));
        Assertions.assertEquals(new NBTTagByte((byte)0),call("bool",new NBTTagFloat(-0.9F)),"float 强转截断到 0");
    }

    /**
     * Claude Generated
     * 测试单参 uuid 把 UUID 字符串拆成四个整型，结果与 UUID.fromString 的高低位拆分一致
     */
    @Test
    public void uuidRoundTripTest() throws NickelRuntimeException {
        final String raw = "123e4567-e89b-12d3-a456-426614174000";
        final UUID uuid = UUID.fromString(raw);
        final long most = uuid.getMostSignificantBits();
        final long least = uuid.getLeastSignificantBits();
        final int[] expected = {
                (int)(most >> Integer.SIZE),
                (int)most,
                (int)(least >> Integer.SIZE),
                (int)least
        };
        final NBTTagIntArray actual = (NBTTagIntArray) call("uuid",new NBTTagString(raw));
        镍测试._镍日志_.info("uuid({}) -> {}",raw,actual);
        Assertions.assertArrayEquals(expected,actual.getIntArray());
    }

    /**
     * Claude Generated
     * 测试双参 uuid 产出 <key>Most/<key>Least 两个 long 键
     */
    @Test
    public void uuidTwoArgTest() throws NickelRuntimeException {
        final String raw = "123e4567-e89b-12d3-a456-426614174000";
        final UUID uuid = UUID.fromString(raw);
        final NBTTagCompound actual = (NBTTagCompound) call("uuid",new NBTTagString("Owner"),new NBTTagString(raw));
        Assertions.assertEquals(2,actual.getKeySet().size());
        Assertions.assertEquals(uuid.getMostSignificantBits(),actual.getLong("OwnerMost"));
        Assertions.assertEquals(uuid.getLeastSignificantBits(),actual.getLong("OwnerLeast"));
    }

    /**
     * Claude Generated
     * 测试 uuid 对非法格式与空串抛 NickelRuntimeException（单参与双参）
     */
    @Test
    public void uuidInvalidFormatTest(){
        Assertions.assertThrows(
                NickelRuntimeException.class,
                () -> call("uuid",new NBTTagString("not-a-uuid"))
        );
        Assertions.assertThrows(
                NickelRuntimeException.class,
                () -> call("uuid",new NBTTagString(""))
        );
        Assertions.assertThrows(
                NickelRuntimeException.class,
                () -> call("uuid",new NBTTagString("k"),new NBTTagString("bad"))
        );
    }

    /**
     * Claude Generated
     * concat(COMPOUND,COMPOUND) 的原地修改语义（预期行为）：返回的就是 a 实例，b 的同键覆盖 a，b 不被改动
     */
    @Test
    public void concatCompoundInPlaceTest() throws NickelRuntimeException {
        final NBTTagCompound a = new NBTTagCompound();
        a.setInteger("x",1);
        a.setInteger("y",1);
        final NBTTagCompound b = new NBTTagCompound();
        b.setInteger("y",2);
        final NBTBase result = call("concat",a,b);
        Assertions.assertSame(a,result,"应返回 a 实例本身（原地修改）");
        Assertions.assertEquals(1,a.getInteger("x"));
        Assertions.assertEquals(2,a.getInteger("y"),"同键应被 b 覆盖");
        Assertions.assertEquals(1,b.getKeySet().size(),"b 不应被改动");
    }

    /**
     * Claude Generated
     * 测试 concat(BYTE[],BYTE[]) 产出新数组且不改动入参
     */
    @Test
    public void concatByteArrayTest() throws NickelRuntimeException {
        final NBTTagByteArray a = new NBTTagByteArray(new byte[]{1,2});
        final NBTTagByteArray b = new NBTTagByteArray(new byte[]{3});
        final NBTTagByteArray result = (NBTTagByteArray) call("concat",a,b);
        Assertions.assertNotSame(a,result);
        Assertions.assertNotSame(b,result);
        Assertions.assertArrayEquals(new byte[]{1,2,3},result.getByteArray());
        Assertions.assertArrayEquals(new byte[]{1,2},a.getByteArray(),"入参 a 不应被改动");
        Assertions.assertArrayEquals(new byte[]{3},b.getByteArray(),"入参 b 不应被改动");
    }

    /**
     * Claude Generated
     * 测试 concat(INT[],INT[]) 与 concat(LONG[],LONG[]) 的拼接顺序与空数组边界
     */
    @Test
    public void concatIntAndLongArrayTest() throws NickelRuntimeException {
        final NBTTagIntArray ints = (NBTTagIntArray) call("concat",
                new NBTTagIntArray(new int[]{11451}),new NBTTagIntArray(new int[]{4,-1}));
        Assertions.assertArrayEquals(new int[]{11451,4,-1},ints.getIntArray());

        final NBTTagIntArray emptyInts = (NBTTagIntArray) call("concat",
                new NBTTagIntArray(new int[0]),new NBTTagIntArray(new int[0]));
        Assertions.assertEquals(0,emptyInts.getIntArray().length);

        final NBTTagLongArray longs = (NBTTagLongArray) call("concat",
                new NBTTagLongArray(new long[]{1L}),new NBTTagLongArray(new long[]{2L,3L}));
        Assertions.assertEquals(new NBTTagLongArray(new long[]{1L,2L,3L}),longs);
    }

    /**
     * Claude Generated
     * 测试 concat(LIST,LIST) 产出新列表、顺序拼接且不改动入参
     */
    @Test
    public void concatListTest() throws NickelRuntimeException {
        final NBTTagList a = new NBTTagList();
        a.appendTag(new NBTTagInt(1));
        final NBTTagList b = new NBTTagList();
        b.appendTag(new NBTTagInt(2));
        b.appendTag(new NBTTagInt(3));
        final NBTTagList result = (NBTTagList) call("concat",a,b);
        Assertions.assertNotSame(a,result);
        Assertions.assertEquals(3,result.tagCount());
        Assertions.assertEquals(1,((NBTTagInt)result.get(0)).getInt());
        Assertions.assertEquals(3,((NBTTagInt)result.get(2)).getInt());
        Assertions.assertEquals(1,a.tagCount(),"入参 a 不应被改动");
        Assertions.assertEquals(2,b.tagCount(),"入参 b 不应被改动");
    }

    /**
     * Claude Generated
     * concat(LIST,LIST) 混型拼接应保留两侧全部元素。异构列表的实现约定：
     * 非复合值以空键包裹成复合标签（{"":值}）作为容器，空键即异构容器的保留语义，与高版本一致
     */
    @Disabled("混型列表拼接有待异构列表支持：NBTTagList.appendTag 会静默丢弃类型不符的元素")
    @Test
    public void concatListMixedTypeTest() throws NickelRuntimeException {
        final NBTTagList bytes = new NBTTagList();
        bytes.appendTag(new NBTTagByte((byte)1));
        final NBTTagList strings = new NBTTagList();
        strings.appendTag(new NBTTagString("s"));
        final NBTTagList result = (NBTTagList) call("concat",bytes,strings);
        Assertions.assertEquals(2,result.tagCount(),"混型元素不得静默丢弃");
        final NBTTagCompound wrappedByte = new NBTTagCompound();
        wrappedByte.setTag("",new NBTTagByte((byte)1));
        final NBTTagCompound wrappedString = new NBTTagCompound();
        wrappedString.setTag("",new NBTTagString("s"));
        Assertions.assertEquals(wrappedByte,result.get(0),"非复合值应以空键包裹成复合标签");
        Assertions.assertEquals(wrappedString,result.get(1),"非复合值应以空键包裹成复合标签");
    }

    /**
     * Claude Generated
     * 测试 concat(STRING,STRING) 拼接，含中文与空串
     */
    @Test
    public void concatStringTest() throws NickelRuntimeException {
        Assertions.assertEquals(new NBTTagString("天圆地方"),
                call("concat",new NBTTagString("天圆"),new NBTTagString("地方")));
        Assertions.assertEquals(new NBTTagString(""),
                call("concat",new NBTTagString(""),new NBTTagString("")));
        Assertions.assertEquals(new NBTTagString("a"),
                call("concat",new NBTTagString("a"),new NBTTagString("")));
    }

    /**
     * Claude Generated
     * 测试 resolve 的 null 语义：未知函数名、参数个数不符、参数类型无重载可匹配均返回 null 而不抛
     */
    @Test
    public void resolveNullSemanticsTest(){
        Assertions.assertNull(SNBTOperations.resolve("nosuchfunc",new NBTBase[]{new NBTTagInt(1)}),"未知函数名");
        Assertions.assertNull(SNBTOperations.resolve("bool",new NBTBase[0]),"参数过少");
        Assertions.assertNull(SNBTOperations.resolve("bool",
                new NBTBase[]{new NBTTagInt(1),new NBTTagInt(2)}),"参数过多");
        Assertions.assertNull(SNBTOperations.resolve("bool",new NBTBase[]{new NBTTagString("s")}),"STRING 不是 NUMBER");
        Assertions.assertNull(SNBTOperations.resolve("concat",
                new NBTBase[]{new NBTTagByte((byte)1),new NBTTagByte((byte)2)}),"concat 无数值重载");
        Assertions.assertNull(SNBTOperations.resolve("concat",
                new NBTBase[]{new NBTTagString("a"),new NBTTagCompound()}),"两参类型分属不同重载");
    }

    /**
     * Claude Generated
     * 测试 concat 各重载按实参类型正确分派（以返回类型佐证）
     */
    @Test
    public void concatDispatchTest() throws NickelRuntimeException {
        Assertions.assertTrue(call("concat",new NBTTagString("a"),new NBTTagString("b")) instanceof NBTTagString);
        Assertions.assertTrue(call("concat",new NBTTagCompound(),new NBTTagCompound()) instanceof NBTTagCompound);
        Assertions.assertTrue(call("concat",new NBTTagList(),new NBTTagList()) instanceof NBTTagList);
        Assertions.assertTrue(call("concat",
                new NBTTagByteArray(new byte[0]),new NBTTagByteArray(new byte[0])) instanceof NBTTagByteArray);
        Assertions.assertTrue(call("concat",
                new NBTTagIntArray(new int[0]),new NBTTagIntArray(new int[0])) instanceof NBTTagIntArray);
        Assertions.assertTrue(call("concat",
                new NBTTagLongArray(new long[]{1L}),new NBTTagLongArray(new long[]{2L})) instanceof NBTTagLongArray);
    }

    /**
     * Claude Generated
     * concat(LONG[],LONG[]) 对空数组返回空 NBTTagLongArray
     */
    @Test
    public void concatEmptyLongArrayTest() throws NickelRuntimeException {
        final NBTBase result = call("concat",
                new NBTTagLongArray(new long[0]),new NBTTagLongArray(new long[0]));
        Assertions.assertEquals(new NBTTagLongArray(new long[0]),result);
    }
}
