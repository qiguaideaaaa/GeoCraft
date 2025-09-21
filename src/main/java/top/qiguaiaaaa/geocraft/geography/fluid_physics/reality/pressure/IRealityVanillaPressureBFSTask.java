package top.qiguaiaaaa.geocraft.geography.fluid_physics.reality.pressure;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import top.qiguaiaaaa.geocraft.api.util.FluidUtil;
import top.qiguaiaaaa.geocraft.util.fluid.BlockLiquidUtil;
import top.qiguaiaaaa.geocraft.util.fluid.FluidSearchUtil;

import javax.annotation.Nonnull;

/**
 * @author QiguaiAAAA
 */
public interface IRealityVanillaPressureBFSTask extends IRealityPressureBFSTask{
    @Override
    default boolean search_Inner(@Nonnull WorldServer world,@Nonnull BlockPos pos){
        if(!world.isBlockLoaded(pos)) return false;
        IBlockState state = world.getBlockState(pos);
        if(state.getMaterial() == Material.AIR){
            if(pos.getY() != getBeginPos().getY() || getBeginQuanta() > 1)
                putBlockPosToResults(pos);
        }else if(FluidUtil.getFluid(state) == getFluid()){
            int quanta = 8-state.getValue(BlockLiquid.LEVEL);
            if((pos.getY()<getBeginPos().getY() && quanta <8) || (pos.getY() == getBeginPos().getY() && quanta < getBeginQuanta()-1))
                putBlockPosToResults(pos);
        }
        if(hasFoundEnoughResults()) return true; //够了
        if(hasSearchTimeReachedMax()) return true;

        if(state.getMaterial() == Material.AIR) return false;
        for(int[] dir: FluidSearchUtil.DIRS6){
            if(pos.getY() == getBeginPos().getY() && dir[1]>0) continue;
            mutablePos.setPos(pos.getX()+dir[0],pos.getY()+dir[1],pos.getZ()+dir[2]);
            if(mutablePos.getY()<0) continue;
            if(isVisited(mutablePos)) continue;
            markVisited(mutablePos);
            if(canSearchInto(world,mutablePos,dir)){
                queued(mutablePos);
            }
        }
        return false;
    }
    default boolean canSearchInto(@Nonnull WorldServer world,@Nonnull BlockPos pos, int[] dir){
        if(!world.isBlockLoaded(pos)) return false;
        IBlockState state = world.getBlockState(pos);
        if(state.getMaterial() == Material.AIR && (dir[1] != 0 || getBeginQuanta()>1 || pos.getY() < getBeginPos().getY())) return true;
        if(FluidUtil.getFluid(state) == getFluid()) return true;
        return !BlockLiquidUtil.isBlocked(state);
    }

    @Override
    default boolean isEqualState(@Nonnull IBlockState curState) {
        if(FluidUtil.getFluid(curState) == getFluid()){
            return curState.getValue(BlockLiquid.LEVEL) == 8-getBeginQuanta();
        }
        return false;
    }

    @Override
    default byte getQuantaPerBlock(){
        return 8;
    }
}
