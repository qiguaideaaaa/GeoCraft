package top.qiguaiaaaa.geocraft.geography.fluid_physics.reality.pressure;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fluids.Fluid;
import top.qiguaiaaaa.geocraft.api.util.FluidUtil;
import top.qiguaiaaaa.geocraft.util.FluidSearchUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

/**
 * 注意两边方法需要同步
 * @see ShortRealityVanillaPressureSearchTask
 * @author QiguaiAAAA
 */
public class ByteRealityVanillaPressureSearchTask extends ByteRealityPressureSearchTask {
    protected final byte beginQuanta;

    ByteRealityVanillaPressureSearchTask(@Nonnull Fluid fluid, @Nonnull IBlockState beginState, @Nonnull BlockPos beginPos, int searchRange) {
        super(fluid, beginState, beginPos, searchRange);
        beginQuanta = (byte) (8-beginState.getValue(BlockLiquid.LEVEL));
    }

    @Override
    public boolean isFinished() {
        return res.size()>8 || searchTimes>=maxSearchTimes || isQueueEmpty();
    }

    @Nullable
    @Override
    public Collection<BlockPos> search(WorldServer world) {
        for(int i=0;i<64;i++){
            if(queue.isEmpty()) break;
            BlockPos pos = pull();
            searchTimes++;
            if(pos.getY() > beginPos.getY()) continue;
            if(!world.isBlockLoaded(pos)) continue;
            IBlockState state = world.getBlockState(pos);
            if(state.getMaterial() == Material.AIR){
                air:{
                    if(pos.getY() == beginPos.getY() && beginQuanta == 1) break air;
                    putBlockPosToResults(pos);
                }
            }else if(FluidUtil.getFluid(state) == fluid){
                int quanta = 8-state.getValue(BlockLiquid.LEVEL);
                if((pos.getY()<beginPos.getY() && quanta <8) || (pos.getY() == beginPos.getY() && quanta < beginQuanta-1))
                    putBlockPosToResults(pos);
            }
            if(res.size()>8) return res; //够了
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
}
