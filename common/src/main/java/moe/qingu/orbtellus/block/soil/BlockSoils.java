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

package moe.qingu.orbtellus.block.soil;

import moe.qingu.orbtellus.api.atmosphere.AtmosphereSystemManager;
import moe.qingu.orbtellus.api.atmosphere.accessor.IAtmosphereAccessor;
import moe.qingu.orbtellus.api.fluid.StateOfMatter;
import moe.qingu.orbtellus.api.fluidphysics.FluidPhysicsDesign;
import moe.qingu.orbtellus.api.fluidphysics.FluidPhysicsMode;
import moe.qingu.orbtellus.api.laminarifer.ILaminarifer;
import moe.qingu.orbtellus.api.laminarifer.Laminarifers;
import moe.qingu.orbtellus.api.laminarifer.qb.QBUnit;
import moe.qingu.orbtellus.api.util.AtmosphereUtil;
import moe.qingu.orbtellus.api.util.FluidUtil;
import moe.qingu.orbtellus.api.util.annotation.ThreadOnly;
import moe.qingu.orbtellus.api.util.annotation.ThreadType;
import moe.qingu.orbtellus.api.util.math.FlowChoice;
import moe.qingu.orbtellus.api.util.math.vec.MBlockPos;
import moe.qingu.orbtellus.api.util.modifier.BlockFlagModifiers;
import moe.qingu.orbtellus.geography.fluidphysics.finite.flow.FiniteFlowings;
import moe.qingu.orbtellus.util.BaseUtil;
import moe.qingu.orbtellus.util.ChunkUtil;
import moe.qingu.orbtellus.util.WaterUtil;
import moe.qingu.orbtellus.util.fluid.FluidOperationUtil;
import moe.qingu.orbtellus.util.laminarifer.FillLaminariferRequest;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCauldron;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static moe.qingu.orbtellus.api.fluidphysics.FluidPhysicsMode.getCurrentMode;
import static moe.qingu.orbtellus.configs.FluidPhysicsConfig.FLUID_PHYSICS_MODE;

/**
 * @author QGMoe
 */
public final class BlockSoils { //unfinished todo
    @ThreadOnly(ThreadType.MINECRAFT_SERVER) private static final MBlockPos mutablePos = new MBlockPos();
    @ThreadOnly(ThreadType.MINECRAFT_SERVER) private static final FillLaminariferRequest fillRequest = new FillLaminariferRequest();
    @ThreadOnly(ThreadType.MINECRAFT_SERVER) private static final List<FlowChoice> _平均流动所有流动选择_ = new ArrayList<>();

    private BlockSoils(){}

    @Nonnull
    private static FillLaminariferRequest getFillRequest(){
        if(fillRequest.isUsing()) return new FillLaminariferRequest(); //一般情况下不应该using的
        else return fillRequest;
    }

    /**
     * 土壤将自身水掉下去的能力
     * @return 湿度变化
     */
    static int 壤中流重力流动(@Nonnull final IBlockSoil $土壤, @Nonnull final World worldIn, @Nonnull final BlockPos pos){
        final IBlockState downState = worldIn.getBlockState(mutablePos.setPos(pos).downM());
        for(final @Nonnull 壤中流重力流动情况 $情况: 壤中流重力流动情况._所有情况_)
            switch ($情况.尝试渗出(worldIn,pos,downState,$土壤)){
                case FAIL:return 0;
                case SUCCESS:return -1;
                case PASS:
                default:
            }
        return 0;
    }

    private static abstract class 壤中流重力流动情况 {

