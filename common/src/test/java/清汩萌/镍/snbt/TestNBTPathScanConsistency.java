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

import moe.qingu.nickel.command.exception.NickelScanEOFSignal;
import net.minecraft.command.CommandException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.stream.Stream;

import 清汩萌.镍.镍测试;

/**
 * NBTPathReader 与 NBTPathScanner 对同一输入的结论对照测试。
 * 已知的不一致在数据文件的"注"字段记录并为预期行为；数据在 data/nickel/snbt/nbtpath/读扫一致性.yaml。
 * @author Claude
 * @see moe.qingu.nickel.nbt.path.NBTPathReader
 * @see moe.qingu.nickel.nbt.path.NBTPathScanner
 */
public final class TestNBTPathScanConsistency extends 镍测试 {
    public static final @Nonnull String DATA_DIR = "data/nickel/snbt/nbtpath/";

    /**
     * Claude Generated
     * 同一输入分别过读者与扫描器，分别断言期望结论并在两侧结论不一致时打日志
     */
    @ParameterizedTest
    @MethodSource("pullDataForConsistency")
    public void consistencyTest(final @Nonnull ConsistencyCase c){
        final String readerVerdict = runReader(c.input);
        final String scannerVerdict = runScanner(c.input);
        镍测试._镍日志_.info("一致性用例[{}] 输入=<{}> 读者={} 扫描={}{}",
                c.name,c.input,readerVerdict,scannerVerdict,
                readerVerdict.equals(scannerVerdict)?"":"（结论不一致"+(c.note==null?"":"："+c.note)+"）");
        Assertions.assertEquals(c.reader,readerVerdict,"用例 "+c.name+" 读者结论");
        Assertions.assertEquals(c.scanner,scannerVerdict,"用例 "+c.name+" 扫描结论");
        if(!readerVerdict.equals(scannerVerdict))
            Assertions.assertNotNull(c.note,"用例 "+c.name+" 读扫结论不一致但数据未在注中说明"); //新的不一致必须显式记录
    }

    @Nonnull
    private static String runReader(final @Nonnull String input){
        try{
            NBTPathTestSupport.parse(input);
            return "通过";
        }catch (final CommandException e){
            return "报错";
        }
    }

    @Nonnull
    private static String runScanner(final @Nonnull String input){
        try{
            NBTPathTestSupport.scan(input);
            return "通过";
        }catch (final NickelScanEOFSignal e){
            return "信号";
        }catch (final CommandException e){
            return "报错";
        }
    }

    public static @Nonnull Stream<ConsistencyCase> pullDataForConsistency(){
        return NBTPathTestSupport.loadYamlCases(DATA_DIR,"读扫一致性.yaml").stream().map(ConsistencyCase::new);
    }

    /**
     * 读扫一致性用例数据类。
     * @author Claude
     */
    public static final class ConsistencyCase {
        final @Nonnull String name;
        final @Nonnull String input;
        final @Nonnull String reader;
        final @Nonnull String scanner;
        final @Nullable String note;

        ConsistencyCase(final @Nonnull Map<String,Object> raw){
            final String n = NBTPathTestSupport.str(raw,"名");
            this.name = n == null?"未命名":n;
            final String in = NBTPathTestSupport.str(raw,"输入");
            this.input = in == null?"":in;
            final String r = NBTPathTestSupport.str(raw,"读者");
            final String s = NBTPathTestSupport.str(raw,"扫描");
            if(r == null || s == null) throw new IllegalArgumentException("用例 "+name+" 缺少读者/扫描期望");
            this.reader = r;
            this.scanner = s;
            this.note = NBTPathTestSupport.str(raw,"注");
        }

        @Override
        public @Nonnull String toString() {
            return name;
        }
    }
}
