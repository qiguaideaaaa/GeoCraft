package top.qiguaiaaaa.geocraft.api.atmosphere.gen;

import top.qiguaiaaaa.geocraft.api.atmosphere.storage.AtmosphereData;

import javax.annotation.Nullable;
import java.util.Collection;

public interface IAtmosphereDataProvider {
    @Nullable
    AtmosphereData getLoadedAtmosphereData(int x, int z);

    Collection<AtmosphereData> getLoadedAtmosphereDataCollection();

    AtmosphereData provideAtmosphereData(int x, int z);

    void queueUnloadAtmosphereData(int x,int z);
    void saveAtmosphereData(int x,int z);

    void saveAllAtmosphereData();

    boolean tick();

    String makeString();

    boolean doesAtmosphereExistsAt(int x, int z);
}
