package top.qiguaiaaaa.fluidgeography.atmosphere.property;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.Chunk;
import top.qiguaiaaaa.fluidgeography.api.FGFluids;
import top.qiguaiaaaa.fluidgeography.api.FGInfo;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.property.GasProperty;
import top.qiguaiaaaa.fluidgeography.atmosphere.state.CarbonDioxideState;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.state.GasState;

public class CarbonDioxide extends GasProperty {
    public static final CarbonDioxide CARBON_DIOXIDE = new CarbonDioxide();

    protected CarbonDioxide(){
        super(FGFluids.CARBON_DIOXIDE,false,true);
        setRegistryName(new ResourceLocation(FGInfo.getModId(),"carbon_dioxide"));
    }

    @Override
    public void onAtmosphereFlow(Atmosphere from, Chunk fromChunk, Atmosphere to, Chunk toChunk, EnumFacing direction, Vec3d windSpeed) {

    }

    @Override
    public GasState getStateInstance() {
        return new CarbonDioxideState(0);
    }
}
