package top.qiguaiaaaa.fluidgeography.api.atmosphere;

import net.minecraft.world.WorldServer;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.model.IAtmosphereModel;

public class AtmosphereWorldInfo {
    protected final WorldServer world;
    protected AtmosphereWorldType type = AtmosphereWorldType.NORMAL;
    protected final IAtmosphereModel model;

    public AtmosphereWorldInfo(WorldServer world,IAtmosphereModel model) {
        this.world = world;
        this.model = model;
    }

    public WorldServer getWorld() {
        return world;
    }

    public AtmosphereWorldType getType() {
        return type;
    }

    /**
     * 获取运行大气的模型
     * @return 大气模型
     */
    public IAtmosphereModel getModel() {
        return model;
    }

    public boolean isTemperatureConstant() {
        return type.isTempConstant;
    }

    public boolean isWorldClosed() {
        return type.isWorldClosed;
    }

    public void setType(AtmosphereWorldType type) {
        this.type = type;
    }
}
