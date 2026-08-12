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

package moe.qingu.orbtellus.api.fluid;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.Objects;

import static moe.qingu.orbtellus.api.OrbTellusAPI.LOGGER;

/**
 * @author QGMoe
 */
public class QBFluidStack{ //unfinished todo
    private final @Nonnull Fluid fluid;
    public @Nullable NBTTagCompound tag;
    public long amount;

    public QBFluidStack(final @Nonnull Fluid fluid,final long amount) {
        if (!FluidRegistry.isFluidRegistered(Objects.requireNonNull(fluid))) {
            LOGGER.error("Cannot create a QBFluidStack for an unregistered Fluid {} (from {}).",fluid.getName(),fluid.getClass());
            throw new IllegalArgumentException("Cannot create a QBFluidStack from an unregistered fluid.");
        }
        this.fluid = fluid;
        this.amount = amount;
    }

    public QBFluidStack(final @Nonnull Fluid fluid,final long amount,final @Nullable NBTTagCompound nbt) {
        this(fluid, amount);

        if (nbt != null) tag = nbt.copy();
    }

    @Nonnull
    public Fluid getFluid() {
        return fluid;
    }

    @Nonnull
    public QBFluidStack copy(){
        return new QBFluidStack(this.fluid,this.amount,this.tag);
    }
}
