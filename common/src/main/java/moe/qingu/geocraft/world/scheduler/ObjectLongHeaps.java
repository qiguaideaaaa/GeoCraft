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
/*
 * This file contains code derived from fastutil (https://fastutil.di.unimi.it/)
 * Original class: it.unimi.dsi.fastutil.objects.ObjectHeaps (partial)
 * Copyright (C) 2003-2024 Paolo Boldi and Sebastiano Vigna
 * Licensed under the Apache License, Version 2.0
 *
 * Modifications: To dual-array (Object & long)
 *
 * 本文件包含源自 fastutil (https://fastutil.di.unimi.it/) 的代码
 * 原始类：it.unimi.dsi.fastutil.objects.ObjectHeaps（部分）
 * 版权所有 (C) 2003-2024 Paolo Boldi 与 Sebastiano Vigna
 * 根据 Apache 许可证第 2.0 版许可
 *
 * 修改内容：改为对象和长整型双数组实现
 */

package moe.qingu.geocraft.world.scheduler;

import javax.annotation.Nonnull;

/**
 * @author QGMoe
 */
@SuppressWarnings("UnusedReturnValue")
public final class ObjectLongHeaps {

    private ObjectLongHeaps(){}

    public static <K> int downHeap(final @Nonnull K[] heap,
                                   final @Nonnull long[] additions,
                                   final int size,
                                   int i,
                                   final @Nonnull BiComparator<? super K> c) {
        assert i < size;
        final K e = heap[i];
        final long extE = additions[i];
        int child;
        while ((child = (i << 1) + 1) < size) {
            K t = heap[child];
            long extT = additions[child];
            final int right = child + 1;
            if (right < size && c.compare(heap[right],additions[right], t,extT) < 0){
                t = heap[child = right];
                extT = additions[child];
            }
            if (c.compare(e, extE, t, extT) <= 0) break;
            heap[i] = t;
            additions[i] = extT;
            i = child;
        }
        heap[i] = e;
        additions[i] = extE;
        return i;
    }

    public static <K> int upHeap(final @Nonnull K[] heap,
                                 final @Nonnull long[] additions,
                                 final int size,
                                 int i,
                                 final @Nonnull BiComparator<K> c) {
        assert i < size;
        final K e = heap[i];
        final long extE = additions[i];
        while (i != 0) {
            final int parent = (i - 1) >>> 1;
            final K t = heap[parent];
            final long extT = additions[parent];
            if (c.compare(t, extT, e, extE) <= 0) break;
            heap[i] = t;
            additions[i] = additions[parent];
            i = parent;
        }
        heap[i] = e;
        additions[i] = extE;
        return i;
    }

    public static <K> void makeHeap(final @Nonnull K[] heap,
                                    final @Nonnull long[] additions,
                                    final int size,
                                    final @Nonnull BiComparator<? super K> c) {
        int i = size >>> 1;
        while (i-- != 0) downHeap(heap, additions, size, i, c);
    }

    public static abstract class BiComparator<K>{
        public abstract int compare(final @Nonnull K k1,final long l1,final @Nonnull K k2,final long l2);
    }
}
