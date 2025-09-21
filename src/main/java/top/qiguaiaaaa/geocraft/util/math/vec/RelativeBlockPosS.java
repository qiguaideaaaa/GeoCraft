package top.qiguaiaaaa.geocraft.util.math.vec;

import net.minecraft.util.math.Vec3i;

import javax.annotation.Nonnull;

/**
 * @author QiguaiAAAA
 */
public class RelativeBlockPosS extends Vec3s{
    public RelativeBlockPosS(short rx, short ry, short rz) {
        super(rx, ry, rz);
    }
    public RelativeBlockPosS(int x,int y,int z,int tx,int ty,int tz){
        this((short) (tx-x), (short) (ty-y), (short) (tz-z));
    }
    public RelativeBlockPosS(@Nonnull Vec3i origin, @Nonnull Vec3i target){
        this(origin.getX(),origin.getY(),origin.getZ(),target.getX(),target.getY(),target.getZ());
    }

    public RelativeBlockPosS(@Nonnull IVec3i origin, @Nonnull IVec3i target){
        this(origin.getX(),origin.getY(),origin.getZ(),target.getX(),target.getY(),target.getZ());
    }

    public RelativeBlockPosS(IVec3i pos){
        super(pos);
    }

    public static class Mutable extends RelativeBlockPosS{
        public static final Mutable MUTABLE = new Mutable();
        public Mutable(){
            super((short) 0, (short) 0, (short) 0);
        }

        public Mutable(short ax, short ay, short az) {
            super(ax, ay, az);
        }

        public Mutable(int x, int y, int z, int tx, int ty, int tz) {
            super(x, y, z, tx, ty, tz);
        }
        public Mutable(Vec3i origin,Vec3i target) {
            super(origin,target);
        }
        public Mutable(IVec3i origin,IVec3i target) {
            super(origin,target);
        }

        public Mutable setPos(short x,short y,short z){
            this.x = x;
            this.y = y;
            this.z = z;
            return this;
        }

        public Mutable setPos(int cx, int cy, int cz, int ax, int ay, int az){
            return this.setPos((short) (ax-cx),(short) (ay-cy),(short) (az-cz));
        }

        public Mutable setPos(@Nonnull IVec3i vec3i){
            return this.setPos((short) vec3i.getX(),(short) vec3i.getY(),(short) vec3i.getZ());
        }

        public Mutable setPos(@Nonnull IVec3i center, @Nonnull IVec3i to){
            return this.setPos(center.getX(),center.getY(),center.getZ(),to.getX(),to.getY(),to.getZ());
        }

        public Mutable setPos(@Nonnull Vec3i center, @Nonnull Vec3i to){
            return this.setPos(center.getX(),center.getY(),center.getZ(),to.getX(),to.getY(),to.getZ());
        }

        @Nonnull
        @Override
        public RelativeBlockPosS toImmutable(){
            return new RelativeBlockPosS(this);
        }
    }

    /**
     * 指定中心点的相对可变坐标
     */
    public static class CenteredMutable extends Mutable implements ICenteredMutableRelativeBlockPos{
        protected int cx,cy,cz; //中心点坐标

        /**
         * 创建一个中心点为(0,0,0)的相对坐标(~0,~0,~0)
         */
        public CenteredMutable(){
            super();
        }

        /**
         * 创建一个中心点为(0,0,0),为(~rx,~ry,~rz)的相对坐标
         * @param rx 相对中心点的X坐标
         * @param ry 相对中心点的Y坐标
         * @param rz 相对中心点的Z坐标
         */
        public CenteredMutable(short rx, short ry, short rz) {
            super(rx, ry, rz);
        }

        /**
         * 创建一个中心点为(x,y,z),目标点为(tx,ty,tz)的相对坐标(~(tx-x),~(ty-y),~(tz-z))
         * @param x 中心点X坐标
         * @param y 中心点Y坐标
         * @param z 中心点Z坐标
         * @param tx 目标点X坐标
         * @param ty 目标点Y坐标
         * @param tz 目标点Z坐标
         */
        public CenteredMutable(int x, int y, int z, int tx, int ty, int tz) {
            super(x, y, z, tx, ty, tz);
            setCenterPos(x,y,z);
        }

        /**
         * 创建一个相对原点为origin，绝对坐标为target的相对坐标
         * @param origin 原点坐标
         * @param target 绝对坐标
         */
        public CenteredMutable(Vec3i origin,Vec3i target){
            super(origin,target);
        }

        public CenteredMutable(IVec3i origin,IVec3i target){
            super(origin,target);
        }

        /**
         * 设置中心点坐标
         * @param x 中心点X坐标
         * @param y 中心点Y坐标
         * @param z 中心点Z坐标
         */
        @Override
        public CenteredMutable setCenterPos(int x,int y,int z){
            this.cx = x;
            this.cy = y;
            this.cz = z;
            return this;
        }

        /**
         * 设置表示的绝对坐标
         * @param x 绝对坐标X
         * @param y 绝对坐标Y
         * @param z 绝对坐标Z
         */
        public CenteredMutable setAbsolutePos(int x,int y,int z){
            this.setPos((short) (x-cx),(short) (y-cy),(short) (z-cz));
            return this;
        }
    }
}
