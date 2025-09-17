package top.qiguaiaaaa.geocraft.geography.fluid_physics.reality.pressure;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;
import top.qiguaiaaaa.geocraft.api.util.FluidUtil;
import top.qiguaiaaaa.geocraft.util.FluidSearchUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

/**
 * 两边需要时刻保持同步
 * @see ShortRealityModClassicPressureSearchTask
 * @author QiguaiAAAA
 */
public class ByteRealityModClassicPressureSearchTask extends ByteRealityPressureSearchTask {
    protected final byte beginQuanta;
    protected final byte quantaPerBlock;
    protected final byte densityDir;

    ByteRealityModClassicPressureSearchTask(@Nonnull Fluid fluid, @Nonnull IBlockState beginState, @Nonnull BlockPos beginPos, int searchRange, int quantaPerBlock) {
        super(fluid, beginState, beginPos, searchRange);
        beginQuanta = (byte) (quantaPerBlock-beginState.getValue(BlockFluidBase.LEVEL));
        this.quantaPerBlock = (byte) quantaPerBlock;
        this.densityDir = (byte) (fluid.getDensity()>0?1:-1);
    }

    @Nullable
    @Override
    public Collection<BlockPos> search(WorldServer world) {
        for(int i=0;i<32;i++){
            if(queue.isEmpty()) break;
            BlockPos pos = pull();
            searchTimes++;
            if(!isValidPos(pos)) continue;
            if(!world.isBlockLoaded(pos)) continue;
            IBlockState state = world.getBlockState(pos);
            if(state.getMaterial() == Material.AIR){
                air:{
                    if(pos.getY() == beginPos.getY() && beginQuanta == 1) break air;
                    putBlockPosToResults(pos);
                }
            }else if(FluidUtil.getFluid(state) == fluid){
                int quanta = quantaPerBlock-state.getValue(BlockFluidBase.LEVEL);
                if((isLowerPos(pos) && quanta <quantaPerBlock) || (pos.getY() == beginPos.getY() && quanta < beginQuanta-1))
                    putBlockPosToResults(pos);
            }
            if(res.size()>quantaPerBlock) return res; //够了
            if(searchTimes>maxSearchTimes) return res;

            if(state.getMaterial() == Material.AIR) continue;
            for(int[] dir: FluidSearchUtil.DIRS6){
                if(pos.getY() == beginPos.getY() && dir[1]* densityDir >0) continue;
                mutablePos.setPos(pos.getX()+dir[0],pos.getY()+dir[1],pos.getZ()+dir[2]);
                if(mutablePos.getY()<0 || mutablePos.getY()>=world.getHeight()) continue;
                if(isVisited(mutablePos)) continue;
                markVisited(mutablePos);
                if(canSearchInto(world,mutablePos,dir)){
                    queued(mutablePos);
                }
            }
        }
        return res;
    }

    protected boolean isValidPos(BlockPos pos){
        return densityDir>0?pos.getY()<=beginPos.getY():pos.getY()>=beginPos.getY();
    }
    protected boolean isLowerPos(BlockPos pos){
        return densityDir>0?pos.getY()<beginPos.getY():pos.getY()>beginPos.getY();
    }

    public boolean canSearchInto(WorldServer world,BlockPos pos,int[] dir){
        if(!world.isBlockLoaded(pos)) return false;
        IBlockState state = world.getBlockState(pos);
        if(state.getMaterial() == Material.AIR &&
                (dir[1] != 0 || beginQuanta>1 || isLowerPos(pos))
        ) return true;
        return FluidUtil.getFluid(state) == fluid;
    }
}
