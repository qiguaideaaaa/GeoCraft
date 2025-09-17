package top.qiguaiaaaa.geocraft.util.math.vec;

/**
 * @author QiguaiAAAA
 */
public class Vec3s {
    protected short x,y,z;
    public Vec3s(short x, short y, short z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public Vec3s(Vec3s vec){
        this(vec.x, vec.y,vec.z);
    }
    public short getX() {
        return this.x;
    }

    public short getY() {
        return this.y;
    }

    public short getZ() {
        return this.z;
    }

    @Override
    public int hashCode() {
        return (this.getY() + this.getZ() * 31) * 31 + this.getX();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Vec3s){
            Vec3s b = (Vec3s) obj;
            return b.getX() == getX() && b.getY() == getY() && b.getZ() == getZ();
        }
        return false;
    }

    @Override
    public String toString() {
        return this.getClass().getTypeName()+"("+getX()+","+getY()+","+getZ()+")";
    }
}
