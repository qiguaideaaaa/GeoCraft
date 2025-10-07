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

package top.qiguaiaaaa.geocraft.api.configs.item.collection.list;

import net.minecraftforge.common.config.Configuration;
import top.qiguaiaaaa.geocraft.api.configs.ConfigCategory;
import top.qiguaiaaaa.geocraft.api.configs.value.collection.ConfigurableList;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * @author QiguaiAAAA
 */
public class ConfigDoubleList extends ConfigList<Double>{
    protected double minValue = Double.NEGATIVE_INFINITY,
    maxValue = Double.POSITIVE_INFINITY;
    public ConfigDoubleList(ConfigCategory category, String configKey, ConfigurableList<Double> defaultValue) {
        this(category, configKey, defaultValue,null);
    }

    public ConfigDoubleList(ConfigCategory category, String configKey, ConfigurableList<Double> defaultValue, String comment) {
        this(category, configKey, defaultValue, comment, false);
    }

    public ConfigDoubleList(ConfigCategory category, String configKey, ConfigurableList<Double> defaultValue, String comment, boolean isFinal) {
        this(category, configKey, defaultValue, comment,-1, isFinal);
    }

    public ConfigDoubleList(ConfigCategory category, String configKey, ConfigurableList<Double> defaultValue, String comment, int maxListSize, boolean isFinal) {
        super(category, configKey, defaultValue, comment, maxListSize, Double::parseDouble, isFinal);
    }

    public ConfigDoubleList setMinValue(double minValue) {
        this.minValue = minValue;
        return this;
    }

    public ConfigDoubleList setMaxValue(double maxValue) {
        this.maxValue = maxValue;
        return this;
    }

    public double getMinValue() {
        return minValue;
    }

    public double getMaxValue() {
        return maxValue;
    }

    @Override
    public void save() {
        if(property == null) return;
        property.setValues(toDoubleList(value));
        property.setComment(getPolishedComment());
    }

    @Override
    public void load(@Nonnull Configuration config) {
        property = config.get(category.getPath(),key,toDoubleList(defaultValue),comment,minValue,maxValue,isListSizeFixed,maxListSize);
        property.setComment(getPolishedComment());
        load(property);
    }

    protected String getPolishedComment(){
        return (comment==null?"":comment)+" [range: " + minValue + " ~ " + maxValue + (maxListSize>=0?", maxSize: " + maxListSize:"") + "]";
    }

    protected double[] toDoubleList(@Nonnull Collection<Double> c){
        double[] doubles = new double[c.size()];
        int i=0;
        for(Double d:c){
            doubles[i++]=d;
        }
        return doubles;
    }
}
