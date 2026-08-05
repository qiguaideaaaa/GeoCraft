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

package moe.qingu.orbtellus.api.event.world;

import moe.qingu.orbtellus.api.world.tick.scheduler.BlockTickScheduler;
import moe.qingu.orbtellus.api.world.tick.validator.BlockTickValidator;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * @author QGMoe
 */
public class BlockTickSchedulerEvent extends Event {
    private final @Nonnull World world;

    public BlockTickSchedulerEvent(final @Nonnull World world) {
        this.world = world;
    }

    @Nonnull
    public World getWorld() {
        return world;
    }

    @HasResult
    @Cancelable
    public static class Create extends BlockTickSchedulerEvent {
        private Supplier<BlockTickScheduler> candidate;

        public Create(@Nonnull final World world) {
            super(world);
        }

        public final void setCandidate(final Supplier<BlockTickScheduler> candidate) {
            this.candidate = candidate;
        }

        @Nullable
        public Supplier<BlockTickScheduler> getCandidate() {
            return candidate;
        }
    }

    @HasResult
    public static class InitValidator extends BlockTickSchedulerEvent {
        private final BlockTickScheduler scheduler;
        private Supplier<BlockTickValidator> candidate;

        public InitValidator(final @Nonnull BlockTickScheduler scheduler) {
            super(scheduler.getWorld());
            this.scheduler = scheduler;
        }

        public void setCandidate(final Supplier<BlockTickValidator> candidate) {
            this.candidate = candidate;
        }

        @Nonnull
        public BlockTickScheduler getScheduler() {
            return scheduler;
        }

        @Nullable
        public Supplier<BlockTickValidator> getCandidate() {
            return candidate;
        }
    }
}
