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

package top.qiguaiaaaa.geocraft.api.configs.item.collection;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import top.qiguaiaaaa.geocraft.api.GeoCraftAPI;
import top.qiguaiaaaa.geocraft.api.configs.ConfigCategory;
import top.qiguaiaaaa.geocraft.api.configs.item.ConfigItem;
import top.qiguaiaaaa.geocraft.api.configs.value.collection.ConfigurableList;

import javax.annotation.Nonnull;
import java.util.function.Function;
import java.util.regex.Pattern;

public class ConfigList<ValueType> extends ConfigItem<ConfigurableList<ValueType>> {
    protected final Function<String,ValueType> parser;

    protected int maxListSize = -1;

    protected boolean isListSizeFixed = false;

    protected Pattern validatedPattern = null;

    public ConfigList(ConfigCategory category, String configKey, ConfigurableList<ValueType> defaultValue, Function<String,ValueType> parser) {
        this(category, configKey, defaultValue,null,parser);
    }

    public ConfigList(ConfigCategory category, String configKey, ConfigurableList<ValueType> defaultValue, String comment, Function<String,ValueType> parser) {
        this(category,configKey,defaultValue,comment,parser,false);
    }

    public ConfigList(ConfigCategory category, String configKey, ConfigurableList<ValueType> defaultValue, String comment, Function<String,ValueType> parser, boolean isFinal) {
        this(category, configKey, defaultValue, comment,-1,parser,isFinal);
    }

    public ConfigList(ConfigCategory category, String configKey, ConfigurableList<ValueType> defaultValue, String comment, int maxListSize, Function<String,ValueType> parser, boolean isFinal) {
        super(category, configKey, defaultValue, comment, isFinal);
        this.parser = parser;
        this.maxListSize = maxListSize;
    }

    public ConfigList<ValueType> setMaxListSize(int maxListSize) {
        this.maxListSize = maxListSize;
        return this;
    }

    public ConfigList<ValueType> setListSizeFixed(boolean listSizeFixed) {
        isListSizeFixed = listSizeFixed;
        return this;
    }

    public ConfigList<ValueType> setValidatedPattern(Pattern validatedPattern) {
        this.validatedPattern = validatedPattern;
        return this;
    }

    @Override
    public void save() {
        if(property == null) return;
        property.setValues(value.toStringList());
        property.setComment(comment);
    }

    @Override
    public void load(@Nonnull Configuration config) {
        property = config.get(category.getPath(),key,defaultValue.toStringList(),comment,isListSizeFixed,maxListSize,validatedPattern);
        load(property);
    }

    @Override
    protected void load(@Nonnull Property property) {
        value = new ConfigurableList<>();

        String[] strings = property.getStringList();
        for(String string:strings){
            try {
                ValueType loadedVal = parser.apply(string);
                value.add(loadedVal);
            }catch (Throwable e){
                GeoCraftAPI.LOGGER.warn("loading configuration {} in {} error",string,category);
                GeoCraftAPI.LOGGER.warn("Error Detailed:",e);
            }
        }
    }
}
