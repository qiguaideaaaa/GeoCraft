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

import net.minecraft.command.CommandException;
import moe.qingu.orbtellus.api.atmosphere.layer.AtmosphereLayer;
import moe.qingu.orbtellus.api.state.FluidState;
import moe.qingu.orbtellus.geography.atmosphere.SurfaceAtmosphere;
import moe.qingu.orbtellus.util.BaseUtil;

import javax.annotation.Nonnull;
import java.lang.invoke.MethodHandles;
import java.util.Collections;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * @author QGMoe
 */
@SuppressWarnings({"unused","unchecked","DataFlowIssue"})
public final class AtmosphereSets {
    private static final MethodHandles.Lookup PERMISSION = MethodHandles.lookup();
    public static final Map<String, BiConsumer<AtmosphereCommandContext,Double>> SetConsumer = Collections.unmodifiableMap(
            (Map<String, BiConsumer<AtmosphereCommandContext,Double>>) (Map<?,?>) BaseUtil.getLambdasFrom(PERMISSION, AtmosphereSets.class, BiConsumer.class));

    private AtmosphereSets(){}

    public static void steam(final @Nonnull AtmosphereCommandContext context,final @Nonnull Double value){
        final FluidState steam = (context.layer instanceof AtmosphereLayer)?((AtmosphereLayer)context.layer).getSteam():null;
        if(context.isNull(steam)) return;
        steam.setAmount(value.intValue());
        context.execute.notifyCommandListener("geocraft.command.atmosphere.set.steam", context.x, context.y, context.z, value.intValue());
    }

    public static void water(final @Nonnull AtmosphereCommandContext context,final @Nonnull Double value){
        final FluidState water = context.layer.getWater();
        if(context.isNull(water)) return;
        water.setAmount(value.intValue());
        context.execute.notifyCommandListener("geocraft.command.atmosphere.set.water", context.x,context.y,context.z,value.intValue());
    }

    public static void temp(final @Nonnull AtmosphereCommandContext context,final @Nonnull Double value){
        context.layer.getTemperature().set(value.floatValue());
        context.execute.notifyCommandListener("geocraft.command.atmosphere.set.temp",context.x,context.y,context.z, value.floatValue());
    }

    public static void debug(final @Nonnull AtmosphereCommandContext context,final @Nonnull Double value){
        if(!(context.atmosphere instanceof SurfaceAtmosphere)){
            context.exception = new CommandException("geocraft.command.atmosphere.unknown");
            return;
        }
        final SurfaceAtmosphere a = (SurfaceAtmosphere) context.atmosphere;
        a.setDebug(value>0);
        if(value>0) context.execute.notifyCommandListener("geocraft.command.atmosphere.set.debug",context.x,context.z,a.isDebug());
    }
}
