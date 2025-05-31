package top.qiguaiaaaa.fluidgeography.atmosphere;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class LowerAtmosphereStorage implements Capability.IStorage<LowerAtmosphere> {

    @Nullable
    @Override
    public NBTBase writeNBT(Capability<LowerAtmosphere> capability, LowerAtmosphere instance, EnumFacing side) {
        return instance.serializeNBT();
    }

    @Override
    public void readNBT(Capability<LowerAtmosphere> capability, LowerAtmosphere instance, EnumFacing side, NBTBase nbt) {
        if (nbt instanceof NBTTagCompound) {
            instance.deserializeNBT((NBTTagCompound) nbt);
        }
    }
}
