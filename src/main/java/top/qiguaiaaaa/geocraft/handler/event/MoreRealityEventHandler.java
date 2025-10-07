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

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import net.minecraft.stats.StatList;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import top.qiguaiaaaa.geocraft.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.geocraft.api.block.BlockProperties;
import top.qiguaiaaaa.geocraft.api.block.IPermeableBlock;
import top.qiguaiaaaa.geocraft.api.configs.value.minecraft.ConfigurableFluid;
import top.qiguaiaaaa.geocraft.api.event.atmosphere.AtmosphereUpdateEvent;
import top.qiguaiaaaa.geocraft.api.event.block.StaticLiquidUpdateEvent;
import top.qiguaiaaaa.geocraft.api.event.player.FillGlassBottleEvent.FillGlassBottleOnFluidEvent;
import top.qiguaiaaaa.geocraft.api.setting.GeoFluidSetting;
import top.qiguaiaaaa.geocraft.api.util.FluidUtil;
import top.qiguaiaaaa.geocraft.block.IBlockSoil;
import top.qiguaiaaaa.geocraft.geography.fluid_physics.reality.MoreRealityFluidPhysicsCore;
import top.qiguaiaaaa.geocraft.handler.ServerStatusMonitor;
import top.qiguaiaaaa.geocraft.mixin.common.entity.EntityFallingBlockAccessor;
import top.qiguaiaaaa.geocraft.util.WaterUtil;
import top.qiguaiaaaa.geocraft.util.fluid.FluidMixinUtil;
import top.qiguaiaaaa.geocraft.util.fluid.FluidOperationUtil;
import top.qiguaiaaaa.geocraft.util.wrappers.InfiniteFluidBucketWrapper;
import top.qiguaiaaaa.geocraft.util.wrappers.PhysicsBlockLiquidWrapper;
import top.qiguaiaaaa.geocraft.util.wrappers.PhysicsFluidBlockWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

import static net.minecraft.block.BlockLiquid.LEVEL;
import static net.minecraftforge.fluids.FluidUtil.tryPlaceFluid;
import static top.qiguaiaaaa.geocraft.configs.FluidPhysicsConfig.*;

public final class MoreRealityEventHandler {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onBucketEvent(FillBucketEvent event){
        World worldIn = event.getWorld();
        EntityPlayer playerIn = event.getEntityPlayer();
        ItemStack itemstack = event.getEmptyBucket();
        RayTraceResult raytraceresult = event.getTarget();
        if (raytraceresult == null) return;
        else if (raytraceresult.typeOfHit != RayTraceResult.Type.BLOCK) return;

        BlockPos pos = raytraceresult.getBlockPos();
        if (!playerIn.canPlayerEdit(pos.offset(raytraceresult.sideHit), raytraceresult.sideHit, itemstack)) {
            return;
        }

