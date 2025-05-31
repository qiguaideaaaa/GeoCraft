package top.qiguaiaaaa.fluidgeography.atmosphere;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LowerAtmosphereProvider implements ICapabilitySerializable<NBTTagCompound> {
    private final LowerAtmosphere instance;
    private final Capability<LowerAtmosphere> capability;

    public LowerAtmosphereProvider() {
        this.instance = new LowerAtmosphere();
        this.capability = LowerAtmosphere.LOWER_ATMOSPHERE;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return this.capability.equals(capability);
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        return this.capability.equals(capability) ? this.capability.cast(instance) : null;
    }

    @Override
    public NBTTagCompound serializeNBT() {
        return this.instance.serializeNBT();
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {
        this.instance.deserializeNBT(nbt);
    }
}
