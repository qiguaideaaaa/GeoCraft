package top.qiguaiaaaa.fluidgeography.config;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import top.qiguaiaaaa.fluidgeography.api.configs.transfer.IConfigurableTransfer;
import top.qiguaiaaaa.fluidgeography.api.configs.value.Configurable;
import top.qiguaiaaaa.fluidgeography.api.configs.value.ConfigurableArray;
import top.qiguaiaaaa.fluidgeography.api.configs.value.base.ConfigBoolean;
import top.qiguaiaaaa.fluidgeography.api.configs.value.number.ConfigDouble;
import top.qiguaiaaaa.fluidgeography.api.configs.value.number.ConfigInteger;
import top.qiguaiaaaa.fluidgeography.api.configs.value.number.ConfigNumber;
import top.qiguaiaaaa.fluidgeography.util.BaseUtil;

import java.io.File;

public class ExtendedConfiguration extends Configuration {
    public ExtendedConfiguration(){
        super();
    }
    public ExtendedConfiguration(File file){
        super(file);
    }
    @SuppressWarnings("unchecked")
    public <T extends Configurable> T get(String category, String key, T defaultValue, String comment){
        if(defaultValue instanceof ConfigurableArray){
            ConfigurableArray<?> array = (ConfigurableArray<?>) defaultValue;
            IConfigurableTransfer<?> transfer = array.getTransfer();
            Class<?> typeClass = transfer.getTransferClass();
            String[] defaultArray = array.toStringArray();
            if(typeClass.isAssignableFrom(ConfigInteger.class)){
                int[] intDefaultArray = BaseUtil.toIntArray(defaultArray);
                return (T) array.getInstanceByStringArray(get(category,key,intDefaultArray,comment).getStringList());
            }else if(typeClass.isAssignableFrom(ConfigDouble.class)){
                double[] doubleDefaultArray = BaseUtil.toDoubleArray(defaultArray);
                return (T) array.getInstanceByStringArray(get(category,key,doubleDefaultArray,comment).getStringList());
            }else if(typeClass.isAssignableFrom(ConfigBoolean.class)){
                boolean[] booleanDefaultArray = BaseUtil.toBooleanArray(defaultArray);
                return (T) array.getInstanceByStringArray(get(category,key,booleanDefaultArray,comment).getStringList());
            }
            return (T) array.getInstanceByStringArray(get(category,key,defaultArray,comment).getStringList());
        }
        Property result;
        if(defaultValue instanceof ConfigNumber){
            ConfigNumber<?> number = (ConfigNumber<?>) defaultValue;
            if(number.value instanceof Integer){
                result = get(category,key,number.intValue(),comment);
            }else if(number.value instanceof Double){
                result = get(category,key,number.doubleValue(),comment);
            }else if(number.value instanceof Long){
                result = get(category,key,number.longValue(),comment);
            }
            result = get(category,key,number.toString(),comment);
        }else if(defaultValue instanceof ConfigBoolean){
            ConfigBoolean bool = (ConfigBoolean) defaultValue;
            result = get(category,key,bool.value,comment);
        }else{
            result = get(category,key,defaultValue.toString(),comment);
        }
        return (T) defaultValue.getInstanceByString(result.getString());
    }
}
