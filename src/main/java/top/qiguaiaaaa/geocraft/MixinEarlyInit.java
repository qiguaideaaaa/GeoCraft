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

package top.qiguaiaaaa.geocraft;

import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.Side;
import top.qiguaiaaaa.geocraft.configs.GeneralConfig;
import top.qiguaiaaaa.geocraft.configs.SimulationConfig;
import top.qiguaiaaaa.geocraft.api.configs.value.geo.SimulationMode;
import zone.rong.mixinbooter.IEarlyMixinLoader;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
@IFMLLoadingPlugin.Name(GeoCraft.MODID)
@IFMLLoadingPlugin.MCVersion(ForgeVersion.mcVersion)
public class MixinEarlyInit implements IFMLLoadingPlugin, IEarlyMixinLoader {
    public static final boolean isClient = FMLLaunchHandler.side() == Side.CLIENT;

    @Override
    public List<String> getMixinConfigs() {
        CommonProxy.earlyInit();
        List<String> mixinList =  Lists.newArrayList("mixins.geocraft.json");
        SimulationMode mode = SimulationConfig.SIMULATION_MODE.getValue();
        switch (mode){
            case VANILLA:{
                break;
            }
            case VANILLA_LIKE:{
                mixinList.add("mixins.geocraft_vanilla_like.json");
                break;
            }
            case MORE_REALITY:
            default:{
                mixinList.add("mixins.geocraft_reality.json");
                break;
            }
        }
        if(!GeneralConfig.ALLOW_CLIENT_TO_READ_HUMIDITY_DATA.getValue()){
            mixinList.add("mixins/ground_water/mixins.geocraft_client_fake.json");
        }
        mixinList.add("mixins/ground_water/mixins.geocraft_ground_water.json");
        mixinList.add("mixins.geocraft_atmosphere.json");
        System.out.println("GeoCraft's Fluid Physics is using mode "+mode);

        return mixinList;
    }

    @Override
    public String[] getASMTransformerClass() {
        return new String[0];
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {}

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}