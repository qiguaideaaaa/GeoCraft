package top.qiguaiaaaa.geocraft.api.atmosphere.raypack;

import top.qiguaiaaaa.geocraft.api.util.APIUtil;

import javax.annotation.Nullable;

/**
 * 辐射热量包，用于在层级之间（一般是上下）传递热量
 */
public class HeatPack implements StuffPack {
    public static final int MIN_HEAT = 20; //小于该值的热量包被视为空
    protected double heat;
    protected final HeatType type;

    /**
     * 创建一个空的热量包
     * @param type 热量包类型，可以为null
     */
    public HeatPack(@Nullable HeatType type){
        this(type,0);
    }

    /**
     * 创建一个具有指定热量的热量包
     * @param type 热量包类型，可以为null
     * @param raw 热量包内存有的热量
     */
    public HeatPack(@Nullable HeatType type, double raw){
        this.type = type;
        this.heat = Math.max(raw,0);
        if(Double.isInfinite(raw) || Double.isNaN(raw)){
            APIUtil.LOGGER.warn("{} creates a NaN or Infinite Heat Pack! Something must be wrong!",APIUtil.callerInfo(1));
        }
    }

    /**
     * 该热量包是否空了。
     * 若热量包的内容小于{@link #MIN_HEAT}，或值无效，则被视为空的
     * @return 若热量包是空的，则返回true
     */
    public boolean isEmpty(){
        return heat < MIN_HEAT || Double.isInfinite(heat) || Double.isNaN(heat);
    }
    public double getAmount() {
        return heat;
    }

    /**
     * 从该热量包中抽取指定量的热量
     * @param amount 期望抽取量
     * @return 实际抽取量
     */
    public double drawHeat(double amount){
        amount = Math.min(amount,heat);
        heat -= amount;
        return amount;
    }

    /**
     * 获取当前热量包的类型
     * @return 热量包类型
     */
    @Nullable
    public HeatType getType() {
        return type;
    }

    /**
     * 热量包的类型
     * 若类型为null，则层级应当将热量全部吸收
     */
    public enum HeatType {
        SHORT_WAVE, //短波辐射
        LONG_WAVE //长波辐射
    }
}
