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

package top.qiguaiaaaa.geocraft.geography.atmosphere.info;

import com.google.gson.JsonObject;
import top.qiguaiaaaa.geocraft.api.configs.value.geo.AtmosphereSystemInfo;
import top.qiguaiaaaa.geocraft.api.configs.value.geo.AtmosphereSystemType;
import top.qiguaiaaaa.geocraft.util.BaseUtil;
import top.qiguaiaaaa.geocraft.util.JsonUtil;

import javax.annotation.Nonnull;

/**
 * @author QiguaiAAAA
 */
public class QiguaiAtmosphereSystemInfo extends AtmosphereSystemInfo {
    public static final String WORLD_INFO = "world_info",
    WORLD_INFO_CAN_WATER_FREEZE = "can_water_freeze",
    WORLD_INFO_CAN_WATER_EVAPORATE = "can_water_evaporate",
    WORLD_INFO_RAIN_SMOOTHING_CONSTANT = "rain_smoothing_constant",
    WORLD_INFO_VAPOR_EXCHANGE_RATE = "vapor_exchange_rate";
    public QiguaiAtmosphereSystemInfo(@Nonnull String jsonStr) {
        super(jsonStr);
    }

    public QiguaiAtmosphereSystemInfo(@Nonnull JsonObject object) {
        super(object);
    }

    public QiguaiAtmosphereSystemInfo(@Nonnull JsonObject object, @Nonnull AtmosphereSystemType type) {
        super(object, type);
    }

    @Nonnull
    public JsonObject getWorldInfoJson(){
        return JsonUtil.readObject(json,WORLD_INFO);
    }

    public boolean canWaterFreeze(){
        return JsonUtil.readBoolean(getWorldInfoJson(),WORLD_INFO_CAN_WATER_FREEZE,true);
    }

    public boolean canWaterEvaporate(){
        return JsonUtil.readBoolean(getWorldInfoJson(),WORLD_INFO_CAN_WATER_EVAPORATE,true);
    }

    public int getRainSmoothingConstant(){
        return JsonUtil.readInt(getWorldInfoJson(),WORLD_INFO_RAIN_SMOOTHING_CONSTANT,4096,1,Integer.MAX_VALUE);
    }

    public double getVaporExchangeRate(){
        return JsonUtil.readDouble(getWorldInfoJson(),WORLD_INFO_VAPOR_EXCHANGE_RATE,1e-6,0,Double.POSITIVE_INFINITY);
    }

    public QiguaiAtmosphereSystemInfo waterFreeze(boolean enable){
        JsonUtil.setBoolean(getWorldInfoJson(),WORLD_INFO_CAN_WATER_FREEZE,enable);
        return this;
    }

    public QiguaiAtmosphereSystemInfo waterEvaporate(boolean enable){
        JsonUtil.setBoolean(getWorldInfoJson(),WORLD_INFO_CAN_WATER_EVAPORATE,enable);
        return this;
    }

    public QiguaiAtmosphereSystemInfo setRainSmoothingConstant(int constant){
        JsonUtil.setInt(getWorldInfoJson(),WORLD_INFO_RAIN_SMOOTHING_CONSTANT, BaseUtil.checkAndReturn(constant,1,Integer.MAX_VALUE));
        return this;
    }

    public QiguaiAtmosphereSystemInfo setVaporExchangeRate(double rate){
        JsonUtil.setDouble(getWorldInfoJson(),WORLD_INFO_VAPOR_EXCHANGE_RATE,BaseUtil.checkAndReturn(rate,0,Double.POSITIVE_INFINITY));
        return this;
    }

}
