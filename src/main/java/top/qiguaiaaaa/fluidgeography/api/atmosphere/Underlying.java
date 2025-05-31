package top.qiguaiaaaa.fluidgeography.api.atmosphere;

import net.minecraft.world.chunk.Chunk;
import top.qiguaiaaaa.fluidgeography.api.util.ChunkUtil;

public class Underlying{
    public static final int DEFAULT_HEAT_CAPACITY = 305152000;
    public final long heatCapacity;
    public final double averageReflectivity;
    public final double averageEmissivity;
    protected double averageHeight;


    public Underlying(long heatCapacity, double averageReflectivity,double averageEmissivity) {
        this(heatCapacity,averageReflectivity,averageEmissivity,64);
    }
    public Underlying(long heatCapacity, double averageReflectivity,double averageEmissivity,double averageHeight) {
        this.heatCapacity = heatCapacity;
        this.averageReflectivity = averageReflectivity;
        this.averageEmissivity = averageEmissivity;
        setAverageHeight(averageHeight);
    }

    public void calculateAverageHeight(Chunk chunk, AtmosphereWorldInfo worldInfo){
        if(!worldInfo.isWorldClosed()) setAverageHeight(ChunkUtil.getAverageHeight(chunk));
        else setAverageHeight(worldInfo.getWorld().getSeaLevel());
    }

    public void setAverageHeight(double averageHeight) {
        if(averageHeight<0 || averageHeight>254) return;
        this.averageHeight = averageHeight;
    }

    public double getAverageHeight() {
        return averageHeight;
    }

    public Underlying copy(){
        return new Underlying(heatCapacity,averageReflectivity,averageEmissivity,averageHeight);
    }
}
