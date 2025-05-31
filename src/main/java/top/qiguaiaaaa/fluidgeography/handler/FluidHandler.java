package top.qiguaiaaaa.fluidgeography.handler;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import top.qiguaiaaaa.fluidgeography.api.FGFluids;
import top.qiguaiaaaa.fluidgeography.fluid.FluidCarbonDioxide;

public final class FluidHandler {
    public static void initRegisteredFluids(){
        if(FGFluids.CARBON_DIOXIDE == null){
            FGFluids.CARBON_DIOXIDE = getValidFluid(FluidCarbonDioxide.fluidName,new FluidCarbonDioxide());
        }
    }
    public static Fluid getValidFluid(String fluidName,Fluid defaultFluid){
        Fluid fluid = FluidRegistry.getFluid(fluidName);
        if(fluid == null) fluid = defaultFluid;
        return fluid;
    }
}
