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

package top.qiguaiaaaa.geocraft.api.configs.item.number;


import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import top.qiguaiaaaa.geocraft.api.configs.ConfigCategory;
import top.qiguaiaaaa.geocraft.api.configs.item.ConfigItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * {@link Integer}配置项
 */
public class ConfigInteger extends ConfigItem<Integer> {
    protected int minValue = Integer.MIN_VALUE,
    maxValue = Integer.MAX_VALUE;

    /**
     * @see #ConfigInteger(ConfigCategory, String, int, String, int, int, boolean)
     */
    public ConfigInteger(@Nonnull ConfigCategory category,@Nonnull String configKey, int defaultValue) {
        super(category, configKey, defaultValue);
    }

    /**
     * @see #ConfigInteger(ConfigCategory, String, int, String, int, int, boolean)
     */
    public ConfigInteger(@Nonnull ConfigCategory category,@Nonnull String configKey, int defaultValue,@Nullable String comment) {
        super(category, configKey, defaultValue, comment);
    }

    /**
     * @see #ConfigInteger(ConfigCategory, String, int, String, int, int, boolean)
     */
    public ConfigInteger(@Nonnull ConfigCategory category,@Nonnull String configKey, int defaultValue,@Nullable String comment, boolean isFinal) {
        this(category, configKey, defaultValue, comment,Integer.MIN_VALUE,Integer.MAX_VALUE, isFinal);
    }

    /**
     * 创建一个Integer类型配置项
     * @param category 配置所在目录
     * @param configKey 配置的key
     * @param defaultValue 配置的默认值
     * @param comment 配置的注释
     * @param min 最小值
     * @param max 最大值
     * @param isFinal 配置是否在初始化后不可更改
     */
    public ConfigInteger(@Nonnull ConfigCategory category, @Nonnull String configKey, int defaultValue, @Nullable String comment, int min, int max, boolean isFinal) {
        super(category, configKey, defaultValue, comment, isFinal);
        this.minValue = min;
        this.maxValue = max;
    }

    public ConfigInteger setMinValue(int minValue) {
        this.minValue = minValue;
        return this;
    }

    public ConfigInteger setMaxValue(int maxValue) {
        this.maxValue = maxValue;
        return this;
    }

    public int getMinValue() {
        return minValue;
    }

    public int getMaxValue() {
        return maxValue;
    }

    /**
     * {@inheritDoc}
     * @param config {@inheritDoc}
     */
    @Override
    public void load(@Nonnull Configuration config) {
        Property property = config.get(category.getPath(),key,defaultValue,comment,minValue,maxValue);
        property.setComment((comment == null?"":comment) + " [range: " + minValue + " ~ " + maxValue + ", default: " + defaultValue + "]");
        load(property);
    }

    /**
     * {@inheritDoc}
     * @param property {@inheritDoc}
     */
    @Override
    protected void load(@Nonnull Property property) {
        this.value = property.getInt(defaultValue);
    }
}
