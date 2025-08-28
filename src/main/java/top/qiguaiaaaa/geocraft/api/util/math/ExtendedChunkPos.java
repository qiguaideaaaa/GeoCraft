package top.qiguaiaaaa.geocraft.api.util.math;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

public class ExtendedChunkPos extends ChunkPos {
    public ExtendedChunkPos(int x, int z) {
        super(x, z);
    }

    public ExtendedChunkPos(BlockPos pos) {
        super(pos);
    }
    public ExtendedChunkPos(ChunkPos chunkPos){
        this(chunkPos.x,chunkPos.z);
    }
    public ExtendedChunkPos north(){
        return north(1);
    }
    public ExtendedChunkPos north(int n){
        return offset(EnumFacing.NORTH,n);
    }
    public ExtendedChunkPos south(){
        return south(1);
    }
    public ExtendedChunkPos south(int n){
        return offset(EnumFacing.SOUTH,n);
    }
    public ExtendedChunkPos west(){
        return west(1);
    }
    public ExtendedChunkPos west(int n){
        return offset(EnumFacing.WEST,n);
    }
    public ExtendedChunkPos east(){
        return east(1);
    }
    public ExtendedChunkPos east(int n){
        return offset(EnumFacing.EAST,n);
    }
    public ExtendedChunkPos offset(EnumFacing facing){
        return offset(facing,1);
    }
    public ExtendedChunkPos offset(EnumFacing facing,int n){
        if(facing == EnumFacing.UP || facing == EnumFacing.DOWN) throw new IllegalArgumentException();
        if(n == 0)
            return this;
        return new ExtendedChunkPos(this.x+facing.getXOffset()*n,this.z+facing.getZOffset()*n);
    }
}
