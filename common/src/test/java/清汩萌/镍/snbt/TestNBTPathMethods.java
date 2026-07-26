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
import moe.qingu.nickel.nbt.path.method.NBTPathArgsProcessor;
import moe.qingu.nickel.nbt.path.method.NBTPathMethod;
import moe.qingu.nickel.nbt.path.method.NBTPathMethods;
import net.minecraft.command.CommandException;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import 清汩萌.镍.镍测试;

/**
 * NBTPath 路径方法（NBTPathMethods 注册表与内置方法）测试。
 * 注册表是静态全局状态：内置方法由基类 {@link 镍测试} 幂等加载；自定义注册用唯一名并带存在性守卫，
 * 避免重复签名触发 NickelAPI 日志（其类初始化依赖 FML 网络注册，测试环境不可用）。
 * @author Claude
 * @see moe.qingu.nickel.nbt.path.method.NBTPathMethods
 */
public final class TestNBTPathMethods extends 镍测试 {
    private @Nonnull NBTTagCompound root;

    /**
     * Claude Generated
     * 构造混合类型测试树
     */
    @BeforeEach
    public void setup(){
        root = new NBTTagCompound();
        final NBTTagCompound mixed = new NBTTagCompound();
        mixed.setInteger("a",42);
        mixed.setString("b","foo");
        mixed.setDouble("c",1.5);
        mixed.setTag("d",new NBTTagCompound());
        root.setTag("mixed",mixed);

        final NBTTagList strings = new NBTTagList();
        strings.appendTag(new NBTTagString("foo"));
        strings.appendTag(new NBTTagString("bar"));
        strings.appendTag(new NBTTagString("fob"));
        root.setTag("strings",strings);

        root.setString("name","GeoCraft");
        root.setDouble("half",1.5);
        root.setFloat("quarter",0.25F);
        root.setInteger("num",42);
    }

    @Nonnull
    private List<NBTBase> resolve(final @Nonnull String path) throws CommandException {
        return NBTPathTestSupport.parse(path).resolve(root);
    }

    // ======================== 注册表决议 ========================

    /**
     * Claude Generated
     * 内置方法与处理器分表注册：values 在方法表、pattern/match/type 在处理器表，互不可见
     */
    @Test
    public void registryTablesTest(){
        Assertions.assertNotNull(NBTPathMethods.resolveMethod("values",new NBTBase[0]));
        Assertions.assertNull(NBTPathMethods.resolveMethod("values",new NBTBase[]{new NBTTagInt(1)})); //参数个数不符
        Assertions.assertNull(NBTPathMethods.resolveMethod("pattern",new NBTBase[]{new NBTTagString("x")})); //处理器不在方法表
        Assertions.assertNotNull(NBTPathMethods.resolveProcessor("pattern",new NBTBase[]{new NBTTagString("x")}));
        Assertions.assertNotNull(NBTPathMethods.resolveProcessor("match",new NBTBase[]{new NBTTagInt(1)}));
        Assertions.assertNull(NBTPathMethods.resolveProcessor("nosuchmeth",new NBTBase[0]));
    }

    /**
     * Claude Generated
     * type 的重载决议：数字实参决议到 NUMBER 版、字符串实参决议到 STRING 版，签名可区分
     */
    @Test
    public void overloadResolutionTest(){
        final NBTPathArgsProcessor byNum = NBTPathMethods.resolveProcessor("type",new NBTBase[]{new NBTTagInt(3)});
        final NBTPathArgsProcessor byStr = NBTPathMethods.resolveProcessor("type",new NBTBase[]{new NBTTagString("INT")});
        Assertions.assertNotNull(byNum);
        Assertions.assertNotNull(byStr);
        Assertions.assertNotSame(byNum,byStr);
        Assertions.assertEquals("type(NUMBER)",NBTPathMethods.signatureOf(byNum));
        Assertions.assertEquals("type(STRING)",NBTPathMethods.signatureOf(byStr));
    }

