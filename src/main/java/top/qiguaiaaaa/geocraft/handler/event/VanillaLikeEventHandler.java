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

package top.qiguaiaaaa.geocraft.handler.event;

import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import top.qiguaiaaaa.geocraft.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.geocraft.api.configs.value.minecraft.ConfigurableFluid;
import top.qiguaiaaaa.geocraft.api.event.atmosphere.AtmosphereUpdateEvent;
import top.qiguaiaaaa.geocraft.api.event.block.StaticLiquidUpdateEvent;
import top.qiguaiaaaa.geocraft.api.setting.GeoFluidSetting;
import top.qiguaiaaaa.geocraft.api.util.FluidUtil;
import top.qiguaiaaaa.geocraft.geography.fluid_physics.vanilla_like.VanillaLikeFluidPhysicsCore;
import top.qiguaiaaaa.geocraft.geography.fluid_physics.vanilla.VanillaFluidPhysicsCore;
import top.qiguaiaaaa.geocraft.util.BaseUtil;
import top.qiguaiaaaa.geocraft.util.WaterUtil;

import static top.qiguaiaaaa.geocraft.configs.SimulationConfig.fluidsNotToSimulateInVanillaLike;

public final class VanillaLikeEventHandler{
    @SubscribeEvent(priority = EventPriority.HIGH)
    public void afterStaticWaterUpdate(StaticLiquidUpdateEvent.After event){
        if(event.getLiquid() != FluidRegistry.WATER) return;
        World worldIn = event.getWorld();
        BlockPos pos = event.getPos();
        VanillaFluidPhysicsCore.evaporateWater(worldIn,pos, worldIn.rand);
    }
    @SubscribeEvent
    public void onAtmosphereRainAndSnow(AtmosphereUpdateEvent.RainAndSnow event){
        Atmosphere atmosphere = event.getAtmosphere();
        World world = event.getWorld();
        BlockPos randPos = event.getRandPos();
        if (WaterUtil.canSnowAt(world,randPos, true)) {
            atmosphere.drainWater(FluidUtil.ONE_IN_EIGHT_OF_BUCKET_VOLUME,randPos,false);
            event.setResult(Event.Result.ALLOW);
            event.setState(Blocks.SNOW_LAYER.getDefaultState());
        }
        if(!BaseUtil.getRandomResult(world.rand,event.getRainPossibility())) return;
        if(VanillaLikeFluidPhysicsCore.canRainAt(world,randPos.down())){
            atmosphere.drainWater(Fluid.BUCKET_VOLUME,randPos,false);
            //因为不是更新指定的位置,所以不设置结果
            world.setBlockState(randPos.down(),Blocks.FLOWING_WATER.getDefaultState());
        }
    }

    public static void onPostInit(FMLPostInitializationEvent event){
        for(ConfigurableFluid fluid:fluidsNotToSimulateInVanillaLike.getValue()){
            if(fluid == null) continue;
            GeoFluidSetting.setFluidToBePhysical(fluid.toString(),false);
        }
    }
}
