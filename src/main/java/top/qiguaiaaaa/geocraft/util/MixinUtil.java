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

package top.qiguaiaaaa.geocraft.util;

import net.minecraft.init.Blocks;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Loader;
import top.qiguaiaaaa.geocraft.configs.FluidPhysicsConfig;
import top.qiguaiaaaa.geocraft.util.mixinapi.FluidSettable;
import top.qiguaiaaaa.geocraft.api.configs.value.geo.FluidPhysicsMode;

import java.util.ArrayList;
import java.util.List;

public final class MixinUtil {
    public static void linkLiquidWithFluid(){
        if(Blocks.FLOWING_WATER instanceof FluidSettable){
            ((FluidSettable) Blocks.FLOWING_WATER).setCorrespondingFluid(FluidRegistry.WATER);
            ((FluidSettable) Blocks.FLOWING_LAVA).setCorrespondingFluid(FluidRegistry.LAVA);
        }
        if(Blocks.WATER instanceof FluidSettable){
            ((FluidSettable) Blocks.WATER).setCorrespondingFluid(FluidRegistry.WATER);
            ((FluidSettable) Blocks.LAVA).setCorrespondingFluid(FluidRegistry.LAVA);
        }
    }
    public static List<String> getModMixins(){
        FluidPhysicsMode mode = FluidPhysicsConfig.FLUID_PHYSICS_MODE.getValue();
        List<String> mixins = new ArrayList<>();
        switch (mode){
            case MORE_REALITY:{
                mixins = getMoreRealityModeMixins(mixins);
                break;
            }
        }

        return mixins;
    }

    public static List<String> getMoreRealityModeMixins(List<String> mixinList){
        if(FluidPhysicsConfig.enableSupportForIC2.getValue() && Loader.isModLoaded("ic2"))
            mixinList.add("mixins/ic2/mixins.geocraft_reality.json");
        if(FluidPhysicsConfig.enableSupportForIE.getValue() && Loader.isModLoaded("immersiveengineering"))
            mixinList.add("mixins/immersiveengineering/mixins.geocraft_reality.json");
        return mixinList;
    }

}