    /**
     * Claude Generated
     * signatureOf 对未注册对象抛 IllegalArgumentException
     */
    @Test
    public void signatureOfUnknownTest(){
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> NBTPathMethods.signatureOf(new Object())
        );
    }

    // ======================== values ========================

    /**
     * Claude Generated
     * values()：复合返回全部值集合，非复合返回空
     */
    @Test
    public void valuesTest() throws CommandException {
        final List<NBTBase> vals = resolve("mixed.values()");
        Assertions.assertEquals(4,vals.size());
        Assertions.assertTrue(vals.contains(new NBTTagInt(42)));
        Assertions.assertTrue(vals.contains(new NBTTagString("foo")));
        Assertions.assertTrue(resolve("name.values()").isEmpty()); //字符串上求值为空
        Assertions.assertTrue(resolve("strings.values()").isEmpty()); //列表也不是复合
    }

    /**
     * Claude Generated
     * 直接调用静态 values：复合与非复合
     */
    @Test
    public void valuesDirectTest(){
        final NBTTagCompound compound = new NBTTagCompound();
        compound.setInteger("k",1);
        final Collection<NBTBase> vals = NBTPathMethods.values(compound);
        Assertions.assertEquals(1,vals.size());
        Assertions.assertTrue(NBTPathMethods.values(new NBTTagInt(1)).isEmpty());
    }

    // ======================== pattern ========================

    /**
     * Claude Generated
     * pattern：字符串全匹配语义（matches 而非 find），扇出过滤
     */
    @Test
    public void patternStringTest() throws CommandException {
        Assertions.assertEquals(2,resolve("strings[].pattern(\"fo.\")").size()); //foo、fob
        Assertions.assertEquals(1,resolve("strings[].pattern(\"bar\")").size());
        Assertions.assertTrue(resolve("strings[].pattern(\"fo\")").isEmpty()); //全匹配：前缀命中不算
        Assertions.assertEquals(1,resolve("name.pattern(\"Geo.*\")").size());
    }

    /**
     * Claude Generated
     * pattern 作用于数值：Float/Double 用小数文本，其余 Primitive 用 getLong 十进制文本；复合为空
     */
    @Test
    public void patternNumericTest() throws CommandException {
        Assertions.assertEquals(1,resolve("half.pattern(\"1\\\\.5\")").size()); //double 1.5 → "1.5"
        Assertions.assertEquals(1,resolve("quarter.pattern(\"0\\\\.25\")").size()); //float 0.25 → "0.25"
        Assertions.assertEquals(1,resolve("num.pattern(\"42\")").size()); //int 42 → getLong → "42"
        Assertions.assertTrue(resolve("num.pattern(\"42\\\\.0\")").isEmpty()); //整数不是小数文本
        Assertions.assertTrue(resolve("mixed.pattern(\".*\")").isEmpty()); //复合直接为空
    }

    /**
     * Claude Generated
     * pattern 直接调用：返回的过滤函数按标签类型分派
     */
    @Test
    public void patternDirectTest(){
        final java.util.function.Function<NBTBase,Collection<NBTBase>> f = NBTPathMethods.pattern(new NBTTagString("1\\.5"));
        Assertions.assertEquals(1,f.apply(new NBTTagDouble(1.5)).size());
        Assertions.assertEquals(1,f.apply(new NBTTagFloat(1.5F)).size());
        Assertions.assertTrue(f.apply(new NBTTagInt(1)).isEmpty());
        Assertions.assertTrue(f.apply(new NBTTagList()).isEmpty());
    }

    // ======================== match ========================

    /**
     * Claude Generated
     * match：以字面量矩阵过滤标签本身，类型严格（int 42 不匹配字符串 "42"）
     */
    @Test
    public void matchTest() throws CommandException {
        Assertions.assertEquals(1,resolve("num.match(42)").size());
        Assertions.assertTrue(resolve("num.match(43)").isEmpty());
        Assertions.assertTrue(resolve("num.match(\"42\")").isEmpty()); //类型严格：字符串矩阵不匹配 int
        Assertions.assertEquals(1,resolve("mixed.match({a:42})").size()); //复合子集匹配
        Assertions.assertTrue(resolve("mixed.match({a:1})").isEmpty());
    }

    // ======================== type ========================

    /**
     * Claude Generated
     * type：按名字（大小写不敏感）过滤，NUMBER 为 isAssignableFrom 继承语义
     */
    @Test
    public void typeByNameTest() throws CommandException {
        Assertions.assertEquals(1,resolve("mixed.values().type(\"STRING\")").size());
        Assertions.assertEquals(1,resolve("mixed.values().type(\"int\")").size()); //大小写不敏感
        Assertions.assertEquals(2,resolve("mixed.values().type(\"NUMBER\")").size()); //int + double
        Assertions.assertEquals(1,resolve("mixed.values().type(\"COMPOUND\")").size());
        Assertions.assertEquals(4,resolve("mixed.values().type(\"BASE\")").size()); //BASE 匹配一切
        Assertions.assertTrue(resolve("mixed.values().type(\"LIST\")").isEmpty());
    }

    /**
     * Claude Generated
     * type：按 NBT 类型编号过滤（3=INT、8=STRING、10=COMPOUND）
     */
    @Test
    public void typeByIdTest() throws CommandException {
        Assertions.assertEquals(1,resolve("mixed.values().type(3)").size());
        Assertions.assertEquals(1,resolve("mixed.values().type(8)").size());
        Assertions.assertEquals(1,resolve("mixed.values().type(10)").size());
        Assertions.assertTrue(resolve("mixed.values().type(9)").isEmpty()); //没有 LIST
    }

    /**
     * Claude Generated
     * 处理器错参在解析期报错：越界编号、未知类型名与非法正则
     */
    @Test
    public void processorBadArgsTest(){
        Assertions.assertThrows(CommandException.class,() -> NBTPathTestSupport.parse("a.type(99)"));
        Assertions.assertThrows(CommandException.class,() -> NBTPathTestSupport.parse("a.type(-1)"));
        Assertions.assertThrows(CommandException.class,() -> NBTPathTestSupport.parse("a.type(\"XYZ\")"));
        Assertions.assertThrows(CommandException.class,() -> NBTPathTestSupport.parse("a.pattern(\"[\")"));
    }

    // ======================== 自定义注册 ========================

    /**
     * Claude Generated
     * 经公开 register 注册自定义方法：调用时首参拼上当前标签
     */
    @Test
    @SuppressWarnings("unchecked")
    public void registerCustomMethodTest() throws CommandException {
        if(NBTPathMethods.resolveMethod("cl-custom-echo",new NBTBase[0]) == null){ //守卫：注册表全局，重复注册会触发错误日志
            final NBTPathMethod echo = args -> Collections.singletonList(args[0]);
            NBTPathMethods.register("cl-custom-echo",new NBTFunctionType((Class<? extends NBTBase>[]) new Class<?>[0]),echo);
        }
        final List<NBTBase> res = resolve("mixed.cl-custom-echo()");
        Assertions.assertEquals(1,res.size());
        Assertions.assertSame(root.getTag("mixed"),res.get(0)); //args[0] 即当前标签
        镍测试._镍日志_.info("自定义方法 cl-custom-echo 求值结果：{}",res.get(0));
    }

    /**
     * Claude Generated
     * 经公开 register 注册自定义处理器：解析期以字面实参执行一次得到过滤函数
     */
    @Test
    @SuppressWarnings("unchecked")
    public void registerCustomProcessorTest() throws CommandException {
        if(NBTPathMethods.resolveProcessor("cl-custom-const",new NBTBase[]{new NBTTagString("x")}) == null){
            final NBTPathArgsProcessor constant = args -> tag -> Collections.singletonList(new NBTTagString(((NBTTagString) args[0]).getString()));
            NBTPathMethods.register("cl-custom-const",new NBTFunctionType((Class<? extends NBTBase>[]) new Class<?>[]{NBTTagString.class}),constant);
        }
        final List<NBTBase> res = resolve("name.cl-custom-const(\"K\")");
        Assertions.assertEquals(1,res.size());
        Assertions.assertEquals(new NBTTagString("K"),res.get(0));
    }

    /**
     * Claude Generated
     * 自定义处理器在解析期抛 NickelRuntimeException：读者以 METHOD_PROCESS_ERR 报错
     */
    @Test
    @SuppressWarnings("unchecked")
    public void customProcessorFailAtParseTest(){
        if(NBTPathMethods.resolveProcessor("cl-custom-fail",new NBTBase[0]) == null){
            final NBTPathArgsProcessor failing = args -> {
                throw new moe.qingu.nickel.command.exception.NickelRuntimeException(
                        moe.qingu.nickel.text.Texts.plain("总是失败"));
            };
            NBTPathMethods.register("cl-custom-fail",new NBTFunctionType((Class<? extends NBTBase>[]) new Class<?>[0]),failing);
        }
        final CommandException e = Assertions.assertThrows(
                CommandException.class,
                () -> NBTPathTestSupport.parse("a.cl-custom-fail()")
        );
        NBTPathTestSupport.assertInfoHasKey(e,moe.qingu.nickel.I18nKeys.NBTPath.METHOD_PROCESS_ERR);
    }
}
