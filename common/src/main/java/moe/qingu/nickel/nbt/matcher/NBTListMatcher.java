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

package moe.qingu.nickel.nbt.matcher;

import com.google.common.collect.Lists;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * @author QGMoe
 */
public final class NBTListMatcher extends NBTMatcher<NBTTagList> {

    private final Set<NBTMatcher<?>> matchers = new HashSet<>();

    public void expect(final @Nonnull NBTMatcher<?> matcher){
        matcher.setStrict(strict);
        matchers.add(matcher);
    }

    @Nonnull
    @Override
    public Class<NBTTagList> getMatchType() {
        return NBTTagList.class;
    }

    @Override
    public void setStrict(final boolean strict) {
        super.setStrict(strict);
        for(final NBTMatcher<?> matcher:matchers) matcher.setStrict(strict);
    }

    @Nonnull
    @Override
    public NBTTagList toNBT() {
        final NBTTagList list = new NBTTagList();
        for(final NBTMatcher<?> matcher:matchers) list.appendTag(matcher.toNBT());
        return list;
    }

    @Override
    public boolean _match(final @Nonnull NBTTagList list) {
        if(matchers.isEmpty()) return list.isEmpty();
        if(list.tagCount() < matchers.size()) return false;
        if(strict) return _matchStrict(list);
        for(final NBTMatcher<?> matcher:matchers)
            match:{
                for(final NBTBase nbt: list) if(matcher.match(nbt)) break match;
                return false;
            }
        return true;
    }

    private boolean _matchStrict(final @Nonnull NBTTagList list) {
        if(list.tagCount() != matchers.size()) return false;
        final List<NBTMatcher<?>> expects = new ArrayList<>(matchers);
        final List<NBTBase> candidates = Lists.newArrayList(list);
        while (!expects.isEmpty() && !candidates.isEmpty()){
            final Iterator<NBTBase> iterator = candidates.iterator();
            final NBTMatcher<?> expect = expects.remove(expects.size()-1);
            match:{
                while (iterator.hasNext())
                    if(expect.match(iterator.next())) {
                        iterator.remove();
                        break match;
                    }
                return false;
            }
        }
        return expects.isEmpty() && candidates.isEmpty();
    }

    @Override
    public int hashCode() {
        return matchers.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if(obj instanceof NBTListMatcher){
            return this.strict == ((NBTListMatcher) obj).strict && this.matchers.equals(((NBTListMatcher) obj).matchers);
        }else return false;
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder("[");
        boolean first = true;
        for(final NBTMatcher<?> matcher:matchers){
            if(first) first = false;
            else builder.append(',');
            builder.append(matcher);
        }
        return builder.append(']').toString();
    }
}
