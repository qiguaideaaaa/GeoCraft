package top.qiguaiaaaa.fluidgeography.api.configs.value.base;

import top.qiguaiaaaa.fluidgeography.api.configs.value.Configurable;

public class ConfigString implements Configurable,Comparable<String>,CharSequence {
    public final String value;
    public ConfigString(String value){
        this.value = value.trim();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
        if(obj instanceof ConfigString)
            return ((ConfigString)obj).value.equalsIgnoreCase(value);
        return obj.equals(value);
    }

    @Override
    public int length() {
        return value.length();
    }

    @Override
    public char charAt(int index) {
        return value.charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return value.subSequence(start,end);
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public ConfigString getInstanceByString(String content) {
        return new ConfigString(content);
    }

    @Override
    public int compareTo(String o) {
        return value.compareTo(o);
    }
}
