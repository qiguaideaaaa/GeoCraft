package top.qiguaiaaaa.fluidgeography.atmosphere.property;

import net.minecraft.util.ResourceLocation;
import top.qiguaiaaaa.fluidgeography.api.FGFluids;
import top.qiguaiaaaa.fluidgeography.api.FGInfo;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.property.FluidProperty;
import top.qiguaiaaaa.fluidgeography.atmosphere.state.CarbonDioxideState;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.state.FluidState;

public class CarbonDioxide extends FluidProperty {
    public static final CarbonDioxide CARBON_DIOXIDE = new CarbonDioxide();

    protected CarbonDioxide(){
        super(FGFluids.CARBON_DIOXIDE,false,true);
        setRegistryName(new ResourceLocation(FGInfo.getModId(),"carbon_dioxide"));
    }

    @Override
    public FluidState getStateInstance() {
        return new CarbonDioxideState(0);
    }
}
