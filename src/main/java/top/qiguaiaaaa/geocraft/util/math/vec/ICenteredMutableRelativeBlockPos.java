package top.qiguaiaaaa.geocraft.util.math.vec;

import net.minecraft.util.math.Vec3i;

/**
 * @author QiguaiAAAA
 */
public interface ICenteredMutableRelativeBlockPos extends IVec3i{
    /**
     * 设置中心点坐标
     * @param x 中心点X坐标
     * @param y 中心点Y坐标
     * @param z 中心点Z坐标
     */
    ICenteredMutableRelativeBlockPos setCenterPos(int x, int y, int z);

    /**
     * 设置中心点坐标
     * @param pos 中心点坐标
     */
    default ICenteredMutableRelativeBlockPos setCenterPos(Vec3i pos){
        return this.setCenterPos(pos.getX(),pos.getY(),pos.getZ());
    }

    default ICenteredMutableRelativeBlockPos setCenterPos(IVec3i pos){
        return this.setCenterPos(pos.getX(),pos.getY(),pos.getZ());
    }

    /**
     * 设置表示的绝对坐标
     * @param x 绝对坐标X
     * @param y 绝对坐标Y
     * @param z 绝对坐标Z
     */
    ICenteredMutableRelativeBlockPos setAbsolutePos(int x, int y, int z);

    default ICenteredMutableRelativeBlockPos setAbsolutePos(Vec3i pos){
        return this.setAbsolutePos(pos.getX(),pos.getY(),pos.getZ());
    }

    default ICenteredMutableRelativeBlockPos setAbsolutePos(IVec3i pos){
        return this.setAbsolutePos(pos.getX(),pos.getY(),pos.getZ());
    }
}
