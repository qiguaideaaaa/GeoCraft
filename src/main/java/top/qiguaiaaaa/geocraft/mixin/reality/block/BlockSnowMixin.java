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
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.qiguaiaaaa.geocraft.api.GeoFluids;
import top.qiguaiaaaa.geocraft.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.geocraft.api.atmosphere.AtmosphereSystemManager;
import top.qiguaiaaaa.geocraft.api.atmosphere.accessor.IAtmosphereAccessor;
import top.qiguaiaaaa.geocraft.api.block.IPermeableBlock;
import top.qiguaiaaaa.geocraft.api.property.TemperatureProperty;
import top.qiguaiaaaa.geocraft.api.util.AtmosphereUtil;
import top.qiguaiaaaa.geocraft.api.util.FluidUtil;
import top.qiguaiaaaa.geocraft.geography.fluid_physics.reality.RealitySnowUpdater;
import top.qiguaiaaaa.geocraft.util.fluid.FluidOperationUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;
import java.util.Set;

import static top.qiguaiaaaa.geocraft.api.block.BlockProperties.MIXTURE;

@Mixin(value = BlockSnow.class)
public class BlockSnowMixin extends Block implements IPermeableBlock{
    @Shadow @Final public static PropertyInteger LAYERS;

    public BlockSnowMixin(Material materialIn) {
        super(materialIn);
    }

    @Override
    @Unique
    public int tickRate(@Nonnull World worldIn) {
        return 5;
    }

    @Unique
    private boolean trySmelt(@Nonnull World world,@Nonnull BlockPos pos,@Nonnull IBlockState state,@Nonnull Random random){
        int layer = state.getValue(BlockSnow.LAYERS);
        boolean isMixture = state.getValue(MIXTURE);
        IAtmosphereAccessor accessor = AtmosphereSystemManager.getAtmosphereAccessor(world,pos,true);
        if (world.getLightFor(EnumSkyBlock.BLOCK, pos) > 11) {
            this.turnIntoWater(world,pos,accessor == null?null:accessor.getAtmosphereHere(),8-layer); //用的是发光Block产生的热量,所以不扣地表温度
            return true;
        }

        if(accessor == null) return false;
        int light = world.getLightFor(EnumSkyBlock.SKY,pos);
        if(light == 0) return false;
        accessor.setSkyLight(light);
        double temp = accessor.getTemperature();
        if(temp > TemperatureProperty.ICE_POINT){
            if(isMixture){
                this.turnIntoWater(world,pos,accessor.getAtmosphereHere(),8-layer);
            }else{
                this.turnIntoMixture(world,pos,layer);
            }
            accessor.drawHeatFromUnderlying(AtmosphereUtil.Constants.WATER_MELT_LATENT_HEAT_PER_QUANTA*layer/2.0);
            return !isMixture;
        }else{
            if(isMixture){
                if(layer == 8){
                    world.setBlockState(pos,Blocks.ICE.getDefaultState());
                }else{
                    world.setBlockState(pos, Blocks.SNOW_LAYER.getDefaultState().withProperty(LAYERS,layer).withProperty(MIXTURE,false));
                }
                accessor.putHeatToUnderlying(AtmosphereUtil.Constants.WATER_MELT_LATENT_HEAT_PER_QUANTA*layer/2.0);
                return layer==8;
            }
            return false;
        }
    }

