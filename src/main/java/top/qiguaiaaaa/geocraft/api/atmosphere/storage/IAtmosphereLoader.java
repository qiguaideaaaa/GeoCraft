package top.qiguaiaaaa.geocraft.api.atmosphere.storage;

import net.minecraft.world.MinecraftException;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.io.IOException;

public interface IAtmosphereLoader {
    @Nullable
    AtmosphereData loadAtmosphereData(World worldIn, int x, int z) throws IOException;

    void saveAtmosphereData(World worldIn, AtmosphereData chunkIn) throws MinecraftException, IOException;
    boolean doesAtmosphereExistsAt(int x, int z);

    void flush();
}
