package top.qiguaiaaaa.fluidgeography.api.atmosphere.property;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.registries.IForgeRegistryEntry;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.layer.AtmosphereLayer;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.state.IAtmosphereState;

public abstract class AtmosphereProperty extends IForgeRegistryEntry.Impl<AtmosphereProperty> {
    protected final boolean windEffect;
    protected final boolean flowable;
    public AtmosphereProperty(boolean windEffect,boolean flowable){
        this.windEffect = windEffect;
        this.flowable = flowable;
    }

    public boolean haveWindEffect() {
        return windEffect;
    }

    public boolean isFlowable() {
        return flowable;
    }

    /**
     * 计算本层朝向邻居大气的风速在这一属性上的分量
     * 若windEffect为true则会调用
     * @param self 本层大气
     * @param neighbor 邻居大气
     * @param direction B相对A的方向
     * @return 风速分量
     */
    public Vec3d getWind(AtmosphereLayer self, Atmosphere neighbor, EnumFacing direction){
        return Vec3d.ZERO;
    }

    /**
     * 当大气平流的时候
     * 若flowable为true则会调用
     * @param from 源大气层
     * @param to 朝向大气
     * @param direction 朝向方位
     * @param windSpeed 风速，包含垂直分量
     */
    public void onFlow(AtmosphereLayer from, Chunk fromChunk, Atmosphere to , Chunk toChunk, EnumFacing direction, Vec3d windSpeed){}

    /**
     * 当大气对流的时候
     * 若flowable为true则会调用
     * @param lower 低层
     * @param upper 高层
     * @param speed 风速,正为低层往高层,负为高层往低层
     */
    public void onConvect(AtmosphereLayer lower,AtmosphereLayer upper,double speed){}

    /**
     * 当大气初始化的时候
     */
    public void onAtmosphereInitialise(Atmosphere atmosphere, Chunk chunk){}

    /**
     * 获取对应大气状态的Instance
     * @return 一个符合该属性的大气状态
     */
    public abstract IAtmosphereState getStateInstance();
}
