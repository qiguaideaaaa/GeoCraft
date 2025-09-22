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
 * {@link Double}配置项
 */
public class ConfigDouble extends ConfigItem<Double> {
    protected double minValue = Double.NEGATIVE_INFINITY;
    protected double maxValue = Double.POSITIVE_INFINITY;

    /**
     * @see #ConfigDouble(ConfigCategory, String, double, String, boolean)
     */
    public ConfigDouble(@Nonnull ConfigCategory category,@Nonnull String configKey, double defaultValue) {
        super(category, configKey, defaultValue);
    }

    /**
     * @see #ConfigDouble(ConfigCategory, String, double, String, boolean)
     */
    public ConfigDouble(@Nonnull ConfigCategory category, @Nonnull String configKey, double defaultValue,@Nullable String comment) {
        super(category, configKey, defaultValue, comment);
    }

    /**
     * 创建一个Double类型配置项
     * @param category 配置所在目录
     * @param configKey 配置的key
     * @param defaultValue 配置的默认值
     * @param comment 配置的注释
     * @param isFinal 配置是否在初始化后不可更改
     */
    public ConfigDouble(@Nonnull ConfigCategory category,@Nonnull String configKey, double defaultValue,@Nullable String comment, boolean isFinal) {
        this(category, configKey, defaultValue, comment,Double.NEGATIVE_INFINITY,Double.POSITIVE_INFINITY,isFinal);
    }

    public ConfigDouble(@Nonnull ConfigCategory category, @Nonnull String configKey, double defaultValue, @Nullable String comment, double min, double max, boolean isFinal) {
        super(category, configKey, defaultValue, comment, isFinal);
        this.minValue = min;
        this.maxValue = max;
    }

    public ConfigDouble setMaxValue(double maxValue) {
        this.maxValue = maxValue;
        return this;
    }

    public ConfigDouble setMinValue(double minValue) {
        this.minValue = minValue;
        return this;
    }

    public double getMinValue() {
        return minValue;
    }

    public double getMaxValue() {
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
        this.value = property.getDouble(defaultValue);
    }
}
