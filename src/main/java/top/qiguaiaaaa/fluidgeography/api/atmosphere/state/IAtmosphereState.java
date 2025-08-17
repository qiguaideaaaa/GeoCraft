package top.qiguaiaaaa.fluidgeography.api.atmosphere.state;

import net.minecraft.nbt.NBTBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.INBTSerializable;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.property.AtmosphereProperty;

public interface IAtmosphereState extends INBTSerializable<NBTBase> {
    /**
     * 分步模拟大气时，状态的更新，不涉及物质或能量迁移
     * @param atmosphere 大气
     * @param chunk 区块
     */
    @Deprecated
    default void onUpdate(Atmosphere atmosphere, Chunk chunk){
    }

    /**
     * 获得该大气状态对应的属性
     * @return 大气属性
     */
    AtmosphereProperty getProperty();
    String getNBTTagKey();
}
