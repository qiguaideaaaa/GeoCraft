package top.qiguaiaaaa.geocraft.geography.fluid_physics.reality;

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
 * @author QiguaiAAAA
 */
public class MoreRealityBlockFluidClassicPressureSearchTask extends MoreRealityPressureSearchTask {
    protected final int beginQuanta;
    protected final int quantaPerBlock;
    protected final int densityDir;

    public MoreRealityBlockFluidClassicPressureSearchTask(@Nonnull Fluid fluid, @Nonnull IBlockState beginState, @Nonnull BlockPos beginPos,int searchRange,int quantaPerBlock) {
        super(fluid, beginState, beginPos,searchRange);
        beginQuanta = quantaPerBlock-beginState.getValue(BlockFluidBase.LEVEL);
        this.quantaPerBlock = quantaPerBlock;
        this.densityDir = fluid.getDensity()>0?1:-1;
    }

    @Nullable
    @Override
    public Collection<BlockPos> search(WorldServer world) {
        BlockPos.MutableBlockPos curPos = new BlockPos.MutableBlockPos();
        for(int i=0;i<32;i++){
            if(queue.isEmpty()) break;
            BlockPos pos = queue.poll();
            searchTimes++;
            if(!isValidPos(pos)) continue;
            if(!world.isBlockLoaded(pos)) continue;
            IBlockState state = world.getBlockState(pos);
            if(state.getMaterial() == Material.AIR){
                air:{
                    if(pos.getY() == beginPos.getY() && beginQuanta == 1) break air;
                    res.add(pos);
                }
            }else if(FluidUtil.getFluid(state) == fluid){
                int quanta = quantaPerBlock-state.getValue(BlockFluidBase.LEVEL);
                if((isLowerPos(pos) && quanta <quantaPerBlock) || (pos.getY() == beginPos.getY() && quanta < beginQuanta-1)) res.add(pos);
            }
            if(res.size()>quantaPerBlock) return res; //够了
            if(searchTimes>maxSearchTimes) return res;

            if(state.getMaterial() == Material.AIR) continue;
            for(int[] dir: FluidSearchUtil.DIRS6){
                if(pos.getY() == beginPos.getY() && dir[1]* densityDir >0) continue;
                curPos.setPos(pos.getX()+dir[0],pos.getY()+dir[1],pos.getZ()+dir[2]);
                if(curPos.getY()<0 || curPos.getY()>=world.getHeight()) continue;
                BlockPos curPosIm = curPos.toImmutable();
                if(visited.contains(curPosIm)) continue;
                visited.add(curPosIm);
                if(canSearchInto(world,curPosIm,dir)){
                    queue.add(curPosIm);
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
