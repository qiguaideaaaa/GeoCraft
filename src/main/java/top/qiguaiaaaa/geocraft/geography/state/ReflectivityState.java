package top.qiguaiaaaa.geocraft.geography.state;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagDouble;
import top.qiguaiaaaa.geocraft.api.property.GeographyProperty;
import top.qiguaiaaaa.geocraft.api.state.GeographyState;
import top.qiguaiaaaa.geocraft.geography.property.ReflectivityProperty;

import javax.annotation.Nonnull;

public class ReflectivityState implements GeographyState {
    public double reflectivity = 0.08;
    @Override
    public boolean isInitialised() {
        return true;
    }

    @Nonnull
    @Override
    public GeographyProperty getProperty() {
        return ReflectivityProperty.REFLECTIVITY;
    }

    @Nonnull
    @Override
    public String getNBTTagKey() {
        return "rf";
    }

    @Override
    public NBTBase serializeNBT() {
        return new NBTTagDouble(reflectivity);
    }

    @Override
    public void deserializeNBT(NBTBase nbt) {
        if(nbt instanceof NBTPrimitive){
            reflectivity = ((NBTPrimitive)nbt).getDouble();
        }
    }
}
