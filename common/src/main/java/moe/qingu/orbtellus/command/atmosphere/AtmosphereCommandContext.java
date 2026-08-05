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

package moe.qingu.orbtellus.command.atmosphere;

import moe.qingu.nickel.command.context.ExecuteContext;
import net.minecraft.command.CommandException;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.util.math.BlockPos;
import moe.qingu.orbtellus.api.atmosphere.Atmosphere;
import moe.qingu.orbtellus.api.atmosphere.accessor.IAtmosphereAccessor;
import moe.qingu.orbtellus.api.atmosphere.layer.Layer;
import moe.qingu.orbtellus.api.state.FluidState;
import moe.qingu.orbtellus.api.state.GeographyState;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static moe.qingu.orbtellus.command.GeoArguments.MULTIPLY;
import static moe.qingu.orbtellus.command.GeoArguments.POS;

/**
 * @author QGMoe
 */
public class AtmosphereCommandContext {
    public static final String ACCESSOR = "accessor";
    public static final String ATMOSPHERE = "atmosphere";
    public static final String LAYER = "layer";
    public final IAtmosphereAccessor accessor;
    public final Atmosphere atmosphere;
    public final Layer layer;
    public final BlockPos pos;
    public final int x;
    public final int y;
    public final int z;
    public final double multiply;
    public final ExecuteContext execute;
    public CommandException exception;

    AtmosphereCommandContext(final @Nonnull ExecuteContext execute) {
        this.accessor = execute.get(ACCESSOR);
        this.atmosphere = execute.get(ATMOSPHERE);
        this.layer = execute.get(LAYER);
        this.pos = execute.getBlockPos(POS);
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        final @Nullable Object multiply = execute.getContexts().get(MULTIPLY);
        if (!(multiply instanceof Double)) this.multiply = 1d;
        else this.multiply = (double) multiply;
        this.execute = execute;
    }

    public boolean isNull(final @Nullable GeographyState state){
        if(state == null){
            this.exception = new CommandException("geocraft.command.atmosphere.property.null");
            return true;
        }else return false;
    }

    public boolean fillFailed(final @Nonnull FluidState state, final @Nonnull Double value){
        if(state.fill(value.intValue(),true) == 0){
            this.exception = new NumberInvalidException("commands.generic.num.tooSmall", value, -state.getFluidAmount());
            return true;
        }else return false;
    }
}
