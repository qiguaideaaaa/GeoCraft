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

package top.qiguaiaaaa.geocraft.geography.property;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import top.qiguaiaaaa.geocraft.GeoCraft;
import top.qiguaiaaaa.geocraft.api.property.AtmosphereProperty;
import top.qiguaiaaaa.geocraft.api.property.GeographyProperty;
import top.qiguaiaaaa.geocraft.util.registry.ServerOnlyRegistryBuilder;

import java.util.*;

@Mod.EventBusSubscriber
public final class GeographyPropertyManager {
    private static boolean isLoaded = false;
    private static final ResourceLocation NAME = new ResourceLocation(GeoCraft.MODID,"geography_property");
    private static IForgeRegistry<GeographyProperty> properties;
    private static final Set<AtmosphereProperty> atmosphereProperties = new HashSet<>();
    private static final Set<AtmosphereProperty> windEffectedProperties = new HashSet<>();
    private static final Set<AtmosphereProperty> flowableProperties = new HashSet<>();

    @SubscribeEvent
    public static void onNewRegistryEvent(RegistryEvent.NewRegistry registry){
        if(isLoaded) return;
        properties = new ServerOnlyRegistryBuilder<GeographyProperty>()
                .setType(GeographyProperty.class)
                .create();
        RegistryEvent<GeographyProperty> event = new RegistryEvent.Register<>(NAME,properties);
        MinecraftForge.EVENT_BUS.post(event);
        sortProperties();
        isLoaded = true;
    }

    public static IForgeRegistry<GeographyProperty> getProperties() {
        return properties;
    }
    public static Set<AtmosphereProperty> getAtmosphereProperties(){return atmosphereProperties;}

    public static Set<AtmosphereProperty> getWindEffectedProperties() {
        return windEffectedProperties;
    }
    public static Set<AtmosphereProperty> getFlowableProperties() {
        return flowableProperties;
    }

    private static void sortProperties(){
        for(GeographyProperty property:properties){
            if(property instanceof AtmosphereProperty){
                AtmosphereProperty atmosphereProperty = (AtmosphereProperty) property;
                atmosphereProperties.add(atmosphereProperty);
                if(atmosphereProperty.haveWindEffect())
                    windEffectedProperties.add(atmosphereProperty);
                if(atmosphereProperty.isFlowable())
                    flowableProperties.add(atmosphereProperty);
            }

        }
    }
}
