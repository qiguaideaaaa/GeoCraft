package top.qiguaiaaaa.fluidgeography.simulation;

import net.minecraft.block.BlockSnow;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.AtmosphereSystemManager;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.fluidgeography.api.util.AtmosphereUtil;
import top.qiguaiaaaa.fluidgeography.api.util.FluidUtil;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.property.AtmosphereTemperature;
import top.qiguaiaaaa.fluidgeography.util.BaseUtil;

import javax.annotation.Nullable;
import java.util.Random;

import static net.minecraft.block.BlockLiquid.LEVEL;

public class MoreRealitySimulationCore {
    @Nullable
    public static IBlockState evaporateWater(World world, BlockPos pos, IBlockState state, Random rand){
        if(world.getLightFor(EnumSkyBlock.SKY,pos) == 0) return state;
        Atmosphere atmosphere = AtmosphereSystemManager.getAtmosphere(world,pos);
        if(atmosphere == null) return state;
        double possibility = getWaterEvaporatePossibility(world,pos,state,atmosphere);
        int meta = state.getValue(LEVEL);
        if(!BaseUtil.getRandomResult(rand,possibility)){
            return state;
        }
        if(meta >=8) return null;
        if(!atmosphere.addWaterAmount(FluidUtil.ONE_IN_EIGHT_OF_BUCKET_VOLUME)) return state;
        atmosphere.addHeatQuantity(-(AtmosphereUtil.WATER_EVAPORATE_LATENT_HEAT_PER_QUANTA));
        if(meta == 7) return null;
        state = state.withProperty(LEVEL,meta+1);
        return state;
    }
    public static IBlockState freezeWater(World world, BlockPos pos, IBlockState state, Random rand){
        if(world.getLightFor(EnumSkyBlock.SKY,pos) == 0) return state;
        int meta = state.getValue(LEVEL);
        if(meta >=8) return state;
        Atmosphere atmosphere = AtmosphereSystemManager.getAtmosphere(world, pos);
        if(atmosphere == null) return state;
        double possibility  = AtmosphereUtil.getFreezePossibility(atmosphere,pos);
        if(possibility <= 0) return state;
        if(meta == 7) possibility = Math.min(possibility*8,1);
        else if(meta == 6) possibility = Math.min(possibility*4,1);
        else if(meta == 5) possibility = Math.min(possibility*2,1);
        if(!BaseUtil.getRandomResult(rand,possibility*0.85+0.15)){
            return state;
        }
        if(meta == 0){
            if(!AtmosphereUtil.canWaterFreeze(world,pos,true)) return state;
            return Blocks.ICE.getDefaultState();
        }
        if(!AtmosphereUtil.canPlaceSnow(world,pos)) return state;
        int quanta = 8-meta;
        atmosphere.addHeatQuantity(AtmosphereUtil.WATER_MELT_LATENT_HEAT_PER_QUANTA*quanta);
        return Blocks.SNOW_LAYER.getDefaultState().withProperty(BlockSnow.LAYERS,quanta);
    }

    public static double getWaterEvaporatePossibility(World world,BlockPos pos,IBlockState state,Atmosphere atmosphere){
        double possibility = AtmosphereUtil.getWaterEvaporatePossibility(atmosphere);
        if(!world.isAreaLoaded(pos,1)) return possibility;

        int meta = state.getValue(LEVEL);
        if(meta <5) return possibility;

        byte neighborsWater = 0;
        for(EnumFacing facing:EnumFacing.HORIZONTALS){
            BlockPos facingPos = pos.offset(facing);
            IBlockState facingState = world.getBlockState(facingPos);
            if(FluidUtil.getFluid(facingState) == FluidRegistry.WATER){
                neighborsWater++;
            }
        }
        if(neighborsWater == 4) return possibility;

        if(pos.getY() <= 0) return possibility;
        IBlockState downState= world.getBlockState(pos.down());
        if(FluidUtil.getFluid(downState) == FluidRegistry.WATER) return possibility;

        if(meta == 7) possibility = Math.min(possibility*8,1);
        else if(meta == 6) possibility = Math.min(possibility*4,1);
        else if(meta == 5) possibility = Math.min(possibility*2,1);

        return possibility;
    }

    /**
     * 是否能够在指定位置降水
     * @param world 世界
     * @param pos 位置
     * @return 如果能，则返回true
     */
    public static boolean canRainAt(World world,BlockPos pos){
        Atmosphere atmosphere = AtmosphereSystemManager.getAtmosphere(world, pos);
        if(atmosphere == null) return false;
        if(atmosphere.getWaterAmount()<FluidUtil.ONE_IN_EIGHT_OF_BUCKET_VOLUME) return false;
        float temp = atmosphere.getTemperature(pos);
        if (!(temp < AtmosphereTemperature.ICE_POINT) && !(temp > AtmosphereTemperature.BOILED_POINT)) {
            if (pos.getY() >= 0 && pos.getY() < 256) {
                IBlockState state = world.getBlockState(pos);

                return state.getBlock().isAir(state, world, pos) && Blocks.FLOWING_WATER.canPlaceBlockAt(world, pos);
            }

        }
        return false;
    }
}
