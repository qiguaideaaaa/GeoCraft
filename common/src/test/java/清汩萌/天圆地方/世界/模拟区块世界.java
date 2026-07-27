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

package 清汩萌.天圆地方.世界;

import net.minecraft.profiler.Profiler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.storage.WorldInfo;
import 清汩萌.天圆地方.世界.区块.模拟区块提供器;

import javax.annotation.Nonnull;

/**
 * @author QGMoe
 */
public class 模拟区块世界 extends 模拟世界 {
    protected 模拟区块世界(final @Nonnull WorldInfo info,
                           final @Nonnull WorldProvider providerIn,
                           final @Nonnull Profiler profilerIn,
                           final boolean client) {
        super(info, providerIn, profilerIn, client);
        this.chunkProvider = createChunkProvider();
    }

    @Nonnull
    public static 模拟区块世界 构建(final @Nonnull WorldInfo info, final boolean isClient){
        return 构建(info,new MockWorldProvider(),isClient);
    }

    @Nonnull
    public static 模拟区块世界 构建(final @Nonnull WorldInfo info, final @Nonnull MockWorldProvider provider, final boolean isClient){
        return new 模拟区块世界(
                info,
                provider,
                new Profiler(),
                isClient
        );
    }

    @Nonnull
    @Override
    protected 模拟区块提供器 createChunkProvider() {
        return new 模拟区块提供器(this);
    }

    @Nonnull
    @Override
    public 模拟区块提供器 getChunkProvider() {
        return (模拟区块提供器) this.chunkProvider;
    }

    @Override
    protected boolean isChunkLoaded(final int x,final int z,final boolean allowEmpty) {
        return true;
    }

    @Override
    public boolean checkLight(final @Nonnull BlockPos pos) {
        return false;
    }
}
