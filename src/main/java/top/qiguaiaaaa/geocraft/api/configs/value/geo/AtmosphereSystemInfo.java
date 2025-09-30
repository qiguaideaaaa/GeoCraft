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

package top.qiguaiaaaa.geocraft.api.configs.value.geo;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import top.qiguaiaaaa.geocraft.api.configs.value.json.ConfigurableJSONObject;
import top.qiguaiaaaa.geocraft.api.util.exception.ConfigParseError;

import javax.annotation.Nonnull;

/**
 * 配置文件中配置的大气信息，基于JSON对象
 * @author QiguaiAAAA
 */
public class AtmosphereSystemInfo extends ConfigurableJSONObject {
    public static final String FILED_ID = "id";
    protected String name;
    protected AtmosphereSystemType type;
    public AtmosphereSystemInfo(@Nonnull String jsonStr) {
        super(jsonStr);
        init(jsonStr);
    }

    public AtmosphereSystemInfo(@Nonnull JsonObject object){
        super(object);
        init(toString());
    }

    protected void init(@Nonnull String jsonStr){
        JsonElement element = json.get(FILED_ID);
        if(!element.isJsonPrimitive()) throw new ConfigParseError("Invalid id in AtmosphereSystemInfo "+jsonStr);
        name = element.getAsString().trim();
        if(name.equals("")){
            name = AtmosphereSystemType.NO_ATMOSPHERE_SYSTEM.configName;
        }
        json.addProperty(FILED_ID,name);
        type = AtmosphereSystemType.getInstanceByString(name);
    }

    @Nonnull
    public String getName() {
        return name;
    }

    @Nonnull
    public AtmosphereSystemType getType() {
        return type;
    }

    @Nonnull
    public JsonObject getDetails(){
        return json;
    }

    public static class Final extends AtmosphereSystemInfo{
        public static final Final FINAL = new Final();
        private Final() {
            super("{\""+FILED_ID+"\":\""+AtmosphereSystemType.NO_ATMOSPHERE_SYSTEM.configName+"\"}");
        }

        @Nonnull
        @Override
        public String getName() {
            return AtmosphereSystemType.NO_ATMOSPHERE_SYSTEM.configName;
        }

        @Nonnull
        @Override
        public AtmosphereSystemType getType() {
            return AtmosphereSystemType.NO_ATMOSPHERE_SYSTEM;
        }

        @Nonnull
        @Override
        public JsonObject getDetails() {
            JsonObject object = new JsonObject();
            object.addProperty(FILED_ID,AtmosphereSystemType.NO_ATMOSPHERE_SYSTEM.configName);
            return object;
        }
    }
}
