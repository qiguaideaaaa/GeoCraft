package top.qiguaiaaaa.fluidgeography.api.atmosphere;

import net.minecraft.world.chunk.Chunk;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.layer.Layer;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.listener.IAtmosphereListener;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.property.AtmosphereProperty;
import top.qiguaiaaaa.fluidgeography.atmosphere.GeographyPropertyManager;

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
    protected final Set<IAtmosphereListener> listeners = new HashSet<>();

    @Override
    public void initialise(Chunk chunk, AtmosphereWorldInfo info) {
        this.setAtmosphereWorldInfo(info);
        for(Layer layer:layers){
            layer.initialise(chunk);
        }
        for(AtmosphereProperty property: GeographyPropertyManager.getAtmosphereProperties()){
            property.onAtmosphereInitialise(this,chunk);
        }
    }

    @Override
    public void addListener(IAtmosphereListener listener){
        if(listener == null) return;
        listeners.add(listener);
    }
    @Override
    public void removeListener(IAtmosphereListener listener){
        listeners.remove(listener);
    }

    @Override
    public void setAtmosphereWorldInfo(AtmosphereWorldInfo worldInfo) {
        if(worldInfo == null) return;
        this.worldInfo = worldInfo;
    }

    @Override
    public AtmosphereWorldInfo getAtmosphereWorldInfo() {
        return worldInfo;
    }

    @Override
    public boolean isInitialised(){
        for(Layer layer:layers){
            if(!layer.isInitialise()) return false;
        }
        return true;
    }

    @Override
    public long tickTime() {
        return tickTimes;
    }

    @Override
    public Layer getTopLayer() {
        return layers.get(layers.size()-1);
    }

    @Override
    public Layer getBottomLayer() {
        return layers.get(0);
    }
}
