package top.qiguaiaaaa.fluidgeography.api.atmosphere.state;

import net.minecraft.nbt.NBTBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.INBTSerializable;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.property.AtmosphereProperty;

public interface IAtmosphereState extends INBTSerializable<NBTBase> {
    /**
     * 初始化状态
     * @param atmosphere 大气
     */
    default void initialise(Atmosphere atmosphere){}
    /**
     * 获得该大气状态对应的属性
     * @return 大气属性
     */
    AtmosphereProperty getProperty();
    String getNBTTagKey();
}
