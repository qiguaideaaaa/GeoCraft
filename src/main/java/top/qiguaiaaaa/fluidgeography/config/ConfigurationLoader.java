package top.qiguaiaaaa.fluidgeography.config;

import top.qiguaiaaaa.fluidgeography.api.configs.item.ConfigItem;

import java.io.File;
import java.util.ArrayList;


public class ConfigurationLoader {
    private static boolean initialised = false;
    private static ExtendedConfiguration config;
    private static final ArrayList<ConfigItem<?>> configItems = new ArrayList<>();

    public static void init(File configFile){
        if(initialised) return;
        config = new ExtendedConfiguration(configFile);
        config.load();
        initialised = true;
    }
    public static void registerConfigItem(ConfigItem<?> item){
        if(item == null) return;
        if(configItems.contains(item)) return;
        configItems.add(item);
    }

    public static void load(){
        for(ConfigItem<?> item:configItems){
            item.load(config);
        }
        config.save();
    }
    public static boolean isInitialised(){
        return initialised;
    }
}
