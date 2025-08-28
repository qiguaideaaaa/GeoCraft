package top.qiguaiaaaa.geocraft.api.util.exception;

public class ConfigParseError extends RuntimeException{
    public ConfigParseError(String message) {
        super(message);
    }

    public ConfigParseError(String message, Throwable cause) {
        super(message, cause);
    }
}
