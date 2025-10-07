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

package top.qiguaiaaaa.geocraft.handler.event;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import top.qiguaiaaaa.geocraft.api.block.IPermeableBlock;
import top.qiguaiaaaa.geocraft.api.configs.value.minecraft.ConfigurableBiome;
import top.qiguaiaaaa.geocraft.api.configs.value.minecraft.ConfigurableBlockState;
import top.qiguaiaaaa.geocraft.api.event.player.ExtendedUseHoeEvent;
import top.qiguaiaaaa.geocraft.api.setting.GeoSoilSetting;
import top.qiguaiaaaa.geocraft.block.IBlockSoil;
import top.qiguaiaaaa.geocraft.configs.SoilConfig;

import java.util.Map;

import static top.qiguaiaaaa.geocraft.api.block.BlockProperties.HUMIDITY;

/**
 * @author QiguaiAAAA
 */
public final class SoilEventHandler {
    @SubscribeEvent
    public static void onHoeUseEvent(ExtendedUseHoeEvent event){
        World world = event.getWorld();
        BlockPos pos = event.getPos();
        EnumFacing facing = event.getFacing();
        EntityPlayer player = event.getEntityPlayer();
        IBlockState curState = world.getBlockState(pos);
        Block block = curState.getBlock();

        if(!(block instanceof IBlockSoil)) return;

        IPermeableBlock farmland = (IPermeableBlock)Blocks.FARMLAND;

        if (facing != EnumFacing.DOWN && world.isAirBlock(pos.up())) {
            if (block == Blocks.GRASS || block == Blocks.GRASS_PATH) {
                int humidity = curState.getValue(HUMIDITY);
                setBlock_Hoe(player, world, pos, farmland.getQuantaState(Blocks.FARMLAND.getDefaultState(), FluidRegistry.WATER,humidity));
                event.setResult(Event.Result.ALLOW);
                return;
            }

            if (block == Blocks.DIRT) {
                int humidity = curState.getValue(HUMIDITY);
                switch (curState.getValue(BlockDirt.VARIANT)) {
                    case PODZOL:return;
                    case DIRT:
                        setBlock_Hoe(player, world, pos, farmland.getQuantaState(Blocks.FARMLAND.getDefaultState(),FluidRegistry.WATER,humidity));
                        break;
                    case COARSE_DIRT:
                        setBlock_Hoe(player, world, pos, Blocks.DIRT.getDefaultState()
                                .withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.DIRT)
                                .withProperty(HUMIDITY,humidity));
                        break;
                    default:return;
                }
                event.setResult(Event.Result.ALLOW);
            }
        }
    }

    /**
     * @see net.minecraft.item.ItemHoe#setBlock(ItemStack, EntityPlayer, World, BlockPos, IBlockState) 
     */
    private static void setBlock_Hoe(EntityPlayer player, World worldIn, BlockPos pos, IBlockState state) {
        worldIn.playSound(player, pos, SoundEvents.ITEM_HOE_TILL, SoundCategory.BLOCKS, 1f, 1f);

        if (!worldIn.isRemote) {
            worldIn.setBlockState(pos, state, Constants.BlockFlags.SEND_TO_CLIENTS| Constants.BlockFlags.NOTIFY_NEIGHBORS | Constants.BlockFlags.RERENDER_MAIN_THREAD);
        }
    }

    public static void onPostInit(FMLPostInitializationEvent event){
        for(ConfigurableBiome biome: SoilConfig.GENERATION_BIOME_BLACK_LIST){
            Biome b = biome.getBiome();
            if(b == null) continue;
            GeoSoilSetting.addBiomeToGenerationBlacklist(b);
        }
        for(Map.Entry<ConfigurableBiome, ConfigurableBlockState> entry:SoilConfig.WATER_PROTECTION_BLOCK.getValue().entrySet()){
            final Biome biome = entry.getKey().getBiome();
            if(biome == null) continue;
            final IBlockState state = entry.getValue().getState();
            if(state == null) continue;
            GeoSoilSetting.setBiomeWaterProtectionBlock(biome,state);
        }
    }
}
