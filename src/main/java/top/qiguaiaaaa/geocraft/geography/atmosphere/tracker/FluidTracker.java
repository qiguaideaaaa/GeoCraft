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

package top.qiguaiaaaa.geocraft.geography.atmosphere.tracker;

import net.minecraft.util.math.BlockPos;
import top.qiguaiaaaa.geocraft.GeoCraft;
import top.qiguaiaaaa.geocraft.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.geocraft.api.atmosphere.layer.Layer;
import top.qiguaiaaaa.geocraft.api.property.FluidProperty;
import top.qiguaiaaaa.geocraft.api.state.FluidState;
import top.qiguaiaaaa.geocraft.api.state.GeographyState;
import top.qiguaiaaaa.geocraft.api.atmosphere.tracker.InformationLoggingTracker;
import top.qiguaiaaaa.geocraft.api.util.io.FileLogger;

public class FluidTracker extends InformationLoggingTracker {
    protected final FluidProperty propertyToTrack;
    protected final  BlockPos pos;
    public FluidTracker(FileLogger logger, FluidProperty property, BlockPos pos, int time) {
        super(logger, time);
        propertyToTrack = property;
        this.pos = pos;
    }

    @Override
    public void notify(Atmosphere atmosphere) {
        Layer layer = atmosphere.getLayer(pos);
        if(layer == null){
            GeoCraft.getLogger().warn("Couldn't track {} at {} , because there doesn't exist an atmosphere layer.",propertyToTrack.getRegistryName(),pos);
            return;
        }
        GeographyState s = layer.getState(propertyToTrack);
        if(s == null){
            GeoCraft.getLogger().warn("Couldn't track {} at {} , because the layer there doesn't have this property.",propertyToTrack.getRegistryName(),pos);
            return;
        }
        if(!(s instanceof FluidState)){
            GeoCraft.getLogger().warn("Couldn't track {} at {} , because this isn't a valid fluid state.",propertyToTrack.getRegistryName(),pos);
            return;
        }
        FluidState state = (FluidState) s;
        String msg = String.format("%d,%d",atmosphere.getAtmosphereWorldInfo().getWorld().getTotalWorldTime(),state.getAmount());
        logger.println(msg);
        GeoCraft.getLogger().info("track {} ({} mB)",propertyToTrack.getRegistryName(),msg);
        nowTime++;
        this.checkLoggingTime(atmosphere);
    }
}
