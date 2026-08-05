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

package moe.qingu.orbtellus.configs;

import moe.qingu.orbtellus.api.configs.ConfigCategory;
import moe.qingu.orbtellus.api.configs.GeoConfig;
import moe.qingu.orbtellus.api.configs.item.base.ConfigBoolean;
import moe.qingu.orbtellus.api.util.annotation.EarlyLoaded;
import net.minecraftforge.common.config.Config;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * @author QGMoe
 */
@EarlyLoaded
public final class OptimisationConfig {

    @Config.Comment({"对原版或模组的优化","Optimisations for Vanilla & Mods"})
    @GeoConfig.Support(since = "0.3.0-alpha.1")
    public static final ConfigCategory _优化类_ = new ConfigCategory("optimisation");

    @GeoConfig.Support(since = "0.3.0-alpha.1")
    @Config.Comment({"[原版]优化标记方块变更以用于网络同步的逻辑，极端场景下可减少最高 20% 左右的方块更新开销（尤其是在大量流体流动场景）。",
            "[Vanilla] Optimize the logic for marking block changes used for network synchronization. In extreme scenarios, this can reduce block update overhead by up to approximately 20% (especially in scenarios with massive fluid flow)."})
    @Config.RequiresMcRestart
    public static final ConfigBoolean _优化$方块放置同步_ = new ConfigBoolean(_优化类_,"skipRedundantBlockChangeRecords",true);

    public static void 加载优化注入(final @Nonnull List<String> collector){
        if(_优化$方块放置同步_.getValue()) collector.add("mixins/optimisation/mixins.orbtellus.optimisation.block_change.json");
    }
}