    @Inject(method = "canPlaceBlockAt",at = @At("HEAD"),cancellable = true)
    public void canPlaceBlockAt(World worldIn, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        cir.cancel();
        IBlockState state = worldIn.getBlockState(pos.down());
        Block block = state.getBlock();

        if (block != Blocks.PACKED_ICE && block != Blocks.BARRIER) {
            BlockFaceShape blockfaceshape = state.getBlockFaceShape(worldIn, pos.down(), EnumFacing.UP);
            cir.setReturnValue(blockfaceshape == BlockFaceShape.SOLID || state.getBlock().isLeaves(state, worldIn, pos.down()) || block == this && state.getValue(BlockSnow.LAYERS) == 8);
        } else {
            cir.setReturnValue(false);
        }
    }
    @Inject(method = "checkAndDropBlock",at = @At("HEAD"),cancellable = true)
    private void checkAndDropBlock(World worldIn, BlockPos pos, IBlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (!this.canPlaceBlockAt(worldIn, pos)) {
            worldIn.scheduleUpdate(pos,this,tickRate(worldIn));
            cir.setReturnValue(false);
        } else {
            cir.setReturnValue(true);
        }
    }
    @Inject(method = "updateTick",at =@At("HEAD"),cancellable = true)
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand, CallbackInfo ci) {
        ci.cancel();
        if(worldIn.isRemote) return;
        if(trySmelt(worldIn, pos, state, rand)) return;
        state = worldIn.getBlockState(pos);
        tryFallDown(worldIn, pos, state);
    }

    @Unique
    protected boolean tryFallDown(World world,BlockPos pos,IBlockState state){
        if(pos.getY() <= 0) return false;
        if(world.isRemote) return false;
        BlockPos downPos = pos.down();
        IBlockState downState = world.getBlockState(downPos);
        if(RealitySnowUpdater.isBlocked(world,downPos,downState,state)){
            return false;
        }
        boolean isMixture = state.getValue(MIXTURE);
        if(downState.getBlock() == Blocks.SNOW_LAYER){
            boolean isDownMixture = downState.getValue(MIXTURE);
            int newLayers = state.getValue(BlockSnow.LAYERS) + downState.getValue(BlockSnow.LAYERS);
            if(isMixture == isDownMixture){
                if(newLayers<=8){
                    world.setBlockToAir(pos);
                    world.setBlockState(downPos,downState.withProperty(BlockSnow.LAYERS,newLayers));
                }else{
                    world.setBlockState(pos,state.withProperty(BlockSnow.LAYERS,newLayers-8));
                    world.setBlockState(downPos,downState.withProperty(BlockSnow.LAYERS,8));
                }
                world.scheduleUpdate(downPos,this,tickRate(world));
            }else{
                double totalWater = isMixture?getQuanta(state,null)/2d:getQuanta(downState,null)/2d;
                if(newLayers<=8){
                    world.setBlockToAir(pos);
                    world.setBlockState(downPos,downState
                            .withProperty(BlockSnow.LAYERS,newLayers)
                            .withProperty(MIXTURE,false));
                }else{
                    world.setBlockState(pos,state.withProperty(BlockSnow.LAYERS,newLayers-8)
                            .withProperty(MIXTURE,false));
                    world.setBlockState(downPos,downState.withProperty(BlockSnow.LAYERS,8)
                            .withProperty(MIXTURE,false));
                }
                IAtmosphereAccessor accessor = AtmosphereSystemManager.getAtmosphereAccessor(world,pos,true);
                if(accessor == null) return true;
                int light = world.getLightFor(EnumSkyBlock.SKY,pos);
                if(light == 0) return true;
                accessor.setSkyLight(light);

                accessor.putHeatToUnderlying(AtmosphereUtil.Constants.WATER_MELT_LATENT_HEAT_PER_QUANTA*totalWater);
            }
        }else if(downState.getBlock() instanceof IPermeableBlock){
            IPermeableBlock permeableBlock = (IPermeableBlock) downState.getBlock();
            int curLayers = getQuanta(state,null);
            boolean hasHalfQuanta = (curLayers&1) !=0 && state.getValue(MIXTURE);

            IAtmosphereAccessor accessor = AtmosphereSystemManager.getAtmosphereAccessor(world,pos,true);
            int light = world.getLightFor(EnumSkyBlock.SKY,pos);
            if(accessor != null) accessor.setSkyLight(light);

            int curQuantaWater = getQuanta(state,FluidRegistry.WATER);
            if(hasHalfQuanta) curQuantaWater++;
            boolean changed = false;
            if(curQuantaWater>0){
                int filledQuanta = permeableBlock.addQuanta(world,downPos,downState, FluidRegistry.WATER,curQuantaWater,true);
                curQuantaWater = curQuantaWater-filledQuanta;
                changed = filledQuanta>0;
                if(hasHalfQuanta && accessor != null)
                    accessor.drawHeatFromUnderlying(AtmosphereUtil.Constants.WATER_MELT_LATENT_HEAT_PER_QUANTA*0.5);
            }
            if(changed) downState = world.getBlockState(downPos);
            int curQuantaSnow = getQuanta(state,GeoFluids.SNOW);
            if(!changed && hasHalfQuanta){
                curQuantaSnow++;
                curQuantaWater--;
            }
            if(curQuantaSnow>0){
                int filled =permeableBlock.addQuanta(world,downPos,downState,GeoFluids.SNOW,curQuantaSnow,true);
                curQuantaSnow -= filled;
                changed |= filled>0;
                if(hasHalfQuanta && accessor != null)
                    accessor.putHeatToUnderlying(AtmosphereUtil.Constants.WATER_MELT_LATENT_HEAT_PER_QUANTA*0.5);
            }

            if(!changed) return false;

            if(curQuantaSnow <= 0 && curQuantaWater <=0){
                world.setBlockToAir(pos);
            }else{
                int total = curQuantaSnow+curQuantaWater;
                if(curQuantaWater>curQuantaSnow){
                    turnIntoWater(world,pos,accessor == null?null:accessor.getAtmosphereHere(),total);
                    if(accessor != null)
                        accessor.drawHeatFromUnderlying(AtmosphereUtil.Constants.WATER_MELT_LATENT_HEAT_PER_QUANTA*curQuantaSnow);
                }else if(curQuantaSnow == curQuantaWater){
                    world.setBlockState(pos,state.withProperty(LAYERS,total)
                            .withProperty(MIXTURE,true));
                }else {
                    world.setBlockState(pos,state.withProperty(LAYERS,total)
                            .withProperty(MIXTURE,false));
                    if(accessor != null)
                        accessor.putHeatToUnderlying(AtmosphereUtil.Constants.WATER_MELT_LATENT_HEAT_PER_QUANTA*curQuantaWater);
                }
            }
            return true;
        }else{
            FluidOperationUtil.triggerDestroyBlockEffectByFluid(world,downPos,downState, GeoFluids.SNOW);
            world.setBlockToAir(pos);
            world.setBlockState(downPos,state);
            world.scheduleUpdate(downPos,this,tickRate(world));
        }
        return true;
    }

    @Unique
    protected void turnIntoMixture(World worldIn, BlockPos pos, int layer) {
        worldIn.setBlockState(pos, Blocks.SNOW_LAYER.getDefaultState().withProperty(LAYERS,layer).withProperty(MIXTURE,true));
    }

    @Unique
    protected void turnIntoWater(World worldIn, BlockPos pos, @Nullable Atmosphere atmosphere, int level) {
        if (worldIn.provider.doesWaterVaporize()) {
            if(atmosphere != null){
                atmosphere.addSteam((8-level)* FluidUtil.ONE_IN_EIGHT_OF_BUCKET_VOLUME,pos);
            }
            worldIn.setBlockToAir(pos);
        } else {
            worldIn.setBlockState(pos, Blocks.WATER.getDefaultState().withProperty(BlockLiquid.LEVEL,level));
            worldIn.neighborChanged(pos, Blocks.WATER, pos);
        }
    }

    @Shadow
    public boolean canPlaceBlockAt(@Nonnull World worldIn, @Nonnull BlockPos pos) {return false;}

    //**********
    // Permeable Block
    //**********

    @Nonnull
    @Override
    public Set<Fluid> getFluid(@Nonnull IBlockState state) {
        return GeoFluids.FluidSets.SNOW_LAYER_SET;
    }

    @Override
    public int getQuanta(@Nonnull IBlockState state, @Nullable Fluid fluid) {
        if(fluid == FluidRegistry.WATER){
            return state.getValue(MIXTURE)?state.getValue(LAYERS)/2:0;
        }else if(fluid == GeoFluids.SNOW){
            return state.getValue(MIXTURE)?state.getValue(LAYERS)/2:state.getValue(LAYERS);
        }else if(fluid == null){
            return state.getValue(LAYERS);
        }
        return 0;
    }

    @Override
    public int getMaxQuanta(@Nonnull IBlockState state, @Nullable Fluid fluid) {
        if(fluid == null) return 8;
        if(fluid != FluidRegistry.WATER && fluid != GeoFluids.SNOW) return 0;
        boolean mixture = state.getValue(MIXTURE);
        if(mixture) return 4;
        return fluid == GeoFluids.SNOW?8:8-state.getValue(LAYERS);
    }

    @Override
    public int getEmptyHeight(@Nonnull IBlockState state, @Nullable Fluid fluid) {
        if(fluid == FluidRegistry.WATER){
            return 0;
        }else if(fluid == GeoFluids.SNOW){
            return state.getValue(MIXTURE)?state.getValue(LAYERS)*getHeightPerQuanta(state)/2:0;
        }
        return 0;
    }

    @Override
    public int getHeightPerQuanta(@Nonnull IBlockState state) {
        return EIGHTH_OF_HEIGHT;
    }

    @Override
    public void addQuanta(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Fluid fluid, int quanta) {
        if(fluid != FluidRegistry.WATER && fluid != GeoFluids.SNOW) return;
        if(quanta == 0) return;
        boolean mixture = state.getValue(MIXTURE);

        IAtmosphereAccessor accessor = AtmosphereSystemManager.getAtmosphereAccessor(world,pos,true);
        int light = world.getLightFor(EnumSkyBlock.SKY,pos);
        if(light <=0) accessor = null;
        if(accessor != null){
            accessor.setSkyLight(light);
        }

        if(mixture){
            int curSnowQuanta = state.getValue(LAYERS)/2;
            int curWaterQuanta = curSnowQuanta;
            quanta = MathHelper.clamp(quanta,-curWaterQuanta,4-curWaterQuanta);
            if(fluid == FluidRegistry.WATER){
                curWaterQuanta += quanta;
                if(quanta <0){
                    world.setBlockState(pos,state.withProperty(MIXTURE,false)
                            .withProperty(LAYERS,curSnowQuanta+curWaterQuanta));
                    if(accessor != null)
                        accessor.putHeatToUnderlying(AtmosphereUtil.Constants.WATER_MELT_LATENT_HEAT_PER_QUANTA*curWaterQuanta);
                }else{
                    world.setBlockState(pos,Blocks.FLOWING_WATER.getDefaultState()
                            .withProperty(BlockLiquid.LEVEL,8-(curSnowQuanta+curWaterQuanta)));
                    if(accessor != null)
                        accessor.drawHeatFromUnderlying(AtmosphereUtil.Constants.WATER_MELT_LATENT_HEAT_PER_QUANTA*curSnowQuanta);
                }
            }else{
                curSnowQuanta += quanta;
                if(quanta<0){
                    world.setBlockState(pos,Blocks.FLOWING_WATER.getDefaultState()
                            .withProperty(BlockLiquid.LEVEL,8-(curSnowQuanta+curWaterQuanta)));
                    if(accessor != null)
                        accessor.drawHeatFromUnderlying(AtmosphereUtil.Constants.WATER_MELT_LATENT_HEAT_PER_QUANTA*curSnowQuanta);
                }else{
                    world.setBlockState(pos,state.withProperty(MIXTURE,false)
                            .withProperty(LAYERS,curSnowQuanta+curWaterQuanta));
                    if(accessor != null)
                        accessor.putHeatToUnderlying(AtmosphereUtil.Constants.WATER_MELT_LATENT_HEAT_PER_QUANTA*curWaterQuanta);
                }
            }
        }else{
            int curSnowQuanta = state.getValue(LAYERS);
            if(fluid == GeoFluids.SNOW){
                quanta = MathHelper.clamp(quanta,-curSnowQuanta,8-curSnowQuanta);
                world.setBlockState(pos,state.withProperty(LAYERS,curSnowQuanta+quanta));
            }else {
                quanta = MathHelper.clamp(quanta,0,8-curSnowQuanta);
                if(quanta<curSnowQuanta){
                    world.setBlockState(pos,state.withProperty(LAYERS,curSnowQuanta+quanta));
                    if(accessor != null)
                        accessor.putHeatToUnderlying(AtmosphereUtil.Constants.WATER_MELT_LATENT_HEAT_PER_QUANTA*quanta);
                }else if(quanta == curSnowQuanta){
                    world.setBlockState(pos,state.withProperty(LAYERS,curSnowQuanta+quanta)
                            .withProperty(MIXTURE,true));
                }else{
                    world.setBlockState(pos,Blocks.FLOWING_WATER.getDefaultState()
                            .withProperty(BlockLiquid.LEVEL,8-(curSnowQuanta+quanta)));
                    if(accessor != null){
                        accessor.drawHeatFromUnderlying(AtmosphereUtil.Constants.WATER_MELT_LATENT_HEAT_PER_QUANTA*quanta);
                    }
                }
            }
        }
    }

    @Override
    public void setQuanta(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Fluid fluid, int newQuanta) {
        if(!getFluid(state).contains(fluid)) return;
        int curQuanta = getQuanta(state,fluid);
        addQuanta(world, pos, state, fluid, newQuanta-curQuanta);
    }

    @Nullable
    @Override
    public IBlockState getQuantaState(@Nonnull IBlockState state, @Nonnull Fluid fluid, int quanta) {
        if(fluid != FluidRegistry.WATER && fluid != GeoFluids.SNOW) return null;
        boolean mixture = state.getValue(MIXTURE);
        quanta = mixture? MathHelper.clamp(quanta,0,4):MathHelper.clamp(quanta,0,8);
        int curLayer = state.getValue(LAYERS);
        if(fluid == GeoFluids.SNOW){
            if(mixture){
                if(quanta<<1==curLayer) return state;
                if(quanta == 0 && curLayer>1) return Blocks.FLOWING_WATER.getDefaultState().withProperty(BlockLiquid.LEVEL,8-curLayer/2);
                if(quanta<<1<curLayer) return Blocks.FLOWING_WATER.getDefaultState()
                        .withProperty(BlockLiquid.LEVEL,8-(curLayer/2+quanta));
                if(quanta<<1>curLayer) return state.withProperty(MIXTURE,false).withProperty(LAYERS,curLayer/2+quanta);
                return null;
            }
            if(quanta == 0) return Blocks.AIR.getDefaultState();
            return state.withProperty(LAYERS,quanta);
        }else if(fluid == FluidRegistry.WATER){
            if(mixture){
                if(quanta<<1==curLayer) return state;
                if(quanta == 0) return state.withProperty(MIXTURE,false)
                        .withProperty(LAYERS,curLayer/2);
                if(quanta<<1>curLayer) return Blocks.FLOWING_WATER.getDefaultState()
                        .withProperty(BlockLiquid.LEVEL,8-(curLayer/2+quanta));
                if(quanta<<1<curLayer) return state.withProperty(MIXTURE,false).withProperty(LAYERS,curLayer/2+quanta);
                return null;
            }
            if(quanta == 0) return state;
            quanta = MathHelper.clamp(quanta,0,8-curLayer);
            if(quanta == curLayer) return
                    state.withProperty(MIXTURE,true)
                            .withProperty(LAYERS,quanta<<1);
            else if(quanta > curLayer) return Blocks.FLOWING_WATER.getDefaultState()
                    .withProperty(BlockLiquid.LEVEL,8-(curLayer+quanta));
            else return state.withProperty(LAYERS,quanta+curLayer);
        }
        return null;
    }

    @Override
    public boolean canFill(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull IBlockState state, @Nonnull Fluid fluid, @Nonnull EnumFacing side, @Nullable IBlockState source) {
        return !isFull(state, fluid);
    }
}
