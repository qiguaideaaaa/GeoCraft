package top.qiguaiaaaa.geocraft.geography.fluid_physics;

import io.netty.util.internal.ConcurrentSet;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.apache.commons.lang3.tuple.Pair;
import top.qiguaiaaaa.geocraft.GeoCraft;
import top.qiguaiaaaa.geocraft.handler.BlockUpdater;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;

import static top.qiguaiaaaa.geocraft.geography.fluid_physics.FluidUpdateManager.*;
import static top.qiguaiaaaa.geocraft.util.MiscUtil.getValidWorld;

/**
 * @author QiguaiAAAA
 */
public final class FluidPressureSearchManager implements Runnable{
    static final Map<WorldServer, WorldPressureInfo> worldMap = new ConcurrentHashMap<>(); //running的task、结果和running的task的poses
    static final Map<WorldServer,WorldQueueTaskInfo> queueMap = new ConcurrentHashMap<>(); //等待被加入queue的task
    static final Map<WorldServer,Queue<Pair<BlockPos,Block>>> queueToLoadPos = new ConcurrentHashMap<>();

    public static boolean isTaskRunning(@Nonnull World world,@Nonnull BlockPos pos){ //Minecraft主线程调用
        WorldServer validWorld = getValidWorld(world);
        if(validWorld == null) return false;
        WorldPressureInfo runningInfo = getOrCreateWorldInfo(validWorld);
        WorldQueueTaskInfo queueInfo = queueMap.computeIfAbsent(validWorld,k->new WorldQueueTaskInfo());
        return runningInfo.getRunningTaskLocks().contains(pos) || queueInfo.queuedTaskLocks.contains(pos);
    }

    @Nullable
    public static Collection<BlockPos> getTaskResult(@Nonnull World world,@Nonnull BlockPos pos){ //Minecraft主线程调用
        WorldServer validWorld = getValidWorld(world);
        if(validWorld == null) return null;
        WorldPressureInfo info = getOrCreateWorldInfo(validWorld);
        info.getRunningTaskLocks().remove(pos);
        return info.getTaskResults().remove(pos);
    }

    public static void addTask(@Nonnull World world,@Nonnull IFluidPressureSearchTask task){ //Minecraft主线程调用
        WorldServer validWorld = getValidWorld(world);
        if(validWorld == null) return;
        WorldQueueTaskInfo info = queueMap.computeIfAbsent(validWorld,k-> new WorldQueueTaskInfo());
        info.queueTask(task);
    }

    public static void onWorldTick(@Nonnull WorldServer world){ //Minecraft主线程调用
        Queue<Pair<BlockPos,Block>> posesToLoad = queueToLoadPos.get(world);
        if(posesToLoad == null) return;
        for(int i=0;i<MAX_UPDATE_NUM*2;i++){
            Pair<BlockPos, Block> task = posesToLoad.poll();
            if(task == null) break;
            BlockUpdater.scheduleUpdate(world,task.getLeft(),task.getRight(),0);
        }
        posesToLoad.clear();

        if(world.getTotalWorldTime()%60 == 0){
            WorldPressureInfo info = getOrCreateWorldInfo(world);
            info.getTaskResults().clear();
        }
    }

    @Override
    public void run() {  //自身线程调用
        boolean running = true;
        while (running){
            long startTime = System.currentTimeMillis();

            WorldServer[] loadedWorld = FMLCommonHandler.instance().getMinecraftServerInstance().worlds;
            for(WorldServer world:loadedWorld){
                WorldPressureInfo info = getOrCreateWorldInfo(world);
                pushNewTasks(world,info);
                updateTasks(world,info);
                if(info.getRunningTasks().isEmpty()){
                    info.getRunningTaskLocks().clear();
                    continue;
                }
                if(world.getTotalWorldTime()%1000 == 0){
                    info.getRunningTasks().clear();
                    info.getRunningTaskLocks().clear();
                    continue;
                }
                if(world.getTotalWorldTime()%250 == 0){
                    int size = info.getRunningTasks().size();
                    while (size>10000){
                        IFluidPressureSearchTask task = info.getRunningTasks().poll();
                        if(task == null) break;
                        info.unlockPos(task);
                        size--;
                    }
                }
            }
            if(Thread.interrupted()){
                running = false;
                break;
            }
            long usedTime = System.currentTimeMillis()-startTime;
            if(usedTime<40){
                try {
                    Thread.sleep(40-usedTime);
                } catch (InterruptedException ignored) {
                    running = false;
                }
            }
        }
        GeoCraft.getLogger().info("FluidPressureSearchManager quited");
        quit();
    }

