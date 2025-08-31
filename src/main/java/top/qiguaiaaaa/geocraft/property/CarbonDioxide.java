package top.qiguaiaaaa.geocraft.property;

import net.minecraft.util.ResourceLocation;
import top.qiguaiaaaa.geocraft.GeoCraft;
import top.qiguaiaaaa.geocraft.api.GeoCraftFluids;
import top.qiguaiaaaa.geocraft.api.property.FluidProperty;
import top.qiguaiaaaa.geocraft.api.state.FluidState;
import top.qiguaiaaaa.geocraft.state.CarbonDioxideState;

public class CarbonDioxide extends FluidProperty {
    public static final CarbonDioxide CARBON_DIOXIDE = new CarbonDioxide();

    protected CarbonDioxide(){
        super(GeoCraftFluids.CARBON_DIOXIDE,false,true);
        setRegistryName(new ResourceLocation(GeoCraft.MODID,"carbon_dioxide"));
    }

    @Override
    public FluidState getStateInstance() {
        return new CarbonDioxideState(0);
    }
}
