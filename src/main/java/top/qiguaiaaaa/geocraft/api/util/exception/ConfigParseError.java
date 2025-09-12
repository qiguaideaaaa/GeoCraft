package top.qiguaiaaaa.geocraft.api.util.exception;

import javax.annotation.Nonnull;

/**
 * 当无法反序列化某个配置值时抛出
 * @author QiguaiAAAA
 */
public class ConfigParseError extends RuntimeException{
    public ConfigParseError(@Nonnull String message) {
        super(message);
    }

    public ConfigParseError(@Nonnull String message,@Nonnull Throwable cause) {
        super(message, cause);
    }
}
