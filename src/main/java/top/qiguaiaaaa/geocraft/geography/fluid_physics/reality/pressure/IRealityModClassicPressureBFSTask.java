package top.qiguaiaaaa.geocraft.geography.fluid_physics.reality.pressure;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fluids.BlockFluidBase;
import top.qiguaiaaaa.geocraft.api.util.FluidUtil;
import top.qiguaiaaaa.geocraft.util.fluid.FluidSearchUtil;

import javax.annotation.Nonnull;

/**
 * @author QiguaiAAAA
 */
public interface IRealityModClassicPressureBFSTask extends IRealityPressureBFSTask{
    byte getDensityDir();

    @Override
    default boolean search_Inner(@Nonnull WorldServer world,@Nonnull BlockPos pos){
        if(!world.isBlockLoaded(pos)) return false;
        IBlockState state = world.getBlockState(pos);
        if(state.getMaterial() == Material.AIR){
            if(pos.getY() != getBeginPos().getY() || getBeginQuanta() >1)
                putBlockPosToResults(pos);
        }else if(FluidUtil.getFluid(state) == getFluid()){
            int quanta = getQuantaPerBlock()-state.getValue(BlockFluidBase.LEVEL);
            if((isLowerPos(pos) && quanta <getQuantaPerBlock()) || (pos.getY() == getBeginPos().getY() && quanta < getBeginQuanta()-1))
                putBlockPosToResults(pos);
        }
        if(hasFoundEnoughResults()) return true; //够了
        if(hasSearchTimeReachedMax()) return true;

        if(state.getMaterial() == Material.AIR) return false;
        for(int[] dir: FluidSearchUtil.DIRS6){
            if(pos.getY() == getBeginPos().getY() && dir[1]* getDensityDir() >0) continue;
            mutablePos.setPos(pos.getX()+dir[0],pos.getY()+dir[1],pos.getZ()+dir[2]);
            if(mutablePos.getY()<0 || mutablePos.getY()>=world.getHeight()) continue;
            if(isVisited(mutablePos)) continue;
            markVisited(mutablePos);
            if(canSearchInto(world,mutablePos,dir)){
                queued(mutablePos);
            }
        }
        return false;
    }

    default boolean isLowerPos(BlockPos pos){
        return getDensityDir()>0?pos.getY()<getBeginPos().getY():pos.getY()>getBeginPos().getY();
    }

    default boolean canSearchInto(@Nonnull WorldServer world,@Nonnull BlockPos pos, int[] dir){
        if(!world.isBlockLoaded(pos)) return false;
        IBlockState state = world.getBlockState(pos);
        if(state.getMaterial() == Material.AIR &&
                (dir[1] != 0 || getBeginQuanta()>1 || isLowerPos(pos))
        ) return true;
        if(FluidUtil.getFluid(state) == getFluid()) return true;
        return ((BlockFluidBase)getBeginState().getBlock()).canDisplace(world,pos);
    }

    @Override
    default boolean isEqualState(@Nonnull IBlockState curState){
        return getBeginState() == curState;
    }
}
