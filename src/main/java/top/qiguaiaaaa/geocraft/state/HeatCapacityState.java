package top.qiguaiaaaa.geocraft.state;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagLong;
import top.qiguaiaaaa.geocraft.api.GeoCraftProperties;
import top.qiguaiaaaa.geocraft.api.property.GeographyProperty;
import top.qiguaiaaaa.geocraft.api.state.GeographyState;

public class HeatCapacityState implements GeographyState {
    public long heatCapacity = (long) 1e8;
    @Override
    public boolean isInitialised() {
        return true;
    }

    @Override
    public GeographyProperty getProperty() {
        return GeoCraftProperties.HEAT_CAPACITY;
    }

    @Override
    public String getNBTTagKey() {
        return "hc";
    }

    @Override
    public NBTBase serializeNBT() {
        return new NBTTagLong(heatCapacity);
    }

    @Override
    public void deserializeNBT(NBTBase nbt) {
        if(nbt instanceof NBTPrimitive){
            heatCapacity = ((NBTPrimitive)nbt).getLong();
        }
    }
}
