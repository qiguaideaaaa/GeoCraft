package top.qiguaiaaaa.geocraft.atmosphere.tracker;

import net.minecraft.util.math.BlockPos;
import top.qiguaiaaaa.geocraft.GeoCraft;
import top.qiguaiaaaa.geocraft.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.geocraft.api.atmosphere.layer.Layer;
import top.qiguaiaaaa.geocraft.api.property.FluidProperty;
import top.qiguaiaaaa.geocraft.api.state.FluidState;
import top.qiguaiaaaa.geocraft.api.state.GeographyState;
import top.qiguaiaaaa.geocraft.api.atmosphere.tracker.InformationLoggingTracker;
import top.qiguaiaaaa.geocraft.api.util.io.FileLogger;

public class FluidTracker extends InformationLoggingTracker {
    protected final FluidProperty propertyToTrack;
    protected final  BlockPos pos;
    public FluidTracker(FileLogger logger, FluidProperty property, BlockPos pos, int time) {
        super(logger, time);
        propertyToTrack = property;
        this.pos = pos;
    }

    @Override
    public void notify(Atmosphere atmosphere) {
        Layer layer = atmosphere.getLayer(pos);
        if(layer == null){
            GeoCraft.getLogger().warn("Couldn't track {} at {} , because there doesn't exist an atmosphere layer.",propertyToTrack.getRegistryName(),pos);
            return;
        }
        GeographyState s = layer.getState(propertyToTrack);
        if(s == null){
            GeoCraft.getLogger().warn("Couldn't track {} at {} , because the layer there doesn't have this property.",propertyToTrack.getRegistryName(),pos);
            return;
        }
        if(!(s instanceof FluidState)){
            GeoCraft.getLogger().warn("Couldn't track {} at {} , because this isn't a valid fluid state.",propertyToTrack.getRegistryName(),pos);
            return;
        }
        FluidState state = (FluidState) s;
        String msg = String.format("%d,%d",atmosphere.getAtmosphereWorldInfo().getWorld().getTotalWorldTime(),state.getAmount());
        logger.println(msg);
        GeoCraft.getLogger().info("track {} ({} mB)",propertyToTrack.getRegistryName(),msg);
        nowTime++;
        this.checkLoggingTime(atmosphere);
    }
}
