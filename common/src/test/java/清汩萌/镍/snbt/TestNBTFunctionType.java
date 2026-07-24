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

import moe.qingu.nickel.nbt.NBTFunctionType;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.annotation.Nonnull;
import java.util.LinkedHashMap;
import java.util.Map;

import 清汩萌.镍.镍测试;

/**
 * NBTFunctionType 重载决议与基础契约的单元测试
 * @author QGMoe, Claude
 * @see NBTFunctionType
 */
public final class TestNBTFunctionType {

    @Nonnull
    @SuppressWarnings("unchecked")
    private static NBTFunctionType type(final @Nonnull Class<?>... inputs){
        return new NBTFunctionType((Class<? extends NBTBase>[]) inputs);
    }

    /**
     * Claude Generated
     * 测试 distance 沿继承链计算的距离与不可达返回 -1
     */
    @Test
    @SuppressWarnings("unchecked")
    public void distanceTest(){
        final Object[][] cases = {
                {NBTTagByte.class,NBTTagByte.class,0},
                {NBTTagByte.class,NBTPrimitive.class,1},
                {NBTTagByte.class,NBTBase.class,2},
                {NBTTagString.class,NBTBase.class,1},
                {NBTTagString.class,NBTPrimitive.class,-1},
                {NBTPrimitive.class,NBTTagByte.class,-1}, //父类到子类不可达
                {NBTTagCompound.class,NBTBase.class,1},
                {NBTTagIntArray.class,NBTPrimitive.class,-1}
        };
        for(final Object[] c:cases){
            final Class<? extends NBTBase> arg = (Class<? extends NBTBase>) c[0];
            final Class<? extends NBTBase> para = (Class<? extends NBTBase>) c[1];
            final int expected = (Integer) c[2];
            final int actual = NBTFunctionType.distance(arg,para);
            镍测试.镍日志.info("distance({},{})={} expected={}",arg.getSimpleName(),para.getSimpleName(),actual,expected);
            Assertions.assertEquals(expected,actual,"distance("+arg.getSimpleName()+","+para.getSimpleName()+")");
        }
    }

    /**
     * Claude Generated
     * 测试 resolve 精确匹配优先于沿继承链的宽松匹配
     */
    @Test
    public void resolveExactBeatsLooseTest(){
        final Map<NBTFunctionType,String> candidates = new LinkedHashMap<>();
        candidates.put(type(NBTTagByte.class),"精确");
        candidates.put(type(NBTPrimitive.class),"宽松");
        candidates.put(type(NBTBase.class),"最宽");
        Assertions.assertEquals("精确",NBTFunctionType.resolve(candidates,new NBTBase[]{new NBTTagByte((byte)1)}));
        Assertions.assertEquals("宽松",NBTFunctionType.resolve(candidates,new NBTBase[]{new NBTTagInt(1)}));
        Assertions.assertEquals("最宽",NBTFunctionType.resolve(candidates,new NBTBase[]{new NBTTagCompound()}));
    }

    /**
     * Claude Generated
     * 测试 resolve 只考虑参数个数相等的候选，零元候选与零参数匹配
     */
    @Test
    public void resolveParameterCountTest(){
        final Map<NBTFunctionType,String> candidates = new LinkedHashMap<>();
        candidates.put(type(NBTTagByte.class,NBTTagByte.class),"二元");
        candidates.put(type(),"零元");
        Assertions.assertNull(NBTFunctionType.resolve(candidates,new NBTBase[]{new NBTTagByte((byte)1)}));
        Assertions.assertEquals("二元",NBTFunctionType.resolve(candidates,
                new NBTBase[]{new NBTTagByte((byte)1),new NBTTagByte((byte)2)}));
        Assertions.assertEquals("零元",NBTFunctionType.resolve(candidates,new NBTBase[0]));
    }

    /**
     * Claude Generated
     * 测试并列最小总分判定为歧义并返回 null，更优候选可打破歧义
     */
    @Test
    public void resolveAmbiguousTest(){
        final Map<NBTFunctionType,String> candidates = new LinkedHashMap<>();
        candidates.put(type(NBTPrimitive.class,NBTTagByte.class),"甲");
        candidates.put(type(NBTTagByte.class,NBTPrimitive.class),"乙");
        final NBTBase[] args = {new NBTTagByte((byte)1),new NBTTagByte((byte)2)};
        Assertions.assertNull(NBTFunctionType.resolve(candidates,args),"两候选总分并列应判为歧义");

        candidates.put(type(NBTTagByte.class,NBTTagByte.class),"丙");
        Assertions.assertEquals("丙",NBTFunctionType.resolve(candidates,args),"加入总分更小的候选后歧义消除");
    }

