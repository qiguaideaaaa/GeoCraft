package top.qiguaiaaaa.fluidgeography.api.util.math;

import net.minecraft.world.chunk.Chunk;

public class Altitude {
    /**
     * 游戏海拔，单位为格
     */
    protected double val;

    /**
     * @param val 游戏海拔
     */
    public Altitude(double val){
        this(val,false);
    }
    public Altitude(double val,boolean isPhysical){
        if(isPhysical) val = get游戏海拔(val);
        this.val = val;
    }

    /**
     * 获得游戏海拔
     * @return 游戏海拔，单位为格
     */
    public double get(){
        return val;
    }

    /**
     * 获得物理海拔，物理海拔=(游戏海拔-64)*24
     * @return 物理海拔，单位为米
     */
    public double get物理海拔(){
        return get物理海拔(val);
    }
    public void set(double newAltitude){
        this.val = newAltitude;
    }
    public void set物理海拔(double newAltitude){
        this.val = get游戏海拔(newAltitude);
    }
    public void set(Altitude altitude){
        this.val = altitude.val;
    }
    public boolean between(double a,double b){
        return val >= a && val <=b;
    }
    public static double get物理海拔(double 游戏海拔){
        return (游戏海拔-63)*64;
    }
    public static double get游戏海拔(double 物理海拔){
        return (物理海拔/64)+63;
    }
    public static double to游戏高度(double 物理高度){return 物理高度/64;}
    public static double to物理高度(double 游戏高度){return 游戏高度*64;}

    @Override
    public String toString() {
        return get物理海拔()+" m [ "+val+" ]";
    }

    public static Altitude getAverageHeight(Chunk chunk){
        int totalHeight = 0;
        int[] heightMap = chunk.getHeightMap();
        for (int j : heightMap) {
            totalHeight += j;
        }
        return new Altitude(((float) totalHeight)/heightMap.length);
    }
}
