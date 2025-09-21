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
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.qiguaiaaaa.geocraft.api.atmosphere.AtmosphereSystemManager;
import top.qiguaiaaaa.geocraft.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.geocraft.api.atmosphere.accessor.IAtmosphereAccessor;
import top.qiguaiaaaa.geocraft.api.property.TemperatureProperty;
import top.qiguaiaaaa.geocraft.api.util.AtmosphereUtil;
import top.qiguaiaaaa.geocraft.api.util.FluidUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Random;

@Mixin(value = BlockSnow.class)
public class BlockSnowMixin extends Block{
    public BlockSnowMixin(Material materialIn) {
        super(materialIn);
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
            if(tryFallDown(worldIn,pos,state)){
                cir.setReturnValue(true);
                return;
            }
            worldIn.setBlockToAir(pos);
            cir.setReturnValue(false);
        }
        else {
            cir.setReturnValue(true);
        }
    }
    @Inject(method = "updateTick",at =@At("HEAD"),cancellable = true)
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand, CallbackInfo ci) {
        ci.cancel();
        int layer = state.getValue(BlockSnow.LAYERS);
        IAtmosphereAccessor accessor = AtmosphereSystemManager.getAtmosphereAccessor(worldIn,pos,true);
        if (worldIn.getLightFor(EnumSkyBlock.BLOCK, pos) > 11) {
            this.turnIntoWater(worldIn,pos,accessor == null?null:accessor.getAtmosphereHere(),8-layer); //用的是发光Block产生的热量,所以不扣地表温度
            return;
        }

        if(accessor == null) return;
        int light = worldIn.getLightFor(EnumSkyBlock.SKY,pos);
        if(light == 0) return;
        accessor.setSkyLight(light);
        double temp = accessor.getTemperature();
        if(temp > TemperatureProperty.ICE_POINT){
            this.turnIntoWater(worldIn,pos,accessor.getAtmosphereHere(),8-layer);
            accessor.drawHeatFromUnderlying(AtmosphereUtil.Constants.WATER_MELT_LATENT_HEAT_PER_QUANTA*layer);
        }
    }
    protected boolean tryFallDown(World world,BlockPos pos,IBlockState state){
        if(pos.getY() <= 0) return false;
        if(world.isRemote) return false;
        IBlockState downState = world.getBlockState(pos.down());
        if(downState.getBlock().getMaterial(downState) == Material.AIR){
            world.setBlockToAir(pos);
            world.setBlockState(pos.down(),state);
            return true;
        }
        if(downState.getBlock() != Blocks.SNOW_LAYER) return false;
        if(downState.getValue(BlockSnow.LAYERS) == 8) return false;
        int newLayers = state.getValue(BlockSnow.LAYERS) + downState.getValue(BlockSnow.LAYERS);
        if(newLayers<=8){
            world.setBlockToAir(pos);
            world.setBlockState(pos.down(),downState.withProperty(BlockSnow.LAYERS,newLayers));
        }else{
            world.setBlockState(pos,state.withProperty(BlockSnow.LAYERS,newLayers-8));
            world.setBlockState(pos.down(),downState.withProperty(BlockSnow.LAYERS,8));
        }
        return true;
    }
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
}
