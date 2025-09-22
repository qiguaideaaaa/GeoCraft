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

package top.qiguaiaaaa.geocraft.mixin.vanilla;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.qiguaiaaaa.geocraft.api.setting.GeoFluidSetting;
import top.qiguaiaaaa.geocraft.mixin.common.BlockFluidBaseAccessor;
import top.qiguaiaaaa.geocraft.util.fluid.FluidOperationUtil;
import top.qiguaiaaaa.geocraft.util.fluid.FluidSearchUtil;
import top.qiguaiaaaa.geocraft.api.util.FluidUtil;

import javax.annotation.Nonnull;
import java.util.*;

import static net.minecraft.block.BlockLiquid.LEVEL;
import static top.qiguaiaaaa.geocraft.configs.FluidPhysicsConfig.*;

@Mixin(value = BlockFluidClassic.class)
public class BlockFluidClassicMixin {
    @Final
    @Shadow(remap = false)
    protected static final List<EnumFacing> SIDES = Collections.unmodifiableList(Arrays.asList(
            EnumFacing.WEST, EnumFacing.EAST, EnumFacing.NORTH, EnumFacing.SOUTH));
    @Shadow(remap = false)
    protected boolean canCreateSources;
    private BlockFluidClassic thisBlock;
    @Inject(method = "<init>(Lnet/minecraftforge/fluids/Fluid;Lnet/minecraft/block/material/Material;Lnet/minecraft/block/material/MapColor;)V",at = @At("RETURN"))
    private void onInit(Fluid fluid, Material material, MapColor mapColor, CallbackInfo ci) {
        thisBlock = (BlockFluidClassic) (Object)this;
    }
    @Inject(method = "<init>(Lnet/minecraftforge/fluids/Fluid;Lnet/minecraft/block/material/Material;)V",at = @At("RETURN"))
    private void onInit(Fluid fluid, Material material, CallbackInfo ci) {
        thisBlock = (BlockFluidClassic) (Object)this;
    }
    /**
     * @author QiguaiAAAA
     * @reason Configure for Forge Fluids
     */
    @Overwrite
    public void updateTick(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Random rand) {
        if(!GeoFluidSetting.isFluidToBePhysical(thisBlock.getFluid())) return;
        int quantaPerBlock = ((BlockFluidBaseAccessor) this).getQuantaPerBlock();
        int densityDir = ((BlockFluidBaseAccessor) this).getDensityDir();
        int quantaRemaining = quantaPerBlock - state.getValue(LEVEL);
        int expQuanta = -101;

        //是否能够往下流
        Optional<BlockPos> sourcePosOption = Optional.empty();
        if(quantaRemaining == quantaPerBlock) sourcePosOption = Optional.of(pos);
        boolean canMoveSourceDown = this.canMoveInto(world, pos.up(densityDir));
        if(canMoveSourceDown){
            if (!sourcePosOption.isPresent())
                sourcePosOption = FluidSearchUtil.findSource(world,pos,thisBlock.getFluid(),false,false,
                        findSourceMaxIterationsWhenVerticalFlowing.getValue(),
                        findSourceMaxSameLevelIterationsWhenVerticalFlowing.getValue());
            if(sourcePosOption.isPresent()){
                FluidOperationUtil.moveFluidSource(world,sourcePosOption.get(),pos.up(densityDir));
                if(sourcePosOption.get() == pos) return;
            }
        }else if(quantaRemaining == quantaPerBlock-1){
            sourcePosOption = FluidSearchUtil.findSource(world,pos,thisBlock.getFluid(),true,false,
                    findSourceMaxIterationsWhenHorizontalFlowing.getValue(),
                    findSourceMaxSameLevelIterationsWhenHorizontalFlowing.getValue());
            if(sourcePosOption.isPresent()){
                FluidOperationUtil.moveFluidSource(world,sourcePosOption.get(),pos);
                world.scheduleUpdate(pos, thisBlock, ((BlockFluidBaseAccessor) this).getTickRate());
                return;
            }
        }

        if (quantaRemaining < quantaPerBlock) {
            int adjacentSourceBlocks = 0;

            if (ForgeEventFactory.canCreateFluidSource(world, pos, state, canCreateSources))
                for (EnumFacing side : EnumFacing.Plane.HORIZONTAL)
                    if (isSourceBlock(world, pos.offset(side))) adjacentSourceBlocks++;

            // 无限液体
            if (!disableInfiniteFluidForAllModFluid.getValue() && adjacentSourceBlocks >= 2 && (world.getBlockState(pos.up(densityDir)).getMaterial().isSolid() || isSourceBlock(world, pos.up(densityDir)))) {
                expQuanta = quantaPerBlock;
            } else if (((BlockFluidBaseAccessor)this).hasVerticalFlowR(world, pos) && !isSameFluidUnder(world,pos.up(densityDir))) {//垂直流入
                expQuanta = quantaPerBlock - 1;
            } else { //水平流动
                int maxQuanta = -100;
                for (EnumFacing side : EnumFacing.Plane.HORIZONTAL) {
                    maxQuanta = getLargerQuanta(world, pos.offset(side), maxQuanta);
                }
                expQuanta = maxQuanta - 1;
            }

            // 更新液体状态
            if (expQuanta != quantaRemaining) {
                quantaRemaining = expQuanta;
                if (expQuanta <= 0) {
                    world.setBlockToAir(pos);
                } else {
                    world.setBlockState(pos, state.withProperty(LEVEL, quantaPerBlock - expQuanta), Constants.BlockFlags.SEND_TO_CLIENTS);
                    world.scheduleUpdate(pos, thisBlock, ((BlockFluidBaseAccessor) this).getTickRate());
                    world.notifyNeighborsOfStateChange(pos, thisBlock, false);
                }
            }
        }
        // 垂直流入
        if (thisBlock.canDisplace(world, pos.up(densityDir))) {
            flowIntoBlock(world, pos.up(densityDir), 1);
            return;
        }

        // 水平流动
        int flowMeta = quantaPerBlock - quantaRemaining + 1;
        if (flowMeta >= quantaPerBlock) return;

        if (FluidUtil.isFullFluid(world,pos.up(densityDir),world.getBlockState(pos.up(densityDir))) || !isFlowingVertically(world, pos)) {
            if (((BlockFluidBaseAccessor)this).hasVerticalFlowR(world, pos)) flowMeta = 1;
            boolean[] flowTo = getOptimalFlowDirections(world, pos);
            for (int i = 0; i < 4; i++)
                if (flowTo[i]) flowIntoBlock(world, pos.offset(SIDES.get(i)), flowMeta);
        }
    }
    private boolean canMoveInto(World worldIn,BlockPos pos){
        IBlockState state = worldIn.getBlockState(pos);
        if(FluidUtil.isFluid(state)){
            if(FluidUtil.getFluid(state) != thisBlock.getFluid()) return false;
            return state.getValue(LEVEL) != 0;
        }
        return thisBlock.canDisplace(worldIn,pos);
    }

    private boolean isSameFluidUnder(World worldIn,BlockPos pos){
        Fluid thisFluid = thisBlock.getFluid();
        Fluid underFluid = FluidUtil.getFluid(worldIn.getBlockState(pos));
        return thisFluid == underFluid;
    }

    @Shadow(remap = false)
    protected void flowIntoBlock(World world, BlockPos pos, int meta) {}
    @Shadow(remap = false)
    public boolean isSourceBlock(IBlockAccess world, BlockPos pos){return false;}
    @Shadow(remap = false)
    protected int getLargerQuanta(IBlockAccess world, BlockPos pos, int compare){return 0;}
    @Shadow(remap = false)
    public boolean isFlowingVertically(IBlockAccess world, BlockPos pos){return false;}
    @Shadow(remap = false)
    protected boolean[] getOptimalFlowDirections(World world, BlockPos pos){return null;}
}
