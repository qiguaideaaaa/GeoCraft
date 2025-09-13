package top.qiguaiaaaa.geocraft.geography.fluid_physics;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import top.qiguaiaaaa.geocraft.GeoCraft;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * 流体更新管理器，为保证最佳效果所有流体的更新应该走这个管理器
 * @author QiguaiAAAA
 */
@Mod.EventBusSubscriber
public final class FluidUpdateManager {
    static final int MAX_UPDATE_NUM = 65536*2;
    static final Map<WorldServer, Pair<PriorityQueue<IFluidUpdateTask>,PriorityQueue<IFluidUpdateTask>>> updateTaskQueuesMap = new HashMap<>();
    static final Map<WorldServer, List<Triple<BlockPos, Block,Long>>> scheduledUpdates = new HashMap<>();

    public static void scheduleUpdate(World world,BlockPos pos,Block block,int delay){
        WorldServer validWorld = getValidWorld(world);
        if(validWorld == null) return;
        List<Triple<BlockPos, Block,Long>> list = getOrCreateLists(validWorld);
        list.add(Triple.of(pos,block,world.getTotalWorldTime()+delay));
    }

    public static void addTask(@Nonnull World world,@Nonnull IFluidUpdateTask task){
        WorldServer validWorld = getValidWorld(world);
        if(validWorld == null) return;
        Pair<PriorityQueue<IFluidUpdateTask>,PriorityQueue<IFluidUpdateTask>> queuePair = getOrCreateQueues(validWorld);
        if(task.getFluid().getDensity()>=0){
            queuePair.getLeft().add(task);
        }else{
            queuePair.getRight().add(task);
        }
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event){
        if(event.phase == TickEvent.Phase.START) return;
        WorldServer world = getValidWorld(event.world);
        if(world == null) return;
        List<Triple<BlockPos, Block,Long>> scheduledUpdateList = getOrCreateLists(world);
        for(int i=0;i<MAX_UPDATE_NUM*2;i++){
            if(scheduledUpdateList.isEmpty()) break;
            Triple<BlockPos, Block,Long> triple = scheduledUpdateList.get(0);
            if(triple.getRight()<world.getTotalWorldTime()){
                continue;
            }
            scheduledUpdateList.remove(0);
            IBlockState state =world.getBlockState(triple.getLeft());
            if(state.getBlock() != triple.getMiddle()) continue;
            state.getBlock().updateTick(world,triple.getLeft(),state,world.rand);
        }
        Pair<PriorityQueue<IFluidUpdateTask>,PriorityQueue<IFluidUpdateTask>> queues = getOrCreateQueues(world);
        updateTasks(world,queues.getLeft());
        updateTasks(world,queues.getRight());
        FluidPressureSearchManager.onWorldTick(world);
    }

    static void updateTasks(@Nonnull WorldServer world,@Nonnull PriorityQueue<IFluidUpdateTask> queue){
        for(int i=0;i<MAX_UPDATE_NUM*2;i++){
            if(queue.isEmpty()) break;
            IFluidUpdateTask task = queue.poll();
            if(task == null) continue;
            if(!world.isBlockLoaded(task.getPos())) continue;
            IBlockState state = world.getBlockState(task.getPos());
            if(state.getBlock() != task.getBlock()) continue;
            try {
                task.onUpdate(world,state,world.rand);
            }catch (Throwable e){
                GeoCraft.getLogger().warn("When updating fluid {} at {} in world {},",task.getFluid().getUnlocalizedName(),task.getPos(),world.provider.getDimension());
                GeoCraft.getLogger().warn("FluidUpdateManager caught an error:",e);
            }
        }
    }

    static Pair<PriorityQueue<IFluidUpdateTask>,PriorityQueue<IFluidUpdateTask>> getOrCreateQueues(@Nonnull WorldServer world){
        return updateTaskQueuesMap.computeIfAbsent(world,k ->
                Pair.of(
                new PriorityQueue<>(Comparator.comparingInt(value -> value.getPos().getY())) //小根堆，向下流的流体放这里
                ,new PriorityQueue<>((v1,v2) -> v2.getPos().getY()-v1.getPos().getY()) //大根堆，向上流的流体放这里
        ));
    }

    static List<Triple<BlockPos, Block,Long>> getOrCreateLists(@Nonnull WorldServer world){
        return scheduledUpdates.computeIfAbsent(world,k -> new LinkedList<>());
    }

    @Nullable
    static WorldServer getValidWorld(@Nonnull World world){
        if(world.isRemote) return null;
        return (world instanceof WorldServer)?(WorldServer) world:null;
    }
}
