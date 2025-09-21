package top.qiguaiaaaa.geocraft.util.math.vec;

import javax.annotation.Nonnull;

import static top.qiguaiaaaa.geocraft.util.math.Int10.toInt10;
import static top.qiguaiaaaa.geocraft.util.math.Int21.toInt21;

/**
 * @author QiguaiAAAA
 */
public class Vec3s implements IVec3i{
    protected short x,y,z;
    public Vec3s(short x, short y, short z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public Vec3s(Vec3s vec){
        this(vec.x, vec.y,vec.z);
    }
    public Vec3s(IVec3i vec){
        this((short) vec.getX(), (short) vec.getY(), (short) vec.getZ());
    }
    public short getSX() {
        return this.x;
    }

    public short getSY() {
        return this.y;
    }

    public short getSZ() {
        return this.z;
    }

    @Override
    public int toInt(){
        return toInt10(x)<<20 | toInt10(y) << 10 | toInt10(z);
    }

    @Override
    public long toLong(){
        return toInt21(x) <<42 | toInt21(y) <<21 | toInt21(z);
    }

    @Override
    public int hashCode() {
        return (this.getSY() + this.getSZ() * 31) * 31 + this.getSX();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Vec3s){
            Vec3s b = (Vec3s) obj;
            return b.getSX() == getSX() && b.getSY() == getSY() && b.getSZ() == getSZ();
        }
        return false;
    }

    @Override
    public String toString() {
        return this.getClass().getName()+"("+ getSX()+","+ getSY()+","+ getSZ()+")";
    }

    //******
    //IVec3i
    //******
    @Override
    public int getX() {
        return getSX();
    }

    @Override
    public int getY() {
        return getSY();
    }

    @Override
    public int getZ() {
        return getSZ();
    }

    @Override
    @Nonnull
    public IVec3i toImmutable() {
        return this;
    }
}
