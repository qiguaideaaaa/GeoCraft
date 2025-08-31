package top.qiguaiaaaa.geocraft.simulation;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import top.qiguaiaaaa.geocraft.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.geocraft.api.atmosphere.AtmosphereSystemManager;
import top.qiguaiaaaa.geocraft.api.property.TemperatureProperty;
import top.qiguaiaaaa.geocraft.util.ChunkUtil;
import top.qiguaiaaaa.geocraft.api.util.FluidUtil;

public class VanillaLikeSimulationCore {
    /**
     * 是否能够在指定位置降水
     * @param world 世界
     * @param pos 位置
     * @return 如果能，则返回true
     */
    public static boolean canRainAt(World world, BlockPos pos){
        if (pos.getY() < 0 || pos.getY() >= 256) {
            return false;
        }
        if(!world.isAreaLoaded(pos,1)) return false;
        Atmosphere atmosphere = AtmosphereSystemManager.getAtmosphere(world,pos);
        if(atmosphere == null) return false;
        if(atmosphere.drainWater(Fluid.BUCKET_VOLUME,pos,true)< Fluid.BUCKET_VOLUME) return false;
        float temp = atmosphere.getAtmosphereTemperature(pos);
        if (!(temp < TemperatureProperty.ICE_POINT) && !(temp > TemperatureProperty.BOILED_POINT)) {
            IBlockState state = world.getBlockState(pos);
            if(FluidUtil.getFluid(state) != FluidRegistry.WATER) return false;
            if(state.getValue(BlockLiquid.LEVEL) != 1) return false;
            int adjacentSourceBlocks = 0;
            for(EnumFacing facing: ChunkUtil.HORIZONTALS){
                BlockPos facingPos = pos.offset(facing);
                IBlockState facingState = world.getBlockState(facingPos);
                if(FluidUtil.getFluid(facingState) != FluidRegistry.WATER) continue;
                adjacentSourceBlocks += FluidUtil.isFullFluid(world,facingPos,facingState)?1:0;
            }
            return adjacentSourceBlocks>=2;
        }
        return false;
    }
}
