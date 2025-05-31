package top.qiguaiaaaa.fluidgeography.api.configs.value;

public interface Configurable {
    default boolean isArray(){
        return false;
    }
    String toString();
    Configurable getInstanceByString(String content);
}
