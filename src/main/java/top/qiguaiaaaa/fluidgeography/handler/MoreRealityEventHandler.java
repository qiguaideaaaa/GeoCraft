package top.qiguaiaaaa.fluidgeography.handler;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
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
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.fluidgeography.api.event.atmosphere.AtmosphereUpdateEvent;
import top.qiguaiaaaa.fluidgeography.api.event.block.StaticLiquidUpdateEvent;
import top.qiguaiaaaa.fluidgeography.api.util.AtmosphereUtil;
import top.qiguaiaaaa.fluidgeography.util.FluidOperationUtil;
import top.qiguaiaaaa.fluidgeography.api.util.FluidUtil;
import top.qiguaiaaaa.fluidgeography.api.event.player.FillGlassBottleEvent.FillGlassBottleOnFluidEvent;
import top.qiguaiaaaa.fluidgeography.simulation.MoreRealitySimulationCore;
import top.qiguaiaaaa.fluidgeography.util.wrappers.InfiniteFluidBucketWrapper;

import java.util.Objects;

import static net.minecraft.block.BlockLiquid.LEVEL;
import static net.minecraftforge.fluids.FluidUtil.tryPlaceFluid;
import static top.qiguaiaaaa.fluidgeography.api.configs.SimulationConfig.*;

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

            FluidStack stack = FluidOperationUtil.tryDrainFluid(worldIn,pos, Fluid.BUCKET_VOLUME,bucketFindFluidMaxDistance.getValue().value,false);
            if(stack == null) return;
            if(allowBucketToDrainFluidWhenAmountIsSmallerThan1000mB.getValue().value && stack.amount>0){
                FluidOperationUtil.tryDrainFluid(worldIn,pos,Fluid.BUCKET_VOLUME,bucketFindFluidMaxDistance.getValue().value,true);
                return;
            }else{
                if(stack.amount < Fluid.BUCKET_VOLUME) return;
                FluidOperationUtil.tryDrainFluid(worldIn,pos,Fluid.BUCKET_VOLUME,bucketFindFluidMaxDistance.getValue().value,true);
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
            if(fluidsWhoseBucketsBehavesAsVanillaBuckets.containsEquivalent(FluidRegistry.WATER)) return;
            stack = new FluidStack(FluidRegistry.WATER, Fluid.BUCKET_VOLUME);
            boolean success = tryPlaceFluid(playerIn, worldIn, pos, InfiniteFluidBucketWrapper.INFINITE_WATER_BUCKET_WRAPPER,stack);
            if(!success){
                event.setCanceled(true);
                return;
            }
        }else {
            if(fluidsWhoseBucketsBehavesAsVanillaBuckets.containsEquivalent(FluidRegistry.LAVA)) return;
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
        FluidStack stack = FluidOperationUtil.tryDrainFluid(worldIn,blockpos,target,bottleFindFluidMaxDistance.getValue().value,false);
        if(stack == null || stack.amount<target)
            return;
        event.setCanceled(false);
        event.setResult(Event.Result.ALLOW);
        FluidOperationUtil.tryDrainFluid(worldIn,blockpos,target,bottleFindFluidMaxDistance.getValue().value,true);
        worldIn.playSound(player, player.posX, player.posY, player.posZ, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0F, 1.0F);
        event.setFilledGlassBottle(PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionTypes.WATER));
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public void afterStaticWaterUpdate(StaticLiquidUpdateEvent.After event){
        if(event.getLiquid() != FluidRegistry.WATER) return;
        IBlockState state = event.getState();
        World worldIn = event.getWorld();
        BlockPos pos = event.getPos();
        int oldMeta = state.getValue(LEVEL);
        state = MoreRealitySimulationCore.evaporateWater(worldIn,pos,state,worldIn.rand);
        if(state == null){
            event.setResult(Event.Result.ALLOW);
            event.setNewState(Blocks.AIR.getDefaultState());
            return;
        } else if(state.getValue(LEVEL) != oldMeta){
            event.setResult(Event.Result.ALLOW);
            event.setNewState(state);
            return;
        }
        IBlockState newState = MoreRealitySimulationCore.freezeWater(worldIn,pos,state,worldIn.rand);
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
        if (AtmosphereUtil.canSnowAt(world,randPos, true)) {
            if(atmosphere.add水量(-FluidUtil.ONE_IN_EIGHT_OF_BUCKET_VOLUME)){
                event.setResult(Event.Result.ALLOW);
                event.setState(Blocks.SNOW_LAYER.getDefaultState());
            }
        }else if(MoreRealitySimulationCore.canRainAt(world,randPos)){
            if(atmosphere.add水量(-FluidUtil.ONE_IN_EIGHT_OF_BUCKET_VOLUME)) {
                event.setResult(Event.Result.ALLOW);
                event.setState(Blocks.FLOWING_WATER.getDefaultState().withProperty(BlockLiquid.LEVEL,7));
            }
        }
    }
    public static void onPostInit(FMLPostInitializationEvent event){
        if(fluidsNotToSimulate.containsEquivalent(FluidRegistry.WATER)){
            Blocks.WATER.setTickRandomly(false);
        }
        if(fluidsNotToSimulate.containsEquivalent(FluidRegistry.LAVA)){
            Blocks.LAVA.setTickRandomly(false);
        }
    }
}
