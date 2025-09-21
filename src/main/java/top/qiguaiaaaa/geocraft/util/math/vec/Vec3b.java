package top.qiguaiaaaa.geocraft.util.math.vec;

import javax.annotation.Nonnull;

import static top.qiguaiaaaa.geocraft.util.math.Int10.toInt10;

/**
 * @author QiguaiAAAA
 */
public class Vec3b implements IVec3i{
    protected byte x,y,z;
    public Vec3b(byte x,byte y,byte z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vec3b(Vec3b vec){
        this(vec.x, vec.y, vec.z);
    }

    public Vec3b(IVec3i vec){
        this((byte) vec.getX(), (byte) vec.getY(), (byte) vec.getZ());
    }

    public byte getBX() {
        return x;
    }

    public byte getBY() {
        return y;
    }

    public byte getBZ() {
        return z;
    }

    @Override
    public int toInt(){
        return toInt10(x)<<20 | toInt10(y)<<10 | toInt10(z);
    }

    @Override
    public int hashCode() {
        return (x<<16) | (y<<8) |z;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Vec3b) {
            Vec3b b = (Vec3b) obj;
            return b.getBX() == getBX() && b.getBY() == getBY() && b.getBZ() == getBZ();
        }
        return false;
    }

    //******
    //IVec3i
    //******

    @Override
    public int getX() {
        return getBX();
    }

    @Override
    public int getY() {
        return getBY();
    }

    @Override
    public int getZ() {
        return getBZ();
    }

    @Nonnull
    @Override
    public IVec3i toImmutable() {
        return this;
    }
}
