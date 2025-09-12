package top.qiguaiaaaa.geocraft.api.atmosphere.accessor;

import net.minecraft.util.math.BlockPos;
import top.qiguaiaaaa.geocraft.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.geocraft.api.atmosphere.storage.AtmosphereData;
import top.qiguaiaaaa.geocraft.api.atmosphere.system.IAtmosphereSystem;
import top.qiguaiaaaa.geocraft.api.util.exception.AtmosphereNotLoadedException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * {@link IAtmosphereAccessor}抽象层，没有数据获取功能
 */
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

    @Nonnull
    @Override
    public IAtmosphereSystem getSystem() {
        return system;
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Nullable
    @Override
    public Atmosphere getAtmosphereHere() {
        if(data.isUnloaded()) return null;
        return data.getAtmosphere();
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Nullable
    @Override
    public AtmosphereData getAtmosphereDataHere() {
        return data.isUnloaded() ?null:data;
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public boolean isAtmosphereLoaded() {
        return isAtmosphereDataLoaded() && data.getAtmosphere().isLoaded();
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public boolean refresh() {
        return false;
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public void setSkyLight(int light) {
        this.skyLight = light;
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public void setNotAir(boolean notAir) {
        this.notAir = notAir;
    }

    @Override
    public double getTemperature(boolean notAir) {
        boolean old = this.notAir;
        setNotAir(notAir);
        double temp = getTemperature();
        setNotAir(old);
        return temp;
    }

    /**
     * 检查大气数据是否已经被加载，该方法不会检查大气是否已经初始化
     * @return 若大气数据已经加载，则返回true
     */
    protected boolean isAtmosphereDataLoaded(){
        return !data.isUnloaded() && data.getAtmosphere() != null;
    }

    /**
     * 检查大气数据是否已经已经加载，该方法会调用{@link #isAtmosphereDataLoaded()}
     * 需要注意若检查失败，会抛出{@link AtmosphereNotLoadedException}
     * @throws AtmosphereNotLoadedException 若大气数据未加载，则抛出该错误。
     */
    protected void checkAtmosphereDataLoaded(){
        if(!isAtmosphereDataLoaded()) throw new AtmosphereNotLoadedException(getWorld().provider.getDimension(),pos);
    }
}