    /**
     * Claude Generated
     * 测试任一实参为 null 时 resolve 返回 null
     */
    @Test
    public void resolveNullArgTest(){
        final Map<NBTFunctionType,String> candidates = new LinkedHashMap<>();
        candidates.put(type(NBTTagByte.class,NBTTagByte.class),"二元");
        Assertions.assertNull(NBTFunctionType.resolve(candidates,new NBTBase[]{new NBTTagByte((byte)1),null}));
    }

    /**
     * Claude Generated
     * 测试空候选集与全部不可达候选均返回 null
     */
    @Test
    public void resolveNoCandidateTest(){
        Assertions.assertNull(NBTFunctionType.resolve(new LinkedHashMap<NBTFunctionType,String>(),
                new NBTBase[]{new NBTTagByte((byte)1)}));

        final Map<NBTFunctionType,String> unreachable = new LinkedHashMap<>();
        unreachable.put(type(NBTTagString.class),"字符串版");
        Assertions.assertNull(NBTFunctionType.resolve(unreachable,new NBTBase[]{new NBTTagByte((byte)1)}));
    }

    /**
     * Claude Generated
     * 测试 equals 与 hashCode 按 inputs 数组内容判定
     */
    @Test
    public void equalsHashCodeTest(){
        final NBTFunctionType a = type(NBTTagString.class,NBTTagCompound.class);
        final NBTFunctionType b = type(NBTTagString.class,NBTTagCompound.class);
        final NBTFunctionType c = type(NBTTagString.class);
        final NBTFunctionType d = type(NBTTagCompound.class,NBTTagString.class);
        Assertions.assertEquals(a,b);
        Assertions.assertEquals(a.hashCode(),b.hashCode());
        Assertions.assertNotEquals(a,c);
        Assertions.assertNotEquals(a,d);
        Assertions.assertNotEquals((Object)"(STRING,COMPOUND)",a);
    }

    /**
     * Claude Generated
     * 测试 toString 输出 (TYPE1,TYPE2) 形式，类型名取自 TYPES 映射
     */
    @Test
    public void toStringTest(){
        Assertions.assertEquals("(STRING,COMPOUND)",type(NBTTagString.class,NBTTagCompound.class).toString());
        Assertions.assertEquals("(NUMBER)",type(NBTPrimitive.class).toString());
        Assertions.assertEquals("(BYTE,INT[])",type(NBTTagByte.class,NBTTagIntArray.class).toString());
        Assertions.assertEquals("()",type().toString());
    }

    /**
     * Claude Generated
     * 测试构造器与 getInputTypes 的防御性拷贝：外部改动数组不影响实例
     */
    @Test
    @SuppressWarnings("unchecked")
    public void defensiveCopyTest(){
        final Class<? extends NBTBase>[] raw = (Class<? extends NBTBase>[]) new Class<?>[]{NBTTagByte.class};
        final NBTFunctionType t = new NBTFunctionType(raw);
        raw[0] = NBTTagString.class;
        Assertions.assertEquals(NBTTagByte.class,t.getInputTypeAt(0),"构造后改动入参数组不应影响实例");

        final Class<? extends NBTBase>[] out = t.getInputTypes();
        out[0] = NBTTagCompound.class;
        Assertions.assertEquals(NBTTagByte.class,t.getInputTypeAt(0),"改动 getInputTypes 返回值不应影响实例");
    }

    /**
     * Claude Generated
     * 测试 getParameterCount 与 getInputTypeAt 越界抛 ArrayIndexOutOfBoundsException
     */
    @Test
    public void getInputTypeAtTest(){
        final NBTFunctionType t = type(NBTTagByte.class,NBTTagString.class);
        Assertions.assertEquals(2,t.getParameterCount());
        Assertions.assertEquals(NBTTagString.class,t.getInputTypeAt(1));
        Assertions.assertThrows(
                ArrayIndexOutOfBoundsException.class,
                () -> t.getInputTypeAt(2)
        );
    }
}
