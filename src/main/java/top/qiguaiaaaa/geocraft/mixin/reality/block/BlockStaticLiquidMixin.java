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
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.qiguaiaaaa.geocraft.GeoCraft;
import top.qiguaiaaaa.geocraft.api.event.EventFactory;
import top.qiguaiaaaa.geocraft.api.setting.GeoFluidSetting;
import top.qiguaiaaaa.geocraft.api.util.FluidUtil;
import top.qiguaiaaaa.geocraft.configs.FluidPhysicsConfig;
import top.qiguaiaaaa.geocraft.geography.fluid_physics.FluidPressureSearchManager;
import top.qiguaiaaaa.geocraft.geography.fluid_physics.reality.pressure.RealityPressureTaskBuilder;
import top.qiguaiaaaa.geocraft.geography.fluid_physics.task.pressure.IFluidPressureSearchTaskResult;
import top.qiguaiaaaa.geocraft.handler.ServerStatusMonitor;
import top.qiguaiaaaa.geocraft.util.BaseUtil;
import top.qiguaiaaaa.geocraft.util.mixinapi.FluidSettable;
import top.qiguaiaaaa.geocraft.util.mixinapi.IVanillaFlowChecker;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Random;

@Mixin(value = BlockStaticLiquid.class)
public class BlockStaticLiquidMixin extends BlockLiquid implements IVanillaFlowChecker, FluidSettable {
    @Unique
    private static final boolean debug = false;
    @Unique
    private Fluid thisFluid;
    @Unique
    private boolean curRandomTick = false;

    protected BlockStaticLiquidMixin(Material materialIn) {
        super(materialIn);
    }

    @Override
    @Unique
    public void randomTick(@Nonnull World worldIn, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Random random) {
        curRandomTick = true;
        super.randomTick(worldIn, pos, state, random);
        curRandomTick = false;
    }

