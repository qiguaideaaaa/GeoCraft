package top.qiguaiaaaa.fluidgeography.api.atmosphere.state;

import net.minecraft.nbt.NBTBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.INBTSerializable;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.Atmosphere;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.property.AtmosphereProperty;

public interface IAtmosphereState extends INBTSerializable<NBTBase> {
    void onUpdate(Atmosphere atmosphere, Chunk chunk);
    AtmosphereProperty getProperty();
    String getNBTTagKey();
}
