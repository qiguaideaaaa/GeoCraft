package top.qiguaiaaaa.fluidgeography.mixin.reality.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockSnow;
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
import top.qiguaiaaaa.fluidgeography.api.atmosphere.AtmosphereSystemManager;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.fluidgeography.api.util.AtmosphereUtil;
import top.qiguaiaaaa.fluidgeography.api.util.FluidUtil;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.property.AtmosphereTemperature;

import javax.annotation.Nullable;
import java.util.Random;

@Mixin(value = BlockSnow.class)
public class BlockSnowMixin {
    @Inject(method = "canPlaceBlockAt",at = @At("HEAD"),cancellable = true)
    public void canPlaceBlockAt(World worldIn, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        cir.cancel();
        IBlockState state = worldIn.getBlockState(pos.down());
        Block block = state.getBlock();

        if (block != Blocks.PACKED_ICE && block != Blocks.BARRIER) {
            BlockFaceShape blockfaceshape = state.getBlockFaceShape(worldIn, pos.down(), EnumFacing.UP);
            cir.setReturnValue(blockfaceshape == BlockFaceShape.SOLID || state.getBlock().isLeaves(state, worldIn, pos.down()) || block == (Object) this && state.getValue(BlockSnow.LAYERS) == 8);
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
        Atmosphere atmosphere = AtmosphereSystemManager.getAtmosphere(worldIn,pos);
        if (worldIn.getLightFor(EnumSkyBlock.BLOCK, pos) > 11) {
            this.turnIntoWater(worldIn,pos,atmosphere,8-layer);
            return;
        }
        if(worldIn.getLightFor(EnumSkyBlock.SKY,pos) == 0) return;
        if(atmosphere == null) return;
        float temp = atmosphere.getTemperature(pos);
        if(temp > AtmosphereTemperature.ICE_POINT){
            this.turnIntoWater(worldIn,pos,atmosphere,8-layer);
            atmosphere.addHeatQuantity(-(AtmosphereUtil.WATER_MELT_LATENT_HEAT_PER_QUANTA*layer));
        }
    }
    protected boolean tryFallDown(World world,BlockPos pos,IBlockState state){
        if(pos.getY() <= 0) return false;
        if(world.isRemote) return false;
        IBlockState downState = world.getBlockState(pos.down());
        if(downState.getBlock() == Blocks.AIR){
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
                atmosphere.addWaterAmount((8-level)* FluidUtil.ONE_IN_EIGHT_OF_BUCKET_VOLUME);
            }
            worldIn.setBlockToAir(pos);
        } else {
            worldIn.setBlockState(pos, Blocks.WATER.getDefaultState().withProperty(BlockLiquid.LEVEL,level));
            worldIn.neighborChanged(pos, Blocks.WATER, pos);
        }
    }
    @Shadow
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {return false;}
}
