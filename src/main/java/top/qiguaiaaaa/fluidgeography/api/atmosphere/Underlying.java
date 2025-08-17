package top.qiguaiaaaa.fluidgeography.api.atmosphere;

import net.minecraft.world.chunk.Chunk;
import top.qiguaiaaaa.fluidgeography.api.util.ChunkUtil;
import top.qiguaiaaaa.fluidgeography.api.util.math.Altitude;

public class Underlying{
    public static final int 默认热容 = 305152000;
    public final long 热容;
    public final double 平均返照率;
    public final double 平均发射率;
    protected Altitude 地面平均海拔 = new Altitude(63);


    public Underlying(long 热容, double 平均返照率, double 平均发射率) {
        this(热容,平均返照率, 平均发射率,64);
    }
    public Underlying(long 热容, double 平均返照率, double 平均发射率, double 地面平均海拔) {
        this(热容,平均返照率,平均发射率,new Altitude(地面平均海拔));
    }
    public Underlying(long 热容, double 平均返照率, double 平均发射率, Altitude 地面平均海拔) {
        this.热容 = 热容;
        this.平均返照率 = 平均返照率;
        this.平均发射率 = 平均发射率;
        set地面平均海拔(地面平均海拔);
    }

    public void 更新平均海拔(Chunk chunk, AtmosphereWorldInfo worldInfo){
        if(!worldInfo.isWorldClosed()) set地面平均海拔(ChunkUtil.getAverageHeight(chunk));
        else set地面平均海拔(worldInfo.getWorld().getSeaLevel());
    }

    /**
     * 设置地面平均海拔，类型为游戏海拔
     * @param 地面平均海拔 类型为游戏海拔
     */
    public void set地面平均海拔(double 地面平均海拔) {
        if(地面平均海拔<0 || 地面平均海拔 >254) return;
        this.地面平均海拔.set(地面平均海拔);
    }
    public void set地面平均海拔(Altitude 地面平均海拔) {
        if(!地面平均海拔.between(0,254)) return;
        this.地面平均海拔.set(地面平均海拔);
    }

    /**
     * 获取地面平均海拔
     * @return 地面平均海拔，类型为游戏海拔
     */
    public Altitude get地面平均海拔() {
        return 地面平均海拔;
    }

    public Underlying copy(){
        return new Underlying(热容, 平均返照率, 平均发射率, 地面平均海拔);
    }
}
