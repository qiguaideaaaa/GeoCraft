package top.qiguaiaaaa.fluidgeography.api.configs.value.base;

public class ConfigNameWrapper extends ConfigString {
    public ConfigNameWrapper(String name) {
        super(name.trim());
    }

    @Override
    public ConfigNameWrapper getInstanceByString(String content) {
        if(content == null || content.trim().isEmpty()) return null;
        return new ConfigNameWrapper(content);
    }
}
