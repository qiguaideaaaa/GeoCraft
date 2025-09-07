package top.qiguaiaaaa.geocraft.handler.network;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

import static top.qiguaiaaaa.geocraft.api.block.BlockProperties.HUMIDITY;

public class NetworkFakeStateHandler {
    public static IBlockState overwriteState(IBlockState state){
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
}
