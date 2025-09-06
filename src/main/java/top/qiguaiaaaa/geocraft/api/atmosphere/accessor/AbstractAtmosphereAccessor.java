package top.qiguaiaaaa.geocraft.api.atmosphere.accessor;

import net.minecraft.util.math.BlockPos;
import top.qiguaiaaaa.geocraft.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.geocraft.api.atmosphere.storage.AtmosphereData;
import top.qiguaiaaaa.geocraft.api.atmosphere.system.IAtmosphereSystem;
import top.qiguaiaaaa.geocraft.api.util.exception.AtmosphereNotLoadedException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class AbstractAtmosphereAccessor implements IAtmosphereAccessor{
    protected final IAtmosphereSystem system;
    protected final AtmosphereData data;
    protected final BlockPos pos;
    protected boolean notAir;
    protected int skyLight = -1;

    public AbstractAtmosphereAccessor(@Nonnull IAtmosphereSystem system, @Nonnull AtmosphereData data,@Nonnull BlockPos pos, boolean notAir) {
        this.system = system;
        this.data = data;
        this.pos = pos;
        this.notAir = notAir;
    }

    @Override
    public IAtmosphereSystem getSystem() {
        return system;
    }

    @Nullable
    @Override
    public Atmosphere getAtmosphereHere() {
        if(data.isUnloaded()) return null;
        return data.getAtmosphere();
    }

    @Nullable
    @Override
    public AtmosphereData getAtmosphereDataHere() {
        return data.isUnloaded() ?null:data;
    }

    @Override
    public boolean isAtmosphereLoaded() {
        return isAtmosphereDataLoaded() && data.getAtmosphere().isInitialised();
    }

    @Override
    public boolean refresh() {
        return false;
    }

    @Override
    public void setSkyLight(int light) {
        this.skyLight = light;
    }

    @Override
    public void setNotAir(boolean notAir) {
        this.notAir = notAir;
    }

    protected boolean isAtmosphereDataLoaded(){
        return !data.isUnloaded() && data.getAtmosphere() != null;
    }

    protected void checkAtmosphereDataLoaded(){
        if(!isAtmosphereDataLoaded()) throw new AtmosphereNotLoadedException(getWorld().provider.getDimension(),pos);
    }
}
