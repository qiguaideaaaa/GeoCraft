package top.qiguaiaaaa.geocraft.api.atmosphere.tracker;

import top.qiguaiaaaa.geocraft.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.geocraft.api.util.APIUtil;
import top.qiguaiaaaa.geocraft.api.util.io.FileLogger;

import javax.annotation.Nonnull;
import java.util.Date;

/**
 * 跟踪大气信息的监听器的抽象实现，该监听器会将大气信息记录到指定文件中
 */
public abstract class InformationLoggingTracker implements IAtmosphereTracker {
    protected final long id;
    protected final FileLogger logger;
    protected final int time;
    protected int nowTime = 0;

    /**
     * 创建一个大气信息追踪器
     * @param logger 一个文件Logger，用于将信息记录到文件中
     * @param time 追踪持续时间，单位大气刻
     */
    public InformationLoggingTracker(@Nonnull FileLogger logger, int time) {
        this.logger =logger;
        this.time = time;
        this.id = new Date().getTime();
    }

    public long getId() {
        return id;
    }

    public int getContinuousTime() {
        return time;
    }

    public int getProcessedTime() {
        return nowTime;
    }

    /**
     * 检查是否已经到结束记录的时间
     * @param atmosphere 记录的大气实例
     * @return 若已经结束，则返回true。该方法会自动关闭文件数据流。
     */
    protected boolean checkLoggingTime(@Nonnull Atmosphere atmosphere){
        if(nowTime >= time){
            APIUtil.LOGGER.info("track atmosphere task id={} completed",id);
            atmosphere.removeTracker(this);
            logger.close();
            return true;
        }
        return false;
    }
}
