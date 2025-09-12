package top.qiguaiaaaa.geocraft.api.property;

import top.qiguaiaaaa.geocraft.api.state.TemperatureState;

import javax.annotation.Nonnull;

/**
 * 温度属性
 * @author QiguaiAAAA
 */
public abstract class TemperatureProperty extends GeographyProperty {
    public static final int BOILED_POINT = 373;
    public static final int ICE_POINT = 273;
    public static final int STANDARD_TEMP = 298;
    public static final int MIN = 3;
    public static final int UNAVAILABLE = -100;

    @Nonnull
    @Override
    public abstract TemperatureState getStateInstance() ;

    /**
     * 将指定温度（单位开尔文）转换为摄氏温度
     * @param temperature 单位为开尔文的温度
     * @return 摄氏温度
     */
    public static double toCelsiusFromKelvin(double temperature){
        return temperature-TemperatureProperty.ICE_POINT;
    }
}
