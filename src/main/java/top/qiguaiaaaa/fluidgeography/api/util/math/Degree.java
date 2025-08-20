package top.qiguaiaaaa.fluidgeography.api.util.math;

public class Degree{
    //角度制
    protected double degree;

    /**
     * @param degree 角度制角度
     */
    public Degree(double degree){
        this(degree,false);
    }

    public void setDegree(double degree) {
        this.degree = degree;
    }
    public void setRadian(double radian){
        this.degree = toDegree(radian);
    }

    public double getDegree() {
        return degree;
    }
    public double getRadian(){
        return toRadian(degree);
    }

    /**
     * @param degree 角度
     * @param isRadian 是否是弧度制
     */
    public Degree(double degree, boolean isRadian){
        this.degree = isRadian?toDegree(degree):degree;
    }
    public static double toRadian(double degree){
        return degree/180*Math.PI;
    }
    public static double toDegree(double radian){
        return radian*180/Math.PI;
    }
}
