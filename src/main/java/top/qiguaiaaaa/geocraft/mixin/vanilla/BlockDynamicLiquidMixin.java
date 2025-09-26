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

import net.minecraft.block.Block;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.IFluidBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.qiguaiaaaa.geocraft.api.setting.GeoFluidSetting;
import top.qiguaiaaaa.geocraft.api.util.FluidUtil;
import top.qiguaiaaaa.geocraft.geography.fluid_physics.FluidUpdateManager;
import top.qiguaiaaaa.geocraft.geography.fluid_physics.vanilla_like.mixin.IVanillaLikeFluidBlock;
import top.qiguaiaaaa.geocraft.geography.fluid_physics.vanilla_like.update.VanillaLikeBlockDynamicLiquidUpdateTask;
import top.qiguaiaaaa.geocraft.handler.BlockUpdater;
import top.qiguaiaaaa.geocraft.util.fluid.FluidOperationUtil;
import top.qiguaiaaaa.geocraft.util.fluid.FluidSearchUtil;
import top.qiguaiaaaa.geocraft.util.mixinapi.FluidSettable;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

import static top.qiguaiaaaa.geocraft.configs.FluidPhysicsConfig.*;

@Mixin(value = BlockDynamicLiquid.class)
public class BlockDynamicLiquidMixin extends BlockLiquid implements FluidSettable, IVanillaLikeFluidBlock {
    @Shadow
    int adjacentSourceBlocks;
    private Fluid thisFluid;

    protected BlockDynamicLiquidMixin(Material materialIn) {
        super(materialIn);
    }

