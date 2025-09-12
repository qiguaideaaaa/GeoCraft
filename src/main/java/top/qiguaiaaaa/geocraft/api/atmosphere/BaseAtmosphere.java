package top.qiguaiaaaa.geocraft.api.atmosphere;

import net.minecraft.world.chunk.Chunk;
import top.qiguaiaaaa.geocraft.api.atmosphere.layer.Layer;
import top.qiguaiaaaa.geocraft.api.atmosphere.tracker.IAtmosphereTracker;
import top.qiguaiaaaa.geocraft.api.property.AtmosphereProperty;
import top.qiguaiaaaa.geocraft.geography.property.GeographyPropertyManager;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class BaseAtmosphere implements Atmosphere{
    protected AtmosphereWorldInfo worldInfo = null;
    protected long tickTimes = 0;
    /**
     * 从下往上的层状列表
     */
    protected final List<Layer> layers = new ArrayList<>();
    protected final Set<IAtmosphereTracker> listeners = new HashSet<>();

    @Override
    public void onLoad(@Nonnull Chunk chunk, @Nonnull AtmosphereWorldInfo info) {
        this.setAtmosphereWorldInfo(info);
        for(Layer layer:layers){
            layer.onLoad(chunk);
        }
        for(AtmosphereProperty property: GeographyPropertyManager.getAtmosphereProperties()){
            property.onAtmosphereInitialise(this,chunk);
        }
    }

    @Override
    public void onLoadWithoutChunk(@Nonnull AtmosphereWorldInfo info) {
        this.setAtmosphereWorldInfo(info);
        for(Layer layer:layers){
            layer.onLoadWithoutChunk();
        }
        for(AtmosphereProperty property: GeographyPropertyManager.getAtmosphereProperties()){
            property.onAtmosphereInitialise(this,null);
        }
    }

    @Override
    public void addTracker(@Nonnull IAtmosphereTracker tracker){
        listeners.add(tracker);
    }
    @Override
    public void removeTracker(@Nonnull IAtmosphereTracker tracker){
        listeners.remove(tracker);
    }

    public void setAtmosphereWorldInfo(@Nonnull AtmosphereWorldInfo worldInfo) {
        this.worldInfo = worldInfo;
    }

    @Nonnull
    @Override
    public AtmosphereWorldInfo getAtmosphereWorldInfo() {
        return worldInfo;
    }

    @Override
    public boolean isLoaded(){
        for(Layer layer:layers){
            if(!layer.isInitialise()) return false;
        }
        return true;
    }

    @Override
    public long tickTime() {
        return tickTimes;
    }

    @Nonnull
    @Override
    public Layer getTopLayer() {
        return layers.get(layers.size()-1);
    }

    @Nonnull
    @Override
    public Layer getBottomLayer() {
        return layers.get(0);
    }
}