    static void pushNewTasks(WorldServer world, WorldPressureInfo info){  //自身线程调用
        WorldQueueTaskInfo queueInfo = queueMap.get(world);

        if(queueInfo != null){
            queueInfo.queuedTasks.forEach(info::pushNewTask);
            queueInfo.clear();
        }
    }

    static void updateTasks(WorldServer world, WorldPressureInfo info){ //自身线程调用
        final Map<BlockPos,Collection<BlockPos>> resMap = info.getTaskResults();
        final Deque<IFluidPressureSearchTask> queue = info.getRunningTasks();
        for(int i=0;i<MAX_UPDATE_NUM;i++){
            if(queue.isEmpty()) break;
            IFluidPressureSearchTask task = queue.poll();
            if(task == null) continue;
            if(task.isFinished()){
                info.unlockPos(task);
                continue;
            }
            if(!world.isBlockLoaded(task.getBeginPos())){
                info.unlockPos(task);
                continue;
            }
            IBlockState beginState = world.getBlockState(task.getBeginPos());
            if(!task.isEqualState(beginState)){
                info.unlockPos(task);
                continue;
            }
            try {
                Collection<BlockPos> res = task.search(world);
                if(task.isFinished()){
                    if(res == null) resMap.put(task.getBeginPos(),Collections.emptySet());
                    else resMap.put(task.getBeginPos(),res);
                    scheduleUpdate(world,task.getBeginPos(),beginState.getBlock());
                    info.unlockPos(task);
                    task.finish();
                }else{
                    queue.add(task);
                }
            }catch (Throwable e){
                GeoCraft.getLogger().warn("When loading pressure for fluid {} at {} in world {},",task.getFluid().getUnlocalizedName(),task.getBeginPos(),world.provider.getDimension());
                GeoCraft.getLogger().warn("FluidPressureSearchManager caught an error:",e);
                task.cancel();
                info.unlockPos(task);
            }
        }
    }

    static void scheduleUpdate(WorldServer world,BlockPos pos,Block block){  //自身线程调用
        Queue<Pair<BlockPos, Block>> scheduleSet = queueToLoadPos.computeIfAbsent(world, k-> new ConcurrentLinkedQueue<>());
        scheduleSet.add(Pair.of(pos,block));
    }

    static void quit(){  //自身线程调用
        worldMap.clear();
        queueMap.clear();
        queueToLoadPos.clear();
    }

    @Nonnull
    static WorldPressureInfo getOrCreateWorldInfo(@Nonnull WorldServer world){ //多线程调用
        return worldMap.computeIfAbsent(world,k->new WorldPressureInfo());
    }

    static class WorldPressureInfo{
        public final Deque<IFluidPressureSearchTask> runningTasks = new ConcurrentLinkedDeque<>();
        public final Map<BlockPos,Collection<BlockPos>> taskResults = new ConcurrentHashMap<>();
        public final Set<BlockPos> runningTaskLocks = new ConcurrentSet<>();

        public Deque<IFluidPressureSearchTask> getRunningTasks() {
            return runningTasks;
        }

        public Map<BlockPos, Collection<BlockPos>> getTaskResults() {
            return taskResults;
        }

        public Set<BlockPos> getRunningTaskLocks() {
            return runningTaskLocks;
        }

        public void pushNewTask(IFluidPressureSearchTask task){
            runningTasks.add(task);
            runningTaskLocks.add(task.getBeginPos());
        }

        public void unlockPos(IFluidPressureSearchTask task){
            runningTaskLocks.remove(task.getBeginPos());
        }
    }

    static class WorldQueueTaskInfo{
        public final Set<IFluidPressureSearchTask> queuedTasks = new ConcurrentSet<>();
        public final Set<BlockPos> queuedTaskLocks = new ConcurrentSet<>();

        public void queueTask(IFluidPressureSearchTask task){
            queuedTasks.add(task);
            queuedTaskLocks.add(task.getBeginPos());
        }

        public void clear(){
            queuedTasks.clear();
            queuedTaskLocks.clear();
        }
    }
}
