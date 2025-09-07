package top.qiguaiaaaa.geocraft.world.gen;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.IWorldGenerator;
import top.qiguaiaaaa.geocraft.api.block.BlockProperties;
import top.qiguaiaaaa.geocraft.api.util.FluidUtil;
import top.qiguaiaaaa.geocraft.block.IBlockDirt;
import top.qiguaiaaaa.geocraft.block.IPermeable;

import java.util.Random;

public class GeographyWaterPopulatingGenerator implements IWorldGenerator {

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        int beginX = chunkX<<4;
        int beginZ = chunkZ<<4;
        for(int x=0;x<16;x++){
            for(int z=0;z<16;z++){
                boolean waterFlag = false;
                for (int y=world.getHeight(beginX+x,beginZ+z);y>=0;y--){
                    pos.setPos(beginX+x,y,beginZ+z);
                    IBlockState state = world.getBlockState(pos);
                    if(FluidUtil.getFluid(state) == FluidRegistry.WATER){
                        waterFlag = true;
                        continue;
                    }
                    if(!waterFlag) continue;
                    if(state.getBlock() instanceof IBlockDirt){
                        world.setBlockState(pos,state.withProperty(BlockProperties.HUMIDITY,4), Constants.BlockFlags.NO_RERENDER | Constants.BlockFlags.NO_OBSERVERS );
                        continue;
                    }
                    if(state.getBlock() instanceof IPermeable){
                        IPermeable permeable = (IPermeable) state.getBlock();
                        if(permeable.getFluid(world,pos,state) != FluidRegistry.WATER) waterFlag = false;
                    }else waterFlag =false;
                }
            }
        }
    }
}
