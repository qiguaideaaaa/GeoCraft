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
import moe.qingu.nickel.command.exception.NickelScanEOFSignal;
import moe.qingu.nickel.nbt.SNBTScanner;
import moe.qingu.nickel.reader.InputReader;
import net.minecraft.command.CommandException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import javax.annotation.Nonnull;
import java.util.stream.Stream;

import 清汩萌.镍.镍测试;

/**
 * SNBT 语法扫描器（SNBTScanner）单元测试：正常走通 / EOF 信号 / 语法错误三态，
 * 以及 typed array（[B;/[I;/[L;）字面量扫描。
 * Claude Generated Tests
 * @author Claude
 * @see SNBTScanner
 */
public final class TestSNBTScanner {

    /**
     * Claude Generated
     * 按用例的 outcome 断言扫描结果：ok 不抛异常、eof 抛 NickelScanEOFSignal、error 抛 NickelRuntimeException
     */
    @ParameterizedTest
    @MethodSource("pullDataForScan")
    public void scanTest(final @Nonnull SNBTTestSupport.SNBTCase data){
        镍测试._镍日志_.info("SNBT scan case [{}] input={} outcome={}",data,data.input,data.outcome);
        Assertions.assertNotNull(data.outcome,"case ["+data+"] without outcome");
        switch (data.outcome){
            case "ok":{
                Assertions.assertDoesNotThrow(() -> scan(data),"Failed case: "+data);
                break;
            }
            case "eof":{
                Assertions.assertThrows(
                        NickelScanEOFSignal.class,
                        () -> scan(data),
                        "Failed case: "+data
                );
                break;
            }
            case "error":{
                Assertions.assertThrows(
                        NickelRuntimeException.class,
                        () -> scan(data),
                        "Failed case: "+data
                );
                break;
            }
            default:Assertions.fail("unknown outcome ["+data.outcome+"] in case: "+data);
        }
    }

    /**
     * Claude Generated
     * 提供扫描数据集（data/nickel/snbt/Scan/）
     */
    public static @Nonnull Stream<SNBTTestSupport.SNBTCase> pullDataForScan(){
        return SNBTTestSupport.loadCases("data/nickel/snbt/Scan/");
    }

    /**
     * Claude Generated
     * 按用例的 mode 选择扫描入口（compound=scanNBTFromInput，single=scanSingleNBTFromInput）
     */
    private static void scan(final @Nonnull SNBTTestSupport.SNBTCase data) throws CommandException, NickelScanEOFSignal {
        final InputReader input = SNBTTestSupport.newInput(data.input);
        if("single".equals(data.mode)) SNBTScanner.scanSingleNBTFromInput(input);
        else SNBTScanner.scanNBTFromInput(input);
    }

    /**
     * Claude Generated
     * 测试 64 层嵌套复合标签的扫描正常走通
     */
    @Test
    public void deepNestingScanTest(){
        final int depth = 64;
        final StringBuilder builder = new StringBuilder();
        for(int i=1;i<depth;i++) builder.append("{a:");
        builder.append("{v:1}");
        for(int i=1;i<depth;i++) builder.append('}');
        final String input = builder.toString();
        镍测试._镍日志_.info("deepNestingScanTest depth={} inputLength={}",depth,input.length());
        Assertions.assertDoesNotThrow(() -> SNBTScanner.scanNBTFromInput(SNBTTestSupport.newInput(input)));
    }

    /**
     * Claude Generated
     * 测试深层嵌套在中途截断时抛出 EOF 信号而非语法错误
     */
    @Test
    public void deepNestingTruncatedScanTest(){
        final int depth = 64;
        final StringBuilder builder = new StringBuilder();
        for(int i=0;i<depth;i++) builder.append("{a:");
        final String input = builder.toString();
        Assertions.assertThrows(
                NickelScanEOFSignal.class,
                () -> SNBTScanner.scanNBTFromInput(SNBTTestSupport.newInput(input))
        );
    }

    // ======================== typed array ========================

    /**
     * Claude Generated
     * 字节数组 [B;1b,2b] 的扫描正常走通
     */
    @Test
    public void typedByteArrayScanTest(){
        Assertions.assertDoesNotThrow(() -> SNBTScanner.scanNBTFromInput(SNBTTestSupport.newInput("{a:[B;1b,2b]}")));
    }

    /**
     * Claude Generated
     * 整数数组 [I;1,2,3] 的扫描正常走通
     */
    @Test
    public void typedIntArrayScanTest(){
        Assertions.assertDoesNotThrow(() -> SNBTScanner.scanNBTFromInput(SNBTTestSupport.newInput("{a:[I;1,2,3]}")));
    }
}
