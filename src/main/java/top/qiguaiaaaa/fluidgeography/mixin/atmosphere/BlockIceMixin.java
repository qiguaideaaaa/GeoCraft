package top.qiguaiaaaa.fluidgeography.mixin.atmosphere;

import net.minecraft.block.BlockIce;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.AtmosphereSystemManager;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.fluidgeography.api.util.AtmosphereUtil;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.property.AtmosphereTemperature;

import java.util.Random;

@Mixin(value = BlockIce.class)
public class BlockIceMixin {
    @Inject(method = "updateTick",at =@At("RETURN"))
    private void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand, CallbackInfo ci) {
        if(worldIn.getBlockState(pos).getBlock() != (Object) this) return;
        if(worldIn.getLightFor(EnumSkyBlock.SKY,pos) == 0) return;
        Atmosphere atmosphere = AtmosphereSystemManager.getAtmosphere(worldIn,pos);
        if(atmosphere == null) return;
        double temp = atmosphere.get温度(pos,false);
        if(temp > AtmosphereTemperature.ICE_POINT){
            this.turnIntoWater(worldIn,pos);
            atmosphere.add低层大气热量(-(AtmosphereUtil.WATER_MELT_LATENT_HEAT_PER_QUANTA*8));
        }
    }
    @Shadow
    protected void turnIntoWater(World worldIn, BlockPos pos) {
    }
}
