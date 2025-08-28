package top.qiguaiaaaa.geocraft.api.configs.item;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

/**
 * 配置项目
 * @param <ValueType> 配置的值类型,需要支持{@link Object#toString()}以写入配置文件
 */
public abstract class ConfigItem<ValueType> {
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
        this.value = newValue;
    }
    public boolean isFinal(){
        return this.isFinal;
    }
    public boolean hasComment(){
        return comment != null;
    }

    /**
     * 提供指定的配置文件,以加载当前配置项目
     * @param config 指定的配置文件
     */
    public void load(Configuration config){
        Property val = config.get(category,key,defaultValue.toString(),comment);
        load(val);
    }

    /**
     * 在这里实现自定义的读取逻辑
     * @param property 属性配置
     */
    protected abstract void load(Property property);
}
