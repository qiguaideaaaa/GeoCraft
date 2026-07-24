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
import moe.qingu.nickel.nbt.NBTFunctionType;
import moe.qingu.nickel.nbt.operation.SNBTFunction;
import moe.qingu.nickel.nbt.operation.SNBTOperation;
import moe.qingu.nickel.nbt.operation.SNBTOperations;
import moe.qingu.nickel.text.Texts;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLongArray;
import net.minecraft.nbt.NBTTagString;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Arrays;

import 清汩萌.镍.镍测试;

/**
 * SNBT 函数注册机制（loadFuncs 发现与登记、register 重复注册、signatureOf）的单元测试。
 * <p>注册表是跨测试的全局状态：内置函数由基类 {@link 镍测试} 幂等加载，本类只做追加式注册，
 * 测试函数用 nickelTest 前缀的唯一名且行为恒定——重复签名被静默忽略、首次登记持续生效，
 * 断言对重复加载免疫。绝不清空注册表。
 * @author QGMoe, Claude
 * @see SNBTOperations
 */
public final class TestSNBTFunctionRegistry extends 镍测试 {

    /**
     * Claude Generated
     * 加载本类的测试用 provider（内置函数由基类加载）；重复加载无害（重复签名被忽略）
     */
    @BeforeAll
    public static void loadAll(){
        SNBTOperations.loadFuncs(GoodProvider.class);
        SNBTOperations.loadFuncs(BadProvider.class);
        镍测试.镍日志.info("SNBT 函数注册表已加载（测试 provider）");
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    private static NBTFunctionType type(final @Nonnull Class<?>... inputs){
        return new NBTFunctionType((Class<? extends NBTBase>[]) inputs);
    }

    /**
     * Claude Generated
     * 内置函数经基类加载后均已注册（bool×1、uuid×2、concat×6 共九个签名）
     */
    @Test
    public void builtinsRegisteredTest(){
        Assertions.assertNotNull(SNBTOperations.resolve("bool",new NBTBase[]{new NBTTagInt(1)}),"bool(NUMBER)");
        Assertions.assertNotNull(SNBTOperations.resolve("uuid",new NBTBase[]{new NBTTagString("x")}),"uuid(STRING)");
        Assertions.assertNotNull(SNBTOperations.resolve("uuid",
                new NBTBase[]{new NBTTagString("k"),new NBTTagString("v")}),"uuid(STRING,STRING)");
        Assertions.assertNotNull(SNBTOperations.resolve("concat",
                new NBTBase[]{new NBTTagCompound(),new NBTTagCompound()}),"concat(COMPOUND,COMPOUND)");
        Assertions.assertNotNull(SNBTOperations.resolve("concat",
                new NBTBase[]{new NBTTagByteArray(new byte[0]),new NBTTagByteArray(new byte[0])}),"concat(BYTE[],BYTE[])");
        Assertions.assertNotNull(SNBTOperations.resolve("concat",
                new NBTBase[]{new NBTTagIntArray(new int[0]),new NBTTagIntArray(new int[0])}),"concat(INT[],INT[])");
        Assertions.assertNotNull(SNBTOperations.resolve("concat",
                new NBTBase[]{new NBTTagLongArray(new long[0]),new NBTTagLongArray(new long[0])}),"concat(LONG[],LONG[])");
        Assertions.assertNotNull(SNBTOperations.resolve("concat",
                new NBTBase[]{new NBTTagList(),new NBTTagList()}),"concat(LIST,LIST)");
        Assertions.assertNotNull(SNBTOperations.resolve("concat",
                new NBTBase[]{new NBTTagString("a"),new NBTTagString("b")}),"concat(STRING,STRING)");
    }

    /**
     * Claude Generated
     * 测试重复 loadFuncs 同一个类是幂等的：重复签名被忽略，首次登记的函数持续生效
     */
    @Test
    public void loadFuncsIdempotentTest() throws NickelRuntimeException {
        final NBTBase[] args = {new NBTTagByte((byte)4)};
        final SNBTOperation first = SNBTOperations.resolve("nickelTestDefaultName",args);
        Assertions.assertNotNull(first);
        SNBTOperations.loadFuncs(GoodProvider.class); //第二次（或更多次）加载
        final SNBTOperation second = SNBTOperations.resolve("nickelTestDefaultName",args);
        Assertions.assertSame(first,second,"重复加载后决议仍指向首次登记的函数实例");
        Assertions.assertEquals((byte)5,((NBTTagByte)second.invoke(args)).getByte());
    }

    /**
     * Claude Generated
     * 测试注解 name 为空时用方法名登记
     */
    @Test
    public void defaultMethodNameTest() throws NickelRuntimeException {
        final SNBTOperation op = SNBTOperations.resolve("nickelTestDefaultName",new NBTBase[]{new NBTTagByte((byte)4)});
        Assertions.assertNotNull(op);
        Assertions.assertEquals((byte)5,((NBTTagByte)op.invoke(new NBTBase[]{new NBTTagByte((byte)4)})).getByte());
    }

    /**
     * Claude Generated
     * 测试 @SNBTFunction(name=...) 别名生效，且原方法名不被登记
     */
    @Test
    public void annotationAliasTest() throws NickelRuntimeException {
        final NBTBase[] args = {new NBTTagInt(21)};
        final SNBTOperation op = SNBTOperations.resolve("nickelTestAlias",args);
        Assertions.assertNotNull(op,"别名应被登记");
        Assertions.assertEquals(42,((NBTTagInt)op.invoke(args)).getInt());
        Assertions.assertNull(SNBTOperations.resolve("renamedMethod",args),"原方法名不应被登记");
    }

    /**
     * Claude Generated
     * 测试未标注 @SNBTFunction 的 public static 方法不被登记
     */
    @Test
    public void notAnnotatedSkippedTest(){
        Assertions.assertNull(SNBTOperations.resolve("nickelTestNotAnnotated",new NBTBase[]{new NBTTagByte((byte)1)}));
    }

    /**
     * Claude Generated
     * 测试非 public 的静态 @SNBTFunction 方法被跳过
     */
    @Test
    public void nonPublicSkippedTest(){
        Assertions.assertNull(SNBTOperations.resolve("nickelTestPrivate",new NBTBase[]{new NBTTagByte((byte)1)}));
    }

    /**
     * Claude Generated
     * 测试实例方法（非 static）即使标注 @SNBTFunction 也被跳过
     */
    @Test
    public void nonStaticSkippedTest(){
        Assertions.assertNull(SNBTOperations.resolve("nickelTestInstance",new NBTBase[]{new NBTTagByte((byte)1)}));
    }

    /**
     * Claude Generated
     * 参数类型非 NBTBase 的方法在 loadFuncs 阶段被跳过（带 warn 日志），不被登记
     */
    @Test
    public void badParameterTypeSkippedTest(){
        Assertions.assertNull(SNBTOperations.resolve("nickelTestBadParam",new NBTBase[]{new NBTTagByte((byte)1)}));
        Assertions.assertNull(SNBTOperations.resolve("nickelTestBadParam",new NBTBase[]{new NBTTagString("s")}));
    }

    /**
     * Claude Generated
     * 返回类型不是 NBTBase 子类的方法在 loadFuncs 阶段被跳过，不被登记
     */
    @Test
    public void badReturnTypeSkippedTest(){
        final NBTBase[] args = {new NBTTagByte((byte)1)};
        final SNBTOperation op = SNBTOperations.resolve("nickelTestBadReturn",args);
        Assertions.assertNull(op,"返回类型非 NBTBase 的方法应在 loadFuncs 阶段被跳过，不被登记");
    }

    /**
     * Claude Generated
     * 测试编程式 register 登记后可决议并调用
     */
    @Test
    public void programmaticRegisterTest() throws NickelRuntimeException {
        SNBTOperations.register("nickelTestManual",type(NBTTagString.class),args -> new NBTTagString("圆"));
        final NBTBase[] args = {new NBTTagString("任意")};
        final SNBTOperation op = SNBTOperations.resolve("nickelTestManual",args);
        Assertions.assertNotNull(op);
        Assertions.assertEquals("圆",((NBTTagString)op.invoke(args)).getString());
    }

    /**
     * Claude Generated
     * 测试重复签名的第二次 register 被静默忽略，首次登记的函数持续生效
     */
    @Test
    public void duplicateRegisterKeepsFirstTest() throws NickelRuntimeException {
        final NBTFunctionType byteType = type(NBTTagByte.class);
        SNBTOperations.register("nickelTestDup",byteType,args -> new NBTTagByte((byte)1));
        SNBTOperations.register("nickelTestDup",byteType,args -> new NBTTagByte((byte)2));
        final NBTBase[] args = {new NBTTagByte((byte)0)};
        final SNBTOperation op = SNBTOperations.resolve("nickelTestDup",args);
        Assertions.assertNotNull(op);
        Assertions.assertEquals((byte)1,((NBTTagByte)op.invoke(args)).getByte(),"应保留首次登记的函数");
    }

    /**
     * Claude Generated
     * 测试同名不同参数类型的函数可共存，并按继承距离正确分派
     */
    @Test
    public void overloadCoexistDispatchTest() throws NickelRuntimeException {
        SNBTOperations.register("nickelTestNear",type(NBTTagByte.class),args -> new NBTTagByte((byte)1));
        SNBTOperations.register("nickelTestNear",type(net.minecraft.nbt.NBTPrimitive.class),args -> new NBTTagByte((byte)2));

        final NBTBase[] byteArg = {new NBTTagByte((byte)0)};
        final SNBTOperation exact = SNBTOperations.resolve("nickelTestNear",byteArg);
        Assertions.assertNotNull(exact);
        Assertions.assertEquals((byte)1,((NBTTagByte)exact.invoke(byteArg)).getByte(),"BYTE 实参应命中精确重载");

        final NBTBase[] intArg = {new NBTTagInt(0)};
        final SNBTOperation loose = SNBTOperations.resolve("nickelTestNear",intArg);
        Assertions.assertNotNull(loose);
        Assertions.assertEquals((byte)2,((NBTTagByte)loose.invoke(intArg)).getByte(),"INT 实参应命中 NUMBER 重载");
    }

    /**
     * Claude Generated
     * 测试 loadFuncs 包装的函数体抛出的 NickelRuntimeException 原样穿透
     */
    @Test
    public void nickelExceptionPassThroughTest(){
        final NBTBase[] args = {new NBTTagString("方")};
        final SNBTOperation op = SNBTOperations.resolve("nickelTestThrower",args);
        Assertions.assertNotNull(op);
        Assertions.assertThrows(
                NickelRuntimeException.class,
                () -> op.invoke(args)
        );
    }

    /**
     * Claude Generated
     * 测试函数体抛出的受检异常被包装为 RuntimeException，原异常作为 cause
     */
    @Test
    public void checkedExceptionWrappedTest(){
        final NBTBase[] args = {new NBTTagByte((byte)1)};
        final SNBTOperation op = SNBTOperations.resolve("nickelTestChecked",args);
        Assertions.assertNotNull(op);
        final RuntimeException e = Assertions.assertThrows(
                RuntimeException.class,
                () -> op.invoke(args)
        );
        Assertions.assertTrue(e.getCause() instanceof IOException,"cause 应是原受检异常");
    }

    /**
     * Claude Generated
     * 测试 signatureOf(operation) 返回登记时的签名，未登记的 operation 抛 IllegalArgumentException
     */
    @Test
    public void signatureOfOperationTest(){
        final SNBTOperation bool = SNBTOperations.resolve("bool",new NBTBase[]{new NBTTagInt(1)});
        Assertions.assertNotNull(bool);
        Assertions.assertEquals("bool(NUMBER)",SNBTOperations.signatureOf(bool));

        final SNBTOperation unregistered = args -> new NBTTagByte((byte)0);
        Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> SNBTOperations.signatureOf(unregistered)
        );
    }

