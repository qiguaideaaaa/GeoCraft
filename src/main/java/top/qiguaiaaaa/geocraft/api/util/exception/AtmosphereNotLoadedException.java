package top.qiguaiaaaa.geocraft.api.util.exception;

import net.minecraft.util.math.BlockPos;

public class AtmosphereNotLoadedException extends RuntimeException{
    public final int dimensionID,x,z;
    public AtmosphereNotLoadedException(int dimID,int x,int z) {
        super("Atmosphere at ChunkPos("+x+","+z+") in dimension "+dimID+" is currently not loaded");
        this.dimensionID = dimID;
        this.x = x;
        this.z = z;
    }
    public AtmosphereNotLoadedException(int dimID, BlockPos pos) {
        this(dimID,pos.getX()>>4,pos.getZ()>>4);
    }
}
