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

package top.qiguaiaaaa.geocraft.mixin.reality.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.qiguaiaaaa.geocraft.api.block.IPermeableBlock;
import top.qiguaiaaaa.geocraft.api.setting.GeoFluidSetting;
import top.qiguaiaaaa.geocraft.api.util.FluidUtil;
import top.qiguaiaaaa.geocraft.api.util.math.FlowChoice;
import top.qiguaiaaaa.geocraft.geography.fluid_physics.FluidUpdateManager;
import top.qiguaiaaaa.geocraft.geography.fluid_physics.reality.RealityBlockLiquidUtil;
import top.qiguaiaaaa.geocraft.geography.fluid_physics.reality.update.RealityBlockDynamicLiquidUpdateTask;
import top.qiguaiaaaa.geocraft.util.mixinapi.FluidSettable;
import top.qiguaiaaaa.geocraft.util.mixinapi.IVanillaFlowChecker;

import javax.annotation.Nonnull;
import java.util.*;

@Mixin(value = BlockDynamicLiquid.class)
public class BlockDynamicLiquidMixin extends BlockLiquid implements FluidSettable, IVanillaFlowChecker, IPermeableBlock {
    private Fluid thisFluid;

    protected BlockDynamicLiquidMixin(Material materialIn) {
        super(materialIn);
    }

    @Inject(method = "updateTick",at = @At("HEAD"),cancellable = true)
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand, CallbackInfo ci) {
        if(!GeoFluidSetting.isFluidToBePhysical(thisFluid)) return;
        ci.cancel();
        if (!worldIn.isAreaLoaded(pos,1)){
            return;
        }
        FluidUpdateManager.addTask(worldIn,new RealityBlockDynamicLiquidUpdateTask(thisFluid,pos,(BlockDynamicLiquid) (Block)this));
    }

    @Override
    public boolean canFlow(World worldIn,BlockPos pos,IBlockState state,Random rand){
        if (!worldIn.isAreaLoaded(pos, RealityBlockLiquidUtil.getSlopeFindDistance(worldIn,this))){
            return false;
        }

        int liquidMeta = state.getValue(LEVEL);
        if(liquidMeta >= 8){
            return false;
        }
        int liquidQuanta = 8-liquidMeta;

        IBlockState stateBelow = worldIn.getBlockState(pos.down());
        boolean canMoveDown = RealityBlockLiquidUtil.canMoveDownTo(material,stateBelow);
        if(canMoveDown) return true;

        //坡度流动模式
        if(liquidMeta == 7){
            if(FluidUtil.getFluid(stateBelow) == thisFluid) return false;
            Set<EnumFacing> directions = RealityBlockLiquidUtil.getPossibleFlowDirections(worldIn, pos,this);
            return !directions.isEmpty();
        }
        //可流动方向检查
        final ArrayList<FlowChoice> averageModeFlowDirections = new ArrayList<>();//平均流动模式可用方向
        RealityBlockLiquidUtil.checkNeighborsToFindFlowChoices(worldIn,pos,this,liquidQuanta,averageModeFlowDirections);

        return !averageModeFlowDirections.isEmpty();
    }

    @Inject(method = "getPossibleFlowDirections",at = @At("HEAD"),cancellable = true)
    private void getPossibleFlowDirections(World worldIn, BlockPos pos, CallbackInfoReturnable<Set<EnumFacing>> cir) {
        if(!GeoFluidSetting.isFluidToBePhysical(thisFluid)) return;
        cir.cancel();
        cir.setReturnValue(RealityBlockLiquidUtil.getPossibleFlowDirections(worldIn,pos,this));
    }

    @Inject(method = "getSlopeDistance",at = @At("HEAD"),cancellable = true)
    private void getSlopeDistance(World worldIn, BlockPos pos, int distance, EnumFacing from, CallbackInfoReturnable<Integer> cir) {
        if(!GeoFluidSetting.isFluidToBePhysical(thisFluid)) return;
        cir.cancel();
        cir.setReturnValue(RealityBlockLiquidUtil.getSlopeDistance(worldIn,pos,distance,from,this));
    }

    //********
    // FluidSettable
    //********

    @Override
    public void setCorrespondingFluid(Fluid fluid) {
        if(thisFluid != null)return;
        this.thisFluid = fluid;
    }

    //*********
    // 透水方块
    //*********

    @Nonnull
    @Override
    public Fluid getFluid(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        return thisFluid;
    }

    @Override
    public int getQuanta(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        return 8-state.getValue(BlockLiquid.LEVEL);
    }

    @Override
    public int getHeight(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state) {
        return getQuanta(world,pos,state)*2;
    }

    @Override
    public int getHeightPerQuanta() {
        return 2;
    }

    @Override
    public void addQuanta(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, int quanta) {
        int newQuanta = 8-(state.getValue(BlockLiquid.LEVEL)-quanta);
        setQuanta(world,pos,state,newQuanta);
    }

    @Override
    public void setQuanta(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, int newQuanta) {
        if(newQuanta <= 0) world.setBlockToAir(pos);
        world.setBlockState(pos,state.withProperty(BlockLiquid.LEVEL,8-newQuanta), net.minecraftforge.common.util.Constants.BlockFlags.SEND_TO_CLIENTS);
    }

    @Nonnull
    @Override
    public IBlockState getQuantaState(@Nonnull IBlockState state, int newQuanta) {
        if(newQuanta <= 0) return Blocks.AIR.getDefaultState();
        return state.withProperty(BlockLiquid.LEVEL,8-newQuanta);
    }
}
