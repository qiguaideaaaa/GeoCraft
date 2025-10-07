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
import top.qiguaiaaaa.geocraft.api.configs.value.collection.IConfigurableCollection;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Pattern;

/**
 * @author QiguaiAAAA
 */
public abstract class ConfigCollection<CollectionType extends IConfigurableCollection<ValueType>,ValueType> extends ConfigItem<CollectionType> implements Collection<ValueType> {
    protected final Function<String,ValueType> parser;
    protected final Supplier<CollectionType> factory;

    protected int maxListSize = -1;

    protected boolean isListSizeFixed = false;

    protected Pattern validatedPattern = null;

    public ConfigCollection(ConfigCategory category, String configKey, CollectionType defaultValue, Function<String,ValueType> parser, Supplier<CollectionType> factory) {
        this(category, configKey, defaultValue,null,parser,factory);
    }

    public ConfigCollection(ConfigCategory category, String configKey, CollectionType defaultValue, String comment, Function<String,ValueType> parser, Supplier<CollectionType> factory) {
        this(category,configKey,defaultValue,comment,parser,factory,false);
    }

    public ConfigCollection(ConfigCategory category, String configKey, CollectionType defaultValue, String comment, Function<String,ValueType> parser, Supplier<CollectionType> factory, boolean isFinal) {
        this(category, configKey, defaultValue, comment,-1,parser,factory,isFinal);
    }

    public ConfigCollection(ConfigCategory category, String configKey,CollectionType defaultValue, String comment, int maxListSize, Function<String,ValueType> parser, Supplier<CollectionType> factory, boolean isFinal) {
        super(category, configKey, defaultValue, comment, isFinal);
        this.parser = parser;
        this.maxListSize = maxListSize;
        this.factory = factory;
    }

    public ConfigCollection<CollectionType,ValueType> setMaxListSize(int maxListSize) {
        this.maxListSize = maxListSize;
        return this;
    }

    public ConfigCollection<CollectionType,ValueType> setListSizeFixed(boolean listSizeFixed) {
        isListSizeFixed = listSizeFixed;
        return this;
    }

    public ConfigCollection<CollectionType,ValueType> setValidatedPattern(Pattern validatedPattern) {
        this.validatedPattern = validatedPattern;
        return this;
    }

    @Override
    public void save() {
        if(property == null) return;
        property.setValues(value.toStringList());
        property.setComment(comment);
    }

    @Nonnull
    @Override
    public abstract CollectionType getValue();

    @Override
    public void setValue(@Nonnull CollectionType newValue) {
        if(isFinal) return;
        if(newValue.size() != value.size() && isListSizeFixed) throw new IllegalArgumentException();
        if(maxListSize >=0 && newValue.size()>maxListSize) throw new IllegalArgumentException();
        super.setValue(newValue);
    }

    @Override
    public void load(@Nonnull Configuration config) {
        property = config.get(category.getPath(),key,defaultValue.toStringList(),comment,isListSizeFixed,maxListSize,validatedPattern);
        load(property);
    }

    @Override
    protected void load(@Nonnull Property property) {
        value = factory.get();

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

    //**********
    // Collection
    //**********

    @Override
    public int size() {
        return value.size();
    }

    @Override
    public boolean isEmpty() {
        return value.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return value.contains(o);
    }

    @Override
    public Iterator<ValueType> iterator() {
        if(isFinal || isListSizeFixed) return Collections.unmodifiableCollection(value).iterator();
        return value.iterator();
    }

    @Override
    public Object[] toArray() {
        return value.toArray();
    }

    @Override
    public <T> T[] toArray(@Nonnull T[] a) {
        return value.toArray(a);
    }

    @Override
    public boolean add(@Nonnull ValueType type) {
        if(isFinal || isListSizeFixed) throw new UnsupportedOperationException();
        return value.add(type);
    }

    @Override
    public boolean remove(Object o) {
        if(isFinal || isListSizeFixed) throw new UnsupportedOperationException();
        return value.remove(o);
    }

    @Override
    public boolean containsAll(@Nonnull Collection<?> c) {
        return value.containsAll(c);
    }

    @Override
    public boolean addAll(@Nonnull Collection<? extends ValueType> c) {
        if(isFinal || isListSizeFixed) throw new UnsupportedOperationException();
        return value.addAll(c);
    }

    @Override
    public boolean removeAll(@Nonnull Collection<?> c) {
        if(isFinal || isListSizeFixed) throw new UnsupportedOperationException();
        return value.removeAll(c);
    }

    @Override
    public boolean removeIf(Predicate<? super ValueType> filter) {
        if(isFinal || isListSizeFixed) throw new UnsupportedOperationException();
        return value.removeIf(filter);
    }

    @Override
    public boolean retainAll(@Nonnull Collection<?> c) {
        if(isFinal || isListSizeFixed) throw new UnsupportedOperationException();
        return value.retainAll(c);
    }

    @Override
    public void clear() {
        if(isFinal || isListSizeFixed) throw new UnsupportedOperationException();
        value.clear();
    }
}