        private static final 壤中流重力流动情况[] _所有情况_ = new 壤中流重力流动情况[]{
                new 壤中流重力流动情况() {
                    @Override
                    protected boolean 满足条件(@Nonnull final IBlockState $土壤下方方块状态) {
                        return $土壤下方方块状态.getMaterial() == Material.AIR;
                    }

                    @Override
                    protected boolean 进行渗出(@Nonnull final World world,
                                               @Nonnull final BlockPos $土壤下方位置,
                                               @Nonnull final IBlockState $土壤下方方块状态,
                                               @Nonnull final IBlockSoil $土壤) {
                        if(getCurrentMode().design == FluidPhysicsDesign.FINITE)
                            world.setBlockState($土壤下方位置, Blocks.FLOWING_WATER.getDefaultState().withProperty(BlockLiquid.LEVEL,7), Constants.BlockFlags.DEFAULT);
                        return true;
                    }
                },
                new 壤中流重力流动情况() {
                    @Override
                    protected boolean 满足条件(@Nonnull final IBlockState $土壤下方方块状态) {
                        return Laminarifers.isLaminarifer($土壤下方方块状态);
                    }

                    @Override
                    protected boolean 进行渗出(@Nonnull final World world,
                                               @Nonnull final BlockPos $土壤下方位置,
                                               @Nonnull final IBlockState $土壤下方方块状态,
                                               @Nonnull final IBlockSoil $土壤) {
                        try (final FillLaminariferRequest request = getFillRequest().open()){
                            return request.target((ILaminarifer) $土壤下方方块状态.getBlock())
                                    .at(world,$土壤下方位置,$土壤下方方块状态).side(EnumFacing.UP)
                                    .withContent(FluidRegistry.WATER, QBUnit.QUANTA_VOLUME)
                                    .withSource($土壤)
                                    .fill(true)>0L;

                        }
                    }
                },
                new 壤中流重力流动情况() {
                    @Override
                    protected boolean 满足条件(@Nonnull final IBlockState $土壤下方方块状态) {
                        return FiniteFlowings.WATER_FLOW.canFlowDownTo($土壤下方方块状态);
                    }

                    @Override
                    protected boolean 进行渗出(@Nonnull final World world,
                                               @Nonnull final BlockPos $土壤下方位置,
                                               @Nonnull final IBlockState $土壤下方方块状态,
                                               @Nonnull final IBlockSoil $土壤) {
                        if(getCurrentMode().design == FluidPhysicsDesign.FINITE) {
                            FluidOperationUtil.triggerDestroyBlockEffectByFluid(world,$土壤下方位置,$土壤下方方块状态,FluidRegistry.WATER);
                            world.setBlockState($土壤下方位置, Blocks.FLOWING_WATER.getDefaultState().withProperty(BlockLiquid.LEVEL,7), Constants.BlockFlags.DEFAULT);
                        }
                        return true;
                    }
                },
                new 壤中流重力流动情况() {
                    @Override
                    protected boolean 满足条件(@Nonnull final IBlockState $土壤下方方块状态) {
                        return $土壤下方方块状态.getBlock() == Blocks.CAULDRON && getCurrentMode().design == FluidPhysicsDesign.CLASSIC;
                    }

                    @Override
                    protected boolean 进行渗出(@Nonnull final World world,
                                               @Nonnull final BlockPos $土壤下方位置,
                                               @Nonnull final IBlockState $土壤下方方块状态,
                                               @Nonnull final IBlockSoil $土壤) {
                        if(!BaseUtil.getRandomResult(world.rand,0.3)) return false;
                        if($土壤下方方块状态.getValue(BlockCauldron.LEVEL) < 3){
                            world.setBlockState($土壤下方位置, $土壤下方方块状态.cycleProperty(BlockCauldron.LEVEL), Constants.BlockFlags.SEND_TO_CLIENTS);
                            return true;
                        }else return false;
                    }
                }
        };

        @Nonnull
        public final EnumActionResult 尝试渗出(@Nonnull final World world,
                                               @Nonnull final BlockPos $土壤下方位置,
                                               @Nonnull final IBlockState $土壤下方方块状态,
                                               @Nonnull final IBlockSoil $土壤){
            if(满足条件($土壤下方方块状态)) return 进行渗出(world, $土壤下方位置, $土壤下方方块状态, $土壤)?EnumActionResult.SUCCESS:EnumActionResult.FAIL;
            return EnumActionResult.PASS;
        }

        protected abstract boolean 满足条件(final @Nonnull IBlockState $土壤下方方块状态);

