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

import top.qiguaiaaaa.geocraft.api.configs.ConfigCategory;
import top.qiguaiaaaa.geocraft.api.configs.item.collection.ConfigCollection;
import top.qiguaiaaaa.geocraft.api.configs.value.collection.ConfigurableList;
import top.qiguaiaaaa.geocraft.api.configs.value.collection.IConfigurableList;
import top.qiguaiaaaa.geocraft.api.configs.value.collection.UnmodifiableConfigurableList;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public class ConfigList<ValueType> extends ConfigCollection<IConfigurableList<ValueType>,ValueType> implements List<ValueType>{

    public ConfigList(ConfigCategory category, String configKey, IConfigurableList<ValueType> defaultValue, Function<String,ValueType> parser) {
        this(category, configKey, defaultValue,null,parser);
    }

    public ConfigList(ConfigCategory category, String configKey, IConfigurableList<ValueType> defaultValue, String comment, Function<String,ValueType> parser) {
        this(category,configKey,defaultValue,comment,parser,false);
    }

    public ConfigList(ConfigCategory category, String configKey, IConfigurableList<ValueType> defaultValue, String comment, Function<String,ValueType> parser, boolean isFinal) {
        this(category, configKey, defaultValue, comment,-1,parser,isFinal);
    }

    public ConfigList(ConfigCategory category, String configKey, IConfigurableList<ValueType> defaultValue, String comment, int maxListSize, Function<String,ValueType> parser, boolean isFinal) {
        this(category, configKey, defaultValue, comment,maxListSize,parser,ConfigurableList::new, isFinal);
    }

    public ConfigList(ConfigCategory category, String configKey, IConfigurableList<ValueType> defaultValue, String comment, int maxListSize, Function<String,ValueType> parser,Supplier<IConfigurableList<ValueType>> factory, boolean isFinal) {
        super(category, configKey, defaultValue, comment, maxListSize,parser,factory,isFinal);
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

    //***********
    // List
    //***********

    @Override
    public boolean addAll(int index,@Nonnull Collection<? extends ValueType> c) {
        if(isFinal || isListSizeFixed) throw new UnsupportedOperationException();
        return value.addAll(c);
    }

    /**
     * @see List#get(int)
     */
    public ValueType get(int index){
        return value.get(index);
    }

    @Override
    public ValueType set(int index,@Nonnull ValueType element) {
        if(isFinal) throw new UnsupportedOperationException();
        return value.set(index,element);
    }

    @Override
    public void add(int index,@Nonnull ValueType element) {
        if(isFinal || isListSizeFixed) throw new UnsupportedOperationException();
        value.add(index,element);
    }

    @Override
    public ValueType remove(int index) {
        if(isFinal || isListSizeFixed) throw new UnsupportedOperationException();
        return value.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return value.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return value.lastIndexOf(o);
    }

    @Override
    public ListIterator<ValueType> listIterator() {
        if(isFinal || isListSizeFixed){
            return Collections.unmodifiableList(value).listIterator();
        }
        return value.listIterator();
    }

    @Override
    public ListIterator<ValueType> listIterator(int index) {
        if(isFinal || isListSizeFixed){
            return Collections.unmodifiableList(value).listIterator(index);
        }
        return value.listIterator(index);
    }

    @Override
    public List<ValueType> subList(int fromIndex, int toIndex) {
        return value.subList(fromIndex, toIndex);
    }

    @Nonnull
    @Override
    public IConfigurableList<ValueType> getValue() {
        if(!isFinal && !isListSizeFixed) return value;
        return new UnmodifiableConfigurableList<>(value);
    }
}
