package top.qiguaiaaaa.geocraft.atmosphere;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class DefaultAtmosphereStorage implements Capability.IStorage<DefaultAtmosphere> {

    @Nullable
    @Override
    public NBTBase writeNBT(Capability<DefaultAtmosphere> capability, DefaultAtmosphere instance, EnumFacing side) {
        return instance.serializeNBT();
    }

    @Override
    public void readNBT(Capability<DefaultAtmosphere> capability, DefaultAtmosphere instance, EnumFacing side, NBTBase nbt) {
        if (nbt instanceof NBTTagCompound) {
            instance.deserializeNBT((NBTTagCompound) nbt);
        }
    }
}
