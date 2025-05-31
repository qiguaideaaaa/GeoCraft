package top.qiguaiaaaa.fluidgeography.api.atmosphere;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.listener.IAtmosphereListener;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.property.AtmosphereProperty;
import top.qiguaiaaaa.fluidgeography.api.atmosphere.state.IAtmosphereState;

import java.util.Random;
import java.util.Set;

public interface Atmosphere {
    NoiseGeneratorPerlin TEMPERATURE_NOISE = new NoiseGeneratorPerlin(new Random(1234L), 1);
    void initialise(Chunk chunk, AtmosphereWorldInfo info);

    //******************
    // Getter And Setter
    //******************
    boolean addWaterAmount(int addAmount);

    void addTemperature(double temp);

    void addHeatQuantity(double Q);

    void addListener(IAtmosphereListener listener);

    void removeListener(IAtmosphereListener listener);

    void setWaterAmount(int waterAmount);

    void setTemperature(float temperature);

    void setAtmosphereWorldInfo(AtmosphereWorldInfo worldInfo);

    void resetTemperature(Chunk chunk);

    Vec3d getWindSpeed(EnumFacing direction);

    Vec3d getWindSpeed(BlockPos pos);

    AtmosphereWorldInfo getAtmosphereWorldInfo();

    long getTickTimes();

    long getHeatCapacity();

    double getRainStrong();

    Underlying getUnderlying();

    int getWaterAmount();

    float getTemperature();

    float getTemperature(BlockPos pos);

    float getTemperatureBase(Chunk chunk);

    Set<IAtmosphereListener> getListeners();

    <T extends IAtmosphereState> T getState(AtmosphereProperty property);

    IAtmosphereState createState(AtmosphereProperty property);

    boolean isInitialised();
}
