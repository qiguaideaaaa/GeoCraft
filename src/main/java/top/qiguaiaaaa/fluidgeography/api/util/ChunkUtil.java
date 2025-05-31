package top.qiguaiaaaa.fluidgeography.api.util;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Biomes;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fluids.Fluid;
import top.qiguaiaaaa.fluidgeography.api.configs.AtmosphereConfig;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.Underlying;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class ChunkUtil {
    public static final List<EnumFacing> HORIZONTALS = Collections.unmodifiableList(Arrays.asList(
            EnumFacing.WEST, EnumFacing.EAST, EnumFacing.NORTH, EnumFacing.SOUTH));
    public static float getAverageHeight(Chunk chunk){
        int totalHeight = 0;
        int[] heightMap = chunk.getHeightMap();
        for (int j : heightMap) {
            totalHeight += j;
        }
        return ((float) totalHeight)/heightMap.length;
    }
    public static Underlying getUnderlying(Chunk chunk,double averageHeight){
        long heatCapacity = 0;
        double averageReflectivity = 0,averageEmissivity = 0;
        for(int x=0;x<16;x++){
            for(int z=0;z<16;z++){
                int height = chunk.getHeightValue(x,z);
                IBlockState state = chunk.getBlockState(x,height,z);
                if(state.getBlock() == Blocks.AIR && height>0){
                    height--;
                    state = chunk.getBlockState(x,height,z);
                }
                int blockC = AtmosphereConfig.getSpecificHeatCapacity(state);
                if(FluidUtil.isFluid(state) && height>0){
                    blockC += blockC*getSameLiquidDepth(chunk,x,height-1,z,FluidUtil.getFluid(state));
                }
                heatCapacity += blockC* 1000L;
                averageReflectivity += AtmosphereConfig.getReflectivity(state);
                averageEmissivity += AtmosphereConfig.getEmissivity(state);
            }
        }
        averageReflectivity /= 256;
        averageEmissivity /= 256;
        return new Underlying(heatCapacity,averageReflectivity,averageEmissivity,averageHeight);
    }
    public static int getSameLiquidDepth(Chunk chunk,int x,int y,int z, Fluid fluid){
        int ans = 0,maxDepth = 5;
        while (FluidUtil.getFluid(chunk.getBlockState(x,y,z))== fluid){
            ans++;
            if(ans >maxDepth) break;
            if(y>0) y--;
        }
        return ans;
    }
    public static Biome getMainBiome(Chunk chunk){
        byte[] biomes = chunk.getBiomeArray();
        short[] frequency = new short[biomes.length+256];
        for (int biome : biomes) {
            if(biome<0) biome = biome+256;
            frequency[biome+256]++;
        }
        int maxPosition = 0,maxFrequency = -1;
        for(int i=0;i< frequency.length;i++){
            if(frequency[i]>maxFrequency){
                maxFrequency = frequency[i];
                maxPosition = i;
            }
        }
        return Biome.getBiome(maxPosition-256, Biomes.PLAINS);
    }
}
