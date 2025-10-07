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

package top.qiguaiaaaa.geocraft.mixin.atmosphere.block;

import net.minecraft.block.BlockCauldron;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.qiguaiaaaa.geocraft.api.atmosphere.AtmosphereSystemManager;
import top.qiguaiaaaa.geocraft.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.geocraft.api.atmosphere.accessor.IAtmosphereAccessor;
import top.qiguaiaaaa.geocraft.api.property.TemperatureProperty;
import top.qiguaiaaaa.geocraft.configs.AtmosphereConfig;

@Mixin(value = BlockCauldron.class)
public class BlockCauldronMixin {
    @Inject(method = "fillWithRain",at =@At("HEAD"),cancellable = true)
    public void fillWithRain(World worldIn, BlockPos pos, CallbackInfo ci) {
        if(AtmosphereConfig.ALLOW_CAULDRON_GET_INFINITE_WATER.getValue()) return;
        ci.cancel();
        if (worldIn.rand.nextInt(20) != 1) {
            return;
        }
        IAtmosphereAccessor accessor = AtmosphereSystemManager.getAtmosphereAccessor(worldIn,pos,true);
        if(accessor == null) return;
        Atmosphere atmosphere = accessor.getAtmosphereHere();
        if(atmosphere == null) return;
        if(atmosphere.drainWater(333,pos,true) <333) return;
        double temp = accessor.getTemperature(false);

        if (temp< TemperatureProperty.ICE_POINT) return;

        IBlockState iblockstate = worldIn.getBlockState(pos);
        if (iblockstate.getValue(BlockCauldron.LEVEL) < 3) {
            atmosphere.drainWater(333,pos,false);
            worldIn.setBlockState(pos, iblockstate.cycleProperty(BlockCauldron.LEVEL), Constants.BlockFlags.SEND_TO_CLIENTS);
        }
    }
}
