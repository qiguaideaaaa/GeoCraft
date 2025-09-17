package top.qiguaiaaaa.geocraft.geography.fluid_physics.reality.pressure;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fluids.Fluid;
import top.qiguaiaaaa.geocraft.GeoCraft;
import top.qiguaiaaaa.geocraft.api.util.FluidUtil;
import top.qiguaiaaaa.geocraft.api.util.debug.IDebug;
import top.qiguaiaaaa.geocraft.util.FluidSearchUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

/**
 * @author QiguaiAAAA
 */
public class ShortRealityVanillaPressureSearchTask extends ShortRealityPressureSearchTask {
    protected final byte beginQuanta;

    ShortRealityVanillaPressureSearchTask(@Nonnull Fluid fluid, @Nonnull IBlockState beginState, @Nonnull BlockPos beginPos, int searchRange) {
        super(fluid, beginState, beginPos, searchRange);
        beginQuanta = (byte) (8-beginState.getValue(BlockLiquid.LEVEL));
    }

    @Override
    public boolean isFinished() {
        return res.size()>beginQuanta+2 || searchTimes>=maxSearchTimes || isQueueEmpty();
    }

    @Nullable
    @Override
    public Collection<BlockPos> search(WorldServer world) {
        for(int i=0;i<128;i++){
            if(queue.isEmpty()) break;
            BlockPos pos = pull();
            searchTimes++;
            if(!world.isBlockLoaded(pos)) continue;
            IBlockState state = world.getBlockState(pos);
            if(state.getMaterial() == Material.AIR){
                if(pos.getY() < beginPos.getY() || beginQuanta > 1)
                    putBlockPosToResults(pos);
            }else if(FluidUtil.getFluid(state) == fluid){
                int quanta = 8-state.getValue(BlockLiquid.LEVEL);
                if((pos.getY()<beginPos.getY() && quanta <8) || (pos.getY() == beginPos.getY() && quanta < beginQuanta-1)) putBlockPosToResults(pos);
            }
            if(res.size()>beginQuanta+2) return res; //够了
            if(searchTimes> maxSearchTimes) return res;

            if(state.getMaterial() == Material.AIR) continue;
            for(int[] dir:FluidSearchUtil.DIRS6){
                if(pos.getY() == beginPos.getY() && dir[1]>0) continue;
                mutablePos.setPos(pos.getX()+dir[0],pos.getY()+dir[1],pos.getZ()+dir[2]);
                if(mutablePos.getY()<0) continue;
                if(isVisited(mutablePos)) continue;
                markVisited(mutablePos);
                if(canSearchInto(world,mutablePos,dir)){
                    queued(mutablePos);
                }
            }
        }
        return res;
    }

    public boolean canSearchInto(WorldServer world,BlockPos pos,int[] dir){
        if(!world.isBlockLoaded(pos)) return false;
        IBlockState state = world.getBlockState(pos);
        if(state.getMaterial() == Material.AIR && (dir[1] != 0 || beginQuanta>1 || pos.getY() < beginPos.getY())) return true;
        return FluidUtil.getFluid(state) == fluid;
    }

    @Override
    public boolean isEqualState(IBlockState curState) {
        if(FluidUtil.getFluid(curState) == fluid){
            return curState.getValue(BlockLiquid.LEVEL) == 8-beginQuanta;
        }
        return false;
    }

    static class Debug extends ShortRealityVanillaPressureSearchTask implements IDebug {
        Debug(@Nonnull Fluid fluid, @Nonnull IBlockState beginState, @Nonnull BlockPos beginPos, int searchRange) {
            super(fluid, beginState, beginPos, searchRange);
            GeoCraft.getLogger().info("task at {} about {} fluid {} searchRange {} is created",beginPos,beginState,fluid.getName(),searchRange);
        }

        @Override
        public boolean isEqualState(IBlockState curState) {
            if(super.isEqualState(curState)){
                return true;
            }
            GeoCraft.getLogger().info("task at {} about {} is stopped because state changed to {}",beginPos,beginState,curState);
            return false;
        }

        @Override
        public boolean isFinished() {
            if(super.isFinished()){
                GeoCraft.getLogger().info("task at {} about {} is finished with visited {} , queued {}, res {}, search times {}, max search times {}",
                        beginPos,beginState,visited.size(),queue.size(),res.size(),searchTimes,maxSearchTimes);
                return true;
            }
            return false;
        }

        @Override
        public void cancel() {
            super.cancel();
            GeoCraft.getLogger().info("task at {} about {} is cancelled",beginPos,beginState);
        }

        @Override
        public void putBlockPosToResults(@Nonnull BlockPos pos) {
            super.putBlockPosToResults(pos);
            GeoCraft.getLogger().info("task at {} about {} found a result {}",beginPos,beginState,pos);
        }

        @Override
        public void queued(@Nonnull BlockPos pos) {
            super.queued(pos);
            GeoCraft.getLogger().info("task at {} about {} queued {}",beginPos,beginState,pos);
        }

        @Override
        public void markVisited(@Nonnull BlockPos pos) {
            super.markVisited(pos);
            GeoCraft.getLogger().info("task at {} about {} mark {} as visited",beginPos,beginState,pos);
        }

        @Override
        public void finish() {
            super.finish();
            GeoCraft.getLogger().info("task at {} about {} finished",beginPos,beginState);
            for(BlockPos pos:res){
                GeoCraft.getLogger().info("res {}",pos);
            }
        }

        @Nonnull
        @Override
        public BlockPos pull() {
            GeoCraft.getLogger().info("task at {} about {} tried pull {}",beginPos,beginState,queue.peek());
            return super.pull();
        }

        @Override
        public boolean canSearchInto(WorldServer world, BlockPos pos, int[] dir) {
            if(super.canSearchInto(world, pos, dir)){
                GeoCraft.getLogger().info("task at {} about {} checked {} by dir ({},{},{}) and sure it can be searched",
                        beginPos,beginState,pos,dir[0],dir[1],dir[2]);
                return true;
            }
            return false;
        }

        @Nullable
        @Override
        public Collection<BlockPos> search(WorldServer world) {
            GeoCraft.getLogger().info("task at {} about {} is being running",beginPos,beginState);
            return super.search(world);
        }
    }

}
