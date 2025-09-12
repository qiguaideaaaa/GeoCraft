package top.qiguaiaaaa.geocraft.api.util.math;

/**
 * 角度包装
 */
public class Degree{
    public static final Degree ZERO = new Degree(0);
    public static final Degree RIGHT_ANGLE = new Degree(90);
    protected final double degree;     //角度制表示

    /**
     * @param degree 角度制角度
     */
    public Degree(double degree){
        this(degree,false);
    }
    /**
     * @param degree 角度
     * @param isRadian 是否是弧度制
     */
    public Degree(double degree, boolean isRadian){
        this.degree = isRadian?toDegree(degree):degree;
    }

    public double getDegree() {
        return degree;
    }
    public double getRadian(){
        return toRadian(degree);
    }


    public static double toRadian(double degree){
        return degree/180*Math.PI;
    }
    public static double toDegree(double radian){
        return radian*180/Math.PI;
    }
}
