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

package top.qiguaiaaaa.geocraft.api.configs.item;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import top.qiguaiaaaa.geocraft.api.configs.ConfigCategory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 配置项目
 * @param <ValueType> 配置的值类型,需要支持{@link Object#toString()}以写入配置文件
 */
public abstract class ConfigItem<ValueType> {
    /**
     * @see #ConfigItem(ConfigCategory, String, Object, String, boolean)
     */
    public ConfigItem(@Nonnull ConfigCategory category, @Nonnull String configKey, @Nonnull ValueType defaultValue){
        this(category,configKey,defaultValue,null,false);
    }

    /**
     * @see #ConfigItem(ConfigCategory, String, Object,String,boolean)
     */
    public ConfigItem(ConfigCategory category,String configKey,ValueType defaultValue,String comment){
        this(category,configKey,defaultValue,comment,false);
    }

    /**
     * 创建一个配置项
     * @param category 配置所在目录
     * @param configKey 配置的key
     * @param defaultValue 配置的默认值，不应为null，因为会调用{@link Object#toString()}
     * @param comment 配置的注释
     * @param isFinal 配置是否在初始化后不可更改
     */
    public ConfigItem(@Nonnull ConfigCategory category, @Nonnull String configKey, @Nonnull ValueType defaultValue, @Nullable String comment, boolean isFinal){
        this.category = category;
        this.key = configKey;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.isFinal = isFinal;
        this.comment = comment;
    }

    protected final ConfigCategory category;
    protected final String key;
    protected final ValueType defaultValue;
    protected final String comment;
    protected final boolean isFinal; //配置初始化后是否不可更改
    protected ValueType value;
    protected Property property;

    @Nonnull
    public ConfigCategory getCategory() {
        return category;
    }

    @Nonnull
    public ValueType getValue(){
        return value;
    }

    @Nonnull
    public ValueType getDefaultValue(){
        return defaultValue;
    }

    @Nullable
    public String getComment() {
        return comment;
    }

    @Nonnull
    public String getKey(){
        return key;
    }

    /**
     * 获取该配置项的配置路径
     * @return 配置路径，例如exampleCategory.exampleItem
     */
    @Nonnull
    public String getPath(){
        return category.getPath()+ Configuration.CATEGORY_SPLITTER+key;
    }

    /**
     * 更新配置项的值
     * @param newValue 新值，注意不能为null
     */
    public void setValue(@Nonnull ValueType newValue){
        if(isFinal) return;
        this.value = newValue;
    }

    /**
     * 该配置项是否不可更新
     * @return 若不能更新，则返回true
     */
    public boolean isFinal(){
        return this.isFinal;
    }

    /**
     * 该配置项是否拥有注释
     * @return 若有，则返回true
     */
    public boolean hasComment(){
        return comment != null;
    }

    /**
     * 提供指定的配置文件,以加载当前配置项目
     * @param config 指定的配置文件
     */
    public void load(@Nonnull Configuration config){
        property = config.get(category.getPath(),key,defaultValue.toString(),comment);
        load(property);
    }

    /**
     * 保存当前配置项目。若在保存前没有{@link #load(Configuration)}则不会生效
     */
    public void save(){
        if(property == null) return;
        property.setValue(value.toString());
    }

    /**
     * 通过{@link Property}的内容来初始化配置项
     * @param property 属性配置
     */
    protected abstract void load(@Nonnull Property property);
}