        protected abstract boolean 进行渗出(@Nonnull final World world,
                                            @Nonnull final BlockPos $土壤下方位置,
                                            @Nonnull final IBlockState $土壤下方方块状态,
                                            @Nonnull final IBlockSoil $土壤);
    }

    /**
     * 土壤水向四周流动的能力
     * @param humidity 当前湿度
     */
    static void 壤中流平均流动(final @Nonnull IBlockSoil $土壤,
                               final @Nonnull World world,
                               final @Nonnull BlockPos pos,
                               final @Nonnull IBlockState state,
                               final int humidity){
        if (!world.isAreaLoaded(pos, 1)) return;
        //可流动方向检查
        _平均流动所有流动选择_.clear();

        final MBlockPos facingPos = new MBlockPos();
        for(final @Nonnull EnumFacing facing:EnumFacing.Plane.HORIZONTAL){
            facingPos.setPos(pos).offsetM(facing);
            final IBlockState facingState = world.getBlockState(facingPos);
            if(!壤中流能主动水平流入(world,facingPos,facingState,facing,$土壤)) continue;
            if(facingState.getMaterial() == Material.AIR){
                _平均流动所有流动选择_.add(new FlowChoice(facing));
                continue;
            }
            final ILaminarifer $载流方块 = (ILaminarifer) facingState.getBlock();
            final long facingHeight = $载流方块.getHeight(world,facingPos,facingState,FluidRegistry.WATER,null);
            final long facingHeightPerLayer = $载流方块.getHeightPerLayer(world,facingPos,facingState,FluidRegistry.WATER,null);
            if(facingHeight+facingHeightPerLayer<=(humidity-1)*$土壤.getHeightPerLayer(world,pos,state,FluidRegistry.WATER,null)){
                _平均流动所有流动选择_.add(new FlowChoice(world,facingPos,facingState,$载流方块,facing,FluidRegistry.WATER));
            }
        }

        final int newHumidity = Laminarifers.averageFlow(
                humidity,
                $土壤.getHeightPerLayer(world,pos,state,FluidRegistry.WATER,null),
                $土壤.getAmountInQBPerLayer(world,pos,state,FluidRegistry.WATER,null),
                $土壤.getMaxStableHumidity(state),
                _平均流动所有流动选择_);

        if(newHumidity != humidity){
            long left = 0;
            for(@Nonnull FlowChoice choice: _平均流动所有流动选择_){ //向四周流动
                if(choice.getAddedLayers() == 0){
                    left += choice.getAddedAmountInQB();
                    continue;
                }
                facingPos.setPos(pos).offsetM(choice.direction);
                if(choice.isAir()){
                    if(FLUID_PHYSICS_MODE.getValue() != FluidPhysicsMode.FINITE) continue;
                    world.setBlockState(facingPos,Blocks.FLOWING_WATER.getDefaultState().withProperty(BlockLiquid.LEVEL,8-choice.getNewLayers()));
                    continue;
                }
                IBlockState facingState = world.getBlockState(facingPos);
                left += choice.apply(world,facingPos,facingState,FluidRegistry.WATER);
            }
            $土壤.setLayer(world,pos,state,FluidRegistry.WATER, null, newHumidity + QBUnit.toQuanta(left), BlockFlagModifiers.KEEP);
        }

        _平均流动所有流动选择_.clear();
    }

    static int 壤中流毛细流动(final @Nonnull IBlockSoil $土壤, //todo
                              final @Nonnull World worldIn,
                              final @Nonnull BlockPos pos,
                              final @Nonnull IBlockState state){
        BlockPos upPos = pos.up();
        IBlockState upState = worldIn.getBlockState(upPos);
        if(upState.getBlock() instanceof ILaminarifer){
            ILaminarifer block = (ILaminarifer) upState.getBlock();
            if(!block.canDrain(worldIn,upPos,upState,FluidRegistry.WATER,EnumFacing.DOWN,state)) return 0;
            int drained = block.drainLayer(worldIn,upPos,upState,FluidRegistry.WATER,1,false);
            if(drained < 1) return 0;
            return block.drainLayer(worldIn,upPos,upState,FluidRegistry.WATER,1,true);
        }
        return 0;
    }

