package top.qiguaiaaaa.geocraft.util.math.vec;

/**
 * @author QiguaiAAAA
 */
public class Vec3b {
    protected byte x,y,z;
    public Vec3b(byte x,byte y,byte z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3b(Vec3b vec){
        this(vec.x, vec.y, vec.z);
    }

    public byte getX() {
        return x;
    }

    public byte getY() {
        return y;
    }

    public byte getZ() {
        return z;
    }

    @Override
    public int hashCode() {
        return (x<<16) | (y<<8) |z;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Vec3b){
            Vec3b b = (Vec3b) obj;
            return b.getX() == getX() && b.getY() == getY() && b.getZ() == getZ();
        }
        return false;
    }
}
