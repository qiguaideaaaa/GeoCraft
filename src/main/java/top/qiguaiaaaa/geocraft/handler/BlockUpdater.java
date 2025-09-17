package top.qiguaiaaaa.geocraft.handler;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import org.apache.commons.lang3.tuple.Triple;
import top.qiguaiaaaa.geocraft.util.misc.ExtendedNextTickListEntry;

import java.util.*;

import static top.qiguaiaaaa.geocraft.util.MiscUtil.getValidWorld;

/**
 * @author QiguaiAAAA
 */
public final class BlockUpdater {
    static final int MAX_UPDATE_NUM = 65536*4;
    static final Map<WorldServer, List<ExtendedNextTickListEntry>> WORLD_SCHEDULE_MAP = new HashMap<>();
    static final List<ExtendedNextTickListEntry> READY_TO_UPDATES = new ArrayList<>(MAX_UPDATE_NUM/4);

    public static void scheduleUpdate(World world, BlockPos pos, Block block, int delay){
        if(delay == 0){
            READY_TO_UPDATES.add(new ExtendedNextTickListEntry(world,pos,block,delay,0));
            return;
        }
        WorldServer validWorld = getValidWorld(world);
        if(validWorld == null) return;
        List<ExtendedNextTickListEntry> schedules = WORLD_SCHEDULE_MAP.computeIfAbsent(validWorld,k -> new LinkedList<>());
        schedules.add(new ExtendedNextTickListEntry(world,pos,block,delay,0));
    }

    public static void onWorldTick(WorldServer world){
        final List<ExtendedNextTickListEntry> schedules = WORLD_SCHEDULE_MAP.get(world);
        if(schedules == null) return;
        int size = schedules.size();
        for(int i=0,j=0;i<size && j <size;i++){
            ExtendedNextTickListEntry entry = schedules.get(j);
            if(world.getTotalWorldTime()<entry.scheduledTime){
                j++;
                continue;
            }
            schedules.remove(j);
            READY_TO_UPDATES.add(entry);
        }
        for(ExtendedNextTickListEntry entry:READY_TO_UPDATES){
            IBlockState state = world.getBlockState(entry.position);
            if(state.getBlock() != entry.getBlock()) continue;
            state.getBlock().updateTick(world,entry.position,state,world.rand);
        }
        READY_TO_UPDATES.clear();
    }

    public static void onServerStop(){
        WORLD_SCHEDULE_MAP.clear();
        READY_TO_UPDATES.clear();
    }
}
