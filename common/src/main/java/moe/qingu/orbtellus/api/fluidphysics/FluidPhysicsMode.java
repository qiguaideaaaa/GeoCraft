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

package moe.qingu.orbtellus.api.fluidphysics;

import moe.qingu.nickel.util.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 当前游戏使用的水物理模拟模式<br/>
 * 只有天圆地方自己的模拟模式
 */
public enum FluidPhysicsMode {
    VANILLA("原版","原版模式"),
    CLASSIC("VANILLA_LIKE","VANILLA LIKE","经典","经典模式","經典","經典模式"),
    FINITE("MORE_REALITY","MORE REALITY","有限","有限模式");
    private static final FluidPhysicsMode[] MODES = values();
    private static FluidPhysicsMode CURRENT_MODE = FINITE;

    private final String[] alias;
    private IFluidOperationChecker checker;

    FluidPhysicsMode(final @Nonnull String... alias){
        this.alias = alias;
    }

    public void setChecker(@Nonnull final IFluidOperationChecker checker) {
        this.checker = checker;
    }

    @Nonnull
    public static FluidPhysicsMode getCurrentMode() {
        return CURRENT_MODE;
    }

    @Nonnull
    public IFluidOperationChecker getChecker() {
        return checker;
    }

    private boolean isStringMatched(@Nullable final String s){
        if(s == null) return false;
        if(toString().equalsIgnoreCase(s)) return true;
        for(final String a:alias) if(a.equalsIgnoreCase(s)) return true;
        return false;
    }

    /**
     * 将对应字符串反序列化为对应模拟模式
     * @param content 字符串
     * @return 模拟模式
     */
    public static @Nonnull FluidPhysicsMode getInstanceByString(@Nonnull final String content) {
        final String t = StringUtils.strip(content);
        for(FluidPhysicsMode mode:MODES) if(mode.isStringMatched(t)) return mode;
        return FINITE;
    }

    public static void setCurrentMode(@Nonnull final FluidPhysicsMode currentMode) {
        CURRENT_MODE = currentMode;
    }
}
