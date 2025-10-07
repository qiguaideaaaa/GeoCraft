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

package top.qiguaiaaaa.geocraft.api.setting;

import net.minecraft.block.BlockLiquid;
import net.minecraftforge.fluids.Fluid;
import top.qiguaiaaaa.geocraft.api.util.FluidUtil;

import java.util.HashSet;

/**
 * 查询天圆地方关于流体的配置
 * @author QiguaiAAAA
 */
public final class GeoFluidSetting {
    private static final HashSet<String> FLUIDS_NOT_TO_BE_PHYSICAL = new HashSet<>();
    private static final HashSet<String> FLUIDS_BUCKET_TO_BE_VANILLA = new HashSet<>();

    /**
     * 设置指定流体是否需要被物理化
     * @param fluidName 流体名
     * @param physical 是否需要物理化
     */
    public static void setFluidToBePhysical(String fluidName,boolean physical){
        if(physical) FLUIDS_NOT_TO_BE_PHYSICAL.remove(fluidName);
        else FLUIDS_NOT_TO_BE_PHYSICAL.add(fluidName);
    }

    public static void setFluidToUseVanillaBucketMode(String fluidName,boolean vanilla){
        if(vanilla) FLUIDS_BUCKET_TO_BE_VANILLA.add(fluidName);
        else FLUIDS_BUCKET_TO_BE_VANILLA.remove(fluidName);
    }

    /**
     * 指定流体是否需要物理化
     * @param fluid 流体
     * @return 若需要,则返回true
     */
    public static boolean isFluidToBePhysical(Fluid fluid){
        if(fluid == null) return false;
        return !FLUIDS_NOT_TO_BE_PHYSICAL.contains(fluid.getName());
    }

    public static boolean isFluidToBePhysical(BlockLiquid fluid){
        return isFluidToBePhysical(FluidUtil.getFluid(fluid));
    }

    public static boolean isFluidToUseVanillaBucketMode(Fluid fluid){
        if(fluid == null) return true;
        return FLUIDS_BUCKET_TO_BE_VANILLA.contains(fluid.getName());
    }
}
