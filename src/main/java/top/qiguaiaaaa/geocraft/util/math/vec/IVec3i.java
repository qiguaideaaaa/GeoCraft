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

package top.qiguaiaaaa.geocraft.util.math.vec;

import top.qiguaiaaaa.geocraft.util.math.Int10;
import top.qiguaiaaaa.geocraft.util.math.Int21;

import javax.annotation.Nonnull;

import static top.qiguaiaaaa.geocraft.util.math.Int10.toInt10;
import static top.qiguaiaaaa.geocraft.util.math.Int21.toInt21;

/**
 * 这里i是整数的意思,不是整形的意思
 * @author QiguaiAAAA
 */
public interface IVec3i {
    int X_INT_OFFSET = 20, Y_INT_OFFSET = 10, Z_INT_OFFSET = 0,
    X_LONG_OFFSET = 42, Y_LONG_OFFSET = 21, Z_LONG_OFFSET=0;
    int X_INT_MASK = Int10.ALL_MASK<< X_INT_OFFSET,
    Y_INT_MASK = Int10.ALL_MASK<< Y_INT_OFFSET,
    Z_INT_MASK = Int10.ALL_MASK<< Z_INT_OFFSET;
    long X_LONG_MASK = Int21.ALL_MASK<<X_LONG_OFFSET,
    Y_LONG_MASK = Int21.ALL_MASK << Y_LONG_OFFSET,
    Z_LONG_MASK = Int21.ALL_MASK<<Z_LONG_OFFSET;
    int getX();
    int getY();
    int getZ();

    @Nonnull
    IVec3i toImmutable();

    default int toInt() {
        return toInt10(getX())<< X_INT_OFFSET | toInt10(getY())<< Y_INT_OFFSET | toInt10(getZ());
    }

    default long toLong(){
        return toInt21(getX()) <<X_LONG_OFFSET | toInt21(getY()) <<Y_LONG_OFFSET | toInt21(getZ());
    }
}
