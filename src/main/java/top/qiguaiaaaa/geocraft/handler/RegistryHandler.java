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

package top.qiguaiaaaa.geocraft.handler;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;
import top.qiguaiaaaa.geocraft.api.GeoCraftProperties;
import top.qiguaiaaaa.geocraft.api.configs.value.geo.FluidPhysicsMode;
import top.qiguaiaaaa.geocraft.api.event.EventFactory;
import top.qiguaiaaaa.geocraft.api.property.GeographyProperty;
import top.qiguaiaaaa.geocraft.configs.FluidPhysicsConfig;
import top.qiguaiaaaa.geocraft.geography.property.*;
import top.qiguaiaaaa.geocraft.handler.event.*;

public final class RegistryHandler {
    public static void registerGeographyProperties(RegistryEvent.Register<GeographyProperty> event){
        IForgeRegistry<GeographyProperty> registry =event.getRegistry();
        registry.register(DefaultTemperature.TEMPERATURE);
        registry.register(DeepTemperature.DEEP_TEMPERATURE);
        registry.register(AtmosphereWater.WATER);
        registry.register(AtmosphereSteam.STEAM);
        registry.register(CarbonDioxide.CARBON_DIOXIDE);
        registry.register(AltitudeProperty.ALTITUDE);
        registry.register(HeatCapacity.HEAT_CAPACITY);
        registry.register(ReflectivityProperty.REFLECTIVITY);
        registry.register(FinalTemperature.FINAL_TEMPERATURE);

        GeoCraftProperties.FINAL_TEMPERATURE = FinalTemperature.FINAL_TEMPERATURE;
        GeoCraftProperties.TEMPERATURE = DefaultTemperature.TEMPERATURE;
        GeoCraftProperties.DEEP_TEMPERATURE = DeepTemperature.DEEP_TEMPERATURE;
        GeoCraftProperties.WATER = AtmosphereWater.WATER;
        GeoCraftProperties.CARBON_DIOXIDE = CarbonDioxide.CARBON_DIOXIDE;
        GeoCraftProperties.STEAM = AtmosphereSteam.STEAM;
        GeoCraftProperties.ALTITUDE = AltitudeProperty.ALTITUDE;
        GeoCraftProperties.HEAT_CAPACITY = HeatCapacity.HEAT_CAPACITY;
        GeoCraftProperties.REFLECTIVITY = ReflectivityProperty.REFLECTIVITY;
    }
    public static void registerEventHandler(){
        EventFactory.EVENT_BUS.register(AtmosphereEventHandler.class);
        MinecraftForge.EVENT_BUS.register(SoilEventHandler.class);
        FluidPhysicsMode mode = FluidPhysicsConfig.FLUID_PHYSICS_MODE.getValue();
        switch (mode){
            case MORE_REALITY:{
                registerMoreRealityEventHandler();
                break;
            }
            case VANILLA_LIKE:{
                registerVanillaLikeEventHandler();
                break;
            }
            case VANILLA:
            default:{
                registerVanillaEventHandler();
                break;
            }
        }
    }
    private static void registerMoreRealityEventHandler(){
        MoreRealityEventHandler moreRealityEventHandler = new MoreRealityEventHandler();
        MinecraftForge.EVENT_BUS.register(moreRealityEventHandler);
        EventFactory.EVENT_BUS.register(moreRealityEventHandler);
    }
    private static void registerVanillaLikeEventHandler(){
        VanillaLikeEventHandler handler = new VanillaLikeEventHandler();
        MinecraftForge.EVENT_BUS.register(handler);
        EventFactory.EVENT_BUS.register(handler);
    }
    private static void registerVanillaEventHandler(){
        VanillaEventHandler handler = new VanillaEventHandler();
        MinecraftForge.EVENT_BUS.register(handler);
        EventFactory.EVENT_BUS.register(handler);
    }
}
