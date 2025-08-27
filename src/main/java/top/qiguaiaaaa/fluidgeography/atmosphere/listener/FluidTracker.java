package top.qiguaiaaaa.fluidgeography.atmosphere.listener;

import net.minecraft.util.math.BlockPos;
import top.qiguaiaaaa.fluidgeography.api.FGInfo;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.layer.Layer;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.listener.InformationLoggingTracker;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.property.FluidProperty;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.state.FluidState;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.state.GeographyState;
import top.qiguaiaaaa.fluidgeography.api.util.io.FileLogger;

public class FluidTracker extends InformationLoggingTracker {
    protected final FluidProperty propertyToTrack;
    protected final  BlockPos pos;
    public FluidTracker(FileLogger logger, FluidProperty property, BlockPos pos, int time) {
        super(logger, time);
        propertyToTrack = property;
        this.pos = pos;
    }

    @Override
    public void notifyListener(Atmosphere atmosphere) {
        Layer layer = atmosphere.getLayer(pos);
        if(layer == null){
            FGInfo.getLogger().warn("Couldn't track {} at {} , because there doesn't exist an atmosphere layer.",propertyToTrack.getRegistryName(),pos);
            return;
        }
        GeographyState s = layer.getState(propertyToTrack);
        if(s == null){
            FGInfo.getLogger().warn("Couldn't track {} at {} , because the layer there doesn't have this property.",propertyToTrack.getRegistryName(),pos);
            return;
        }
        if(!(s instanceof FluidState)){
            FGInfo.getLogger().warn("Couldn't track {} at {} , because this isn't a valid fluid state.",propertyToTrack.getRegistryName(),pos);
            return;
        }
        FluidState state = (FluidState) s;
        String msg = String.format("%d,%d",atmosphere.getAtmosphereWorldInfo().getWorld().getTotalWorldTime(),state.getAmount());
        logger.println(msg);
        FGInfo.getLogger().info("track {} ({} mB)",propertyToTrack.getRegistryName(),msg);
        nowTime++;
        this.checkLoggingTime(atmosphere);
    }
}
