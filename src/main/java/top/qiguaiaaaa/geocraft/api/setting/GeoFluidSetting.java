package top.qiguaiaaaa.geocraft.api.setting;

import net.minecraft.block.BlockLiquid;
import net.minecraftforge.fluids.Fluid;
import top.qiguaiaaaa.geocraft.api.util.FluidUtil;

import java.util.HashSet;

/**
 * 查询天圆地方关于流体的配置
 * @author QiguaiAAAA
 */
public class GeoFluidSetting {
    private static final HashSet<String> FLUIDS_NOT_TO_BE_PHYSICAL = new HashSet<>();
    private static final HashSet<String> FLUIDS_BUCKET_TO_BE_VANILLA = new HashSet<>();

    /**
     * 设置指定流体是否需要被物理化
     * @param fluidName 流体名
     * @param physical 是否需要物理化
     */
    public static void setFluidToBePhysical(String fluidName,boolean physical){
        if(physical) FLUIDS_NOT_TO_BE_PHYSICAL.remove(fluidName);
        else FLUIDS_NOT_TO_BE_PHYSICAL.add(fluidName);
    }

    public static void setFluidToUseVanillaBucketMode(String fluidName,boolean vanilla){
        if(vanilla) FLUIDS_BUCKET_TO_BE_VANILLA.add(fluidName);
        else FLUIDS_BUCKET_TO_BE_VANILLA.remove(fluidName);
    }

    /**
     * 指定流体是否需要物理化
     * @param fluid 流体
     * @return 若需要,则返回true
     */
    public static boolean isFluidToBePhysical(Fluid fluid){
        if(fluid == null) return false;
        return !FLUIDS_NOT_TO_BE_PHYSICAL.contains(fluid.getName());
    }

    public static boolean isFluidToBePhysical(BlockLiquid fluid){
        return isFluidToBePhysical(FluidUtil.getFluid(fluid));
    }

    public static boolean isFluidToUseVanillaBucketMode(Fluid fluid){
        if(fluid == null) return true;
        return FLUIDS_BUCKET_TO_BE_VANILLA.contains(fluid.getName());
    }
}
