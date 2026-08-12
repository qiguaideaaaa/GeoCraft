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

package moe.qingu.orbtellus.api.fluid.unit;

import moe.qingu.orbtellus.api.util.APIMathUtil;

import javax.annotation.Nonnull;
import java.util.Random;

/**
 * @author QGMoe
 */
public enum FluidUnit {
    BUCKET {
        @Override
        public final long toBucket(final long amount) {
            return amount;
        }

        @Override
        public final long toQuanta(final long amount) {
            return amount << 3;
        }

        @Override
        public final long toMillibucket(final long amount) {
            return amount * MillibucketUnit.BUCKET_VOLUME;
        }

        @Override
        public final long toQB(final long amount) {
            return amount * QBUnit.BUCKET_VOLUME;
        }

        @Override
        public final double toFractionalBucket(final long amount) {
            return (double) amount;
        }

        @Override
        public final double toFractionalQuanta(final long amount) {
            return amount * QuantaUnit.BUCKET_VOLUME_DOUBLE;
        }

        @Override
        public final double toFractionalMillibucket(final long amount) {
            return amount * MillibucketUnit.BUCKET_VOLUME_DOUBLE;
        }

        @Override
        public final double toFractionalQB(final long amount) {
            return amount * QBUnit.BUCKET_VOLUME_DOUBLE;
        }

        @Override
        public final long sampleBucket(@Nonnull final Random rand,final long amount) {
            return amount;
        }

        @Override
        public final long sampleQuanta(@Nonnull final Random rand,final long amount) {
            return toQuanta(amount);
        }

        @Override
        public final long sampleMillibucket(@Nonnull final Random rand,final long amount) {
            return toMillibucket(amount);
        }

        @Override
        public final long sampleQB(@Nonnull final Random rand,final long amount) {
            return toQB(amount);
        }
    },
    QUANTA{
        @Override
        public final long toBucket(final long amount) {
            return amount >> 3;
        }

        @Override
        public final long toQuanta(final long amount) {
            return amount;
        }

        @Override
        public final long toMillibucket(final long amount) {
            return amount * MillibucketUnit.QUANTA_VOLUME_INT;
        }

        @Override
        public final long toQB(final long amount) {
            return amount * QBUnit.QUANTA_VOLUME;
        }

        @Override
        public final double toFractionalBucket(final long amount) {
            return amount / QuantaUnit.BUCKET_VOLUME_DOUBLE;
        }

        @Override
        public final double toFractionalQuanta(final long amount) {
            return (double) amount;
        }

        @Override
        public final double toFractionalMillibucket(final long amount) {
            return amount * MillibucketUnit.QUANTA_VOLUME_DOUBLE;
        }

        @Override
        public final double toFractionalQB(final long amount) {
            return amount * QBUnit.QUANTA_VOLUME_DOUBLE;
        }

        @Override
        public final long sampleBucket(@Nonnull final Random rand,final long amount) {
            return sample(rand, amount, QuantaUnit.BUCKET_VOLUME);
        }

        @Override
        public final long sampleQuanta(@Nonnull final Random rand,final long amount) {
            return amount;
        }

        @Override
        public final long sampleMillibucket(@Nonnull final Random rand,final long amount) {
            return toMillibucket(amount);
        }

        @Override
        public final long sampleQB(@Nonnull final Random rand,final long amount) {
            return toQB(amount);
        }
    },
    MILLIBUCKET{
        @Override
        public final long toBucket(final long amount) {
            return Math.floorDiv(amount, MillibucketUnit.BUCKET_VOLUME);
        }

        @Override
        public long toQuanta(final long amount) {
            return Math.floorDiv(amount, MillibucketUnit.QUANTA_VOLUME);
        }

        @Override
        public long toMillibucket(final long amount) {
            return amount;
        }

        @Override
        public long toQB(final long amount) {
            return amount * QBUnit.MILLIBUCKET_VOLUME;
        }

        @Override
        public final double toFractionalBucket(final long amount) {
            return amount / MillibucketUnit.BUCKET_VOLUME_DOUBLE;
        }

        @Override
        public final double toFractionalQuanta(final long amount) {
            return amount / MillibucketUnit.QUANTA_VOLUME_DOUBLE;
        }

        @Override
        public final double toFractionalMillibucket(final long amount) {
            return (double) amount;
        }

        @Override
        public final double toFractionalQB(final long amount) {
            return amount * QBUnit.MILLIBUCKET_VOLUME_DOUBLE;
        }

        @Override
        public final long sampleBucket(@Nonnull final Random rand,final long amount) {
            return sample(rand, amount, MillibucketUnit.BUCKET_VOLUME);
        }

        @Override
        public final long sampleQuanta(@Nonnull final Random rand,final long amount) {
            return sample(rand, amount, MillibucketUnit.QUANTA_VOLUME);
        }

        @Override
        public final long sampleMillibucket(@Nonnull final Random rand,final long amount) {
            return amount;
        }

        @Override
        public final long sampleQB(@Nonnull final Random rand,final long amount) {
            return toQB(amount);
        }
    },
    QB{
        @Override
        public final long toBucket(final long amount) {
            return Math.floorDiv(amount, QBUnit.BUCKET_VOLUME);
        }

        @Override
        public final long toQuanta(final long amount) {
            return Math.floorDiv(amount, QBUnit.QUANTA_VOLUME);
        }

        @Override
        public final long toMillibucket(final long amount) {
            return Math.floorDiv(amount, QBUnit.MILLIBUCKET_VOLUME);
        }

        @Override
        public final long toQB(final long amount) {
            return amount;
        }

        @Override
        public final double toFractionalBucket(final long amount) {
            return amount / QBUnit.BUCKET_VOLUME_DOUBLE;
        }

        @Override
        public final double toFractionalQuanta(final long amount) {
            return amount / QBUnit.QUANTA_VOLUME_DOUBLE;
        }

        @Override
        public final double toFractionalMillibucket(final long amount) {
            return amount/ QBUnit.MILLIBUCKET_VOLUME_DOUBLE;
        }

        @Override
        public final double toFractionalQB(final long amount) {
            return (double) amount;
        }

        @Override
        public final long sampleBucket(@Nonnull final Random rand,final long amount) {
            return sample(rand, amount, QBUnit.BUCKET_VOLUME);
        }

        @Override
        public final long sampleQuanta(@Nonnull final Random rand,final long amount) {
            return sample(rand, amount, QBUnit.QUANTA_VOLUME);
        }

        @Override
        public final long sampleMillibucket(@Nonnull final Random rand,final long amount) {
            return sample(rand, amount, QBUnit.MILLIBUCKET_VOLUME);
        }

        @Override
        public final long sampleQB(@Nonnull final Random rand,final long amount) {
            return amount;
        }
    };

