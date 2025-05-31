package top.qiguaiaaaa.fluidgeography.api.atmosphere.property;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.Chunk;
import top.qiguaiaaaa.fluidgeography.api.FGInfo;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.state.CarbonDioxideState;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.state.IAtmosphereState;

public class AtmosphereCarbonDioxide extends AtmosphereProperty{
    public static final AtmosphereCarbonDioxide CARBON_DIOXIDE = new AtmosphereCarbonDioxide();

    protected AtmosphereCarbonDioxide(){
        setRegistryName(new ResourceLocation(FGInfo.getModId(),"carbon_dioxide"));
    }

    @Override
    public void onAtmosphereFlow(Atmosphere from, Chunk fromChunk, Atmosphere to, Chunk toChunk, EnumFacing direction, Vec3d windSpeed) {

    }

    @Override
    public IAtmosphereState getStateInstance() {
        return new CarbonDioxideState(0);
    }
}
