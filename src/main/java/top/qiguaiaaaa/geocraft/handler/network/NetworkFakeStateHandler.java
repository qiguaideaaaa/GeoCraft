package top.qiguaiaaaa.geocraft.handler.network;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import top.qiguaiaaaa.geocraft.util.factor.SpecialBlockID;

import javax.annotation.Nonnull;

import static top.qiguaiaaaa.geocraft.api.block.BlockProperties.HUMIDITY;

public class NetworkFakeStateHandler {
    public static IBlockState overwriteState(@Nonnull IBlockState state){
        Block block = state.getBlock();
        if(block == Blocks.DIRT){
            return state.withProperty(HUMIDITY,0);
        }if(block == Blocks.GRASS){
            return state.withProperty(HUMIDITY,0);
        }
        if(block == Blocks.SAND){
            return state.withProperty(HUMIDITY,0);
        }
        return state;
    }

    public static int overwriteState(int state){
        int id = state>>4;
        int meta = state &0b1111;
        if(id == SpecialBlockID.BLOCK_DIRT_ID){
            return (id<<4)|(meta%3);
        }
        if(id == SpecialBlockID.BLOCK_GRASS_ID){
            return id<<4;
        }
        if(id == SpecialBlockID.BLOCK_SAND_ID){
            return (id<<4)|(meta%2);
        }
        return state;
    }
}
