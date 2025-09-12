package top.qiguaiaaaa.geocraft.configs.item.collection;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import top.qiguaiaaaa.geocraft.GeoCraft;
import top.qiguaiaaaa.geocraft.api.configs.value.collection.ConfigurableHashSet;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;


public class ConfigIntegerSet extends ConfigSet<Integer>{
    public ConfigIntegerSet(String category, String configKey, ConfigurableHashSet<Integer> defaultValue) {
        this(category, configKey, defaultValue,null);
    }

    public ConfigIntegerSet(String category, String configKey, ConfigurableHashSet<Integer> defaultValue, String comment) {
        this(category, configKey, defaultValue, comment,false);
    }

    public ConfigIntegerSet(String category, String configKey, ConfigurableHashSet<Integer> defaultValue, String comment, boolean isFinal) {
        super(category, configKey, defaultValue, comment,Integer::parseInt, isFinal);
    }

    @Override
    public void load(@Nonnull Configuration config) {
        Property val = config.get(category,key,getDefaultValues(),comment);
        load(val);
    }

    protected int[] getDefaultValues(){
        int[] ints = new int[defaultValue.size()];
        int i=0;
        for(Integer integer:defaultValue){
            ints[i++]=integer;
        }
        return ints;
    }
}
