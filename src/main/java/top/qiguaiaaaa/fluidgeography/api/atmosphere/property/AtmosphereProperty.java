package top.qiguaiaaaa.fluidgeography.api.atmosphere.property;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.registries.IForgeRegistryEntry;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.state.IAtmosphereState;

public abstract class AtmosphereProperty extends IForgeRegistryEntry.Impl<AtmosphereProperty> {
    protected boolean windEffect = false;
    protected boolean flowable = false;

    public void setWindEffect(boolean windEffect) {
        this.windEffect = windEffect;
    }

    public void setFlowable(boolean flowable) {
        this.flowable = flowable;
    }

    public boolean haveWindEffect() {
        return windEffect;
    }

    public boolean isFlowable() {
        return flowable;
    }

    /**
     * 计算A区块朝向B区块的风速在这一属性上的分量
     * @param a A区块的大气
     * @param b B区块的大气
     * @param direction B相对A的方向
     * @return 风速度
     */
    public Vec3d getWind(Atmosphere a, Atmosphere b, EnumFacing direction){
        return Vec3d.ZERO;
    }

    /**
     * 当大气流动的时候
     * @param from 源大气
     * @param to 朝向大气
     * @param direction 朝向方位
     * @param windSpeed 风速
     */
    public void onAtmosphereFlow(Atmosphere from, Chunk fromChunk, Atmosphere to , Chunk toChunk, EnumFacing direction, Vec3d windSpeed){}

    /**
     * 当大气初始化的时候
     */
    public void onAtmosphereInitialise(Atmosphere atmosphere,Chunk chunk){}

    /**
     * 获取对应大气状态的Instance
     * @return 一个符合该属性的大气状态
     */
    public abstract IAtmosphereState getStateInstance();
}
