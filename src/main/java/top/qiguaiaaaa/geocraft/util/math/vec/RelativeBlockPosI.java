package top.qiguaiaaaa.geocraft.util.math.vec;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

import javax.annotation.Nonnull;

/**
 * @author QiguaiAAAA
 */
public class RelativeBlockPosI extends BlockPosI{
    public RelativeBlockPosI(int xIn, int yIn, int zIn) {
        super(xIn, yIn, zIn);
    }

    public RelativeBlockPosI(double xIn, double yIn, double zIn) {
        super(xIn, yIn, zIn);
    }

    public RelativeBlockPosI(int x,int y,int z,int tx,int ty,int tz){
        this(tx-x,ty-y, tz-z);
    }

    public RelativeBlockPosI(double x, double y, double z,double tx,double ty,double tz) {
        super(tx-x,ty-y, tz-z);
    }

    public RelativeBlockPosI(Entity source) {
        super(source);
    }

    public RelativeBlockPosI(Vec3d vec) {
        super(vec);
    }

    public RelativeBlockPosI(Vec3i vec) {
        super(vec);
    }

    public RelativeBlockPosI(IVec3i vec) {
        super(vec);
    }

    public RelativeBlockPosI(@Nonnull Vec3i origin, @Nonnull Vec3i target){
        this(origin.getX(),origin.getY(),origin.getZ(),target.getX(),target.getY(),target.getZ());
    }

    public RelativeBlockPosI(@Nonnull IVec3i origin, @Nonnull IVec3i target){
        this(origin.getX(),origin.getY(),origin.getZ(),target.getX(),target.getY(),target.getZ());
    }

    public RelativeBlockPosI(@Nonnull Entity origin, @Nonnull Entity to) {
        this(origin.posX,origin.posY,origin.posZ,to.posX,to.posY,to.posZ);
    }

    public RelativeBlockPosI(@Nonnull Vec3d origin, @Nonnull Vec3d to) {
        this(origin.x,origin.y,origin.z,to.x,to.y,to.z);
    }

    public static class Mutable implements IVec3i{
        public static final Mutable MUTABLE = new Mutable();

        protected int x,y,z;

        public Mutable(){
            this(0,0,0);
        }

        @Override
        public int hashCode() {
            return (this.getY() + this.getZ() * 31) * 31 + this.getX();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            } else if (!(obj instanceof IVec3i)) {
                return false;
            }
            IVec3i vec3i = (IVec3i) obj;

            if (this.getX() != vec3i.getX()) {
                return false;
            } else if (this.getY() != vec3i.getY()) {
                return false;
            } else {
                return this.getZ() == vec3i.getZ();
            }
        }

        public Mutable(int xIn, int yIn, int zIn) {
            x = xIn;
            y = yIn;
            z = zIn;
        }

        public Mutable(double xIn, double yIn, double zIn) {
            x = (int) xIn;
            y = (int) yIn;
            z = (int) zIn;
        }

        public Mutable(int x, int y, int z, int tx, int ty, int tz) {
            this(tx-x,ty-y, tz-z);
        }

        public Mutable(double x, double y, double z, double tx, double ty, double tz) {
            this(tx-x,ty-y, tz-z);
        }

        public Mutable(@Nonnull Entity source) {
            this(source.posX,source.posY,source.posZ);
        }

        public Mutable(@Nonnull Vec3d vec) {
            this(vec.x,vec.y,vec.z);
        }

        public Mutable(@Nonnull Vec3i vec) {
            this(vec.getX(),vec.getY(),vec.getZ());
        }

        public Mutable(@Nonnull IVec3i vec) {
            this(vec.getX(),vec.getY(),vec.getZ());
        }

        public Mutable(@Nonnull Vec3i origin, @Nonnull Vec3i target) {
            this(origin.getX(),origin.getY(),origin.getZ(),target.getX(),target.getY(),target.getZ());
        }

        public Mutable(@Nonnull IVec3i origin, @Nonnull IVec3i target) {
            this(origin.getX(),origin.getY(),origin.getZ(),target.getX(),target.getY(),target.getZ());
        }

        public Mutable(@Nonnull Entity origin, @Nonnull Entity to) {
            this(origin.posX,origin.posY,origin.posZ,to.posX,to.posY,to.posZ);
        }

        public Mutable(@Nonnull Vec3d origin, @Nonnull Vec3d to) {
            this(origin.x,origin.y,origin.z,to.x,to.y,to.z);
        }

        public Mutable setPos(int x,int y, int z){
            this.x = x;
            this.y = y;
            this.z = z;
            return this;
        }

        public Mutable setPos(double x,double y,double z){
            x = (int) x;
            y = (int) y;
            z = (int) z;
            return this;
        }

        public Mutable setPos(int cx, int cy, int cz, int ax, int ay, int az){
            return this.setPos(ax-cx,ay-cy,az-cz);
        }

        public Mutable setPos(double cx,double cy, double cz, double ax,double ay, double az){
            return this.setPos(ax-cx,ay-cy,az-cz);
        }

        public Mutable setPos(@Nonnull IVec3i vec3i){
            return this.setPos(vec3i.getX(),vec3i.getY(), vec3i.getZ());
        }

        public Mutable setPos(@Nonnull Vec3d vec3d){
            return this.setPos(vec3d.x,vec3d.y,vec3d.z);
        }

        public Mutable setPos(@Nonnull Vec3i vec3i){
            return this.setPos(vec3i.getX(),vec3i.getY(), vec3i.getZ());
        }

        public Mutable setPos(@Nonnull Entity entity){
            return this.setPos(entity.posX,entity.posY,entity.posZ);
        }

        public Mutable setPos(@Nonnull IVec3i center, @Nonnull IVec3i to){
            return this.setPos(center.getX(),center.getY(),center.getZ(),to.getX(),to.getY(),to.getZ());
        }

        public Mutable setPos(@Nonnull Vec3i center, @Nonnull Vec3i to){
            return this.setPos(center.getX(),center.getY(),center.getZ(),to.getX(),to.getY(),to.getZ());
        }

        @Override
        public int getX() {
            return x;
        }

        @Override
        public int getY() {
            return y;
        }

        @Override
        public int getZ() {
            return z;
        }

        @Nonnull
        @Override
        public IVec3i toImmutable() {
            return new RelativeBlockPosI(this);
        }
    }
}
