package top.qiguaiaaaa.geocraft.util;

import net.minecraft.init.Blocks;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Loader;
import top.qiguaiaaaa.geocraft.configs.SimulationConfig;
import top.qiguaiaaaa.geocraft.util.mixinapi.FluidSettable;
import top.qiguaiaaaa.geocraft.api.simulation.SimulationMode;

import java.util.ArrayList;
import java.util.List;

public final class MixinUtil {
    public static void linkLiquidWithFluid(){
        if(Blocks.FLOWING_WATER instanceof FluidSettable){
            ((FluidSettable) Blocks.FLOWING_WATER).setCorrespondingFluid(FluidRegistry.WATER);
            ((FluidSettable) Blocks.FLOWING_LAVA).setCorrespondingFluid(FluidRegistry.LAVA);
        }
        if(Blocks.WATER instanceof FluidSettable){
            ((FluidSettable) Blocks.WATER).setCorrespondingFluid(FluidRegistry.WATER);
            ((FluidSettable) Blocks.LAVA).setCorrespondingFluid(FluidRegistry.LAVA);
        }
    }
    public static List<String> getModMixins(){
        SimulationMode mode = SimulationConfig.SIMULATION_MODE.getValue();
        List<String> mixins = new ArrayList<>();
        switch (mode){
            case MORE_REALITY:{
                mixins = getMoreRealityModeMixins(mixins);
                break;
            }
        }

        return mixins;
    }

    public static List<String> getMoreRealityModeMixins(List<String> mixinList){
        if(SimulationConfig.enableSupportForIC2.getValue() && Loader.isModLoaded("ic2"))
            mixinList.add("mixins/ic2/mixins.geocraft_reality.json");
        if(SimulationConfig.enableSupportForIE.getValue() && Loader.isModLoaded("immersiveengineering"))
            mixinList.add("mixins/immersiveengineering/mixins.geocraft_reality.json");
        return mixinList;
    }

}
