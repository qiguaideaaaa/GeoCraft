package top.qiguaiaaaa.geocraft.handler;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import top.qiguaiaaaa.geocraft.api.GeoCraftFluids;
import top.qiguaiaaaa.geocraft.fluid.FluidCarbonDioxide;

public final class FluidHandler {
    public static void initRegisteredFluids(){
        if(GeoCraftFluids.CARBON_DIOXIDE == null){
            GeoCraftFluids.CARBON_DIOXIDE = getValidFluid(FluidCarbonDioxide.fluidName,new FluidCarbonDioxide());
        }
    }
    public static Fluid getValidFluid(String fluidName,Fluid defaultFluid){
        Fluid fluid = FluidRegistry.getFluid(fluidName);
        if(fluid == null) fluid = defaultFluid;
        return fluid;
    }
}
