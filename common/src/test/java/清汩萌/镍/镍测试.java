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

package 清汩萌.镍;

import moe.qingu.nickel.nbt.operation.SNBTOperations;
import moe.qingu.nickel.nbt.path.method.NBTPathMethods;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;

import javax.annotation.Nonnull;

/**
 * @author QGMoe, Claude
 */
@ExtendWith(镍测试.SetupNickelTestExtension.class)
public class 镍测试 {
    public static final String 镍ID = "镍";
    public static final Logger 镍日志 = LogManager.getLogger("NickelTest");

    /**
     * Claude Generated
     * 幂等加载 NBTPathMethods 与 SNBTOperations 两个内置注册表（全局状态，重复 loadFuncs 触发重复签名日志；
     * 两表须一并加载，漏一个会把函数误报为未注册。生产环境由 FML scanProviders 加载，测试环境无 FML）
     */
    public static void loadRegistries(){
        if(NBTPathMethods.resolveMethod("values",new NBTBase[0]) == null)
            NBTPathMethods.loadFuncs(NBTPathMethods.class);
        if(SNBTOperations.resolve("bool",new NBTBase[]{new NBTTagByte((byte)1)}) == null)
            SNBTOperations.loadFuncs(SNBTOperations.class);
    }

    /**
     * Claude Generated
     * JUnit 扩展：测试类运行前加载内置注册表，依赖注册表的测试类继承 {@link 镍测试} 即可获得
     */
    public static final class SetupNickelTestExtension implements BeforeAllCallback {

        @Override
        public void beforeAll(final @Nonnull ExtensionContext context) {
            loadRegistries();
        }
    }
}
