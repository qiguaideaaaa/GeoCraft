package top.qiguaiaaaa.geocraft.state;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagDouble;
import top.qiguaiaaaa.geocraft.api.GeoCraftProperties;
import top.qiguaiaaaa.geocraft.api.property.GeographyProperty;
import top.qiguaiaaaa.geocraft.api.state.GeographyState;
import top.qiguaiaaaa.geocraft.api.util.math.Altitude;
import top.qiguaiaaaa.geocraft.property.AltitudeProperty;

public class AltitudeState implements GeographyState {
    public final Altitude altitude;

    public AltitudeState(Altitude altitude){
        this.altitude = altitude;
    }

    @Override
    public boolean isInitialised() {
        return this.altitude.get()> AltitudeProperty.UNAVAILABLE+1;
    }

    @Override
    public GeographyProperty getProperty() {
        return GeoCraftProperties.ALTITUDE;
    }

    @Override
    public String getNBTTagKey() {
        return "al";
    }

    @Override
    public NBTBase serializeNBT() {
        return new NBTTagDouble(altitude.get());
    }

    @Override
    public void deserializeNBT(NBTBase nbt) {
        if(nbt instanceof NBTPrimitive){
            altitude.set(((NBTPrimitive)nbt).getDouble());
        }
    }

    @Override
    public String toString() {
        return altitude.toString();
    }
}
