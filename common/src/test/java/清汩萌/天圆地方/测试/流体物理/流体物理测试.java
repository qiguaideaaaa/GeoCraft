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

package 清汩萌.天圆地方.测试.流体物理;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import moe.qingu.geocraft.configs.GeneralConfig;
import 清汩萌.天圆地方.原料.方块原料;
import 清汩萌.天圆地方.util.网格工具;
import 清汩萌.天圆地方.天圆地方测试;
import 清汩萌.天圆地方.世界.模拟沙盒世界;
import 清汩萌.天圆地方.世界.沙盒.数组沙盒;
import 清汩萌.天圆地方.世界.配置.模拟世界配置;
import 清汩萌.造.空间.空间构造器;
import 清汩萌.造.空间.词块网格;

import javax.annotation.Nonnull;
import java.lang.reflect.InvocationTargetException;

import static 清汩萌.天圆地方.原料.方块原料.常用.〇;
import static 清汩萌.天圆地方.原料.方块原料.常用.曜;

/**
 * @author QiguaiAAAA
 */
public class 流体物理测试 extends 天圆地方测试 {
    protected static 模拟沙盒世界 world;

    @BeforeEach
    public void beforeFluidPhysicsTest() throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        test();
    }

    @SuppressWarnings("unused")
    public static void beforeFluidPhysicsTest_Inner(){
        GeneralConfig.ENABLE_BLOCK_UPDATER.setValue(false);
        world = 模拟沙盒世界.构建(模拟世界配置.create(b-> b.withGameType(GameType.CREATIVE)), false);
        world.配置空气方块(〇);
    }

    @AfterEach
    public void afterFluidPhysicsTest() throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        test();
    }

    @SuppressWarnings("unused")
    public static void afterFluidPhysicsTest_Inner(){
        world = null;
        GeneralConfig.ENABLE_BLOCK_UPDATER.setValue(true);
    }

    @Nonnull
    public static 数组沙盒 initWorldSandbox(final @Nonnull Object[] $打包网格数据,
                                            final @Nonnull BlockPos beginPos){
        return initWorldSandbox(网格工具.恢复网格数据($打包网格数据),beginPos);
    }

    @Nonnull
    public static 数组沙盒 initWorldSandbox(final @Nonnull 词块网格 $网格,
                                            final @Nonnull BlockPos beginPos){
        天圆地方测试.LOGGER.info("begin pos {}",beginPos);
        return initWorldSandbox($网格);
    }

    @Nonnull
    public static 数组沙盒 initWorldSandbox(final @Nonnull 词块网格 $网格){
        final 空间构造器 $构造器 = 获取或用默认构造器($网格);
        final @Nonnull 数组沙盒 sandbox = new 数组沙盒($网格.构造($构造器));
        sandbox.setOuterBlock(曜);
        world.配置沙盒(sandbox);
        $构造器.打印(sandbox.getStructure());
        return sandbox;
    }

    @Nonnull
    public static 空间构造器 获取或用默认构造器(final @Nonnull 词块网格 $网格){
        return $网格.获取当前构造器() == null? 方块原料.原版流体._流体物理构造器_ :$网格.获取当前构造器();
    }
}
