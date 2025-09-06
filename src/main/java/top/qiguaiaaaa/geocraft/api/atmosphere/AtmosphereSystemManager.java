package top.qiguaiaaaa.geocraft.api.atmosphere;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import top.qiguaiaaaa.geocraft.api.atmosphere.accessor.IAtmosphereAccessor;
import top.qiguaiaaaa.geocraft.api.atmosphere.system.IAtmosphereSystem;

import javax.annotation.Nullable;

public class AtmosphereSystemManager {
    protected static final BiMap<WorldServer, IAtmosphereSystem> atmosphereSystems = HashBiMap.create();

    public static IAtmosphereSystem getAtmosphereSystem(World world){
        WorldServer server = getValidWorld(world);
        if(server == null) return null;
        return atmosphereSystems.get(server);
    }

    /**
     * 获取指定位置的大气访问器
     * @return 如果大气不存在或未初始化，则返回null。正常情况下返回大气访问器。
     */
    @Nullable
    public static IAtmosphereAccessor getAtmosphereAccessor(World world, BlockPos pos,boolean notAir){
        IAtmosphereSystem system = getAtmosphereSystem(world);
        if(system == null) return null;
        return system.getAccessor(pos,notAir);
    }

    /**
     * 获取指定位置的大气
     * @return 如果大气不存在或未初始化，则返回null。正常情况下返回大气。
     */
    @Nullable
    public static Atmosphere getAtmosphere(World world, BlockPos pos){
        IAtmosphereSystem system = getAtmosphereSystem(world);
        if(system == null) return null;
        return system.getAtmosphere(pos);
    }

    /**
     * 获取指定区块的大气
     * @param chunk 区块
     * @return 如果大气不存在或未初始化，则返回null。正常情况下返回大气。
     */
    @Nullable
    public static Atmosphere getAtmosphere(Chunk chunk){
        IAtmosphereSystem system = getAtmosphereSystem(chunk.getWorld());
        if(system == null) return null;
        return system.getAtmosphere(chunk);
    }

    public static Atmosphere getAtmosphere(World world,ChunkPos pos){
        IAtmosphereSystem system = getAtmosphereSystem(world);
        if(system == null) return null;
        return system.getAtmosphere(pos.x,pos.z);
    }

    @Nullable
    protected static WorldServer getValidWorld(World world){
        if(world.isRemote || (!(world instanceof WorldServer))) return null;
        return (WorldServer) world;
    }
}
