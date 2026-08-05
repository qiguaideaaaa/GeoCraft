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

package moe.qingu.orbtellus.world.scheduler;

import moe.qingu.orbtellus.api.world.tick.IScheduledTick;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import org.junit.jupiter.api.Assertions;
import 清汩萌.天圆地方.天圆地方测试;
import 清汩萌.造.空间.空间工具;
import 清汩萌.造.空间.空间构造器;
import 清汩萌.造.管理.空间构造局;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author QGMoe
 */
public final class 计划刻数据 {
    public String upon = 天圆地方测试.MODID+":scheduler";
    public long time = 0L;
    public int[] $原点;
    public boolean $使用网格坐标 = false;
    public List<可测试的计划刻> ticks = Collections.emptyList();
    private BlockPos $基点;

    public void 初始化(){
        if($使用网格坐标 & $原点 != null) $原点 = 空间工具.转换为游戏坐标($原点);
        if($原点 == null) $原点 = new int[]{0,0,0};
        this.$基点 = new BlockPos($原点[0], $原点[1], $原点[2]);
        final 空间构造器 $空间构造器 = 空间构造局.需要(new ResourceLocation(upon));
        for(final 可测试的计划刻 $刻:ticks) $刻.初始化($空间构造器, $原点, $使用网格坐标);
    }

    public void 假设相等(final @Nonnull List<? extends IScheduledTick> $实际){
        outer:
        while (!$实际.isEmpty()){
            final IScheduledTick tick = $实际.remove($实际.size()-1);
            final Iterator<可测试的计划刻> iterator = ticks.iterator();
            while (iterator.hasNext()){
                final 可测试的计划刻 $计划刻 = iterator.next();
                if(可测试的计划刻.严格相等($计划刻,tick)){
                    iterator.remove();
                    continue outer;
                }
            }
            Assertions.fail("出现不存在于答案的计划刻："+tick);
        }
        if(!ticks.isEmpty()) Assertions.fail("出现不存在于输出的计划刻："+ticks);
    }

    @Nonnull
    public BlockPos 获取基点() {
        return $基点;
    }

    /*  ---------------------------
             Setter 用于 YAML
        --------------------------- */

    public void set原点(final @Nonnull int[] $原点) {
        this.$原点 = $原点;
    }

    public void set使用网格坐标(final boolean $网格坐标) {
        this.$使用网格坐标 = $网格坐标;
    }
}