    /**
     * Claude Generated
     * 测试 signatureOf(name,args) 按实参运行时类型拼出签名文本
     */
    @Test
    public void signatureOfNameArgsTest(){
        Assertions.assertEquals("f(BYTE,STRING)",
                SNBTOperations.signatureOf("f",Arrays.asList(new NBTTagByte((byte)1),new NBTTagString("x"))));
        Assertions.assertEquals("g()",SNBTOperations.signatureOf("g",Arrays.<NBTBase>asList()));
    }

    /**
     * 正常登记路径的测试 provider（Claude Generated）
     */
    public static final class GoodProvider {
        private GoodProvider(){}

        @Nonnull
        @SNBTFunction
        public static NBTTagByte nickelTestDefaultName(final @Nonnull NBTTagByte b){
            return new NBTTagByte((byte)(b.getByte()+1));
        }

        @Nonnull
        @SNBTFunction(name = "nickelTestAlias")
        public static NBTTagInt renamedMethod(final @Nonnull NBTTagInt i){
            return new NBTTagInt(i.getInt()*2);
        }

        @Nonnull
        public static NBTTagByte nickelTestNotAnnotated(final @Nonnull NBTTagByte b){
            return b;
        }

        @Nonnull
        @SNBTFunction
        public static NBTTagByte nickelTestThrower(final @Nonnull NBTTagString msg) throws NickelRuntimeException {
            throw new NickelRuntimeException(Texts.plain(msg.getString()));
        }

        @Nonnull
        @SNBTFunction
        public static NBTTagByte nickelTestChecked(final @Nonnull NBTTagByte b) throws Exception {
            throw new IOException("圆");
        }
    }

    /**
     * 各种非法形态 provider（Claude Generated）：非 public/非 static/未注解/返回类型非 NBTBase/
     * 参数类型非 NBTBase 的均被 loadFuncs 跳过，不被登记（非 public、返回类型、参数类型不符的带 warn 日志）
     */
    public static final class BadProvider {
        private BadProvider(){}

        @Nonnull
        @SNBTFunction
        @SuppressWarnings("unused")
        private static NBTTagByte nickelTestPrivate(final @Nonnull NBTTagByte b){
            return b;
        }

        @Nonnull
        @SNBTFunction
        @SuppressWarnings("unused")
        public NBTTagByte nickelTestInstance(final @Nonnull NBTTagByte b){
            return b;
        }

        @Nonnull
        @SNBTFunction
        public static NBTTagByte nickelTestBadParam(final @Nonnull String s){
            return new NBTTagByte((byte)0);
        }

        @Nonnull
        @SNBTFunction
        public static String nickelTestBadReturn(final @Nonnull NBTTagByte b){
            return "";
        }
    }
}
