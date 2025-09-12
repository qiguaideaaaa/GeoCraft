package top.qiguaiaaaa.geocraft.api.util;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import top.qiguaiaaaa.geocraft.api.GeoCraftAPI;

import javax.annotation.Nonnull;

/**
 * @author QiguaiAAAA
 */
public final class APIUtil {
    private APIUtil(){}
    public static final Logger LOGGER = LogManager.getLogger(GeoCraftAPI.PROVIDERS);

    /**
     * 获取当前调用者信息
     * @param who 具体哪个调用者，即往上溯源多少层。例如1就是返回上一层
     * @return 返回一个四个元素的Object数组,包含调用者的className，方法名称，文件名，行数
     */
    @Nonnull
    public static String callerInfo(int who){
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        if (who + 2 >= stackTrace.length)
            return "?.?(?:?)";
        StackTraceElement element = stackTrace[who+2];
        return element.getClassName()+'.'+element.getMethodName()+'('+element.getFileName()+':'+element.getLineNumber()+')';
    }
}
