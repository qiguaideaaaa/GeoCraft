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

import net.minecraft.block.material.Material;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandResultStats;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import moe.qingu.orbtellus.api.atmosphere.layer.AtmosphereLayer;
import moe.qingu.orbtellus.api.atmosphere.layer.UnderlyingLayer;
import moe.qingu.orbtellus.api.state.FluidState;
import moe.qingu.orbtellus.api.state.TemperatureState;
import moe.qingu.orbtellus.geography.atmosphere.layer.surface.SurfaceUnderlying;
import moe.qingu.orbtellus.util.BaseUtil;

import javax.annotation.Nonnull;
import java.lang.invoke.*;
import java.util.Collections;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author QGMoe
 */
@SuppressWarnings({"unused","unchecked","DataFlowIssue"})
public final class AtmosphereQueries {
    private static final MethodHandles.Lookup PERMISSION = MethodHandles.lookup();
    public static final Map<String, Consumer<AtmosphereCommandContext>> QueryConsumer = Collections.unmodifiableMap(
            (Map<String, Consumer<AtmosphereCommandContext>>) (Map<?,?>) BaseUtil.getLambdasFrom(PERMISSION, AtmosphereQueries.class, Consumer.class));

    private AtmosphereQueries(){}

    public static void block_temp(final @Nonnull AtmosphereCommandContext ctx){
        ctx.accessor.setNotAir(ctx.execute.getWorld().getBlockState(ctx.pos).getMaterial() != Material.AIR);
        final double temp = ctx.accessor.getTemperature();
        ctx.execute.notifyCommandListener("geocraft.command.atmosphere.query.block_temp",ctx.x,ctx.y,ctx.z,temp);
        ctx.execute.getSender().setCommandStat(CommandResultStats.Type.QUERY_RESULT,(int)(temp*ctx.multiply));
    }

    public static void steam(final @Nonnull AtmosphereCommandContext ctx){
        final FluidState steam = (ctx.layer instanceof AtmosphereLayer)?((AtmosphereLayer)ctx.layer).getSteam():null;
        if(ctx.isNull(steam)) return;
        ctx.execute.notifyCommandListener("geocraft.command.atmosphere.query.steam",ctx.x,ctx.y,ctx.z,steam);
        ctx.execute.getSender().setCommandStat(CommandResultStats.Type.QUERY_RESULT,(int)(steam.getFluidAmount()* ctx.multiply));
    }

    public static void water(final @Nonnull AtmosphereCommandContext ctx){
        final FluidState water = ctx.layer.getWater();
        if(ctx.isNull(water)) return;
        ctx.execute.notifyCommandListener("geocraft.command.atmosphere.query.water",ctx.x,ctx.y,ctx.z,water);
        ctx.execute.getSender().setCommandStat(CommandResultStats.Type.QUERY_RESULT,(int)(water.getFluidAmount()*ctx.multiply));
    }

    public static void temp(final @Nonnull AtmosphereCommandContext ctx){
        ctx.execute.notifyCommandListener("geocraft.command.atmosphere.query.temp",ctx.x,ctx.y,ctx.z,ctx.layer.getTemperature());
        ctx.execute.getSender().setCommandStat(CommandResultStats.Type.QUERY_RESULT,(int)(ctx.layer.getTemperature().get()*ctx.multiply));
    }

    public static void ground_temp(final @Nonnull AtmosphereCommandContext ctx){
        final TemperatureState state = ctx.atmosphere.getUnderlying(ctx.pos).getTemperature();
        ctx.execute.notifyCommandListener("geocraft.command.atmosphere.query.ground_temp", ctx.x, ctx.y, ctx.z, state);
        ctx.execute.getSender().setCommandStat(CommandResultStats.Type.QUERY_RESULT,(int) (state.get()*ctx.multiply));
    }

    public static void wind(final @Nonnull AtmosphereCommandContext ctx){
        final Vec3d wind = ctx.accessor.getWind();
        for(final @Nonnull EnumFacing facing:EnumFacing.VALUES) ctx.execute.notifyCommandListener("geocraft.command.atmosphere.query.wind."+facing.getName(), ctx.x,ctx.y, ctx.z, wind.dotProduct(new Vec3d(facing.getDirectionVec())));
        ctx.execute.getSender().setCommandStat(CommandResultStats.Type.QUERY_RESULT,(int)(wind.length()*ctx.multiply));
    }

    public static void underlying(final @Nonnull AtmosphereCommandContext ctx){
        final UnderlyingLayer underlying1 = ctx.atmosphere.getUnderlying(ctx.pos);
        final SurfaceUnderlying underlying;
        if(underlying1 instanceof SurfaceUnderlying){
            underlying = (SurfaceUnderlying) underlying1;
        }else{
            ctx.exception = new CommandException("geocraft.command.atmosphere.unknown_underlying");
            return;
        }
        ctx.execute.notifyCommandListener("geocraft.command.atmosphere.query.underlying", ctx.x, underlying.getAltitude(), ctx.z,
                underlying.getHeatCapacity(),
                underlying.平均返照率);
    }

    public static void heat_volume(final @Nonnull AtmosphereCommandContext ctx){
        ctx.execute.notifyCommandListener("geocraft.command.atmosphere.query.heat_volume",ctx.x,ctx.y,ctx.z,ctx.layer.getHeatCapacity());
        ctx.execute.getSender().setCommandStat(CommandResultStats.Type.QUERY_RESULT,(int) (ctx.layer.getHeatCapacity()*ctx.multiply));
    }

    public static void water_pressure(final @Nonnull AtmosphereCommandContext ctx){
        ctx.execute.notifyCommandListener("geocraft.command.atmosphere.query.water_pressure",ctx.x,ctx.y,ctx.z,ctx.accessor.getWaterPressure()*0.01);
        ctx.execute.getSender().setCommandStat(CommandResultStats.Type.QUERY_RESULT,(int)(ctx.accessor.getWaterPressure()*ctx.multiply));
    }

    public static void pressure(final @Nonnull AtmosphereCommandContext ctx){
        ctx.execute.notifyCommandListener("geocraft.command.atmosphere.query.pressure",ctx.x,ctx.y,ctx.z,ctx.accessor.getPressure()*0.01);
        ctx.execute.getSender().setCommandStat(CommandResultStats.Type.QUERY_RESULT,(int)(ctx.accessor.getPressure()*ctx.multiply));
    }
}
