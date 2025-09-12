package top.qiguaiaaaa.geocraft.api.configs.value.map.entry;

import javax.annotation.Nonnull;

/**
 * 表示一个字符串-整型对
 * @author QiguaiAAAA
 */
public class StringIntegerEntry extends ConfigEntry<String, Integer> {
    /**
     * 创建一个字符串-整型对
     * @param s 字符串
     * @param value 值
     */
    public StringIntegerEntry(@Nonnull String s, int value) {
        super(s, value);
    }
}
