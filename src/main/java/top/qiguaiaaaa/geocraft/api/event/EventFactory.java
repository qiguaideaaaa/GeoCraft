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

package top.qiguaiaaaa.geocraft.api.event;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityAreaEffectCloud;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import top.qiguaiaaaa.geocraft.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.geocraft.api.atmosphere.system.IAtmosphereSystem;
import top.qiguaiaaaa.geocraft.api.event.atmosphere.AtmosphereGenerateEvent;
import top.qiguaiaaaa.geocraft.api.event.atmosphere.AtmosphereSystemEvent;
import top.qiguaiaaaa.geocraft.api.event.block.StaticLiquidUpdateEvent;
import top.qiguaiaaaa.geocraft.api.event.atmosphere.AtmosphereUpdateEvent;
import top.qiguaiaaaa.geocraft.api.event.player.FillGlassBottleEvent;
import top.qiguaiaaaa.geocraft.api.event.player.FillGlassBottleEvent.FillGlassBottleOnAreaEffectCloudEvent;
import top.qiguaiaaaa.geocraft.api.event.player.FillGlassBottleEvent.FillGlassBottleOnFluidEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public final class EventFactory {
    public static final EventBus EVENT_BUS = new EventBus();

    public static ActionResult<ItemStack> onGlassBottleUseOnAreaEffectCloud(@Nonnull EntityPlayer player, @Nonnull ItemStack itemStack, World world, @Nonnull List<EntityAreaEffectCloud> entityList){
        FillGlassBottleEvent event = new FillGlassBottleOnAreaEffectCloudEvent(player,itemStack,world,entityList);
        return processOnGlassBottleUseEvent(itemStack,player,event);
    }
    public static ActionResult<ItemStack> onGlassBottleUseOnFluid(@Nonnull EntityPlayer player, @Nonnull ItemStack itemStack, World world, @Nullable RayTraceResult rayTraceResult){
        FillGlassBottleEvent event = new FillGlassBottleOnFluidEvent(player,itemStack,world,rayTraceResult);
        return processOnGlassBottleUseEvent(itemStack,player,event);
    }
    public static IBlockState onAtmosphereRainAndSnow(@Nonnull Chunk chunk, @Nonnull Atmosphere atmosphere, @Nonnull BlockPos randPos, double rainPossibility){
        AtmosphereUpdateEvent.RainAndSnow event = new AtmosphereUpdateEvent.RainAndSnow(chunk,atmosphere,randPos,rainPossibility);
        EVENT_BUS.post(event);
        if(event.getResult() == Result.ALLOW){
            return event.getState();
        }
        return null;
    }
    public static void postAtmosphereUpdate(@Nullable Chunk chunk, @Nonnull Atmosphere atmosphere, int x,int z){
        AtmosphereUpdateEvent.Post event = new AtmosphereUpdateEvent.Post(chunk,atmosphere,x,z);
        EVENT_BUS.post(event);
    }
    public static void preAtmosphereGenerate(@Nonnull WorldServer world,@Nonnull Chunk chunk){
        AtmosphereGenerateEvent.Pre event = new AtmosphereGenerateEvent.Pre(world,chunk);
        EVENT_BUS.post(event);
    }
    public static IBlockState afterBlockLiquidStaticUpdate(@Nonnull Fluid fluid, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state){
        StaticLiquidUpdateEvent.After event = new StaticLiquidUpdateEvent.After(fluid,world,pos,state);
        EVENT_BUS.post(event);
        if(event.getResult() == Result.ALLOW){
            return event.getNewState();
        }
        return null;
    }

    public static IAtmosphereSystem onAtmosphereSystemCreate(@Nonnull WorldServer server){
        AtmosphereSystemEvent.Create event = new AtmosphereSystemEvent.Create(server);
        EVENT_BUS.post(event);
        if(event.isCanceled()) return null;
        return event.getSystem();
    }

    private static ActionResult<ItemStack> processOnGlassBottleUseEvent(ItemStack itemStack,EntityPlayer player,FillGlassBottleEvent event){
        if(EVENT_BUS.post(event)) return new ActionResult<>(EnumActionResult.PASS,itemStack);
        if(event.getResult() == Result.ALLOW){
            itemStack.shrink(1);

            if (itemStack.isEmpty()) {
                return new ActionResult<>(EnumActionResult.SUCCESS,event.getFilledGlassBottle());
            } else {
                if (!player.inventory.addItemStackToInventory(event.getFilledGlassBottle())) {
                    player.dropItem(event.getFilledGlassBottle(), false);
                }

                return new ActionResult<>(EnumActionResult.SUCCESS,itemStack);
            }
        }
        return null;
    }
}
