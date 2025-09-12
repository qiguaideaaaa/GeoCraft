package top.qiguaiaaaa.geocraft.api.util.io;

import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 用于Information Tracker记录信息
 * @author QiguaiAAAA
 */
public class FileLogger {
    private final String filePath;
    private final BufferedWriter writer;
    private final Logger logger;

    /**
     * 创建一个FileLogger实例
     * @param filePath 写入的文件路径
     * @param logger 用于记录日志的日志
     * @throws IOException
     */
    public FileLogger(@Nonnull String filePath,@Nonnull Logger logger) throws IOException {
        this.filePath = filePath;
        writer = new BufferedWriter(new FileWriter(filePath, true));
        this.logger = logger;
    }

    public String getFilePath() {
        return filePath;
    }

    public void println(String message) {
        try {
            writer.write(message);
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            logger.warn(e);
        }
    }
    public void print(String message){
        try {
            writer.write(message);
        } catch (IOException e) {
            logger.warn(e);
        }
    }
    public void close(){
        try {
            writer.flush();
            writer.close();
        } catch (IOException e) {
            logger.warn(e);
        }
    }
    public void flush(){
        try {
            writer.flush();
        }catch (IOException e) {
            logger.warn(e);
        }
    }
}
