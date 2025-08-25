package top.qiguaiaaaa.fluidgeography.api.atmosphere.state;

import net.minecraft.nbt.NBTBase;
import net.minecraftforge.common.util.INBTSerializable;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.layer.AtmosphereLayer;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.property.AtmosphereProperty;

public interface IAtmosphereState extends INBTSerializable<NBTBase> {
    /**
     * 初始化状态
     * @param layer 大气层
     */
    default void initialise(AtmosphereLayer layer){}

    /**
     * 该大气状态是否需要被保存
     * @return 若为true,则大气状态会被保存到大气nbt当中
     */
    default boolean toBeSavedIntoNBT(){return true;}

    /**
     * 该大气状态是否需要从NBT中加载
     * @return 若为true,则大气状态会被提供相应的nbt数据
     */
    default boolean toBeLoadedFromNBT(){return true;}

    /**
     * 该大气状态是否已经初始化完成
     * @return 若完成,则返回true
     */
    boolean isInitialised();

    /**
     * 获得该大气状态对应的属性
     * @return 大气属性
     */
    AtmosphereProperty getProperty();
    String getNBTTagKey();
}
