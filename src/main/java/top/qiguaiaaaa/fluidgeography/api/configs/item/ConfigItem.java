package top.qiguaiaaaa.fluidgeography.api.configs.item;

import net.minecraftforge.common.config.Configuration;
import top.qiguaiaaaa.fluidgeography.config.ExtendedConfiguration;
import top.qiguaiaaaa.fluidgeography.api.configs.value.Configurable;

public class ConfigItem<ValueType extends Configurable> {
    public ConfigItem(String category,String configKey,ValueType defaultValue){
        this(category,configKey,defaultValue,null,false);
    }
    public ConfigItem(String category,String configKey,ValueType defaultValue,String comment){
        this(category,configKey,defaultValue,comment,false);
    }
    public ConfigItem(String category,String configKey,ValueType defaultValue,String comment,boolean isFinal){
        this.category = category;
        this.key = configKey;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
        this.isFinal = isFinal;
        this.comment = comment;
    }
    protected final String category;
    protected final String key;
    protected final ValueType defaultValue;
    protected final String comment;
    protected final boolean isFinal;
    protected ValueType value;

    public String getCategory() {
        return category;
    }

    public ValueType getValue(){
        return value;
    }
    public ValueType getDefaultValue(){
        return defaultValue;
    }
    public String getComment() {
        return comment;
    }

    public String getKey(){
        return key;
    }
    public String getPath(){
        return category+ Configuration.CATEGORY_SPLITTER+key;
    }
    public void setValue(ValueType newValue){
        if(isFinal) return;
        setValue_Inner(newValue);
    }
    public boolean isFinal(){
        return this.isFinal;
    }
    public boolean hasComment(){
        return comment != null;
    }
    public ValueType load(ExtendedConfiguration config){
        setValue_Inner(config.get(category,key,defaultValue,comment));
        return getValue();
    }
    protected void setValue_Inner(ValueType newValue){
        this.value = newValue;
    }
}
