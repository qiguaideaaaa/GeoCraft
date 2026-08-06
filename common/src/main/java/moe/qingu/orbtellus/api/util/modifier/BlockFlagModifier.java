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

package moe.qingu.orbtellus.api.util.modifier;

/**
 * 修改器，用 long 表示，避免对象开销
 * @author QGMoe
 * @since API-0.3.5
 */
public final class BlockFlagModifier {
    public static final long DISABLED_FLAGS_MASK = 0xffffffffL;

    private BlockFlagModifier(){}

    /**
     * 从传入的 blockFlags 创建一个将输入的 blockFlags 修改为恰为指定的 blockFlags 的方块 flag 修改器
     * @param blockFlags 指定的 block flags
     * @return 一个用 long 表示的修改器
     */
    public static long of(final int blockFlags){
        return ((long) blockFlags<<32) | ~blockFlags;
    }

    public static long build(final int enabledFlags){
        return (long) enabledFlags << 32;
    }

    public static long build(final int enabledFlags,final int disabledFlags){
        return ((long)enabledFlags << 32) | disabledFlags;
    }

    public static long enableFor(final long modifier, final int enabledFlags){
        return modifier | ((long) enabledFlags <<32);
    }

    public static long disableFor(final long modifier, final int disabledFlags){
        return modifier | disabledFlags;
    }

    public static int modify(final int flags, final long modifier){
        return (int)((flags | (modifier>>>32)) & ~(modifier&DISABLED_FLAGS_MASK));
    }
}
