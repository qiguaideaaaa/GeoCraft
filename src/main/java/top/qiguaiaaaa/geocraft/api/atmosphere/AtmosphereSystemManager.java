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
import top.qiguaiaaaa.geocraft.api.event.atmosphere.AtmosphereSystemEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * 大气系统管理器，管理所有世界的大气系统
 */
public final class AtmosphereSystemManager {
    /**
     * 请使用{@link AtmosphereSystemEvent.Create}事件来向某个维度创建大气系统
     */
    static final BiMap<WorldServer, IAtmosphereSystem> atmosphereSystems = HashBiMap.create();

    /**
     * 获取某个世界的大气系统
     * @param world 世界，若不是{@link WorldServer}及其子类则始终返回null
     * @return 对应的大气系统，若不存在则返回null
     */
    @Nullable
    public static IAtmosphereSystem getAtmosphereSystem(@Nonnull World world){
        WorldServer server = getValidWorld(world);
        if(server == null) return null;
        return atmosphereSystems.get(server);
    }

    /**
     * 获取指定位置的大气访问器
     * @return 如果大气不存在或未初始化，则返回null。正常情况下返回大气访问器。
     */
    @Nullable
    public static IAtmosphereAccessor getAtmosphereAccessor(@Nonnull World world,@Nonnull BlockPos pos,boolean notAir){
        IAtmosphereSystem system = getAtmosphereSystem(world);
        if(system == null) return null;
        return system.getAccessor(pos,notAir);
    }

    /**
     * 获取指定位置的大气
     * @return 如果大气不存在或未初始化，则返回null。正常情况下返回大气。
     */
    @Nullable
    public static Atmosphere getAtmosphere(@Nonnull World world,@Nonnull BlockPos pos){
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
    public static Atmosphere getAtmosphere(@Nonnull Chunk chunk){
        IAtmosphereSystem system = getAtmosphereSystem(chunk.getWorld());
        if(system == null) return null;
        return system.getAtmosphere(chunk);
    }
    @Nullable
    public static Atmosphere getAtmosphere(@Nonnull World world,@Nonnull ChunkPos pos){
        IAtmosphereSystem system = getAtmosphereSystem(world);
        if(system == null) return null;
        return system.getAtmosphere(pos.x,pos.z);
    }

    @Nullable
    static WorldServer getValidWorld(@Nonnull World world){
        if(world.isRemote || (!(world instanceof WorldServer))) return null;
        return (WorldServer) world;
    }
}
