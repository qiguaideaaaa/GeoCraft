package top.qiguaiaaaa.geocraft.util.math.vec;

import net.minecraft.util.math.Vec3i;

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
    public RelativeBlockPosS(Vec3i origin,Vec3i target){
        this(origin.getX(),origin.getY(),origin.getZ(),target.getX(),target.getY(),target.getZ());
    }
    public RelativeBlockPosS(Vec3s pos){
        super(pos);
    }

    public RelativeBlockPosS toImmutable(){
        return new RelativeBlockPosS(this);
    }

    public static class Mutable extends RelativeBlockPosS{
        public Mutable(){
            super((short) 0, (short) 0, (short) 0);
        }

        public Mutable(short dx, short dy, short dz) {
            super(dx, dy, dz);
        }

        public Mutable(int x, int y, int z, int tx, int ty, int tz) {
            super(x, y, z, tx, ty, tz);
        }
        public Mutable(Vec3i origin,Vec3i target) {
            super(origin,target);
        }

        public void setPos(short x,short y,short z){
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    /**
     * 指定中心点的相对可变坐标
     */
    public static class CenteredMutable extends Mutable{
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

        /**
         * 设置中心点坐标
         * @param x 中心点X坐标
         * @param y 中心点Y坐标
         * @param z 中心点Z坐标
         */
        public void setCenterPos(int x,int y,int z){
            this.cx = x;
            this.cy = y;
            this.cz = z;
        }

        /**
         * 设置中心点坐标
         * @param pos 中心点坐标
         */
        public void setCenterPos(Vec3i pos){
            this.setCenterPos(pos.getX(),pos.getY(),pos.getZ());
        }

        /**
         * 设置表示的绝对坐标
         * @param x 绝对坐标X
         * @param y 绝对坐标Y
         * @param z 绝对坐标Z
         */
        public void setAbsolutePos(int x,int y,int z){
            this.setPos((short) (x-cx),(short) (y-cy),(short) (z-cz));
        }

        public void setAbsolutePos(Vec3i pos){
            this.setAbsolutePos(pos.getX(),pos.getY(),pos.getZ());
        }
    }
}