    static int 蒸发(final @Nonnull IBlockSoil $土壤,
                    final @Nonnull World world,
                    final @Nonnull BlockPos pos,
                    final @Nonnull IBlockState state,
                    final @Nonnull Random random){
        final @Nonnull BlockPos up = pos.up();
        if(!world.isAirBlock(up)) return 0;
        final long humidity = $土壤.getLayers(world,pos,state,FluidRegistry.WATER,null);
        if(humidity == 0) return 0;
        try(@Nullable final IAtmosphereAccessor accessor = AtmosphereSystemManager.getAtmosphereAccessor(world,pos,true)) {
            if (accessor == null) return 0;
            final int light = ChunkUtil.getNeighborsLightFor(world, EnumSkyBlock.SKY,pos);
            accessor.setSkyLight(light);

            if(!accessor.getAtmosphereInfo().canWaterEvaporate()) return 0;
            if(!accessor.canAccessAtmosphere()) return 0;

            double basePossibility = WaterUtil.getWaterEvaporatePossibility(accessor);
            basePossibility /= (8-humidity)*2;
            if(!BaseUtil.getRandomResult(random,basePossibility)) return 0;

            accessor.drainHeatFromUnderlying(AtmosphereUtil.Constants.WATER_EVAPORATE_LATENT_HEAT_PER_QUANTA);
            accessor.fillFluidToAtmosphere(FluidRegistry.WATER, FluidUtil.ONE_IN_EIGHT_OF_BUCKET_VOLUME, StateOfMatter.GAS,accessor.getTemperature(true),true);
            return -1;
        }
    }

    static void onSoilTick(final @Nonnull IBlockSoil $土壤,
                           final @Nonnull World worldIn,
                           final @Nonnull BlockPos pos,
                           final @Nonnull IBlockState state,
                           final @Nonnull Random random){
        if(worldIn.isRemote) return;
        final int humidity = (int) $土壤.getLayers(worldIn,pos,state,FluidRegistry.WATER,null);
        int newHumidity = humidity;
        final int rnd = random.nextInt(3);
        switch (rnd){
            case 0: //吸收上面的水
                if(humidity < 4) {
                    newHumidity += 壤中流毛细流动($土壤,worldIn,pos,state);
                }
                break;
            case 1: //向下掉水
                if(humidity >$土壤.getMaxStableHumidity(state)){
                    newHumidity += 壤中流重力流动($土壤,worldIn, pos);
                }
                break;
            default: //水平平衡
                壤中流平均流动($土壤,worldIn,pos,state,humidity);
                return;
        }
        if(humidity == newHumidity){
            if(humidity == 0) return;
            newHumidity += 蒸发($土壤,worldIn, pos, state, random);
        }
        if(humidity == newHumidity) return;
        $土壤.setLayer(worldIn,pos,state,FluidRegistry.WATER,null,newHumidity,BlockFlagModifiers.KEEP);
    }
    
    /**
     * 检查土壤水是否能够流进指定方块
     * @param $流入位置状态 目标方块状态
     * @return 能，则true，否，则反之
     */
    static boolean 壤中流能主动水平流入(@Nonnull final World world,
                                        @Nonnull final BlockPos $流入位置,
                                        @Nonnull final IBlockState $流入位置状态,
                                        @Nonnull final EnumFacing $流入来向,
                                        @Nonnull final IBlockSoil $土壤){
        if($流入位置状态.getMaterial() == Material.AIR) return true;
        final Block $流入位置方块 = $流入位置状态.getBlock();
        if(Laminarifers.isLaminarifer($流入位置方块)){
            final ILaminarifer laminarifer = (ILaminarifer) $流入位置方块;
            return laminarifer.canFill(world,$流入位置,$流入位置状态,$流入来向,FluidRegistry.WATER,null,$土壤);
        }
        return false;
    }
}