    public abstract long toBucket(final long amount);

    public abstract long toQuanta(final long amount);

    public abstract long toMillibucket(final long amount);

    public abstract long toQB(final long amount);

    public abstract double toFractionalBucket(final long amount);

    public abstract double toFractionalQuanta(final long amount);

    public abstract double toFractionalMillibucket(final long amount);

    public abstract double toFractionalQB(final long amount);

    public abstract long sampleBucket(final @Nonnull Random rand, final long amount);

    public abstract long sampleQuanta(final @Nonnull Random rand, final long amount);

    public abstract long sampleMillibucket(final @Nonnull Random rand, final long amount);

    public abstract long sampleQB(final @Nonnull Random rand, final long amount);

    public final int toBucketAsInt(final long amount){
        return (int) toBucket(amount);
    }

    public final int toQuantaAsInt(final long amount){
        return (int) toQuanta(amount);
    }

    public final int toMillibucketAsInt(final long amount){
        return (int) toMillibucket(amount);
    }

    public final int sampleBucketAsInt(final @Nonnull Random rand, final long amount){
        return (int) sampleBucket(rand,amount);
    }

    public final int sampleQuantaAsInt(final @Nonnull Random rand, final long amount){
        return (int) sampleQuanta(rand, amount);
    }

    public final int sampleMillibucketAsInt(final @Nonnull Random rand, final long amount){
        return (int) sampleMillibucket(rand,amount);
    }

    public static long sample(final @Nonnull Random rand, final long amount, final long granularity){
        final long res = Math.floorDiv(amount, granularity);
        final long extra = Math.floorMod(amount, granularity);
        if (extra > 0L && APIMathUtil.nextLong(rand,granularity) < extra) return res + 1L;
        return res;
    }
}
