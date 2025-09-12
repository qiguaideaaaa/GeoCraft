package top.qiguaiaaaa.geocraft.mixin.atmosphere.block;

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
import top.qiguaiaaaa.geocraft.api.atmosphere.AtmosphereSystemManager;
import top.qiguaiaaaa.geocraft.api.atmosphere.accessor.IAtmosphereAccessor;
import top.qiguaiaaaa.geocraft.api.property.TemperatureProperty;
import top.qiguaiaaaa.geocraft.api.util.AtmosphereUtil;

import java.util.Random;

@Mixin(value = BlockIce.class)
public class BlockIceMixin {
    @Inject(method = "updateTick",at =@At("RETURN"))
    private void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand, CallbackInfo ci) {
        if(worldIn.getBlockState(pos).getBlock() != (Object) this) return;
        int light = worldIn.getLightFor(EnumSkyBlock.SKY,pos);
        if(light == 0) return;
        IAtmosphereAccessor accessor = AtmosphereSystemManager.getAtmosphereAccessor(worldIn,pos,true);
        if(accessor == null) return;
        accessor.setSkyLight(light);
        double temp = accessor.getTemperature();
        if(temp > TemperatureProperty.ICE_POINT){
            this.turnIntoWater(worldIn,pos);
            accessor.drawHeatFromUnderlying(AtmosphereUtil.Constants.WATER_MELT_LATENT_HEAT_PER_QUANTA*8);
        }
    }
    @Shadow
    protected void turnIntoWater(World worldIn, BlockPos pos) {
    }
}
