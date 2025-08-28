package top.qiguaiaaaa.geocraft.api.atmosphere.tracker;

import top.qiguaiaaaa.geocraft.api.GEOInfo;
import top.qiguaiaaaa.geocraft.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.geocraft.api.util.io.FileLogger;

import java.util.Date;

public abstract class InformationLoggingTracker implements IAtmosphereTracker {
    protected final long id;
    protected final FileLogger logger;
    protected final int time;
    protected int nowTime = 0;
    public InformationLoggingTracker(FileLogger logger, int time) {
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

    protected boolean checkLoggingTime(Atmosphere atmosphere){
        if(nowTime >= time){
            GEOInfo.getLogger().info("track atmosphere task id={} completed",id);
            atmosphere.removeTracker(this);
            logger.close();
            return true;
        }
        return false;
    }
}
