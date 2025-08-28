package top.qiguaiaaaa.geocraft.util.mixinapi;

import net.minecraftforge.fluids.Fluid;

/**
 * 该类用于为非IFluidBlock类提供加载自身对应流体的能力
 */
public interface FluidSettable {
    /**
     * 设置相对应的流体
     * @param fluid 对应流体
     */
    void setCorrespondingFluid(Fluid fluid);
}
