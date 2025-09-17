package top.qiguaiaaaa.geocraft.util;

import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author QiguaiAAAA
 */
public class MiscUtil {
    @Nullable
    public static WorldServer getValidWorld(@Nonnull World world){
        if(world.isRemote) return null;
        return (world instanceof WorldServer)?(WorldServer) world:null;
    }
}
