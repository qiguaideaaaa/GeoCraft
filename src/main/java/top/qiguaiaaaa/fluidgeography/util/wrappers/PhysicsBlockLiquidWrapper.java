package top.qiguaiaaaa.fluidgeography.util.wrappers;

import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.wrappers.BlockLiquidWrapper;
import top.qiguaiaaaa.fluidgeography.api.util.FluidUtil;
import top.qiguaiaaaa.fluidgeography.api.util.math.FlowChoice;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PhysicsBlockLiquidWrapper extends BlockLiquidWrapper {
    public static final EnumFacing[] SIDES = new EnumFacing[] {EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST};
    public PhysicsBlockLiquidWrapper(BlockLiquid blockLiquid, World world, BlockPos blockPos) {
        super(blockLiquid, world, blockPos);
    }
    @Override
    public int fill(FluidStack resource, boolean doFill) {
        if (resource.amount < Fluid.BUCKET_VOLUME) {
            return 0;
        }

        if (doFill) {
            BlockPos placePos = findPlaceableLocation();
            if(placePos == null) return 0;

            int quantaLeft = placeLiquid(placePos,8);
            if(quantaLeft<=0) return Fluid.BUCKET_VOLUME;
            List<FlowChoice> choices = new ArrayList<>();
            for(EnumFacing facing:SIDES){
                BlockPos facingPos = placePos.offset(facing);
                if(!world.isBlockLoaded(facingPos)) continue;
                if(!isPlaceable(facingPos)) continue;
                int facingQuanta = FluidUtil.getFluidQuanta(world,facingPos,world.getBlockState(facingPos));
                choices.add(new FlowChoice(facingQuanta,facing));
            }
            if(!choices.isEmpty()){
                choices.sort(Comparator.comparingInt(FlowChoice::getQuanta));
                int target = choices.get(0).getQuanta()+1;
                while (target<=8){
                    for(FlowChoice choice:choices){
                        if(choice.getQuanta()>=target) continue;
                        quantaLeft--;
                        choice.addQuanta(1);
                        if(quantaLeft <=0) break;
                    }
                    if(quantaLeft<=0) break;
                    target++;
                }
                for(FlowChoice choice:choices){
                    if(choice.getQuanta()>target)continue;
                    BlockPos facingPos = placePos.offset(choice.direction);
                    directlyPlaceLiquid(facingPos,8-choice.getQuanta());
                }
            }
            if(quantaLeft<=0) return Fluid.BUCKET_VOLUME;
            BlockPos upPos = placePos.up();
            if(!isPlaceable(upPos)) return Fluid.BUCKET_VOLUME;
            placeLiquid(upPos,quantaLeft);
        }

        return Fluid.BUCKET_VOLUME;
    }

    /**
     * 在某处放置指定量的液体
     * @param placePos 位置
     * @param placeQuanta 放置量
     * @return 剩余未放置的量
     */
    protected int placeLiquid(BlockPos placePos,int placeQuanta){
        if(placeQuanta <=0) return 0;
        IBlockState state = world.getBlockState(placePos);
        int quanta = FluidUtil.getFluidQuanta(world,placePos,state);
        int newQuanta = quanta+placeQuanta;
        if(newQuanta<=8){
            directlyPlaceLiquid(placePos,8-newQuanta);
            return 0;
        }
        directlyPlaceLiquid(placePos,0);
        return newQuanta-8;
    }
    protected void directlyPlaceLiquid(BlockPos placePos,int level){
        Material material = blockLiquid.getDefaultState().getMaterial();
        BlockLiquid block = BlockLiquid.getFlowingBlock(material);
        world.setBlockState(placePos, block.getDefaultState().withProperty(BlockLiquid.LEVEL, level), 11);
    }
    protected BlockPos findPlaceableLocation(){
        BlockPos.MutableBlockPos nowPos = new BlockPos.MutableBlockPos(blockPos);
        while(!isPlaceable(nowPos)){
            nowPos.setPos(nowPos.getX(),nowPos.getY()+1, nowPos.getZ());
            if(nowPos.getY()>255) return null;
        }
        return nowPos;
    }
    protected boolean isPlaceable(BlockPos pos){
        return FluidUtil.isFluidPlaceable(world,pos,FluidUtil.getFluid(blockLiquid));
    }
}
