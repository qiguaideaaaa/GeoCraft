package top.qiguaiaaaa.fluidgeography.api.util;

import net.minecraft.init.Biomes;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fluids.Fluid;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class ChunkUtil {
    public static final List<EnumFacing> HORIZONTALS = Collections.unmodifiableList(Arrays.asList(
            EnumFacing.WEST, EnumFacing.EAST, EnumFacing.NORTH, EnumFacing.SOUTH));

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
