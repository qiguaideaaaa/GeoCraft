package top.qiguaiaaaa.geocraft.atmosphere.property;

import net.minecraft.util.ResourceLocation;
import top.qiguaiaaaa.geocraft.GeoCraft;
import top.qiguaiaaaa.geocraft.api.GEOFluids;
import top.qiguaiaaaa.geocraft.api.GEOInfo;
import top.qiguaiaaaa.geocraft.api.atmosphere.property.FluidProperty;
import top.qiguaiaaaa.geocraft.atmosphere.state.CarbonDioxideState;
import top.qiguaiaaaa.geocraft.api.atmosphere.state.FluidState;

public class CarbonDioxide extends FluidProperty {
    public static final CarbonDioxide CARBON_DIOXIDE = new CarbonDioxide();

    protected CarbonDioxide(){
        super(GEOFluids.CARBON_DIOXIDE,false,true);
        setRegistryName(new ResourceLocation(GeoCraft.MODID,"carbon_dioxide"));
    }

    @Override
    public FluidState getStateInstance() {
        return new CarbonDioxideState(0);
    }
}
