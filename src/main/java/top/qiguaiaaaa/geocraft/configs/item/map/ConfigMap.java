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

package top.qiguaiaaaa.geocraft.configs.item.map;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import top.qiguaiaaaa.geocraft.GeoCraft;
import top.qiguaiaaaa.geocraft.api.configs.ConfigCategory;
import top.qiguaiaaaa.geocraft.api.configs.value.map.ConfigurableLinkedHashMap;
import top.qiguaiaaaa.geocraft.api.configs.value.map.entry.ConfigEntry;
import top.qiguaiaaaa.geocraft.api.configs.item.ConfigItem;

import javax.annotation.Nonnull;
import java.util.function.Function;

public class ConfigMap<K,V> extends ConfigItem<ConfigurableLinkedHashMap<K,V>> {

    protected final Function<String,K> parserK;
    protected final Function<String,V> parserV;

    @SafeVarargs
    public ConfigMap(ConfigCategory category, String configKey, Function<String,K> parserK, Function<String,V> parserV, ConfigEntry<K,V>... entries) {
        this(category,configKey,null,parserK,parserV,entries);
    }

    @SafeVarargs
    public ConfigMap(ConfigCategory category, String configKey, String comment, Function<String,K> parserK, Function<String,V> parserV, ConfigEntry<K,V>... entries) {
        this(category,configKey,comment,parserK,parserV,false,entries);
    }

    @SafeVarargs
    public ConfigMap(ConfigCategory category, String configKey, String comment, Function<String,K> parserK, Function<String,V> parserV, boolean isFinal, ConfigEntry<K,V>... entries) {
        super(category, configKey, new ConfigurableLinkedHashMap<>(), comment, isFinal);
        this.parserK = parserK;
        this.parserV = parserV;
        for(ConfigEntry<K,V> entry:entries){
            if(entry == null) continue;
            defaultValue.put(entry.getKey(),entry.getValue());
        }
    }

    @Override
    public void load(@Nonnull Configuration config) {
        Property property = config.get(category.getPath(),key,defaultValue.toStringList(),comment);
        load(property);
    }

    @Override
    protected void load(@Nonnull Property property) {
        value = new ConfigurableLinkedHashMap<>();
        String[] strings = property.getStringList();
        for(String content:strings){
            String[] spilt = content.trim().split(ConfigurableLinkedHashMap.SPLIT,2);
            if(spilt.length<2){
                GeoCraft.getLogger().warn("loading configuration {} error: {} is not valid key-value pair",category,content);
                continue;
            }
            try{
                K k = parserK.apply(spilt[0]);
                V v = parserV.apply(spilt[1]);
                if(k == null || v == null) continue;
                this.value.put(k,v);
            }catch (Throwable e){
                GeoCraft.getLogger().warn("loading configuration {} in {} error",content,category);
                GeoCraft.getLogger().warn("Error Detailed:",e);
            }
        }
    }
}