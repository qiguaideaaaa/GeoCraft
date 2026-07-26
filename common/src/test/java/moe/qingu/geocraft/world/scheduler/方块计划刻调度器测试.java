/*
 * Copyright 2026 QGMoe
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
 * 版权所有 2026 QGMoe
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

package moe.qingu.geocraft.world.scheduler;

import moe.qingu.geocraft.handler.CapabilityHandler;
import net.minecraftforge.common.capabilities.CapabilityCreator;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import 清汩萌.天圆地方.原料.方块原料;
import 清汩萌.天圆地方.天圆地方测试;
import 清汩萌.造.空间.空间构造器;
import 清汩萌.造.空间.词块网格;

import javax.annotation.Nonnull;

/**
 * @author QGMoe
 */
@ExtendWith(方块计划刻调度器测试.测试环境准备.class)
public class 方块计划刻调度器测试 extends 天圆地方测试 {

    @Nonnull
    @SuppressWarnings("DataFlowIssue")
    public static 空间构造器 获取或用默认构造器(final @Nonnull 词块网格 $网格){
        return $网格.获取当前构造器() == null? 方块原料.方块计划刻测试方块._方块计划刻测试构造器_ :$网格.获取当前构造器();
    }

    static final class 测试环境准备 implements BeforeAllCallback {

        @Override
        public void beforeAll(final @Nonnull ExtensionContext context) throws Exception {
            run(测试环境准备.class.getName(),"setup");
        }

        @SuppressWarnings("unused")
        public static void setup(){
            LOGGER.info("加载可测试计划刻的SNBT辅助函数中");
            可测试的计划刻.加载SNBT辅助函数();
            LOGGER.info("准备Capability");
            CapabilityHandler.CHUNKY_BLOCK_TICK_DATUM = CapabilityCreator.create("chunkyBlockTickDatum",null,null);
        }
    }
}