    @Inject(method = "neighborChanged",at =@At("HEAD"),cancellable = true)
    private void beforeNeighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos,CallbackInfo ci){
        if(ServerStatusMonitor.isServerCloselyLagging()) ci.cancel();
    }

    @Inject(method = "<init>",at = @At("RETURN"))
    private void onInit(Material materialIn, CallbackInfo ci) {
        this.setTickRandomly(true);
    }
    @Inject(method = "updateTick",at = @At("TAIL"))
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand, CallbackInfo ci) {
        if(worldIn.isRemote) return;
        if(!GeoFluidSetting.isFluidToBePhysical(thisFluid)) return;
        if(!canFlow(worldIn,pos,state,rand)){
            if(FluidPhysicsConfig.PRESSURE_SYSTEM_FOR_REALITY.getValue()){
                IFluidPressureSearchTaskResult res = FluidPressureSearchManager.getTaskResult(worldIn,pos);

                if(res == null || res.isEmpty()){
                    sendPressureQuery(worldIn,pos,state,rand,false);
                    if(debug) GeoCraft.getLogger().info("{}: no res,send query",pos);
                }else {
                    IBlockState nowState =state;
                    if(debug) GeoCraft.getLogger().info("{}: has res :",pos);
                    while (res.hasNext()) {
                        BlockPos toPos = res.next();
                        if(!nowState.getMaterial().isLiquid()) break;
                        if(tryMoveInto(worldIn,toPos,pos,nowState)) break;
                        nowState = worldIn.getBlockState(pos);
                        if(debug) GeoCraft.getLogger().info("{} now State: {}",toPos,nowState);
                    }

                    nowState = worldIn.getBlockState(pos);
                    if(nowState!=state && FluidUtil.getFluid(nowState) == thisFluid){
                        sendPressureQuery(worldIn,pos,nowState,rand,true);
                    }else if(nowState == state){
                        sendPressureQuery(worldIn,pos,state,rand,false);
                    }
                    if(nowState!=state) return;
                }
            }
            IBlockState newState = EventFactory.afterBlockLiquidStaticUpdate(thisFluid,worldIn,pos,state,curRandomTick);
            if(newState != null){
                worldIn.setBlockState(pos,newState);
                return;
            }
            return;
        }
        updateLiquid(worldIn,pos,state);
    }

    @Override
    public boolean canFlow(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        BlockDynamicLiquid blockdynamicliquid = BlockLiquid.getFlowingBlock(this.material);
        IVanillaFlowChecker checker = (IVanillaFlowChecker) blockdynamicliquid;
        return checker.canFlow(worldIn,pos,state,rand);
    }

    @Unique
    protected void sendPressureQuery(World world,BlockPos pos,IBlockState state,Random rand,boolean directly){
        if(FluidPressureSearchManager.isTaskRunning(world,pos)){
            if(debug) GeoCraft.getLogger().info("{}: task running, returned",pos);
            return;
        }
        IBlockState up = world.getBlockState(pos.up());
        if(FluidUtil.getFluid(up)==thisFluid && up.getValue(LEVEL)==0){
            if(debug) GeoCraft.getLogger().info("{}: up is full water, returned",pos);
            return;
        }
        if(directly || BaseUtil.getRandomResult(rand,FluidPhysicsConfig.POSSIBILITY_FOR_STATIC_VANILLA_LIQUID_TO_CREATE_PRESSURE_TASK.getValue())) {
            if(debug){
                FluidPressureSearchManager.addTask(world,RealityPressureTaskBuilder.createVanillaTask_Debug(thisFluid,state,pos,BaseUtil.getRandomPressureSearchRange()));
                return;
            }
            FluidPressureSearchManager.addTask(world,
                    RealityPressureTaskBuilder.createVanillaTask(thisFluid,state,pos, BaseUtil.getRandomPressureSearchRange())
            );
        }
    }

    @Unique
    protected boolean tryMoveInto(World world,BlockPos toPos,BlockPos srcPos,IBlockState myState){
        if(!world.isBlockLoaded(toPos)) return false;
        IBlockState toState = world.getBlockState(toPos);
        final int updateFlag = ServerStatusMonitor.getRecommendedBlockFlags();
        if(toState.getMaterial() == Material.AIR){
            int quanta = 8 -myState.getValue(LEVEL);
            int movQuanta = srcPos.getY()==toPos.getY()?quanta/2:quanta;
            if(movQuanta <= 0)return false;
            quanta -=movQuanta;
            if(quanta <= 0){
                world.setBlockState(srcPos, Blocks.AIR.getDefaultState(),updateFlag);
            }else world.setBlockState(srcPos,this.getDefaultState().withProperty(LEVEL,8-quanta),updateFlag);
            world.setBlockState(toPos,this.getDefaultState().withProperty(LEVEL,8-movQuanta),updateFlag);
            return quanta == 0;
        }else if(FluidUtil.getFluid(toState) == thisFluid){
            int toQuanta = 8-toState.getValue(LEVEL);
            int myQuanta = 8 -myState.getValue(LEVEL);
            if(toPos.getY() == srcPos.getY() && toQuanta>=myQuanta-1) return false;
            int movQuanta = srcPos.getY()==toPos.getY()?(myQuanta-toQuanta)/2:Math.min(8-toQuanta,myQuanta);
            myQuanta -=movQuanta;
            if(myQuanta <= 0){
                world.setBlockState(srcPos, Blocks.AIR.getDefaultState(),updateFlag);
            }else world.setBlockState(srcPos,this.getDefaultState().withProperty(LEVEL,8-myQuanta),updateFlag);
            toQuanta += movQuanta;
            world.setBlockState(toPos,this.getDefaultState().withProperty(LEVEL,8-toQuanta),updateFlag);
            return myQuanta==0;
        }
        return false;
    }
    @Shadow
    private void updateLiquid(World worldIn, BlockPos pos, IBlockState state) {}

    @Override
    public void setCorrespondingFluid(Fluid fluid) {
        if(thisFluid == null) thisFluid = fluid;
    }
}
