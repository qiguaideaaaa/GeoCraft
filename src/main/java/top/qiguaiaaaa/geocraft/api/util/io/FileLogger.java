package top.qiguaiaaaa.geocraft.api.util.io;

import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class FileLogger {
    private final String filePath;
    private final BufferedWriter writer;
    private final Logger logger;

    public FileLogger(String filePath,Logger logger) throws IOException {
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
