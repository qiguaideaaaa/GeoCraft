package top.qiguaiaaaa.fluidgeography.mixin.atmosphere;

import net.minecraft.block.BlockCauldron;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.AtmosphereSystemManager;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.property.TemperatureProperty;
import top.qiguaiaaaa.fluidgeography.api.configs.AtmosphereConfig;

@Mixin(value = BlockCauldron.class)
public class BlockCauldronMixin {
    @Inject(method = "fillWithRain",at =@At("HEAD"),cancellable = true)
    public void fillWithRain(World worldIn, BlockPos pos, CallbackInfo ci) {
        if(AtmosphereConfig.ALLOW_CAULDRON_GET_INFINITE_WATER.getValue().value) return;
        ci.cancel();
        if (worldIn.rand.nextInt(20) != 1) {
            return;
        }
        Atmosphere atmosphere = AtmosphereSystemManager.getAtmosphere(worldIn,pos);
        if(atmosphere == null) return;
        if(atmosphere.drainWater(333,pos,true) <333) return;
        float temp = atmosphere.getAtmosphereTemperature(pos);

        if (temp< TemperatureProperty.ICE_POINT) return;

        IBlockState iblockstate = worldIn.getBlockState(pos);
        if (iblockstate.getValue(BlockCauldron.LEVEL) < 3) {
            atmosphere.drainWater(333,pos,false);
            worldIn.setBlockState(pos, iblockstate.cycleProperty(BlockCauldron.LEVEL), 2);
        }
    }
}
