/*
 * Copyright 2026 QGMoe
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
 * 版权所有 2026 QGMoe
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

package moe.qingu.orbtellus.geography.fluidphysics.finite.flow;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.init.Blocks;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.FluidRegistry;

import javax.annotation.Nonnull;

/**
 * 存放常用的 Flowing 逻辑实例
 * 之所以不放在具体逻辑类是为了便利单元测试
 * @author QiguaiAAAA
 */
public final class FiniteFlowings {
    public static final FiniteFlowingVanilla WATER_FLOW = new FiniteFlowingVanilla(Blocks.FLOWING_WATER,Blocks.WATER, FluidRegistry.WATER);
    public static final FiniteFlowingVanilla LAVA_FLOW = new FiniteFlowingVanilla(Blocks.FLOWING_LAVA,Blocks.LAVA,FluidRegistry.LAVA);
    private static final Object2ObjectOpenHashMap<BlockFluidClassic,FiniteFlowingClassic> CLASSIC_FLOWS = new Object2ObjectOpenHashMap<>();

    @Nonnull
    public static FiniteFlowingClassic of(final @Nonnull BlockFluidClassic block){
        return CLASSIC_FLOWS.computeIfAbsent(block, FiniteFlowingClassic::new);
    }
}
