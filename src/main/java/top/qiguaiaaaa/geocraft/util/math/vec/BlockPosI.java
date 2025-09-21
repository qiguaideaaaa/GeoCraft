package top.qiguaiaaaa.geocraft.util.math.vec;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

import javax.annotation.Nonnull;

/**
 * @author QiguaiAAAA
 */
public class BlockPosI extends Vec3i implements IVec3i {
    public static final BlockPosI ORIGIN = new BlockPosI(0,0,0);
    public BlockPosI(int xIn, int yIn, int zIn) {
        super(xIn, yIn, zIn);
    }

    public BlockPosI(double xIn, double yIn, double zIn) {
        super(xIn, yIn, zIn);
    }

    public BlockPosI(@Nonnull Entity source){
        this(source.posX,source.posY,source.posZ);
    }

    public BlockPosI(@Nonnull Vec3d vec){
        this(vec.x, vec.y,vec.z);
    }

    public BlockPosI(@Nonnull Vec3i vec){
        this(vec.getX(),vec.getY(),vec.getZ());
    }

    public BlockPosI(@Nonnull IVec3i vec){
        this(vec.getX(),vec.getY(),vec.getZ());
    }

    @Nonnull
    @Override
    public IVec3i toImmutable() {
        return this;
    }
}
