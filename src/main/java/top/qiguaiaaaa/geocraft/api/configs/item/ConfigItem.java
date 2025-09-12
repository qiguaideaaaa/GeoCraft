package top.qiguaiaaaa.geocraft.api.configs.item;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 配置项目
 * @param <ValueType> 配置的值类型,需要支持{@link Object#toString()}以写入配置文件
 */
public abstract class ConfigItem<ValueType> {
    /**
     * @see #ConfigItem(String, String, Object, String, boolean) 
     */
    public ConfigItem(@Nonnull String category,@Nonnull String configKey,@Nonnull ValueType defaultValue){
        this(category,configKey,defaultValue,null,false);
    }

    /**
     * @see #ConfigItem(String, String, Object,String,boolean) 
     */
    public ConfigItem(String category,String configKey,ValueType defaultValue,String comment){
        this(category,configKey,defaultValue,comment,false);
    }

    /**
     * 创建一个配置项
     * @param category 配置所在目录
     * @param configKey 配置的key
     * @param defaultValue 配置的默认值，不应为null，因为会调用{@link Object#toString()}
     * @param comment 配置的注释
     * @param isFinal 配置是否在初始化后不可更改
     */
    public ConfigItem(@Nonnull String category, @Nonnull String configKey, @Nonnull ValueType defaultValue, @Nullable String comment, boolean isFinal){
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
    protected final boolean isFinal; //配置初始化后是否不可更改
    protected ValueType value;

    @Nonnull
    public String getCategory() {
        return category;
    }

    @Nonnull
    public ValueType getValue(){
        return value;
    }

    @Nonnull
    public ValueType getDefaultValue(){
        return defaultValue;
    }

    @Nullable
    public String getComment() {
        return comment;
    }

    @Nonnull
    public String getKey(){
        return key;
    }

    /**
     * 获取该配置项的配置路径
     * @return 配置路径，例如exampleCategory.exampleItem
     */
    @Nonnull
    public String getPath(){
        return category+ Configuration.CATEGORY_SPLITTER+key;
    }

    /**
     * 更新配置项的值
     * @param newValue 新值，注意不能为null
     */
    public void setValue(@Nonnull ValueType newValue){
        if(isFinal) return;
        this.value = newValue;
    }

    /**
     * 该配置项是否不可更新
     * @return 若不能更新，则返回true
     */
    public boolean isFinal(){
        return this.isFinal;
    }

    /**
     * 该配置项是否拥有注释
     * @return 若有，则返回true
     */
    public boolean hasComment(){
        return comment != null;
    }

    /**
     * 提供指定的配置文件,以加载当前配置项目
     * @param config 指定的配置文件
     */
    public void load(@Nonnull Configuration config){
        Property val = config.get(category,key,defaultValue.toString(),comment);
        load(val);
    }

    /**
     * 通过{@link Property}的内容来初始化配置项
     * @param property 属性配置
     */
    protected abstract void load(@Nonnull Property property);
}