    /**
     * @author QiguaiAAAA
     */
    @Inject(method = "updateTick",at = @At("HEAD"),cancellable = true)
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand, CallbackInfo ci) {
        if(!GeoFluidSetting.isFluidToBePhysical(thisFluid)) return;
        ci.cancel();
        if(worldIn.isRemote) return;
        FluidUpdateManager.addTask(worldIn,new VanillaLikeBlockDynamicLiquidUpdateTask(thisFluid,pos,(BlockDynamicLiquid) (Block)this));
    }

    @Override
    public void onFlowingTask(@Nonnull World world,@Nonnull BlockPos pos,@Nonnull IBlockState state,@Nonnull Random rand){
        if (!world.isAreaLoaded(pos, this.getSlopeFindDistance(world))) return;
        int liquidMeta = state.getValue(LEVEL);
        int spreadLevel = getSpreadLevel(world);

        int updateRate = this.tickRate(world);

        BlockPos downPos = pos.down();
        IBlockState stateBelow = world.getBlockState(downPos);

        //是否能够往下流
        Optional<BlockPos> sourcePosOption = Optional.empty();
        if(liquidMeta == 0) sourcePosOption = Optional.of(pos);
        boolean canMoveSourceDown = this.canMoveInto(world, downPos, stateBelow);
        if(canMoveSourceDown){
            if (!sourcePosOption.isPresent())
                sourcePosOption = FluidSearchUtil.findSource(world,pos,material,false,false,
                        findSourceMaxIterationsWhenVerticalFlowing.getValue(),
                        findSourceMaxSameLevelIterationsWhenVerticalFlowing.getValue());
            if(sourcePosOption.isPresent()){
                FluidOperationUtil.moveFluidSource(world,sourcePosOption.get(),downPos);
                if(sourcePosOption.get() == pos) return;
            }
        }else if(liquidMeta == 1){
            sourcePosOption = FluidSearchUtil.findSource(world,pos,material,true,false,
                    findSourceMaxIterationsWhenHorizontalFlowing.getValue(),
                    findSourceMaxSameLevelIterationsWhenHorizontalFlowing.getValue());
            if(sourcePosOption.isPresent()){
                FluidOperationUtil.moveFluidSource(world,sourcePosOption.get(),pos);
                BlockUpdater.scheduleUpdate(world,pos,BlockLiquid.getStaticBlock(material),updateRate);
                return;
            }
        }

        boolean noSourceFound = canMoveSourceDown && !sourcePosOption.isPresent();

        if(noSourceFound){
            spreadLevel+=8;
        }

        if (liquidMeta > 0) {
            //水平方向处理
            int 相邻方块最高等级 = -100;
            this.adjacentSourceBlocks = 0;

            for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL)
                相邻方块最高等级 = this.checkAdjacentBlock(world, pos.offset(enumfacing), 相邻方块最高等级);

            int newLiquidMeta = 相邻方块最高等级 + spreadLevel;

            if (newLiquidMeta >= 8 || 相邻方块最高等级 < 0) newLiquidMeta = -1;
            //垂直方向处理
            int upBlockMeta = this.getDepth(world.getBlockState(pos.up()));

            if (upBlockMeta >= 0) {
                if (upBlockMeta >= 8) newLiquidMeta = upBlockMeta;
                else newLiquidMeta = upBlockMeta + 8;
            }
            //无限水
            if(enableInfiniteWater.getValue()){
                if (this.adjacentSourceBlocks >= 2 && ForgeEventFactory.canCreateFluidSource(world, pos, state, material == Material.WATER)) {

                    if (stateBelow.getMaterial().isSolid()) {
                        newLiquidMeta = 0;
                    } else if (stateBelow.getMaterial() == material && stateBelow.getValue(LEVEL) == 0) {
                        newLiquidMeta = 0;
                    }
                }
            }
            boolean isQuantaDecreasing = newLiquidMeta < 8 && newLiquidMeta > liquidMeta;
            //岩浆处理
            if (!noSourceFound && (material == Material.LAVA) && isQuantaDecreasing && rand.nextInt(4) != 0){
                updateRate *= 4;
            }

            //更新纹理（流动还是静止）
            if (newLiquidMeta == liquidMeta) {
                this.placeStaticBlock(world, pos, state);
            } else {
                liquidMeta = newLiquidMeta;
                if (newLiquidMeta < 0) world.setBlockToAir(pos);
                else {
                    state = state.withProperty(LEVEL, newLiquidMeta);
                    world.setBlockState(pos, state, 2);
                    BlockUpdater.scheduleUpdate(world,pos, this, updateRate);
                    world.notifyNeighborsOfStateChange(pos, this, false);
                }
            }
        } else {
            this.placeStaticBlock(world, pos, state);
        }
        if(liquidMeta <0) return;
        stateBelow = world.getBlockState(downPos);
        if (canFlowInto(world, downPos, stateBelow)) {
            if (material == Material.LAVA && stateBelow.getMaterial() == Material.WATER) {
                world.setBlockState(downPos, ForgeEventFactory.fireFluidPlaceBlockEvent(world, downPos, pos, Blocks.STONE.getDefaultState()));
                FluidOperationUtil.triggerFluidMixEffects(world, downPos);
                return;
            }
            if (liquidMeta >= 8) this.tryFlowInto(world, downPos, stateBelow, liquidMeta);
            else this.tryFlowInto(world, downPos, stateBelow, liquidMeta + 8);
        } else if (FluidUtil.isFullFluid(world,downPos,stateBelow) || this.isBlocked(world, downPos, stateBelow)){//横向流动
            Set<EnumFacing> directions = this.getPossibleFlowDirections(world, pos);
            int nextLiquidState = liquidMeta + spreadLevel;

            if (liquidMeta >= 8) nextLiquidState = 1;
            if (nextLiquidState >= 8) return;

            for (EnumFacing facing : directions)
                this.tryFlowInto(world, pos.offset(facing), world.getBlockState(pos.offset(facing)), nextLiquidState);
        }
    }

    private boolean canMoveInto(World worldIn,BlockPos pos,IBlockState state){
        if(state.getBlock() instanceof IFluidBlock) return false;
        Material material = state.getMaterial();
        if(material.isLiquid()){
            if(material != this.material) return false;
            return state.getValue(LEVEL) != 0;
        }
        return !this.isBlocked(worldIn,pos,state);
    }

    private int getSpreadLevel(World world){
        if (material == Material.LAVA && !world.provider.doesWaterVaporize()) {
            return 2;
        }
        return 1;
    }

    @Shadow
    private void tryFlowInto(World worldIn, BlockPos pos, IBlockState state, int level) {}
    @Shadow
    private boolean canFlowInto(World worldIn, BlockPos pos, IBlockState state) {return false;}
    @Shadow
    private int getSlopeFindDistance(World worldIn) {
        return 0;
    }
    @Shadow
    protected int checkAdjacentBlock(World worldIn, BlockPos pos, int currentMinLevel){return 0;}
    @Shadow
    private void placeStaticBlock(World worldIn, BlockPos pos, IBlockState currentState){}
    @Shadow
    private Set<EnumFacing> getPossibleFlowDirections(World worldIn, BlockPos pos) {return Collections.emptySet();}
    @Shadow
    private boolean isBlocked(World worldIn, BlockPos pos, IBlockState state){return false;}

    @Override
    public void setCorrespondingFluid(Fluid fluid) {
        if(thisFluid != null)return;
        thisFluid = fluid;
    }

}
