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

package top.qiguaiaaaa.geocraft.util.math;

import org.apache.commons.lang3.Validate;

/**
 * 该类Copy自 {@link net.minecraft.util.BitArray}
 * 用于发往客户端时修改Chunk数据
 */
public class MixinUsageBitArray {
    protected final long[] longArray;
    protected final int bitsPerEntry;
    protected final long maxEntryValue;
    protected final int arraySize;

    public MixinUsageBitArray(int bitsPerEntryIn, int arraySizeIn,long[] longArray) {
        Validate.inclusiveBetween(1L, 32L, bitsPerEntryIn);
        this.arraySize = arraySizeIn;
        this.bitsPerEntry = bitsPerEntryIn;
        this.maxEntryValue = (1L << bitsPerEntryIn) - 1L;
        this.longArray = longArray;
    }

    public void setAt(int index, int value) {
        Validate.inclusiveBetween(0L, this.arraySize - 1, index);
        Validate.inclusiveBetween(0L, this.maxEntryValue, value);
        int i = index * this.bitsPerEntry;
        int j = i / 64;
        int k = ((index + 1) * this.bitsPerEntry - 1) / 64;
        int l = i % 64;
        this.longArray[j] = this.longArray[j] & ~(this.maxEntryValue << l) | ((long)value & this.maxEntryValue) << l;

        if (j != k) {
            int i1 = 64 - l;
            int j1 = this.bitsPerEntry - i1;
            this.longArray[k] = this.longArray[k] >>> j1 << j1 | ((long)value & this.maxEntryValue) >> i1;
        }
    }

    public int getAt(int index) {
        Validate.inclusiveBetween(0L, this.arraySize - 1, index);
        int i = index * this.bitsPerEntry;
        int j = i / 64;
        int k = ((index + 1) * this.bitsPerEntry - 1) / 64;
        int l = i % 64;

        if (j == k) {
            return (int)(this.longArray[j] >>> l & this.maxEntryValue);
        } else {
            int i1 = 64 - l;
            return (int)((this.longArray[j] >>> l | this.longArray[k] << i1) & this.maxEntryValue);
        }
    }

    public long[] getBackingLongArray() {
        return this.longArray;
    }

    public int size()
    {
        return this.arraySize;
    }
}
