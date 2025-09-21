package top.qiguaiaaaa.geocraft.util.math.vec;

import net.minecraft.util.math.Vec3i;

/**
 * @author QiguaiAAAA
 */
public final class BlockPosHelper {
    public static IVec3i getRelativePos_BS(Vec3i origin, Vec3i to){
        final int x = to.getX() - origin.getX(),
                y = to.getY()- origin.getY(),
                z = to.getZ()-origin.getZ();
        if(isRangeBeyondByte(x) | isRangeBeyondByte(y) | isRangeBeyondByte(z))
            return RelativeBlockPosS.Mutable.MUTABLE.setPos(origin,to);
        return RelativeBlockPosB.Mutable.MUTABLE.setPos(origin,to);
    }

    public static IVec3i getRelativePos_BSI(Vec3i origin, Vec3i to){
        final int x = to.getX() - origin.getX(),
                y = to.getY()- origin.getY(),
                z = to.getZ()-origin.getZ();
        if(isRangeBeyondShort(x) | isRangeBeyondShort(y) | isRangeBeyondShort(z))
            return RelativeBlockPosI.Mutable.MUTABLE.setPos(origin,to);
        if(isRangeBeyondByte(x) | isRangeBeyondByte(y) | isRangeBeyondByte(z))
            return RelativeBlockPosS.Mutable.MUTABLE.setPos(origin,to);
        return RelativeBlockPosB.Mutable.MUTABLE.setPos(origin,to);
    }

    public static boolean isRangeBeyondByte(int i){
        return i > Byte.MAX_VALUE | i < Byte.MIN_VALUE;
    }

    public static boolean isRangeBeyondShort(int i){
        return i > Short.MAX_VALUE | i < Short.MIN_VALUE;
    }
}
