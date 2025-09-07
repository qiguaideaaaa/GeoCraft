package top.qiguaiaaaa.geocraft.mixin.reality.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockSnowBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.qiguaiaaaa.geocraft.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.geocraft.api.atmosphere.AtmosphereSystemManager;
import top.qiguaiaaaa.geocraft.api.atmosphere.accessor.IAtmosphereAccessor;
import top.qiguaiaaaa.geocraft.api.property.TemperatureProperty;
import top.qiguaiaaaa.geocraft.api.util.AtmosphereUtil;

import javax.annotation.Nullable;
import java.util.Random;

@Mixin(value = BlockSnowBlock.class)
public class BlockSnowBlockMixin extends Block {
    public BlockSnowBlockMixin(Material materialIn) {
        super(materialIn);
    }
    @Inject(method = "updateTick",at =@At("HEAD"),cancellable = true)
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand, CallbackInfo ci) {
        ci.cancel();
        IAtmosphereAccessor accessor = AtmosphereSystemManager.getAtmosphereAccessor(worldIn,pos,true);
        if (worldIn.getLightFor(EnumSkyBlock.BLOCK, pos) > 11) {
            this.turnIntoWater(worldIn,pos,accessor == null?null:accessor.getAtmosphereHere()); //用的是发光Block产生的热量,所以不扣地表温度
            return;
        }

        if(accessor == null) return;
        int light = worldIn.getLightFor(EnumSkyBlock.SKY,pos);
        if(light == 0) return;
        accessor.setSkyLight(light);
        double temp = accessor.getTemperature();
        if(temp > TemperatureProperty.ICE_POINT){
            this.turnIntoWater(worldIn,pos,accessor.getAtmosphereHere());
            accessor.drawHeatFromUnderlying(AtmosphereUtil.FinalFactors.WATER_MELT_LATENT_HEAT_PER_QUANTA*8);
        }
    }

    protected void turnIntoWater(World worldIn, BlockPos pos, @Nullable Atmosphere atmosphere) {
        if (worldIn.provider.doesWaterVaporize()) {
            if(atmosphere != null){
                atmosphere.addSteam(Fluid.BUCKET_VOLUME,pos);
            }
            worldIn.setBlockToAir(pos);
        } else {
            worldIn.setBlockState(pos, Blocks.WATER.getDefaultState().withProperty(BlockLiquid.LEVEL,0));
            worldIn.neighborChanged(pos, Blocks.WATER, pos);
        }
    }
}