        Item item = itemstack.getItem();
        IBlockState state = worldIn.getBlockState(pos);
        if (item == Items.BUCKET) {
            if(!FluidUtil.isFluid(state)) pos = pos.offset(raytraceresult.sideHit); //非满的水方块会透过去
            if (!worldIn.isBlockModifiable(playerIn, pos)) return;

            FluidStack stack = FluidOperationUtil.tryDrainFluid(worldIn,pos, Fluid.BUCKET_VOLUME,bucketFindFluidMaxDistance.getValue(),false);
            if(stack == null) return;
            if((allowBucketToDrainFluidWhenAmountIsSmallerThan1000mB.getValue() || playerIn.capabilities.isCreativeMode) && stack.amount>0){
                FluidOperationUtil.tryDrainFluid(worldIn,pos,Fluid.BUCKET_VOLUME,bucketFindFluidMaxDistance.getValue(),true);
                playerIn.playSound(SoundEvents.ITEM_BUCKET_FILL, 1.0F, 1.0F);
                return;
            }else{
                if(stack.amount < Fluid.BUCKET_VOLUME) return;
                FluidOperationUtil.tryDrainFluid(worldIn,pos,Fluid.BUCKET_VOLUME,bucketFindFluidMaxDistance.getValue(),true);
            }
            event.setResult(Event.Result.ALLOW);
            playerIn.addStat(Objects.requireNonNull(StatList.getObjectUseStats(item)));
            ItemStack filledBucket;
            if (stack.getFluid() == FluidRegistry.WATER) {
                playerIn.playSound(SoundEvents.ITEM_BUCKET_FILL, 1.0F, 1.0F);
                filledBucket = new ItemStack(Items.WATER_BUCKET,1);
            } else if (stack.getFluid() == FluidRegistry.LAVA) {
                playerIn.playSound(SoundEvents.ITEM_BUCKET_FILL_LAVA, 1.0F, 1.0F);
                filledBucket = new ItemStack(Items.LAVA_BUCKET,1);
            } else {
                playerIn.playSound(SoundEvents.ITEM_BUCKET_FILL, 1.0F, 1.0F);
                filledBucket = net.minecraftforge.fluids.FluidUtil.getFilledBucket(stack);
            }
            if(stack.amount == Fluid.BUCKET_VOLUME){
                event.setFilledBucket(filledBucket);
            }
            return;
        }
        if(item != Items.WATER_BUCKET && item != Items.LAVA_BUCKET) return;
        boolean blockReplaceable = state.getBlock().isReplaceable(worldIn,pos);
        if(!FluidUtil.isFluid(state) && (!blockReplaceable || raytraceresult.sideHit != EnumFacing.UP))
            pos = pos.offset(raytraceresult.sideHit);
        else if(FluidUtil.isFullFluid(worldIn,pos,state)) pos = pos.offset(raytraceresult.sideHit);
        if (!playerIn.canPlayerEdit(pos, raytraceresult.sideHit, itemstack)) {
            return;
        }
        FluidStack stack;
        if(item == Items.WATER_BUCKET){
            if(GeoFluidSetting.isFluidToUseVanillaBucketMode(FluidRegistry.WATER)) return;
            stack = new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME);
            boolean success = tryPlaceFluid(playerIn, worldIn, pos, InfiniteFluidBucketWrapper.INFINITE_WATER_BUCKET_WRAPPER,stack);
            if(!success){
                event.setCanceled(true);
                return;
            }
        }else {
            if(GeoFluidSetting.isFluidToUseVanillaBucketMode(FluidRegistry.LAVA)) return;
            stack = new FluidStack(FluidRegistry.LAVA, Fluid.BUCKET_VOLUME);
            boolean success = tryPlaceFluid(playerIn, worldIn, pos, InfiniteFluidBucketWrapper.INFINITE_LAVA_BUCKET_WRAPPER,stack);
            if(!success){
                event.setCanceled(true);
                return;
            }
        }
        event.setResult(Event.Result.ALLOW);
        if (playerIn instanceof EntityPlayerMP) {
            CriteriaTriggers.PLACED_BLOCK.trigger((EntityPlayerMP)playerIn, pos, itemstack);
        }
        playerIn.addStat(Objects.requireNonNull(StatList.getObjectUseStats(item)));
        itemstack.shrink(1);
        if(itemstack.isEmpty()) itemstack = new ItemStack(Items.BUCKET,1);
        event.setFilledBucket(itemstack);
    }

    @SubscribeEvent
    public void onPlayerPlacedBlock(BlockEvent.PlaceEvent event){
        if(!onBlockReplaced(event.getWorld(), event.getPos(),event.getBlockSnapshot().getReplacedBlock(),event.getBlockSnapshot().getCurrentBlock(),PlaceSource.PLAYER,event.getEntity())){
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onEntityPlacedBlock(BlockEvent.EntityPlaceEvent event){
        if(event instanceof BlockEvent.PlaceEvent) return;
        PlaceSource source = PlaceSource.OTHERS;
        Entity entity = event.getEntity();
        if(entity instanceof EntityFallingBlock) source = PlaceSource.FALLING_BLOCK;
        else if(entity instanceof EntityEnderman) source = PlaceSource.ENDER_MAN;
        if(!onBlockReplaced(event.getWorld(),event.getPos(),event.getBlockSnapshot().getCurrentBlock(),event.getBlockSnapshot().getReplacedBlock(),source,entity)){
            event.setCanceled(true);
        }
    }

    public static boolean onBlockReplaced(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState currentState, @Nonnull IBlockState replacedState, @Nonnull PlaceSource source, @Nullable Entity sourceEntity){
        final Fluid fluid = FluidUtil.getFluid(currentState);
        if(fluid == null) return true;
        final Block block = currentState.getBlock();
        if(block instanceof BlockLiquid){
            PhysicsBlockLiquidWrapper wrapper = new PhysicsBlockLiquidWrapper((BlockLiquid) block,world,pos);
            wrapper.setIgnoreCurrentPos(true);
            int quanta = FluidUtil.getFluidQuanta(world, pos,currentState);
            int curQuanta = 0,canFillQuanta = 0;
            Block placeBlock = replacedState.getBlock();
            IPermeableBlock permeable = null;

            permeable:{
                switch (source){
                    case OTHERS:
                    case ENDER_MAN:
                    default: break permeable;
                    case PLAYER:
                    case FALLING_BLOCK:
                }
                if(placeBlock instanceof IPermeableBlock){
                    permeable = (IPermeableBlock) placeBlock;
                    curQuanta = permeable.getQuanta(replacedState,fluid);
                    canFillQuanta = permeable.addQuanta(world,pos,replacedState,fluid,quanta,false);
                    if(canFillQuanta <=0) {
                        canFillQuanta = 0;
                        break permeable;
                    }
                    if(quanta>canFillQuanta) break permeable; //在最后的时候再处理
                    IBlockState quantaState = null;
                    if(source == PlaceSource.FALLING_BLOCK){
                        if(!(sourceEntity instanceof EntityFallingBlock)){
                            canFillQuanta = 0;
                            break permeable;
                        }
                        quantaState = permeable.getQuantaState(replacedState,fluid,curQuanta+canFillQuanta);
                        if(quantaState == null){
                            canFillQuanta = 0;
                            break permeable;
                        }
                    }

                    switch (source) {
                        case PLAYER:
                            permeable.addQuanta(world,pos,replacedState,fluid,quanta,true);
                            break;
                        case FALLING_BLOCK:
                            ((EntityFallingBlockAccessor)sourceEntity).setFallTile(quantaState);
                            break;
                        default: {
                            canFillQuanta = 0;
                            break permeable;
                        }
                    }
                    return true;
                }
            }

            quanta -= canFillQuanta;

            int amount = quanta*FluidUtil.ONE_IN_EIGHT_OF_BUCKET_VOLUME;
            wrapper.setExpectedQuanta(quanta);
            FluidStack stack = new FluidStack(fluid,amount);
            int available = wrapper.fill(stack,false);
            if(available < amount){
                return false;
            }
            wrapper.fill(stack,true);
            if(canFillQuanta>0){
                switch (source) {
                    case PLAYER:
                        permeable.addQuanta(world,pos,replacedState,fluid,canFillQuanta,true);
                        break;
                    case FALLING_BLOCK:
                        if(sourceEntity == null) break;
                        IBlockState quantaState = permeable.getQuantaState(replacedState,fluid,curQuanta+canFillQuanta);
                        if(quantaState == null) break;
                        ((EntityFallingBlockAccessor)sourceEntity).setFallTile(quantaState);
                        break;
                }
            }
        }else if(block instanceof BlockFluidBase){
            PhysicsFluidBlockWrapper wrapper = new PhysicsFluidBlockWrapper((IFluidBlock) block,world,pos);
            wrapper.setIgnoreCurrentPos(true);
            int amount = FluidMixinUtil.getAmountForBlockFluidBase(currentState);
            FluidStack stack = new FluidStack(fluid,amount);
            int available = wrapper.fill(stack,false);
            if(available < amount){
                return false;
            }
            wrapper.fill(stack,true);
        }
        return true;
    }

    public enum PlaceSource{
        PLAYER,
        FALLING_BLOCK,
        ENDER_MAN,
        OTHERS
    }

    @SubscribeEvent
    public void onGlassBottleFilled(FillGlassBottleOnFluidEvent event){
        World worldIn = event.getWorld();
        EntityPlayer player = event.getEntityPlayer();
        RayTraceResult rayTraceResult = event.getRayTraceResult();
        ItemStack itemStack = event.getEmptyGlassBottle();

        event.setCanceled(true);

        if (rayTraceResult == null) return;

        if (rayTraceResult.typeOfHit != RayTraceResult.Type.BLOCK) return;
        BlockPos blockpos = rayTraceResult.getBlockPos();
        if (!player.canPlayerEdit(blockpos.offset(rayTraceResult.sideHit), rayTraceResult.sideHit, itemStack)) {
            return;
        }
        IBlockState state = worldIn.getBlockState(blockpos);
        if(!FluidUtil.isFluid(state)){
            blockpos = blockpos.offset(rayTraceResult.sideHit);
            state = worldIn.getBlockState(blockpos);
        }
        if(!worldIn.isBlockModifiable(player, blockpos))
            return;
        if(state.getMaterial() != Material.WATER)
            return;

        final int target = 3*FluidUtil.ONE_IN_EIGHT_OF_BUCKET_VOLUME;
        FluidStack stack = FluidOperationUtil.tryDrainFluid(worldIn,blockpos,target,bottleFindFluidMaxDistance.getValue(),false);
        if(stack == null || stack.amount<target)
            return;
        event.setCanceled(false);
        event.setResult(Event.Result.ALLOW);
        FluidOperationUtil.tryDrainFluid(worldIn,blockpos,target,bottleFindFluidMaxDistance.getValue(),true);
        worldIn.playSound(player, player.posX, player.posY, player.posZ, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0F, 1.0F);
        event.setFilledGlassBottle(PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionTypes.WATER));
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void afterStaticWaterUpdate(StaticLiquidUpdateEvent.After event){
        if(event.getLiquid() != FluidRegistry.WATER) return;
        IBlockState state = event.getState();
        World worldIn = event.getWorld();
        if(!event.isRandomTick()){
            if(ServerStatusMonitor.isServerCloselyLagging()) return;
            if(worldIn.rand.nextInt(200) >0) return; //因为压强计算频繁更新，需要降低概率
        }
        BlockPos pos = event.getPos();
        int oldMeta = state.getValue(LEVEL);
        state = MoreRealityFluidPhysicsCore.evaporateWater(worldIn,pos,state,worldIn.rand);
        if(state == null){
            event.setResult(Event.Result.ALLOW);
            event.setNewState(Blocks.AIR.getDefaultState());
            return;
        } else if(state.getValue(LEVEL) != oldMeta){
            event.setResult(Event.Result.ALLOW);
            event.setNewState(state);
            return;
        }
        IBlockState newState = MoreRealityFluidPhysicsCore.freezeWater(worldIn,pos,state,worldIn.rand);
        if(newState != state){
            event.setResult(Event.Result.ALLOW);
            event.setNewState(newState);
        }
    }
    @SubscribeEvent
    public void onAtmosphereRainAndSnow(AtmosphereUpdateEvent.RainAndSnow event){
        Atmosphere atmosphere = event.getAtmosphere();
        World world = event.getWorld();
        BlockPos randPos = event.getRandPos();
        if (WaterUtil.canSnowAt(world,randPos, true)) {
            atmosphere.drainWater(FluidUtil.ONE_IN_EIGHT_OF_BUCKET_VOLUME,randPos,false);
            event.setResult(Event.Result.ALLOW);
            event.setState(Blocks.SNOW_LAYER.getDefaultState());
        }else if(MoreRealityFluidPhysicsCore.canRainAt(world,randPos)){
            atmosphere.drainWater(FluidUtil.ONE_IN_EIGHT_OF_BUCKET_VOLUME,randPos,false);
            event.setResult(Event.Result.ALLOW);
            event.setState(Blocks.FLOWING_WATER.getDefaultState().withProperty(BlockLiquid.LEVEL,7));
        }
    }
    public static void onPostInit(FMLPostInitializationEvent event){
        for(ConfigurableFluid fluid:fluidsNotToSimulate){
            if(fluid == null) continue;
            GeoFluidSetting.setFluidToBePhysical(fluid.toString(),false);
        }
        for(ConfigurableFluid fluid:fluidsWhoseBucketsBehavesAsVanillaBuckets){
            if(fluid == null) continue;
            GeoFluidSetting.setFluidToUseVanillaBucketMode(fluid.toString(),true);
        }
        if(!GeoFluidSetting.isFluidToBePhysical(FluidRegistry.WATER)){
            Blocks.WATER.setTickRandomly(false);
        }
        if(!GeoFluidSetting.isFluidToBePhysical(FluidRegistry.LAVA)){
            Blocks.LAVA.setTickRandomly(false);
        }
    }
}
