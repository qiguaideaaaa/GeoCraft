package top.qiguaiaaaa.geocraft.geography.fluid_physics;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import top.qiguaiaaaa.geocraft.GeoCraft;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

import static top.qiguaiaaaa.geocraft.geography.fluid_physics.FluidUpdateManager.*;

/**
 * @author QiguaiAAAA
 */
public final class FluidPressureSearchManager implements Runnable{
    static final Map<WorldServer, Triple<Deque<IFluidPressureSearchTask>,Map<BlockPos,Collection<BlockPos>>,Set<BlockPos>>> worldMap = new HashMap<>(); //running的task、结果和running的task的poses
    static final Map<WorldServer,Set<IFluidPressureSearchTask>> queueToAdd = new HashMap<>(); //等待被加入queue的task
    static final Map<WorldServer,Set<BlockPos>> queueToAddPos = new HashMap<>(); //等待被加入queue的task的pos
    static final Map<WorldServer,Set<Pair<BlockPos,Block>>> queueToLoadPos = new HashMap<>();

    public static boolean isTaskRunning(@Nonnull World world,@Nonnull BlockPos pos){
        WorldServer validWorld = getValidWorld(world);
        if(validWorld == null) return false;
        Triple<Deque<IFluidPressureSearchTask>,Map<BlockPos,Collection<BlockPos>>,Set<BlockPos>> triple = getOrCreateWorldTriple(validWorld);
        Set<BlockPos> poses = queueToAddPos.computeIfAbsent(validWorld,k->new HashSet<>());
        return triple.getRight().contains(pos) || poses.contains(pos);
    }

    @Nullable
    public static Collection<BlockPos> getTaskResult(@Nonnull World world,@Nonnull BlockPos pos){
        WorldServer validWorld = getValidWorld(world);
        if(validWorld == null) return null;
        Triple<Deque<IFluidPressureSearchTask>,Map<BlockPos,Collection<BlockPos>>,Set<BlockPos>> triple = getOrCreateWorldTriple(validWorld);
        triple.getRight().remove(pos);
        return triple.getMiddle().remove(pos);
    }

    public static void addTask(@Nonnull World world,@Nonnull IFluidPressureSearchTask task){
        WorldServer validWorld = getValidWorld(world);
        if(validWorld == null) return;
        synchronized (queueToAdd){
            Set<IFluidPressureSearchTask> queueSet = queueToAdd.computeIfAbsent(validWorld,k-> new HashSet<>());
            queueSet.add(task);
            Set<BlockPos> posToAdd = queueToAddPos.computeIfAbsent(validWorld,k-> new HashSet<>());
            posToAdd.add(task.getBeginPos());
        }

    }

    public static void onWorldTick(@Nonnull WorldServer world){
        synchronized (queueToLoadPos){
            Set<Pair<BlockPos,Block>> posesToLoad = queueToLoadPos.get(world);
            if(posesToLoad == null) return;
            for(Pair<BlockPos, Block> task:posesToLoad){
                world.scheduleUpdate(task.getLeft(),task.getRight(),1);
            }
            posesToLoad.clear();
        }
    }

    @Override
    public void run() {
        boolean running = true;
        while (running){
            long startTime = System.currentTimeMillis();

            WorldServer[] loadedWorld = FMLCommonHandler.instance().getMinecraftServerInstance().worlds;
            for(WorldServer world:loadedWorld){
                Triple<Deque<IFluidPressureSearchTask>,Map<BlockPos,Collection<BlockPos>>,Set<BlockPos>> triple = getOrCreateWorldTriple(world);
                dealWithNewTasks(world,triple);
                updateTasks(world,triple);
            }
            if(Thread.interrupted()){
                running = false;
                break;
            }
            long usedTime = System.currentTimeMillis()-startTime;
            if(usedTime<500){
                try {
                    Thread.sleep(500-usedTime);
                } catch (InterruptedException ignored) {
                    running = false;
                }
            }
        }
        GeoCraft.getLogger().info("FluidPressureSearchManager quited");
        quit();
    }

    static void dealWithNewTasks(WorldServer world,Triple<Deque<IFluidPressureSearchTask>,Map<BlockPos,Collection<BlockPos>>,Set<BlockPos>> triple){
        synchronized (queueToAdd){
            Set<IFluidPressureSearchTask> queueSet = queueToAdd.get(world);
            Set<BlockPos> posesToAdd = queueToAddPos.get(world);
            if(queueSet != null){
                for(IFluidPressureSearchTask task:queueSet){
                    triple.getLeft().addLast(task);
                    triple.getRight().add(task.getBeginPos());
                }
                queueSet.clear();
            }
            if(posesToAdd != null) posesToAdd.clear();
        }
    }

    static void updateTasks(WorldServer world, Triple<Deque<IFluidPressureSearchTask>,Map<BlockPos,Collection<BlockPos>>,Set<BlockPos>> triple){
        final Map<BlockPos,Collection<BlockPos>> resMap = triple.getMiddle();
        final Deque<IFluidPressureSearchTask> queue = triple.getLeft();
        for(int i=0;i<MAX_UPDATE_NUM;i++){
            if(queue.isEmpty()) break;
            final IFluidPressureSearchTask task = queue.poll();
            if(task == null) continue;
            if(task.isFinished()){
                removeTask(triple,task);
                continue;
            }
            if(!world.isBlockLoaded(task.getBeginPos())){
                removeTask(triple,task);
                continue;
            }
            IBlockState beginState = world.getBlockState(task.getBeginPos());
            if(beginState != task.getBeginState()){
                removeTask(triple,task);
                continue;
            }
            try {
                Collection<BlockPos> res = task.search(world);
                if(task.isFinished()){
                    if(res == null) resMap.put(task.getBeginPos(),Collections.emptySet());
                    else resMap.put(task.getBeginPos(),res);
                    scheduleUpdate(world,task.getBeginPos(),beginState.getBlock());
                    removeTask(triple,task);
                }else{
                    queue.add(task);
                }
            }catch (Throwable e){
                GeoCraft.getLogger().warn("When loading pressure for fluid {} at {} in world {},",task.getFluid().getUnlocalizedName(),task.getBeginPos(),world.provider.getDimension());
                GeoCraft.getLogger().warn("FluidPressureSearchManager caught an error:",e);
                task.cancel();
                removeTask(triple,task);
            }
        }
    }

    static void scheduleUpdate(WorldServer world,BlockPos pos,Block block){
        synchronized (queueToLoadPos){
            Set<Pair<BlockPos, Block>> scheduleSet = queueToLoadPos.computeIfAbsent(world, k-> new HashSet<>());
            scheduleSet.add(Pair.of(pos,block));
        }

    }

    static void removeTask(Triple<Deque<IFluidPressureSearchTask>,Map<BlockPos,Collection<BlockPos>>,Set<BlockPos>> triple,IFluidPressureSearchTask task){
        triple.getRight().remove(task.getBeginPos());
    }

    static void quit(){
        worldMap.clear();
        queueToAdd.clear();
    }

    @Nonnull
    static Triple<Deque<IFluidPressureSearchTask>,Map<BlockPos,Collection<BlockPos>>,Set<BlockPos>> getOrCreateWorldTriple(@Nonnull WorldServer world){
        synchronized (worldMap){
            return worldMap.computeIfAbsent(world, k -> Triple.of(new LinkedList<>(),new HashMap<>(),new HashSet<>()));
        }
    }
}
