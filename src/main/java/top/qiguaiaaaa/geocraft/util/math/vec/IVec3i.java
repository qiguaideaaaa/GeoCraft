package top.qiguaiaaaa.geocraft.util.math.vec;

import javax.annotation.Nonnull;

import static top.qiguaiaaaa.geocraft.util.math.Int10.toInt10;
import static top.qiguaiaaaa.geocraft.util.math.Int21.toInt21;

/**
 * 这里i是整数的意思,不是整形的意思
 * @author QiguaiAAAA
 */
public interface IVec3i {
    int getX();
    int getY();
    int getZ();

    @Nonnull
    IVec3i toImmutable();

    default int toInt() {
        return toInt10(getX())<<20 | toInt10(getY())<<10 | toInt10(getZ());
    }

    default long toLong(){
        return toInt21(getX()) <<42 | toInt21(getY()) <<21 | toInt21(getZ());
    }
}
