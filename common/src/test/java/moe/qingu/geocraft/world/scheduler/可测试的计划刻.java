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

import moe.qingu.geocraft.api.world.tick.IScheduledTick;
import moe.qingu.geocraft.api.world.tick.TickPriority;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import 清汩萌.造.空间.空间构造器;
import 清汩萌.造.词块.词块;

import javax.annotation.Nonnull;

/**
 * @author QGMoe
 */
public final class 可测试的计划刻 implements IScheduledTick {
    public int x;
    public int y;
    public int z;
    public String 块;
    public long 时;
    public 计划刻等级 级 = 计划刻等级.中上7;

    private BlockPos pos;
    private Block block;
    private 词块 $方块;

    public void 初始化(final @Nonnull 空间构造器 $空间构造器){
        this.pos = new BlockPos(x,y,z);
        block = $空间构造器.进行映射(词块.of(块)).getBlock();
        this.$方块 = $空间构造器.进行映射(block.getDefaultState());
    }

    @Nonnull
    @Override
    public BlockPos pos() {
        return pos;
    }

    @Nonnull
    @Override
    public Block block() {
        return block;
    }

    @Override
    public long triggeredTick() {
        return 时;
    }

    @Nonnull
    @Override
    public TickPriority priority() {
        return 级.as;
    }

    @Override
    public String toString() {
        return "#" + $方块 + '[' +
                "x:" + x + ',' +
                "y:" + y + ',' +
                "z:" + z + ',' +
                "p:" + 级 + ',' +
                "t:" + 时 + ']';
    }

    public static boolean 严格相等(final @Nonnull IScheduledTick a,final @Nonnull IScheduledTick b){
        return a.block() == b.block() && a.pos().equals(b.pos()) && a.triggeredTick() == b.triggeredTick() && a.priority() == b.priority();
    }
}
