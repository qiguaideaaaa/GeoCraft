package top.qiguaiaaaa.geocraft.api.state;

import net.minecraft.nbt.NBTBase;
import net.minecraftforge.common.util.INBTSerializable;
import top.qiguaiaaaa.geocraft.api.atmosphere.layer.Layer;
import top.qiguaiaaaa.geocraft.api.property.GeographyProperty;

public interface GeographyState extends INBTSerializable<NBTBase> {
    /**
     * 初始化状态
     * @param layer 层级
     */
    default void initialise(Layer layer){}

    /**
     * 该状态是否需要被保存
     * @return 若为true,则大气状态会被保存到大气nbt当中
     */
    default boolean toBeSavedIntoNBT(){return true;}

    /**
     * 该状态是否需要从NBT中加载
     * @return 若为true,则大气状态会被提供相应的nbt数据
     */
    default boolean toBeLoadedFromNBT(){return true;}

    /**
     * 该状态是否已经初始化完成
     * @return 若完成,则返回true
     */
    boolean isInitialised();

    /**
     * 获得该状态对应的属性
     * @return 大气属性
     */
    GeographyProperty getProperty();
    String getNBTTagKey();
}
