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

package top.qiguaiaaaa.geocraft.configs;

import top.qiguaiaaaa.geocraft.MixinEarlyInit;
import top.qiguaiaaaa.geocraft.api.configs.ConfigCategory;
import top.qiguaiaaaa.geocraft.api.configs.item.ConfigItem;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import static top.qiguaiaaaa.geocraft.configs.AtmosphereConfig.*;
import static top.qiguaiaaaa.geocraft.configs.ConfigurationLoader.registerConfigCategory;
import static top.qiguaiaaaa.geocraft.configs.GeneralConfig.*;
import static top.qiguaiaaaa.geocraft.configs.FluidPhysicsConfig.*;
import static top.qiguaiaaaa.geocraft.configs.ConfigurationLoader.registerConfigItem;

public final class ConfigInit {
    private static boolean hasLoaded = false;
    public static void initConfigs(){
        if(hasLoaded) return;
        initConfigClass(GeneralConfig.class);
        initConfigClass(FluidPhysicsConfig.class);
        initConfigClass(AtmosphereConfig.class);
        hasLoaded = true;
    }

    private static void initConfigClass(Class<?> configClass){
        Field[] fields = configClass.getFields();
        for(Field field:fields){
            int modifiers = field.getModifiers();
            if(!Modifier.isStatic(modifiers)) continue;
            if(!Modifier.isPublic(modifiers)) continue;
            try {
                Object val = field.get(null);
                if(val instanceof ConfigCategory){
                    registerConfigCategory((ConfigCategory) val);
                }else if(val instanceof ConfigItem<?>){
                    registerConfigItem((ConfigItem<?>) val);
                }
            } catch (IllegalAccessException e) {
                MixinEarlyInit.LOGGER.error("Couldn't get field {} in config class {}",field,configClass);
            }
        }
    }
}
